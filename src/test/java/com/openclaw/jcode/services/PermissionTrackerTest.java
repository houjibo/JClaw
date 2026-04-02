package com.openclaw.jcode.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 权限追踪服务测试
 */
@DisplayName("权限追踪服务测试")
class PermissionTrackerTest {
    
    private PermissionTracker permissionTracker;
    
    @BeforeEach
    void setUp() {
        permissionTracker = new PermissionTracker();
    }
    
    @Test
    @DisplayName("测试记录权限拒绝")
    void testRecordDenial() {
        permissionTracker.recordDenial("file_write", "FileWriteTool", "write", "用户拒绝");
        
        List<Map<String, Object>> denials = permissionTracker.listDenials();
        
        assertEquals(1, denials.size());
        assertEquals("file_write", denials.get(0).get("permission"));
        assertEquals("FileWriteTool", denials.get(0).get("tool"));
        assertEquals("write", denials.get(0).get("action"));
        assertEquals("用户拒绝", denials.get(0).get("reason"));
    }
    
    @Test
    @DisplayName("测试记录权限允许")
    void testRecordAllow() {
        permissionTracker.recordAllow("file_read", "FileReadTool", "read");
        
        PermissionTracker.UserPreferences prefs = permissionTracker.getPreferences();
        
        assertTrue(prefs.allowedPermissions.containsKey("file_read"));
        assertTrue(prefs.allowedPermissions.get("file_read"));
    }
    
    @Test
    @DisplayName("测试检查权限状态")
    void testIsPermissionAllowed() {
        // 明确允许的权限
        permissionTracker.recordAllow("file_read", "FileReadTool", "read");
        assertTrue(permissionTracker.isPermissionAllowed("file_read"));
        
        // 明确拒绝的权限
        permissionTracker.recordDenial("file_write", "FileWriteTool", "write", null);
        assertFalse(permissionTracker.isPermissionAllowed("file_write"));
        
        // 未记录的权限（默认需要确认）
        assertFalse(permissionTracker.isPermissionAllowed("unknown"));
    }
    
    @Test
    @DisplayName("测试权限使用次数自动允许")
    void testPermissionAutoAllowByUsage() {
        // 模拟多次使用
        for (int i = 0; i < 5; i++) {
            permissionTracker.recordAllow("frequent_permission", "TestTool", "test");
        }
        
        // 使用超过 5 次应该自动允许
        assertTrue(permissionTracker.isPermissionAllowed("frequent_permission"));
    }
    
    @Test
    @DisplayName("测试获取权限建议 - 拒绝历史")
    void testGetPermissionSuggestionWithDenial() {
        permissionTracker.recordDenial("file_write", "FileWriteTool", "write", "安全原因");
        
        Map<String, Object> suggestion = permissionTracker.getPermissionSuggestion(
            "file_write", "FileWriteTool", "write"
        );
        
        assertEquals(true, suggestion.get("previouslyDenied"));
        assertEquals("deny", suggestion.get("recommendation"));
        assertEquals("安全原因", suggestion.get("reason"));
    }
    
    @Test
    @DisplayName("测试获取权限建议 - 允许历史")
    void testGetPermissionSuggestionWithAllow() {
        // 模拟多次使用
        for (int i = 0; i < 5; i++) {
            permissionTracker.recordAllow("frequent_permission", "TestTool", "test");
        }
        
        Map<String, Object> suggestion = permissionTracker.getPermissionSuggestion(
            "frequent_permission", "TestTool", "test"
        );
        
        assertEquals(true, suggestion.get("previouslyAllowed"));
        assertEquals("allow", suggestion.get("recommendation"));
    }
    
    @Test
    @DisplayName("测试获取权限建议 - 无历史")
    void testGetPermissionSuggestionNoHistory() {
        Map<String, Object> suggestion = permissionTracker.getPermissionSuggestion(
            "new_permission", "TestTool", "test"
        );
        
        assertEquals("ask", suggestion.get("recommendation"));
    }
    
    @Test
    @DisplayName("测试列出拒绝记录")
    void testListDenials() {
        permissionTracker.recordDenial("perm1", "Tool1", "action1", "reason1");
        permissionTracker.recordDenial("perm2", "Tool2", "action2", "reason2");
        
        List<Map<String, Object>> denials = permissionTracker.listDenials();
        
        assertEquals(2, denials.size());
    }
    
    @Test
    @DisplayName("测试清除拒绝记录")
    void testClearDenials() {
        permissionTracker.recordDenial("perm1", "Tool1", "action1", null);
        
        assertEquals(1, permissionTracker.listDenials().size());
        
        permissionTracker.clearDenials();
        
        assertEquals(0, permissionTracker.listDenials().size());
    }
    
    @Test
    @DisplayName("测试重置用户偏好")
    void testResetPreferences() {
        permissionTracker.recordAllow("perm1", "Tool1", "action1");
        permissionTracker.recordDenial("perm2", "Tool2", "action2", null);
        
        permissionTracker.resetPreferences();
        
        PermissionTracker.UserPreferences prefs = permissionTracker.getPreferences();
        assertTrue(prefs.allowedPermissions.isEmpty());
        assertTrue(prefs.deniedPermissions.isEmpty());
    }
    
    @Test
    @DisplayName("测试多次拒绝同一权限")
    void testMultipleDenialsSamePermission() {
        permissionTracker.recordDenial("file_write", "FileWriteTool", "write", "reason1");
        permissionTracker.recordDenial("file_write", "FileWriteTool", "write", "reason2");
        
        List<Map<String, Object>> denials = permissionTracker.listDenials();
        
        assertEquals(1, denials.size());
        assertEquals(2, denials.get(0).get("count"));
        assertEquals("reason2", denials.get(0).get("reason"));
    }
}
