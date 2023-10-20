package com.confession.service;

import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.pojo.ReportRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户举报
 */
public interface ReportRecordService extends IService<ReportRecord> {

    /**
     * 分页查看举报投稿信息
     * 构建查询条件，然后封顶dto返回
     */
    PageResult getReportInfoList(PageTool pageTool);
}
