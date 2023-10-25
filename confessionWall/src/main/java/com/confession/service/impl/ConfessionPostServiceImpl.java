package com.confession.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.confession.comm.*;
import com.confession.config.WallConfig;
import com.confession.dto.ConfessionPostAdminDTO;
import com.confession.dto.ConfessionPostDTO;
import com.confession.dto.UserDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.mapper.ConfessionpostMapper;
import com.confession.mapper.ConfessionwallMapper;
import com.confession.mapper.UserMapper;
import com.confession.pojo.Confessionpost;
import com.confession.pojo.Confessionwall;
import com.confession.pojo.User;
import com.confession.request.AuditRequest;
import com.confession.request.ConfessionPostRequest;
import com.confession.request.DeleteSubmissionRequest;
import com.confession.service.*;
import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.confession.comm.RedisConstant.*;
import static com.confession.comm.ResultCodeEnum.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 作者 xpl
 * @since 2023年08月20日
 */
@Service
public class ConfessionPostServiceImpl extends ServiceImpl<ConfessionpostMapper, Confessionpost> implements ConfessionPostService {

    @Resource
    private ConfessionpostMapper confessionpostMapper;

    @Resource
    private UserService userService;

    @Resource
    private CommentService commentService;

    @Resource
    private AdminService adminService;

    @Resource
    private SensitiveTextFilter sensitiveTextFilter;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ConfessionwallMapper confessionwallMapper;

    @Resource
    private ConfessionwallService confessionwallService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private WallConfig wallConfig;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public int getPostCountByUserIdAndDate(Integer userId, LocalDate date) {
        return confessionpostMapper.getPostCountByUserIdAndDate(userId, date);
    }

    /**
     * 合并敏感词匹配和替换：当前代码中使用两个循环分别对标题和文本内容进行敏感词匹配和替换，可以将它们合并为一个循环来避免重复遍历。
     * <p>
     * 使用 StringBuilder 的 setCharAt 方法替换字符：当前代码中使用 replace 方法替换字符，
     * 每次替换都会创建一个新的 StringBuilder 对象，存在额外的开销。可以使用 setCharAt 方法直接修改字符。
     * <p>
     * 避免不必要的循环：如果没有敏感词匹配，可以直接返回 false，无需进行后续处理。
     *
     * @param request 表白墙发布请求，里面会对敏感字进行替换
     * @return
     */
    @Override
    public boolean filterContent(ConfessionPostRequest request) {
        AhoCorasickDoubleArrayTrie<String> trie = sensitiveTextFilter.getTrie(); // 获取敏感词库的 Trie 实例
        String title = request.getTitle();
        String textContent = request.getTextContent();

        StringBuilder filteredTitleBuilder = new StringBuilder(title);
        StringBuilder filteredTextContentBuilder = new StringBuilder(textContent);
        // 合并标题和文本内容进行敏感词匹配
        Collection<AhoCorasickDoubleArrayTrie.Hit<String>> hits = trie.parseText(title + " " + textContent);

        if (hits.isEmpty()) {
            return false; // 没有敏感词匹配，直接返回 false
        }

        Boolean hasSensitiveWords = false; // 是否存在敏感词,默认是不存在

        if (hits.size() > 0) {
            hasSensitiveWords = true;
        }

        for (AhoCorasickDoubleArrayTrie.Hit<String> hit : hits) {
            int start = hit.begin; // 敏感词在输入文本中的起始位置
            int end = hit.end; // 敏感词在输入文本中的结束位置
            StringBuilder targetBuilder = (start < title.length()) ? filteredTitleBuilder : filteredTextContentBuilder;
            int targetStart = (start < title.length()) ? start : (start - title.length() - 1);
            int targetEnd = (end <= title.length()) ? end : (end - title.length() - 1);

            for (int i = targetStart; i < targetEnd; i++) {
                targetBuilder.setCharAt(i, '*');
            }
        }

        request.setTitle(filteredTitleBuilder.toString());
        request.setTextContent(filteredTextContentBuilder.toString());

        return hasSensitiveWords;

    }

    @Override
    public List<ConfessionPostDTO> getPublishedPosts(Integer userId, PageTool pageTool) {
        // 构建缓存键
        String cacheKey = USER_PUBLISHED_POSTS + userId + ":" + pageTool.getPage();
        // 先尝试从缓存中获取数据
        JSON jsonObj = (JSON) redisTemplate.opsForValue().get(cacheKey);
        if (jsonObj != null) {
            return jsonObj.toJavaObject(List.class);
        }
        // 从数据库或其他数据源获取数据
        List<ConfessionPostDTO> posts = getPostsByStatus(pageTool, userId, 1);
        Object json = JSONObject.toJSON(posts);
        // 将数据存入缓存并设置过期时间
        redisTemplate.opsForValue().set(cacheKey, json, 30, TimeUnit.MINUTES);
        return posts;
    }

