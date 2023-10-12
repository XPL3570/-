package com.confession.service.impl;

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
import com.confession.service.AdminService;
import com.confession.service.CommentService;
import com.confession.service.ConfessionPostService;
import com.confession.service.UserService;
import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.confession.comm.RedisConstant.CONFESSION_PREFIX;
import static com.confession.comm.RedisConstant.WALL_SUBMISSION_RECORD;
import static com.confession.comm.ResultCodeEnum.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 作者
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
    private RedisTemplate redisTemplate;

    @Resource
    private WallConfig wallConfig;

    @Override
    public int getPostCountByUserIdAndDate(Integer userId, LocalDate date) {
        return confessionpostMapper.getPostCountByUserIdAndDate(userId, date);
    }

    /**
     * 合并敏感词匹配和替换：当前代码中使用两个循环分别对标题和文本内容进行敏感词匹配和替换，可以将它们合并为一个循环来避免重复遍历。
     *
     * 使用 StringBuilder 的 setCharAt 方法替换字符：当前代码中使用 replace 方法替换字符，每次替换都会创建一个新的 StringBuilder 对象，存在额外的开销。可以使用 setCharAt 方法直接修改字符。
     *
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

        Collection<AhoCorasickDoubleArrayTrie.Hit<String>> hits = trie.parseText(title + " " + textContent); // 合并标题和文本内容进行敏感词匹配

        if (hits.isEmpty()) {
            return false; // 没有敏感词匹配，直接返回 false
        }

        Boolean hasSensitiveWords = false; // 是否存在敏感词,默认是不存在

        if (hits.size()>0){
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
    @Cacheable(value = "userPublishedPosts#20#m", key = "#userId + '_' + #pageTool.page + '_'")
    public List<ConfessionPostDTO> getPublishedPosts(Integer userId, PageTool pageTool) {
        return getPostsByStatus(pageTool, userId, 1);
    }

    @Override
    @Cacheable(value = "userPendingPosts#20#m", key = "#userId + '_' + #pageTool.page + '_'")
    public List<ConfessionPostDTO> getPendingPosts(Integer userId, PageTool pageTool) {
        return getPostsByStatus(pageTool, userId, 0);
    }



    @Override
    public List<ConfessionPostDTO> getPostsPage(Integer wallId, Integer page, Integer limit){
        LambdaQueryWrapper<Confessionpost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Confessionpost::getWallId, wallId)
                .eq(Confessionpost::getPostStatus, 1)
                .or()
                .eq(Confessionpost::getIsAdminPost, true)
                .orderByDesc(Confessionpost::getCreateTime);
        Page<Confessionpost> pageZj = new Page<>(page, limit);
        List<Confessionpost> list = confessionpostMapper.selectPage(pageZj,queryWrapper).getRecords();
        return list.stream().map(this::convertToDTOAll).collect(Collectors.toList());
    }

    @Override
    public List<ConfessionPostDTO> getPendingPostsAdmin(Integer wallId,PageTool pageTool) {
        LambdaQueryWrapper<Confessionpost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Confessionpost::getPostStatus, 0);
        wrapper.eq(Confessionpost::getWallId,wallId);
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
        if (!admin){
            throw new WallException(LOGIN_ACL);
        }
        LambdaUpdateWrapper<Confessionpost> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Confessionpost::getId, request.getId())
                .eq(Confessionpost::getWallId,request.getWallId())
                .set(Confessionpost::getPostStatus, request.getPostStatus())
                .set(Confessionpost::getPublishTime, LocalDateTime.now());
        int update = confessionpostMapper.update(null, updateWrapper);
        if (update<1){
            throw new WallException(FAIL);
        }
        //这里通过判断发布状态字段同步缓存
        if (request.getPostStatus()==1){
            this.saveRecordsCache(request.getWallId(),request.getId());
        }

    }

    @Override
    public Integer getAdminPostCount() {
        int adminPostCount = confessionpostMapper.getAdminPostCount(LocalDate.now());
        return adminPostCount;
    }

    @Override
    public PageResult confessionWallContentQuery(PageTool pageTool, String fuzzyQueryContent, String wallName, String userName,Boolean isAnonymous, Boolean isAdminPost, Integer postStatus, Boolean reverseOrder) {
        LambdaQueryWrapper<Confessionpost> queryWrapper = new LambdaQueryWrapper<>();
        Page<Confessionpost> page = new Page<>(pageTool.getPage(), pageTool.getLimit());
        // 模糊查询标题和文字内容
        if (StringUtils.isNotBlank(fuzzyQueryContent)) {
            queryWrapper.and(wrapper -> wrapper.like(Confessionpost::getTitle, fuzzyQueryContent)
                    .or().like(Confessionpost::getTextContent, fuzzyQueryContent));
        }
        // 墙名字模糊查询  注意和用户名都要加上非空判断，是空直接返回
        if (StringUtils.isNotBlank(wallName)) {
            List<Integer> wallIds = confessionwallMapper.selectList(new LambdaQueryWrapper<Confessionwall>().like(Confessionwall::getWallName, wallName))
                    .stream()
                    .map(Confessionwall::getId)
                    .collect(Collectors.toList());
            if (wallIds.isEmpty()){
                return new PageResult(null,page.getTotal(),0);
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
                return new PageResult(null,page.getTotal(),0);
            }
            queryWrapper.in(Confessionpost::getUserId, userIds);
        }

        // 是否匿名查询
        if (isAnonymous!=null) {
            queryWrapper.eq(Confessionpost::getIsAnonymous, isAnonymous);
        }
        // 是否超级管理员发布
        if (isAdminPost != null) {
            queryWrapper.eq(Confessionpost::getIsAdminPost, isAdminPost);
        }
        // 发布状态查询
        if (postStatus != null) {
            queryWrapper.eq(Confessionpost::getPostStatus, postStatus);
        }
        // 排序方式，默认按id倒序
        if (reverseOrder != null && reverseOrder) {
            queryWrapper.orderByDesc(Confessionpost::getId);
        }
        // 分页查询
        List<Confessionpost> records = this.page(page, queryWrapper).getRecords();
        List<ConfessionPostAdminDTO> res = records.stream().map(
                this::convertToAdminViewDTO
        ).collect(Collectors.toList());
        return new PageResult<>(res,page.getTotal(),records.size());
    }

    @Override
    public void modifyPublishingStatus(AuditRequest request) {
        LambdaUpdateWrapper<Confessionpost> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Confessionpost::getId, request.getId())
                .eq(Confessionpost::getWallId,request.getWallId())
                .set(Confessionpost::getPostStatus, request.getPostStatus());
        if (request.getPostStatus() == 1) {
            updateWrapper.set(Confessionpost::getPublishTime, LocalDateTime.now());
        }
        int update = confessionpostMapper.update(null, updateWrapper);
        if (update<1){
            throw new WallException(FAIL);
        }
        if (request.getPostStatus() == 1) {
            //放入缓存
            String key = RedisConstant.CONFESSION_PREFIX + request.getWallId();
            Confessionpost confessionPost = confessionpostMapper.selectById(request.getId());
            ConfessionPostDTO dto = convertToDTOAll(confessionPost);
            // 存入Redis
            redisTemplate.opsForValue().set(key, dto);

            redisTemplate.opsForZSet().add(WALL_SUBMISSION_RECORD + request.getWallId(), request.getId(), confessionPost.getPublishTime().toEpochSecond(ZoneOffset.UTC));
        }
    }

    @Override
    public List<ConfessionPostDTO> confessionPostService(Integer wallId, int page, int limit) {
        int startIndex = (page - 1) * page;
        if (startIndex>220404){
            //不太可能有这么多的数据，直接报错
            throw new WallException(NO_SUBMISSION_DATA);
        }
        int endIndex = startIndex + limit - 1;  //注意这里是倒序啊
        Set<Integer> zSetMembers = redisTemplate.opsForZSet().reverseRange(WALL_SUBMISSION_RECORD + wallId, startIndex, endIndex);

        List<ConfessionPostDTO> posts;
        if (zSetMembers.isEmpty()) {
            // 查询这个key的里面集合的数量   这里要加分布式锁，在添加的时候也要获取这个锁 todo
            long missingCount = redisTemplate.opsForZSet().zCard(WALL_SUBMISSION_RECORD + wallId);

            // 从数据库中查询缺失的记录的id和时间戳添加
            List<RecordIDCache> iDsByWallId =
                    confessionpostMapper.getConfessionPostIDsByWallId(wallId, startIndex, endIndex + 1 - (int) missingCount);
            if (iDsByWallId.size()<1){
                throw new WallException(NO_SUBMISSION_DATA); //提示没有数据了
            }
            // 将缺失的数据的id添加到有序集合中
            for ( RecordIDCache record: iDsByWallId) {
                redisTemplate.opsForZSet().add(WALL_SUBMISSION_RECORD + wallId, record.getId(), record.getTimeStamp());
            }
            posts=getPostsFromDatabase(iDsByWallId.stream().map(item-> item.getId()).collect(Collectors.toList()),wallId);

        } else {
            System.out.println(zSetMembers);
            // 从数据库或其他缓存中获取完整的投稿
            List<Integer> postIds = new ArrayList<>();
            for (Integer zSetMember : zSetMembers) {
                postIds.add(zSetMember);
            }
            posts = getPostsFromDatabase(postIds,wallId);
        }
        return posts;
    }

    @Override
    public int userSubmitConfessionWall(ConfessionPostRequest confessionRequest) {
        Integer userId = JwtInterceptor.getUser().getId();

        //判断用户是否可以发布投稿
        User user = userMapper.selectById(userId);
        Integer userStatus = user.getStatus();
        if (userStatus==1||userStatus==3){
            throw new WallException(CANNOT_POST);
        }

        //判断该用户每天的投稿有没有超过限制
        int count = this.getPostCountByUserIdAndDate(userId, LocalDate.now());
        if (count >= wallConfig.getUserDailyPostLimit()) {
            throw new WallException(CONTRIBUTE_OVER_LIMIT);
        }

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

        if (status == 1) {
            this.saveRecordsCache(confessionRequest.getWallId(),confessionPost.getId());
        }
        return status;
    }



    private void saveRecordsCache(Integer wallId,Integer recordId){
        Confessionpost byId = confessionpostMapper.selectById(recordId);
        redisTemplate.opsForZSet().add(WALL_SUBMISSION_RECORD + wallId,
                recordId, byId.getPublishTime().toInstant(ZoneOffset.UTC).getEpochSecond());
        String key = RedisConstant.CONFESSION_PREFIX+recordId;
        redisTemplate.opsForValue().set(key,this.convertToDTOAll(byId));
    }


    /**
     * 获取投稿记录集合
     * @param postIds
     * @return
     */
    private List<ConfessionPostDTO> getPostsFromDatabase(List<Integer> postIds,Integer wallId) {
        List<ConfessionPostDTO> posts = new ArrayList<>();
        for (Integer id:postIds){
            //拿到id集合也是先从缓存里面拿
//            ConfessionPostDTO redisDto =(ConfessionPostDTO) redisTemplate.opsForValue().get(CONFESSION_PREFIX + id);
            JSONObject redisDtoTemp = (JSONObject) redisTemplate.opsForValue().get(CONFESSION_PREFIX + id);
            ConfessionPostDTO redisDto;
            if (redisDtoTemp!=null){
                redisDto=  redisDtoTemp.toJavaObject(ConfessionPostDTO.class);
            }else {
                // 如果没有拿到就要查询数据库并放到缓存里面去
                Confessionpost confessionpost = confessionpostMapper.selectById(id);
                System.out.println("记录："+id+"记录是"+confessionpost);
                ConfessionPostDTO dbDto=null;
                if (confessionpost!=null){
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
                    // 发布时间超过5天，缓存过期时间为1小时
                    expirationSeconds = Duration.ofHours(1).getSeconds();
                }
                // 将查询结果放入缓存，并设置过期时间
                redisTemplate.opsForValue().set(CONFESSION_PREFIX + id, dbDto, expirationSeconds, TimeUnit.SECONDS);
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
        if (confessionpost.getWallId()==0){
            dto.setWallName("超级管理员发布墙"); //这个墙逻辑存在
        }else {
            dto.setWallName( confessionwallMapper.selectById(confessionpost.getWallId()).getWallName());
        }
        dto.setWallId(confessionpost.getWallId());
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
        wrapper.eq(Confessionpost::getIsAdminPost,false);
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
        System.out.println("id是："+post.getId());
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
    @Cacheable(value = "userPostsIds#20#m", key = "#userId")
    public List<Integer> getUserPostId(Integer userId) {
        LambdaQueryWrapper<Confessionpost> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Confessionpost::getId); // 只查询id字段
        wrapper.eq(Confessionpost::getUserId, userId);
        wrapper.eq(Confessionpost::getIsAdminPost,false);
        wrapper.orderByDesc(Confessionpost::getId); // 添加倒序排序条件
        wrapper.last("LIMIT 44"); // 限制最多返回44条记录
        return confessionpostMapper.selectList(wrapper).stream()
                .map(Confessionpost::getId)
                .collect(Collectors.toList());
    }
}
