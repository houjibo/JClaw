package com.jclaw.controller;

import com.jclaw.config.HotReloadConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ConfigController 单元测试（纯单元测试，不使用 Spring）
 */
@DisplayName("ConfigController 单元测试")
class ConfigControllerTest {

    @Mock
    private HotReloadConfig hotReloadConfig;

    @InjectMocks
    private ConfigController configController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("测试获取所有配置")
    void testGetAllConfig() {
        // Arrange
        Map<String, Object> mockConfig = Map.of(
            "key1", "value1",
            "key2", "value2"
        );
        when(hotReloadConfig.getAllConfig()).thenReturn(mockConfig);

        // Act
        Map<String, Object> result = configController.getAllConfig();

        // Assert
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
        verify(hotReloadConfig, times(1)).getAllConfig();
    }

    @Test
    @DisplayName("测试获取配置 - 存在")
    void testGetConfig_Exists() {
        // Arrange
        when(hotReloadConfig.get("key1", null)).thenReturn("value1");

        // Act
        Map<String, Object> result = configController.getConfig("key1");

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("key1", result.get("key"));
        assertEquals("value1", result.get("value"));
        verify(hotReloadConfig, times(1)).get("key1", null);
    }

    @Test
    @DisplayName("测试获取配置 - 不存在")
    void testGetConfig_NotExists() {
        // Arrange
        when(hotReloadConfig.get("nonexistent", null)).thenReturn(null);

        // Act
        Map<String, Object> result = configController.getConfig("nonexistent");

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("error")).contains("配置不存在"));
        verify(hotReloadConfig, times(1)).get("nonexistent", null);
    }

    @Test
    @DisplayName("测试更新配置 - 成功")
    void testUpdateConfig_Success() {
        // Arrange
        Map<String, Object> body = new HashMap<>();
        body.put("value", "newvalue");
        doNothing().when(hotReloadConfig).set("key1", "newvalue");

        // Act
        Map<String, Object> result = configController.updateConfig("key1", body);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("key1", result.get("key"));
        assertEquals("newvalue", result.get("value"));
        verify(hotReloadConfig, times(1)).set("key1", "newvalue");
    }

    @Test
    @DisplayName("测试更新配置 - value 为空")
    void testUpdateConfig_NullValue() {
        // Arrange
        Map<String, Object> body = new HashMap<>();

        // Act
        Map<String, Object> result = configController.updateConfig("key1", body);

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("error")).contains("value 不能为空"));
        verify(hotReloadConfig, never()).set(anyString(), any());
    }

    @Test
    @DisplayName("测试重新加载配置")
    void testReloadConfig() {
        // Arrange
        doNothing().when(hotReloadConfig).reloadConfig();

        // Act
        Map<String, Object> result = configController.reloadConfig();

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("配置已重新加载", result.get("message"));
        verify(hotReloadConfig, times(1)).reloadConfig();
    }

    @Test
    @DisplayName("测试列出配置历史")
    void testListHistory() {
        // Arrange
        List<String> mockHistory = Arrays.asList("v1", "v2", "v3");
        when(hotReloadConfig.listHistory()).thenReturn(mockHistory);

        // Act
        Map<String, Object> result = configController.listHistory();

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals(3, result.get("count"));
        assertEquals(3, ((List<?>) result.get("versions")).size());
        verify(hotReloadConfig, times(1)).listHistory();
    }

    @Test
    @DisplayName("测试回滚配置 - 成功")
    void testRollback_Success() {
        // Arrange
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", "2026-04-07T10:00:00Z");
        when(hotReloadConfig.rollback("2026-04-07T10:00:00Z")).thenReturn(true);

        // Act
        Map<String, Object> result = configController.rollback(body);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertTrue(((String) result.get("message")).contains("配置已回滚"));
        verify(hotReloadConfig, times(1)).rollback("2026-04-07T10:00:00Z");
    }

    @Test
    @DisplayName("测试回滚配置 - 失败")
    void testRollback_Failure() {
        // Arrange
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", "invalid");
        when(hotReloadConfig.rollback("invalid")).thenReturn(false);

        // Act
        Map<String, Object> result = configController.rollback(body);

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertEquals("回滚失败", result.get("error"));
        verify(hotReloadConfig, times(1)).rollback("invalid");
    }

    @Test
    @DisplayName("测试回滚配置 - timestamp 为空")
    void testRollback_NullTimestamp() {
        // Arrange
        Map<String, String> body = new HashMap<>();

        // Act
        Map<String, Object> result = configController.rollback(body);

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("error")).contains("timestamp 不能为空"));
        verify(hotReloadConfig, never()).rollback(anyString());
    }
}
