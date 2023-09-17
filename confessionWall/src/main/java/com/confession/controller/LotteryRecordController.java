package com.confession.controller;

import com.confession.comm.Result;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.pojo.Lottery;
import com.confession.service.LotteryRecordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 拿到纸条记录表 对应的Controller
 */
@RestController
@RequestMapping("api/lotteryRecord")
public class LotteryRecordController {

    @Resource
    private LotteryRecordService lotteryRecordService;



    /**
     * 拿到纸条 随机算法，可能会查询多次，注意接口调用次数  调用了记录表，这里读取之后还要记录一个表
     */
    @GetMapping("extractTape")
    public Result extractTape(@RequestParam Integer schoolId,
                              @RequestParam Integer gender){
        Integer userId = JwtInterceptor.getUser().getId();
        Lottery lottery= lotteryRecordService.extractTape(schoolId,gender,userId);
        return Result.ok(lottery);
    }

    /**
     * 获取用户抽到过的纸条，这里最多获取三个月的，而且最多20条，就不做分页了
     */
    @GetMapping("obtainedNote")
    public Result obtainedNote(){
        Integer userId = JwtInterceptor.getUser().getId();
        lotteryRecordService.obtainedNote(userId);
        return null;
    }


}
