package com.jclaw.controller;

import com.jclaw.security.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 权限管理 REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {
    
    private final PermissionService permissionService;
    
    /**
     * 检查权限
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkPermission(
            @RequestParam String user_id,
            @RequestParam String permission) {
        boolean hasPermission = permissionService.hasPermission(user_id, permission);
        return ResponseEntity.ok(Map.of(
            "user_id", user_id,
            "permission", permission,
            "has_permission", hasPermission
        ));
    }
    
    /**
     * 获取用户权限列表
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUserPermissions(@PathVariable String userId) {
        Set<String> permissions = permissionService.getUserPermissions(userId);
        return ResponseEntity.ok(Map.of(
            "user_id", userId,
            "permissions", permissions
        ));
    }
    
    /**
     * 创建用户
     */
    @PostMapping("/users")
    public ResponseEntity<PermissionService.User> createUser(@RequestBody CreateUserRequest request) {
        PermissionService.User user = permissionService.createUser(
            request.getUserId(),
            request.getRoles()
        );
        return ResponseEntity.ok(user);
    }
    
    /**
     * 列出所有用户
     */
    @GetMapping("/users")
    public ResponseEntity<List<PermissionService.User>> listUsers() {
        return ResponseEntity.ok(permissionService.listUsers());
    }
    
    /**
     * 列出所有角色
     */
    @GetMapping("/roles")
    public ResponseEntity<List<PermissionService.Role>> listRoles() {
        return ResponseEntity.ok(permissionService.listRoles());
    }
    
    /**
     * 添加角色
     */
    @PostMapping("/roles")
    public ResponseEntity<PermissionService.Role> addRole(@RequestBody CreateRoleRequest request) {
        PermissionService.Role role = permissionService.addRole(
            request.getName(),
            request.getPermissions()
        );
        return ResponseEntity.ok(role);
    }
    
    /**
     * 给用户添加角色
     */
    @PostMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<Map<String, String>> addUserRole(
            @PathVariable String userId,
            @PathVariable String roleName) {
        permissionService.addUserRole(userId, roleName);
        return ResponseEntity.ok(Map.of("status", "success"));
    }
    
    /**
     * 移除用户角色
     */
    @DeleteMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<Map<String, String>> removeUserRole(
            @PathVariable String userId,
            @PathVariable String roleName) {
        permissionService.removeUserRole(userId, roleName);
        return ResponseEntity.ok(Map.of("status", "success"));
    }
    
    public static class CreateUserRequest {
        private String userId;
        private List<String> roles;
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }
    }
    
    public static class CreateRoleRequest {
        private String name;
        private Set<String> permissions;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Set<String> getPermissions() { return permissions; }
        public void setPermissions(Set<String> permissions) { this.permissions = permissions; }
    }
}
