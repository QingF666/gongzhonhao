package com.example.demo.common.result;

import lombok.Data;

@Data
public class R<T> {
    private Integer code;
    private String message;
    private T data;

    public static R ok(String message) {
        R r = new R();
        r.setCode(0);
        r.setMessage(message);
        return r;
    }

    public static R error(String message) {
        R r = new R();
        r.setCode(-1);
        r.setMessage(message);
        return r;
    }
}