package com.jclaw.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("参数错误：{}", e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "参数错误", e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("系统异常", e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "系统异常", e.getMessage());
    }
    
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String type, String message) {
        
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", status.value());
        error.put("type", type);
        error.put("message", message);
        
        return ResponseEntity.status(status).body(error);
    }
}
