package com.confession.globalConfig.exception;


import com.confession.comm.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理捕获返回
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody //可以 让他用json输出
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail();
    }

    @ExceptionHandler(WallException.class)
    @ResponseBody
    public Result errer2(WallException e) {
        System.out.println("异常码"+e.getCode()+"，异常信息"+e.getMessage());
        return Result.build(e.getCode(), e.getMessage());
    }


}
