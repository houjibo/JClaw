package com.jclaw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 渐进式加载服务测试
 */
@DisplayName("渐进式加载服务测试")
class ProgressiveLoaderTest {
    
    private ProgressiveLoader loader;
    
    @BeforeEach
    void setUp() {
        loader = new ProgressiveLoader();
    }
    
    @Test
    @DisplayName("测试特性开关初始化")
    void testFeatureFlagsInit() {
        List<Map<String, Object>> features = loader.listFeatures();
        
        assertTrue(features.size() > 0);
        
        // 检查默认启用的特性
        Map<String, Object> sseFeature = features.stream()
            .filter(f -> "sse.enabled".equals(f.get("name")))
            .findFirst()
            .orElse(null);
        
        assertNotNull(sseFeature);
        assertEquals(true, sseFeature.get("enabled"));
    }
    
    @Test
    @DisplayName("测试检查特性状态")
    void testIsFeatureEnabled() {
        // 默认启用的特性
        assertTrue(loader.isFeatureEnabled("sse.enabled"));
        assertTrue(loader.isFeatureEnabled("mcp.enabled"));
        
        // 不存在的特性
        assertFalse(loader.isFeatureEnabled("non.existent"));
    }
    
    @Test
    @DisplayName("测试切换特性状态")
    void testSetFeatureEnabled() {
        // 禁用特性
        loader.setFeatureEnabled("sse.enabled", false);
        assertFalse(loader.isFeatureEnabled("sse.enabled"));
        
        // 重新启用
        loader.setFeatureEnabled("sse.enabled", true);
        assertTrue(loader.isFeatureEnabled("sse.enabled"));
    }
    
    @Test
    @DisplayName("测试列出现有特性")
    void testListFeatures() {
        List<Map<String, Object>> features = loader.listFeatures();
        
        assertTrue(features.size() >= 7); // 至少 7 个内置特性
        
        // 验证特性结构
        for (Map<String, Object> feature : features) {
            assertTrue(feature.containsKey("name"));
            assertTrue(feature.containsKey("enabled"));
            assertTrue(feature.containsKey("description"));
            assertTrue(feature.containsKey("phase"));
        }
    }
    
    @Test
    @DisplayName("测试获取启动指标")
    void testGetStartupMetrics() {
        Map<String, Object> metrics = loader.getStartupMetrics();
        
        assertTrue(metrics.containsKey("startTime"));
        assertTrue(metrics.containsKey("corePhaseTime"));
        assertTrue(metrics.containsKey("servicesPhaseTime"));
        assertTrue(metrics.containsKey("toolsPhaseTime"));
        assertTrue(metrics.containsKey("totalTime"));
        assertTrue(metrics.containsKey("componentTimes"));
    }
    
    @Test
    @DisplayName("测试标记阶段完成")
    void testMarkPhaseComplete() {
        assertEquals(ProgressiveLoader.StartupPhase.CORE, loader.getCurrentPhase());
        
        loader.markPhaseComplete(ProgressiveLoader.StartupPhase.SERVICES);
        assertEquals(ProgressiveLoader.StartupPhase.SERVICES, loader.getCurrentPhase());
        
        loader.markPhaseComplete(ProgressiveLoader.StartupPhase.TOOLS);
        assertEquals(ProgressiveLoader.StartupPhase.TOOLS, loader.getCurrentPhase());
        
        loader.markPhaseComplete(ProgressiveLoader.StartupPhase.READY);
        assertEquals(ProgressiveLoader.StartupPhase.READY, loader.getCurrentPhase());
    }
    
    @Test
    @DisplayName("测试获取当前启动阶段")
    void testGetCurrentPhase() {
        ProgressiveLoader.StartupPhase phase = loader.getCurrentPhase();
        
        assertNotNull(phase);
        assertEquals(ProgressiveLoader.StartupPhase.CORE, phase);
    }
    
    @Test
    @DisplayName("测试预加载组件")
    void testPreloadComponents() {
        List<String> components = List.of("component1", "component2", "component3");
        
        assertDoesNotThrow(() -> loader.preloadComponents(components));
    }
    
    @Test
    @DisplayName("测试关闭服务")
    void testShutdown() {
        assertDoesNotThrow(() -> loader.shutdown());
    }
    
    @Test
    @DisplayName("测试并行启动")
    void testParallelStartup() {
        List<Runnable> tasks = List.of(
            () -> { try { Thread.sleep(10); } catch (InterruptedException e) {} },
            () -> { try { Thread.sleep(10); } catch (InterruptedException e) {} },
            () -> { try { Thread.sleep(10); } catch (InterruptedException e) {} }
        );
        
        assertDoesNotThrow(() -> {
            loader.parallelStartup(tasks, "test-phase").join();
        });
    }
}
