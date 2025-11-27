package com.example.springboot_01.common;

import lombok.Data;

/**
 * 通用返回结果
 */
@Data
public class Result<T> {

    private Integer code;
    private String msg;
    private T data;

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> ok() {
        return new Result<>(0, "ok", null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(0, "ok", data);
    }

    public static <T> Result<T> ok(String msg, T data) {
        return new Result<>(0, msg, data);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<>(1, msg, null);
    }

    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }
}
