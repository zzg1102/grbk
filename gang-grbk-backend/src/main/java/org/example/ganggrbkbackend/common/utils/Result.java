package org.example.ganggrbkbackend.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.example.ganggrbkbackend.common.exception.ResultCodeEnum;

import java.time.LocalDateTime;

/**
 * 统一响应结果
 *
 * @author Gang
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    private Integer code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public Result() {
        this.timestamp = LocalDateTime.now();
    }

    public Result(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    public static <T> Result<T> success() {
        return new Result<>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> success(String message) {
        return new Result<>(ResultCodeEnum.SUCCESS.getCode(), message);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCodeEnum.SUCCESS.getCode(), message, data);
    }

    public static <T> Result<T> ok() {
        return success();
    }

    public static <T> Result<T> ok(T data) {
        return success(data);
    }

    public static <T> Result<T> ok(String message) {
        return success(message);
    }

    public static <T> Result<T> ok(String message, T data) {
        return success(message, data);
    }

    public static <T> Result<T> failed() {
        return new Result<>(ResultCodeEnum.FAILED.getCode(), ResultCodeEnum.FAILED.getMessage());
    }

    public static <T> Result<T> failed(String message) {
        return new Result<>(ResultCodeEnum.FAILED.getCode(), message);
    }

    public static <T> Result<T> failed(Integer code, String message) {
        return new Result<>(code, message);
    }

    public static <T> Result<T> failed(ResultCodeEnum resultCodeEnum) {
        return new Result<>(resultCodeEnum.getCode(), resultCodeEnum.getMessage());
    }

    public static <T> Result<T> failed(ResultCodeEnum resultCodeEnum, T data) {
        return new Result<>(resultCodeEnum.getCode(), resultCodeEnum.getMessage(), data);
    }
}