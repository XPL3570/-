package com.confession.globalConfig.exception;


import com.confession.comm.ResultCodeEnum;
import lombok.Data;

/**
 * 自定义全局异常类
 */
@Data

public class WallException extends RuntimeException {


    private Integer code;

    /**
     * 通过状态码和错误消息创建异常对象
     * @param message
     * @param code
     */
    public WallException(String message, Integer code) {
        super(message);
        this.code = code;
    }
    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public WallException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }
    @Override
    public String toString() {
        return "WallException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
