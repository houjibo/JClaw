package com.jclaw.controller;

import com.jclaw.services.PermissionTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 权限追踪控制器
 * 
 * 提供以下端点：
 * - GET /api/permissions - 获取权限状态
 * - POST /api/permissions/{permission}/allow - 允许权限
 * - POST /api/permissions/{permission}/deny - 拒绝权限
 * - GET /api/permissions/denials - 列出拒绝记录
 * - POST /api/permissions/denials/clear - 清除拒绝记录
 * - GET /api/permissions/suggestion - 获取权限建议
 */
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {
    
    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);
    
    private final PermissionTracker permissionTracker;
    
    public PermissionController(PermissionTracker permissionTracker) {
        this.permissionTracker = permissionTracker;
    }
    
    /**
     * 获取权限状态
     */
    @GetMapping
    public Map<String, Object> getPermissions() {
        logger.info("获取权限状态");
        PermissionTracker.UserPreferences prefs = permissionTracker.getPreferences();
        
        return Map.of(
            "success", true,
            "allowed", prefs.allowedPermissions,
            "denied", prefs.deniedPermissions,
            "usage", prefs.permissionUsage,
            "lastUpdated", prefs.lastUpdated
        );
    }
    
    /**
     * 允许权限
     */
    @PostMapping("/{permission}/allow")
    public Map<String, Object> allowPermission(
            @PathVariable String permission,
            @RequestParam(required = false) String tool,
            @RequestParam(required = false) String action) {
        
        logger.info("允许权限：{}", permission);
        permissionTracker.recordAllow(permission, tool, action);
        
        return Map.of(
            "success", true,
            "permission", permission,
            "status", "allowed"
        );
    }
    
    /**
     * 拒绝权限
     */
    @PostMapping("/{permission}/deny")
    public Map<String, Object> denyPermission(
            @PathVariable String permission,
            @RequestParam(required = false) String tool,
            @RequestParam(required = false) String action,
            @RequestBody(required = false) Map<String, String> body) {
        
        String reason = body != null ? body.get("reason") : null;
        
        logger.info("拒绝权限：{} - {}", permission, reason);
        permissionTracker.recordDenial(permission, tool, action, reason);
        
        return Map.of(
            "success", true,
            "permission", permission,
            "status", "denied"
        );
    }
    
    /**
     * 检查权限
     */
    @GetMapping("/{permission}/check")
    public Map<String, Object> checkPermission(@PathVariable String permission) {
        boolean allowed = permissionTracker.isPermissionAllowed(permission);
        
        return Map.of(
            "success", true,
            "permission", permission,
            "allowed", allowed
        );
    }
    
    /**
     * 列出拒绝记录
     */
    @GetMapping("/denials")
    public Map<String, Object> listDenials() {
        logger.info("列出拒绝记录");
        List<Map<String, Object>> denials = permissionTracker.listDenials();
        
        return Map.of(
            "success", true,
            "count", denials.size(),
            "denials", denials
        );
    }
    
    /**
     * 清除拒绝记录
     */
    @PostMapping("/denials/clear")
    public Map<String, Object> clearDenials() {
        logger.info("清除拒绝记录");
        permissionTracker.clearDenials();
        
        return Map.of(
            "success", true,
            "message", "已清除所有拒绝记录"
        );
    }
    
    /**
     * 重置用户偏好
     */
    @PostMapping("/reset")
    public Map<String, Object> resetPreferences() {
        logger.info("重置用户偏好");
        permissionTracker.resetPreferences();
        
        return Map.of(
            "success", true,
            "message", "已重置用户偏好"
        );
    }
    
    /**
     * 获取权限建议
     */
    @GetMapping("/suggestion")
    public Map<String, Object> getSuggestion(
            @RequestParam String permission,
            @RequestParam(required = false) String tool,
            @RequestParam(required = false) String action) {
        
        logger.info("获取权限建议：{}", permission);
        Map<String, Object> suggestion = permissionTracker.getPermissionSuggestion(
            permission,
            tool != null ? tool : "unknown",
            action != null ? action : "unknown"
        );
        
        return Map.of(
            "success", true,
            "suggestion", suggestion
        );
    }
}
