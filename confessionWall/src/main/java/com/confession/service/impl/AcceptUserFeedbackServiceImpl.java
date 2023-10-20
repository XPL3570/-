package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.dto.AcceptUserFeedbackDTO;
import com.confession.pojo.AcceptUserFeedback;
import com.confession.request.SubmitFeedbackRequest;
import com.confession.service.AcceptUserFeedbackService;
import com.confession.mapper.AcceptUserFeedbackMapper;
import com.confession.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class AcceptUserFeedbackServiceImpl extends ServiceImpl<AcceptUserFeedbackMapper, AcceptUserFeedback>
    implements AcceptUserFeedbackService{

    @Resource
    private AcceptUserFeedbackMapper mapper;

    @Resource
    private UserService userService;

    @Override
    public void userSubmit(Integer userId, SubmitFeedbackRequest request) {
        AcceptUserFeedback feedback = new AcceptUserFeedback();
        feedback.setUserid(userId);
        feedback.setScore(request.getScore());
        feedback.setMessage(request.getMassage());
        save(feedback);
    }

    @Override
    public int getNoReadCount() {
        LambdaQueryWrapper<AcceptUserFeedback> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AcceptUserFeedback::getIsRead,false);
        return  mapper.selectCount(wrapper);
    }

    @Override
    public List<AcceptUserFeedbackDTO> getNoReadInfo() {
        LambdaQueryWrapper<AcceptUserFeedback> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AcceptUserFeedback::getIsRead,false);
        wrapper.last("LIMIT 8");

        // 修改查询到的记录的查询条件为true
        List<AcceptUserFeedback> feedbackList = mapper.selectList(wrapper);
        List<AcceptUserFeedbackDTO> resDto = feedbackList.stream().map(this::toDTO).collect(Collectors.toList());
        feedbackList.forEach(feedback -> {
            feedback.setIsRead(true);
            mapper.updateById(feedback);
        });
        return resDto;
    }

    @Override
    public PageResult obtainingFeedbackInformation(PageTool pageTool) {
        Page<AcceptUserFeedback> page = new Page<>(pageTool.getPage(), pageTool.getLimit());
        LambdaQueryWrapper<AcceptUserFeedback> wrapper = Wrappers.lambdaQuery();
        wrapper.orderByDesc(AcceptUserFeedback::getId);
        List<AcceptUserFeedback> records = mapper.selectPage(page, wrapper).getRecords();
        List<AcceptUserFeedbackDTO> resList = records.stream().map(
                this::toDTO
        ).collect(Collectors.toList());
        return new PageResult(resList,page.getTotal(),records.size());
    }

    private AcceptUserFeedbackDTO toDTO(AcceptUserFeedback userFeedback){
        AcceptUserFeedbackDTO dto = new AcceptUserFeedbackDTO();
        dto.setId(userFeedback.getId());
        dto.setIsRead(userFeedback.getIsRead());
        dto.setCreateTime(userFeedback.getCreateTime());
        dto.setMessage(userFeedback.getMessage());
        dto.setUserName(userService.getById(userFeedback.getUserid()).getUsername());
        return dto;
    }
}