    //删除已经发布投稿的缓存
    private void removeUserPublishedPosts(Integer userId) {
        for (int i = 1; i < 6; i++) {
            redisTemplate.delete(USER_PUBLISHED_POSTS + userId + ":" + i);
        }
    }

    @Override
    public List<ConfessionPostDTO> getPendingPosts(Integer userId, PageTool pageTool) {
        // 构建缓存键
        String cacheKey = USER_PENDING_POSTS + userId + ":" + pageTool.getPage();
        // 先尝试从缓存中获取数据
        JSON jsonObj = (JSON) redisTemplate.opsForValue().get(cacheKey);
        if (jsonObj != null) {
            return jsonObj.toJavaObject(List.class);
        }
        // 从数据库或其他数据源获取数据
        List<ConfessionPostDTO> posts = getPostsByStatus(pageTool, userId, 0);
        // 将数据存入缓存并设置过期时间
        redisTemplate.opsForValue().set(cacheKey, JSONObject.toJSON(posts), 30, TimeUnit.MINUTES);
        return posts;
    }

    private void removeUserPendingPosts(Integer userId) {
        for (int i = 1; i < 3; i++) {
            redisTemplate.delete(USER_PENDING_POSTS + userId + ":" + i);
        }
    }


    @Override
    public List<ConfessionPostDTO> getPostsPage(Integer wallId, Integer page, Integer limit) {
        LambdaQueryWrapper<Confessionpost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Confessionpost::getWallId, wallId)
                .eq(Confessionpost::getPostStatus, 1)
                .or()
                .eq(Confessionpost::getIsAdminPost, true)
                .orderByDesc(Confessionpost::getCreateTime);
        Page<Confessionpost> pageZj = new Page<>(page, limit);
        List<Confessionpost> list = confessionpostMapper.selectPage(pageZj, queryWrapper).getRecords();
        return list.stream().map(this::convertToDTOAll).collect(Collectors.toList());
    }

    @Override
    public List<ConfessionPostDTO> getPendingPostsAdmin(Integer wallId, PageTool pageTool) {
        LambdaQueryWrapper<Confessionpost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Confessionpost::getPostStatus, 0);
        wrapper.eq(Confessionpost::getWallId, wallId);
        // 设置分页信息
        Page<Confessionpost> page = new Page<>(pageTool.getPage(), pageTool.getLimit());

        IPage<Confessionpost> iPage = confessionpostMapper.selectPage(page, wrapper);

        return iPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void submissionReview(Integer userId, AuditRequest request) {
        boolean admin = adminService.isAdmin(userId, request.getWallId());
        if (!admin) {
            throw new WallException(LOGIN_ACL);
        }
        LambdaUpdateWrapper<Confessionpost> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Confessionpost::getId, request.getId())
                .eq(Confessionpost::getWallId, request.getWallId())
                .set(Confessionpost::getPostStatus, request.getPostStatus())
                .set(Confessionpost::getPublishTime, LocalDateTime.now());
        int update = confessionpostMapper.update(null, updateWrapper);
        if (update < 1) {
            throw new WallException(FAIL);
        }
        //这里通过判断发布状态字段同步缓存
        if (request.getPostStatus() == 1) {
            this.obtainWallLockSyncCache(request.getWallId(), request.getId(),new Date().toInstant().getEpochSecond(),true);
            this.removeUserPendingPosts(request.getPostUserId());
            this.removeUserPublishedPosts(request.getPostUserId());
        }

    }

    @Override
    public Integer getAdminPostCount() {
        int adminPostCount = confessionpostMapper.getAdminPostCount(LocalDate.now());
        return adminPostCount;
    }

    @Override
    public PageResult confessionWallContentQuery(PageTool pageTool, String fuzzyQueryContent, String wallName, String userName,
                                                 Boolean isAnonymous, Boolean isAdminPost, Integer postStatus, Boolean reverseOrder) {
        LambdaQueryWrapper<Confessionpost> queryWrapper = new LambdaQueryWrapper<>();
        Page<Confessionpost> page = new Page<>(pageTool.getPage(), pageTool.getLimit());
        // 模糊查询标题和文字内容
        if (StringUtils.isNotBlank(fuzzyQueryContent)) {
            queryWrapper.and(wrapper -> wrapper.like(Confessionpost::getTitle, fuzzyQueryContent)
                    .or().like(Confessionpost::getTextContent, fuzzyQueryContent));
        }
        // 墙名字模糊查询  注意和用户名都要加上非空判断，是空直接返回
        if (StringUtils.isNotBlank(wallName)) {
            List<Integer> wallIds = confessionwallMapper.selectList
                            (new LambdaQueryWrapper<Confessionwall>().like(Confessionwall::getWallName, wallName))
                    .stream()
                    .map(Confessionwall::getId)
                    .collect(Collectors.toList());
            if (wallIds.isEmpty()) {
                return new PageResult(null, page.getTotal(), 0);
            }
            queryWrapper.in(Confessionpost::getWallId, wallIds);
        }
        // 用户名模糊查询
        if (StringUtils.isNotBlank(userName)) {
            List<Integer> userIds = userMapper.selectList(new LambdaQueryWrapper<User>().like(User::getUsername, userName))
                    .stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            if (userIds.isEmpty()) {
                // 如果userIds列表为空，直接返回0条记录
                return new PageResult(null, page.getTotal(), 0);
            }
            queryWrapper.in(Confessionpost::getUserId, userIds);
        }

        // 是否匿名查询
        if (isAnonymous != null) {
            queryWrapper.eq(Confessionpost::getIsAnonymous, isAnonymous);
        }
        // 发布状态查询
        if (postStatus != null) {
            queryWrapper.eq(Confessionpost::getPostStatus, postStatus);
        }
        // 排序方式，默认按id倒序
        if (reverseOrder != null && reverseOrder) {
            queryWrapper.orderByDesc(Confessionpost::getId);
        }
        // 是否超级管理员发布
        if (isAdminPost != null) {
            queryWrapper.eq(Confessionpost::getIsAdminPost, isAdminPost);
        }else {
            if (StringUtils.isNotBlank(fuzzyQueryContent)|| StringUtils.isNotBlank(wallName)||postStatus!=null||isAnonymous!=null||StringUtils.isNotBlank(userName)){
                queryWrapper.or(wrapper -> wrapper.eq(Confessionpost::getIsAdminPost,true));
            }
        }

        // 分页查询
        List<Confessionpost> records = this.page(page, queryWrapper).getRecords();
        List<ConfessionPostAdminDTO> res = records.stream().map(
                this::convertToAdminViewDTO
        ).collect(Collectors.toList());
        return new PageResult<>(res, page.getTotal(), records.size());
    }

    @Override
    public void modifyPublishingStatus(AuditRequest request) {
        LambdaUpdateWrapper<Confessionpost> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Confessionpost::getId, request.getId())
                .eq(Confessionpost::getWallId, request.getWallId())
                .set(Confessionpost::getPostStatus, request.getPostStatus());
        if (request.getPostStatus() == 1) {
            updateWrapper.set(Confessionpost::getPublishTime, LocalDateTime.now());
        }
        int update = confessionpostMapper.update(null, updateWrapper);
        if (update < 1) {
            throw new WallException(FAIL);
        }
        if (request.getPostStatus() == 1) {
            //同步表白墙缓存
            obtainWallLockSyncCache(request.getWallId(),request.getId(),new Date().toInstant().getEpochSecond(),true);
            //删除用户缓存
            this.removeUserPendingPosts(request.getPostUserId());
            this.removeUserPublishedPosts(request.getPostUserId());
        }
        if (request.getPostStatus()==2){
            //删除缓存
            RLock lock = redissonClient.getLock(SCHOOL_WALL_MAIN_LIST_MOD_LOCK + request.getWallId());
            lock.lock();
            redisTemplate.opsForZSet().remove(WALL_POSTS_PREFIX + request.getWallId(),request.getId());
            //删数据库记录
            redisTemplate.delete(POST_SUBMISSION_RECORD + request.getId());
            lock.unlock();
        }
    }

    @Override
    public List<ConfessionPostDTO> confessionPostService(Integer wallId, int page, int limit) {
        int startIndex = page==1?0:(page - 1) * limit-1;
        if (startIndex > 220404) {
            //不太可能有这么多的数据，直接报错
            throw new WallException(NO_SUBMISSION_DATA);
        }
        //这里两个都是索引，包左不包右
        Set<Integer> zSetMembers = redisTemplate.opsForZSet().reverseRange(WALL_POSTS_PREFIX + wallId, startIndex,startIndex+limit-1);
        System.out.println(zSetMembers.size());
        System.out.println(limit);
        List<ConfessionPostDTO> posts;
        if (zSetMembers.isEmpty()||zSetMembers.size()<limit) {
            // 查询这个key的里面集合的数量并添加   这里要加分布式锁，在添加的时候也要获取这个锁
            RLock lock = redissonClient.getLock(SCHOOL_WALL_MAIN_LIST_MOD_LOCK + wallId);
            lock.lock();
            // 计算需要从那个索引下面取值+已经有的值
            List<RecordIDCache> iDsByWallId =
                    confessionpostMapper.getConfessionPostIDsByWallId(wallId, startIndex+ zSetMembers.size(), limit- zSetMembers.size());
            if (iDsByWallId.size() < 1) {
                posts=getPostsFromDatabase(new ArrayList(zSetMembers));
            }else {
                // 将缺失的数据的id添加到有序集合中
                for (int i = 0; i < iDsByWallId.size(); i++) {
                    redisTemplate.opsForZSet().add(WALL_POSTS_PREFIX + wallId, iDsByWallId.get(i).getId(),iDsByWallId.get(i).getTimeStamp());
                    zSetMembers.add( iDsByWallId.get(i).getId());  //这里先用set集合装，也防止数据重复
                }
                if (zSetMembers.size()>0){
                    posts = getPostsFromDatabase(zSetMembers.stream().collect(Collectors.toList()));
                }else {
                    posts=new ArrayList();
                }
            }
            lock.unlock();
        } else {
//            System.out.println(zSetMembers);
            // 从数据库或其他缓存中获取完整的投稿
            List<Integer> postIds = new ArrayList<>();
            for (Integer zSetMember : zSetMembers) {
                postIds.add(zSetMember);
            }
            posts = getPostsFromDatabase(postIds);
        }
        return posts;
    }

    @Override
    public int userSubmitConfessionWall(ConfessionPostRequest confessionRequest) {
        Integer userId = JwtInterceptor.getUser().getId();

        //判断用户是否可以发布投稿
        User user = userMapper.selectById(userId);
        Integer userStatus = user.getStatus();
        if (userStatus == 1 || userStatus == 3) {
            throw new WallException(CANNOT_POST);
        }

        //判断该用户每天的投稿有没有超过限制
        int count = this.getPostCountByUserIdAndDate(userId, LocalDate.now());
        if (count >= wallConfig.getUserDailyPostLimit()) {
            throw new WallException(CONTRIBUTE_OVER_LIMIT);
        }

        //判断该表白墙状态，如果是被禁用的，就拒绝发布


        boolean hasImage = (confessionRequest.getImageURL() != null && !confessionRequest.getImageURL().isEmpty());
        int status = 0;

        if (!hasImage) {
            boolean filterResult = this.filterContent(confessionRequest);
            if (!filterResult) {
                status = 1;
            }
        }
        Confessionpost confessionPost = new Confessionpost();
        if (status == 1) {
            confessionPost.setPublishTime(LocalDateTime.now());
        }
        confessionPost.setUserId(userId);
        confessionPost.setWallId(confessionRequest.getWallId());
        confessionPost.setImageURL(confessionRequest.getImageURL());
        confessionPost.setTitle(confessionRequest.getTitle());
        confessionPost.setTextContent(confessionRequest.getTextContent());
        confessionPost.setIsAnonymous(confessionRequest.getIsAnonymous());
        confessionPost.setPostStatus(status);
        this.save(confessionPost);
        if (status == 1) this.obtainWallLockSyncCache(confessionRequest.getWallId(), confessionPost.getId(),new Date().toInstant().getEpochSecond(),true);
        try {
            if (status == 1) {
                this.removeUserPublishedPosts(userId);
                redisTemplate.delete("userPostIds:" + userId);//重新加载用户发布id，这里查看评论回复会有
//                for (int i = 1; i < 5; i++) {
//                    redisTemplate.delete(USER_COMMENT_REPLY + userId + ":" + i);  //评论回复的缓存
//                }
            } else {
                this.removeUserPendingPosts(userId);
            }
        } catch (Exception e) {
            System.out.println("删除用户投稿缓存数据异常" + e.getMessage());
        }

        return status;
    }


    @Override
    public void obtainWallLockSyncCache(Integer wallId, Integer recordId,long publishTimestamp, boolean isSyncPostCache) {
        //这里获取更新学校表白墙列表的锁
        RLock lock = redissonClient.getLock(SCHOOL_WALL_MAIN_LIST_MOD_LOCK + wallId);
        lock.lock();
        redisTemplate.opsForZSet().add(WALL_POSTS_PREFIX + wallId,
                recordId,publishTimestamp);
        if(isSyncPostCache){
            Confessionpost byId = confessionpostMapper.selectById(recordId);
            String key = RedisConstant.POST_SUBMISSION_RECORD + recordId;
            redisTemplate.opsForValue().set(key, this.convertToDTOAll(byId), 3, TimeUnit.DAYS);
        }
        lock.unlock();
    }


    @Override  //注意点，这里可能会有没有用到的墙但是还是同步了缓存
    public void putSubmissionOfAllWalls(Integer postIds) {
        List<Integer> wallsIds = confessionwallService.getAvailableWallsIds();
        if (wallsIds.size()<1){
            throw new WallException("获取表白墙列表失败，大小为0",224);
        }
        //先把该投稿同步到缓存，然后同步表白墙下面的ZSet集合
        Confessionpost byId = confessionpostMapper.selectById(postIds);
        String key = RedisConstant.WALL_POSTS_PREFIX + postIds;
        redisTemplate.opsForValue().set(key, this.convertToDTOAll(byId), 3, TimeUnit.DAYS);

        for (Integer wallsId : wallsIds) {
            obtainWallLockSyncCache(wallsId,postIds,new Date().toInstant().getEpochSecond(),false);
        }
    }

    @Override
    public void deletePost(DeleteSubmissionRequest request){ //延时双删，没必要用，这里对应的还要去找zSet下面的id的数据
        //删除缓存
        RLock lock = redissonClient.getLock(SCHOOL_WALL_MAIN_LIST_MOD_LOCK + request.getWallId());
        lock.lock();
        confessionpostMapper.deleteById(request.getPostId());
        redisTemplate.opsForZSet().remove(WALL_POSTS_PREFIX + request.getWallId(),request.getPostId());
        //删数据库记录
        redisTemplate.delete(POST_SUBMISSION_RECORD + request.getPostId());
        lock.unlock();
    }


    /**
     * 获取投稿记录集合
     * @param postIds
     * @return
     */
    private List<ConfessionPostDTO> getPostsFromDatabase(List<Integer> postIds) {
        List<ConfessionPostDTO> posts = new ArrayList<>();
        for (Integer id : postIds) {
            //拿到id集合也是先从缓存里面拿
//            ConfessionPostDTO redisDto =(ConfessionPostDTO) redisTemplate.opsForValue().get(CONFESSION_PREFIX + id);
            JSONObject redisDtoTemp = (JSONObject) redisTemplate.opsForValue().get(POST_SUBMISSION_RECORD + id);
            ConfessionPostDTO redisDto;
            if (redisDtoTemp != null) {
                redisDto = redisDtoTemp.toJavaObject(ConfessionPostDTO.class);
            } else {
                // 如果没有拿到就要查询数据库并放到缓存里面去
                Confessionpost confessionpost = confessionpostMapper.selectById(id);
//                System.out.println("记录：" + id + "记录是" + confessionpost);
                ConfessionPostDTO dbDto = null;
                if (confessionpost != null) {
                    dbDto = this.convertToDTOAll(confessionpost);
                }
                // 根据发布时间设置不同的过期时间
                LocalDateTime publishTime = confessionpost.getPublishTime();
                LocalDateTime now = LocalDateTime.now();
                long expirationSeconds;
                if (publishTime.plusDays(5).isAfter(now)) {
                    // 发布时间在5天内，缓存过期时间为1天
                    expirationSeconds = Duration.ofDays(1).getSeconds();
                } else if (publishTime.plusDays(3).isAfter(now)) {
                    // 发布时间在3天内，缓存过期时间为3天
                    expirationSeconds = Duration.ofDays(3).getSeconds();
                } else {
                    // 发布时间超过5天，缓存过期时间为3小时
                    expirationSeconds = Duration.ofHours(3).getSeconds();
                }
                // 将查询结果放入缓存，并设置过期时间
                redisTemplate.opsForValue().set(POST_SUBMISSION_RECORD + id, dbDto, expirationSeconds, TimeUnit.SECONDS);
                redisDto = dbDto;
            }
            posts.add(redisDto);
        }
        return posts;
    }

    private ConfessionPostAdminDTO convertToAdminViewDTO(Confessionpost confessionpost) {
        ConfessionPostAdminDTO dto = new ConfessionPostAdminDTO();
        dto.setId(confessionpost.getId());
        UserDTO userDTO = userService.getUserFromRedisOrDatabase(confessionpost.getUserId());
        //这里有问题，如果是超级管理员发布的所有投稿的话，是没有表白墙名字的,墙id是0,
        if (confessionpost.getWallId() == 0) {
            dto.setWallName("超级管理员发布墙"); //这个墙逻辑存在
        } else {
            dto.setWallName(confessionwallMapper.selectById(confessionpost.getWallId()).getWallName());
        }
        dto.setWallId(confessionpost.getWallId());
        dto.setPostUserId(confessionpost.getUserId());
        dto.setUserName(userDTO.getUsername());
        dto.setUserAvatar(userDTO.getAvatarURL());
        dto.setTitle(confessionpost.getTitle());
        dto.setTextContent(confessionpost.getTextContent());
        dto.setImageURL(confessionpost.getImageURL());
        dto.setCreateTime(confessionpost.getCreateTime());
        dto.setPublishTime(confessionpost.getPublishTime());
        dto.setPostStatus(confessionpost.getPostStatus());
        dto.setIsAnonymous(confessionpost.getIsAnonymous());
        dto.setIsAdminPost(confessionpost.getIsAdminPost());
        return dto;
    }

    //这里是普通用户使用的接口，加上了必须是用户发布的那个字段,获取用
    private List<ConfessionPostDTO> getPostsByStatus(PageTool pageTool, Integer userId, Integer postStatus) {
        LambdaQueryWrapper<Confessionpost> wrapper = new LambdaQueryWrapper<>();
        if (postStatus == 0) {
            wrapper.in(Confessionpost::getPostStatus, 0);
        } else {
            wrapper.notIn(Confessionpost::getPostStatus, 0);
        }
        wrapper.eq(Confessionpost::getUserId, userId);
        wrapper.eq(Confessionpost::getIsAdminPost, false);
        wrapper.orderByDesc(Confessionpost::getId); // 添加倒序排序条件
        // 设置分页信息
        Page<Confessionpost> page = new Page<>(pageTool.getPage(), pageTool.getLimit());

        IPage<Confessionpost> iPage = confessionpostMapper.selectPage(page, wrapper);

        return iPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    //处理投稿数据，不要评论
    private ConfessionPostDTO convertToDTO(Confessionpost post) {
        ConfessionPostDTO dto = new ConfessionPostDTO();
//        System.out.println("id是：" + post.getId());
        dto.setId(post.getId());
        dto.setWallId(post.getWallId());
        dto.setUserId(post.getUserId());
        dto.setTitle(post.getTitle());
        dto.setTextContent(post.getTextContent());
        dto.setImageURL(Arrays.asList(post.getImageURL().split(";")));
        dto.setCreateTime(post.getCreateTime());
        dto.setPublishTime(post.getPublishTime());
        dto.setIsAnonymous(post.getIsAnonymous());
        dto.setPostStatus(post.getPostStatus());  //发布状态
        return dto;
    }

    //处理投稿数据，所有
    private ConfessionPostDTO convertToDTOAll(Confessionpost post) {
        ConfessionPostDTO dto = this.convertToDTO(post);
        if (!post.getIsAnonymous()) {
            dto.setUserInfo(userService.getUserFromRedisOrDatabase(post.getUserId()));
        }
        dto.setMainComments(commentService.viewRecordsOnId(post.getId(), true));
        dto.setSubComments(commentService.viewRecordsOnId(post.getId(), false));
        return dto;
    }

    @Override
    public List<Integer> getUserPostId(Integer userId) {
        JSON json = (JSON) redisTemplate.opsForValue().get("userPostIds:" + userId);
        if (json != null) {
            return json.toJavaObject(List.class);
        }
        LambdaQueryWrapper<Confessionpost> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Confessionpost::getId); // 只查询id字段
        wrapper.eq(Confessionpost::getUserId, userId);
        wrapper.eq(Confessionpost::getIsAdminPost, false);
        wrapper.orderByDesc(Confessionpost::getId); // 添加倒序排序条件
        wrapper.last("LIMIT 44"); // 限制最多返回44条记录
        List<Integer> list = confessionpostMapper.selectList(wrapper).stream()
                .map(Confessionpost::getId)
                .collect(Collectors.toList());
        redisTemplate.opsForValue().set("userPostIds:" + userId, JSONObject.toJSON(list), 20, TimeUnit.MINUTES);
        return list;
    }


}
