package com.confession.globalConfig.exception;


import com.confession.comm.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理捕获返回
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody //可以 让他用json输出
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Result<?> bindExceptionHandler(BindException e) {
        String msg = e.getBindingResult().getFieldErrors()
                .stream()
                .map(n -> String.format("%s: %s", n.getField(), n.getDefaultMessage()))
                .reduce((x, y) -> String.format("%s; %s", x, y))
                .orElse("参数输入有误");
        log.error("BindException异常，参数校验异常：{}", msg);
        return Result.fail(msg);
    }



    @ExceptionHandler(WallException.class)
    @ResponseBody
    public Result errer2(WallException e) {
        System.out.println("异常码"+e.getCode()+"，异常信息"+e.getMessage());
        return Result.build(e.getCode(), e.getMessage());
    }


}
