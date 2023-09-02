package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.confession.dto.ConfessionPostDTO;
import com.confession.mapper.ConfessionpostMapper;
import com.confession.pojo.Confessionpost;
import com.confession.request.ConfessionPostRequest;
import com.confession.service.ConfessionpostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
@Service
public class ConfessionpostServiceImpl extends ServiceImpl<ConfessionpostMapper, Confessionpost> implements ConfessionpostService {

    @Resource
    private ConfessionpostMapper confessionpostMapper;

    @Override
    public int getPostCountByUserIdAndDate(Integer userId, LocalDate date) {
        return confessionpostMapper.getPostCountByUserIdAndDate(userId, date);
    }

    @Override
    public boolean filterContent(ConfessionPostRequest confessionRequest) {
        // 过滤内容的逻辑 todo
        return true;
    }
    @Override
    public List<ConfessionPostDTO> getPublishedPosts(Integer userId) {
        return getPostsByStatus(userId, 1);
    }

    @Override
    public List<ConfessionPostDTO> getPendingPosts(Integer userId) {
        return getPostsByStatus(userId, 0);
    }
    private List<ConfessionPostDTO> getPostsByStatus(Integer userId, Integer postStatus) {
        LambdaQueryWrapper<Confessionpost> wrapper = new LambdaQueryWrapper<>();
        if (postStatus == 0) {
            wrapper.notIn(Confessionpost::getPostStatus, 0);
        } else {
            wrapper.in(Confessionpost::getPostStatus, postStatus);
        }
        wrapper.eq(Confessionpost::getUserId, userId);
        List<Confessionpost> list = confessionpostMapper.selectList(wrapper);
        return list.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ConfessionPostDTO convertToDTO(Confessionpost post) {
        ConfessionPostDTO dto = new ConfessionPostDTO();
        dto.setId(post.getId());
        dto.setWallId(post.getWallId());
        dto.setUserId(post.getUserId());
        dto.setTitle(post.getTitle());
        dto.setTextContent(post.getTextContent());
        dto.setImageURL(post.getImageURL());
        dto.setCreateTime(post.getCreateTime());
        dto.setPublishTime(post.getPublishTime());
        dto.setPostStatus(post.getPostStatus());
        return dto;
    }
}
