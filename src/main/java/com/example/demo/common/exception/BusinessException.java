package com.example.demo.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String message;
    private Integer code = 500;

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    public BusinessException(String message, Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}