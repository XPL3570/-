package com.confession.service;

import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.dto.AcceptUserFeedbackDTO;
import com.confession.pojo.AcceptUserFeedback;
import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.request.SubmitFeedbackRequest;

import java.util.List;

/**
 * 用户反馈
 */
public interface AcceptUserFeedbackService extends IService<AcceptUserFeedback> {

    /**
     *  用户提交反馈，限制用户一天一次反馈
     */
    void userSubmit(Integer userId, SubmitFeedbackRequest request);

    /**
     * 获取未读反馈数
     */
    int getNoReadCount();

    /**
     * 修改反馈状态为
     */
    void modifyReadFeedback(Integer requestId);

    /**
     * 获取未读反馈信息，一次八条，会把获取到的改成已读
     */
    List<AcceptUserFeedbackDTO> getNoReadInfo();

    /**
     *  分页查看用户反馈
     */
    PageResult obtainingFeedbackInformation(PageTool pageTool);


}
