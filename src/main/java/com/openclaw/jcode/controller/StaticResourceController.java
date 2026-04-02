package com.openclaw.jcode.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 静态资源控制器
 * 
 * 处理 favicon.ico 等静态资源请求
 */
@RestController
public class StaticResourceController {
    
    /**
     * 处理 favicon.ico 请求
     * 返回空响应避免 404 错误
     */
    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    
    /**
     * 处理根路径请求，重定向到 Swagger UI
     */
    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok(
            "<html><body><h1>JClaw API Server</h1>" +
            "<p>访问 <a href='/swagger-ui.html'>Swagger UI</a> 查看 API 文档</p>" +
            "<p>访问 <a href='/api/health'>/api/health</a> 检查健康状态</p>" +
            "</body></html>"
        );
    }
}
