package com.confession.controller;


import com.confession.comm.Result;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.pojo.Lottery;
import com.confession.pojo.User;
import com.confession.request.LotteryRequest;
import com.confession.service.LotteryService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 作者
 * @since 2023年09月16日
 */
@RestController
@RequestMapping("/api/lottery")
public class LotteryController {

    @Resource
    private LotteryService lotteryService;

    /**
     * 放入纸条
     * @param request 纸条信息          这里错误返回 238 然后提示前端会拿到 message
     * @return
     */
    @PostMapping("paperTape")
    public Result paperTape(@RequestBody @Validated LotteryRequest request){
        User user = JwtInterceptor.getUser();
        Integer userId = user.getId();
        boolean insertOk = lotteryService.insert(request, userId);
        if (insertOk){
            return Result.ok();
        }
        return Result.fail();
    }

    /**
     * 获取用户投入的纸条，最多二十条，不做分页
     * @return
     */
    @GetMapping("postedNote")
    public Result postedNote(){
        User user = JwtInterceptor.getUser();
        Integer userId = user.getId();
        List<Lottery> list = lotteryService.postedNote(userId);
        return Result.ok(list);
    }


}

