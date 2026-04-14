package com.jclaw.ai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 负载均衡服务测试
 */
@DisplayName("负载均衡服务测试")
class LoadBalancerServiceTest {
    
    private LoadBalancerService loadBalancer;
    
    @BeforeEach
    void setUp() {
        loadBalancer = new LoadBalancerService();
        
        // 注册测试模型
        LoadBalancerService.ModelConfig model1 = new LoadBalancerService.ModelConfig();
        model1.setModelId("model-1");
        model1.setEndpoint("https://api.model1.com");
        model1.setWeight(1);
        loadBalancer.registerModel(model1);
        
        LoadBalancerService.ModelConfig model2 = new LoadBalancerService.ModelConfig();
        model2.setModelId("model-2");
        model2.setEndpoint("https://api.model2.com");
        model2.setWeight(2);
        loadBalancer.registerModel(model2);
    }
    
    @Test
    @DisplayName("测试轮询选择")
    void testSelectModelRoundRobin() {
        LoadBalancerService.ModelConfig selected1 = loadBalancer.selectModelRoundRobin();
        LoadBalancerService.ModelConfig selected2 = loadBalancer.selectModelRoundRobin();
        
        assertNotNull(selected1);
        assertNotNull(selected2);
        // 轮询应该选择不同的模型
        assertNotEquals(selected1.getModelId(), selected2.getModelId());
    }
    
    @Test
    @DisplayName("测试权重选择")
    void testSelectModelWeighted() {
        // 权重模型应该更频繁被选择
        int model1Count = 0;
        int model2Count = 0;
        
        for (int i = 0; i < 100; i++) {
            LoadBalancerService.ModelConfig selected = loadBalancer.selectModelWeighted();
            if ("model-1".equals(selected.getModelId())) {
                model1Count++;
            } else if ("model-2".equals(selected.getModelId())) {
                model2Count++;
            }
        }
        
        // model-2 权重是 model-1 的 2 倍，应该被选择更多
        assertTrue(model2Count > model1Count, "权重高的模型应该被选择更多");
    }
    
    @Test
    @DisplayName("测试最少连接选择")
    void testSelectModelLeastConnections() {
        LoadBalancerService.ModelConfig selected = loadBalancer.selectModelLeastConnections();
        
        assertNotNull(selected);
        
        // 手动增加 model-1 的连接数
        loadBalancer.incrementConnections("model-1");
        loadBalancer.incrementConnections("model-1");
        
        // 下次应该选择 model-2
        LoadBalancerService.ModelConfig selected2 = loadBalancer.selectModelLeastConnections();
        assertEquals("model-2", selected2.getModelId());
    }
    
    @Test
    @DisplayName("测试连接数管理")
    void testConnectionManagement() {
        LoadBalancerService.ModelStatus status = loadBalancer.getModelStatus("model-1");
        assertNotNull(status);
        assertEquals(0, status.getActiveConnections());
        
        loadBalancer.incrementConnections("model-1");
        status = loadBalancer.getModelStatus("model-1");
        assertEquals(1, status.getActiveConnections());
        
        loadBalancer.decrementConnections("model-1");
        status = loadBalancer.getModelStatus("model-1");
        assertEquals(0, status.getActiveConnections());
    }
    
    @Test
    @DisplayName("测试失败记录")
    void testRecordFailure() {
        // 记录多次失败
        for (int i = 0; i < 10; i++) {
            loadBalancer.recordFailure("model-1");
        }
        
        LoadBalancerService.ModelStatus status = loadBalancer.getModelStatus("model-1");
        assertEquals(10, status.getFailedRequests());
        // 失败率超过 50%，应该标记为不健康
        assertFalse(status.isHealthy());
    }
    
    @Test
    @DisplayName("测试响应时间记录")
    void testRecordResponseTime() {
        // 先增加请求数
        loadBalancer.incrementConnections("model-1");
        loadBalancer.decrementConnections("model-1");
        
        // 记录响应时间
        loadBalancer.recordResponseTime("model-1", 100);
        loadBalancer.recordResponseTime("model-1", 200);
        
        LoadBalancerService.ModelStatus status = loadBalancer.getModelStatus("model-1");
        assertEquals(150, status.getAvgResponseTime());
    }
    
    @Test
    @DisplayName("测试禁用/启用模型")
    void testDisableEnableModel() {
        loadBalancer.disableModel("model-1");
        
        // 禁用后不应该被选择
        LoadBalancerService.ModelConfig selected = loadBalancer.selectModelRoundRobin();
        assertNotEquals("model-1", selected.getModelId());
        
        loadBalancer.enableModel("model-1");
        
        // 启用后可能被选择
        LoadBalancerService.ModelConfig selected2 = loadBalancer.selectModelRoundRobin();
        assertNotNull(selected2);
    }
    
    @Test
    @DisplayName("测试重置健康状态")
    void testResetHealth() {
        // 先记录失败使其不健康
        for (int i = 0; i < 10; i++) {
            loadBalancer.recordFailure("model-1");
        }
        
        LoadBalancerService.ModelStatus status = loadBalancer.getModelStatus("model-1");
        assertFalse(status.isHealthy());
        
        // 重置健康状态
        loadBalancer.resetHealth("model-1");
        
        status = loadBalancer.getModelStatus("model-1");
        assertTrue(status.isHealthy());
        assertEquals(0, status.getFailedRequests());
    }
    
    @Test
    @DisplayName("测试获取所有模型状态")
    void testGetAllModelStatuses() {
        java.util.Collection<LoadBalancerService.ModelStatus> statuses = loadBalancer.getAllModelStatuses();
        
        assertNotNull(statuses);
        assertEquals(2, statuses.size());
    }
}
