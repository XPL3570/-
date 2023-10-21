package com.confession.service;

import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.pojo.ReportRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.request.SubReportRecordRequest;

/**
 * 用户举报
 */
public interface ReportRecordService extends IService<ReportRecord> {

    /**
     * 用户提交举报信息  会查询用户单日可举报数3字，单个投稿只能举报一次
     */
    void userSubmitsReport(Integer userId, SubReportRecordRequest request);

    /**
     * 分页查看举报投稿信息
     * 构建查询条件，然后封顶dto返回
     */
    PageResult getReportInfoList(PageTool pageTool);


}
