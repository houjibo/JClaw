package com.jclaw.common.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 通用返回结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    
    /** 成功标志 */
    private boolean success;
    
    /** 消息 */
    private String message;
    
    /** 数据 */
    private T data;
    
    /**
     * 成功结果
     */
    public static <T> Result<T> success() {
        return Result.<T>builder()
            .success(true)
            .message("success")
            .build();
    }
    
    /**
     * 成功结果 (带数据)
     */
    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
            .success(true)
            .message("success")
            .data(data)
            .build();
    }
    
    /**
     * 错误结果
     */
    public static <T> Result<T> error(String message) {
        return Result.<T>builder()
            .success(false)
            .message(message)
            .build();
    }
}
