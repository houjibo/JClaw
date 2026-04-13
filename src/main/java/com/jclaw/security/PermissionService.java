package com.jclaw.security;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 权限管理服务
 */
@Service
public class PermissionService {
    
    // 内存存储用户和权限
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, Role> roles = new ConcurrentHashMap<>();
    
    /**
     * 初始化默认角色
     */
    public void init() {
        // 管理员角色
        Role admin = new Role();
        admin.setName("admin");
        admin.setPermissions(Set.of("*"));
        roles.put("admin", admin);
        
        // 普通用户角色
        Role user = new Role();
        user.setName("user");
        user.setPermissions(Set.of(
            "skill:execute",
            "ai:chat",
            "memory:read",
            "session:read",
            "session:write"
        ));
        roles.put("user", user);
        
        // 访客角色
        Role guest = new Role();
        guest.setName("guest");
        guest.setPermissions(Set.of(
            "ai:chat",
            "memory:read"
        ));
        roles.put("guest", guest);
        
        // 创建默认管理员
        User adminUser = new User();
        adminUser.setId("admin");
        adminUser.setRoles(List.of("admin"));
        adminUser.setCreatedAt(LocalDateTime.now());
        users.put("admin", adminUser);
    }
    
    /**
     * 创建用户
     */
    public User createUser(String userId, List<String> roleNames) {
        User user = new User();
        user.setId(userId);
        user.setRoles(roleNames != null ? roleNames : List.of("user"));
        user.setCreatedAt(LocalDateTime.now());
        users.put(userId, user);
        return user;
    }
    
    /**
     * 检查权限
     */
    public boolean hasPermission(String userId, String permission) {
        User user = users.get(userId);
        if (user == null) {
            return false;
        }
        
        for (String roleName : user.getRoles()) {
            Role role = roles.get(roleName);
            if (role != null && checkRolePermission(role, permission)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查角色权限
     */
    private boolean checkRolePermission(Role role, String permission) {
        if (role.getPermissions().contains("*")) {
            return true;
        }
        return role.getPermissions().contains(permission);
    }
    
    /**
     * 获取用户权限列表
     */
    public Set<String> getUserPermissions(String userId) {
        User user = users.get(userId);
        if (user == null) {
            return Collections.emptySet();
        }
        
        Set<String> permissions = new HashSet<>();
        for (String roleName : user.getRoles()) {
            Role role = roles.get(roleName);
            if (role != null) {
                permissions.addAll(role.getPermissions());
            }
        }
        return permissions;
    }
    
    /**
     * 添加角色
     */
    public Role addRole(String name, Set<String> permissions) {
        Role role = new Role();
        role.setName(name);
        role.setPermissions(permissions);
        roles.put(name, role);
        return role;
    }
    
    /**
     * 给用户添加角色
     */
    public void addUserRole(String userId, String roleName) {
        User user = users.get(userId);
        if (user != null) {
            List<String> newRoles = new ArrayList<>(user.getRoles());
            if (!newRoles.contains(roleName)) {
                newRoles.add(roleName);
                user.setRoles(newRoles);
            }
        }
    }
    
    /**
     * 移除用户角色
     */
    public void removeUserRole(String userId, String roleName) {
        User user = users.get(userId);
        if (user != null) {
            user.getRoles().remove(roleName);
        }
    }
    
    /**
     * 列出所有用户
     */
    public List<User> listUsers() {
        return new ArrayList<>(users.values());
    }
    
    /**
     * 列出所有角色
     */
    public List<Role> listRoles() {
        return new ArrayList<>(roles.values());
    }
    
    /**
     * 用户
     */
    @Data
    @NoArgsConstructor
    public static class User {
        private String id;
        private List<String> roles;
        private LocalDateTime createdAt;
    }
    
    /**
     * 角色
     */
    @Data
    @NoArgsConstructor
    public static class Role {
        private String name;
        private Set<String> permissions;
    }
}
