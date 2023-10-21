package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.dto.ReportRecordDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.mapper.ConfessionpostMapper;
import com.confession.pojo.Confessionpost;
import com.confession.pojo.ReportRecord;
import com.confession.request.SubReportRecordRequest;
import com.confession.service.ReportRecordService;
import com.confession.mapper.ReportRecordMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.confession.comm.ResultCodeEnum.ALREADY_REPORTED_IT;
import static com.confession.comm.ResultCodeEnum.TOO_MANY_REPORTS_TODAY;

/**
 *
 */
@Service
public class ReportRecordServiceImpl extends ServiceImpl<ReportRecordMapper, ReportRecord>
    implements ReportRecordService{

    @Resource
    private ReportRecordMapper mapper;

    @Resource
    private ConfessionpostMapper confessionpostMapper;

    @Override
    public void userSubmitsReport(Integer userId, SubReportRecordRequest request) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayAgo = now.minusDays(1);
        LambdaQueryWrapper<ReportRecord> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ReportRecord::getUserId, userId)
                .ge(ReportRecord::getCreateTime, oneDayAgo)
                .le(ReportRecord::getCreateTime, now);
        Integer dateForCount = mapper.selectCount(queryWrapper);
        if (dateForCount>2){
            throw new WallException(TOO_MANY_REPORTS_TODAY);
        }
        LambdaQueryWrapper<ReportRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportRecord::getUserId,userId).eq(ReportRecord::getReportId,request.getReportId());
        Integer count = mapper.selectCount(wrapper);
        if (count>0){
            throw new WallException(ALREADY_REPORTED_IT);
        }
        ReportRecord reportRecord = new ReportRecord();
        reportRecord.setUserId(JwtInterceptor.getUser().getId());
        reportRecord.setReportId(request.getReportId());
        reportRecord.setMessage(request.getMessage());
        this.save(reportRecord);
    }

    @Override
    public PageResult getReportInfoList (PageTool pageTool) {
        Page<ReportRecord> page = new Page<>(pageTool.getPage(),pageTool.getLimit());
        LambdaQueryWrapper<ReportRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.orderByDesc(ReportRecord::getReportId);
        List<ReportRecord> records = mapper.selectPage(page, wrapper).getRecords();
        List<ReportRecordDTO> dtoList = records.stream().map(
                this::toDto
        ).collect(Collectors.toList());
        return new PageResult(dtoList,page.getTotal(),records.size());
    }



    private ReportRecordDTO toDto(ReportRecord record){
        ReportRecordDTO dto = new ReportRecordDTO();
        dto.setReportId(record.getReportId());
        dto.setUserId(record.getReportId());
        dto.setMessage(record.getMessage());
        dto.setId(record.getId());
        dto.setCreateTime(record.getCreateTime());
        Confessionpost confessionpost = confessionpostMapper.selectById(record.getReportId());
        dto.setUserIdForSubmissionPublisher(confessionpost.getUserId());
        dto.setTitle(confessionpost.getTitle());
        dto.setTextContent(confessionpost.getTextContent());
        dto.setImageURL(confessionpost.getImageURL());
        return dto;
    }
}




