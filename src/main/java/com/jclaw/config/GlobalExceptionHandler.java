package com.jclaw.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 
 * 统一处理所有控制器异常，返回标准化错误响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理 IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        logger.warn("参数错误：{}", e.getMessage());
        return buildErrorResponse(
            "BAD_REQUEST",
            e.getMessage(),
            HttpStatus.BAD_REQUEST
        );
    }
    
    /**
     * 处理 IllegalStateException
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException e) {
        logger.warn("状态错误：{}", e.getMessage());
        return buildErrorResponse(
            "SERVICE_UNAVAILABLE",
            e.getMessage(),
            HttpStatus.SERVICE_UNAVAILABLE
        );
    }
    
    /**
     * 处理 RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException e) {
        logger.error("运行时错误", e);
        return buildErrorResponse(
            "INTERNAL_ERROR",
            "内部错误：" + e.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    
    /**
     * 处理 Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception e) {
        logger.error("未知错误", e);
        return buildErrorResponse(
            "UNKNOWN_ERROR",
            "未知错误：" + e.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    
    /**
     * 构建错误响应
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            String code,
            String message,
            HttpStatus status) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", Map.of(
            "code", code,
            "message", message,
            "timestamp", Instant.now().toString()
        ));
        
        return ResponseEntity.status(status).body(body);
    }
}
