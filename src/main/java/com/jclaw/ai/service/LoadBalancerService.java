package com.jclaw.ai.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * AI 模型负载均衡服务
 * 
 * 功能：
 * - 多模型路由
 * - 负载均衡（轮询/权重/最少连接）
 * - 故障转移
 * - 性能监控
 * 
 * @author JClaw
 * @since 2026-04-14
 */
@Slf4j
@Service
public class LoadBalancerService {
    
    /**
     * 模型配置
     */
    @Data
    public static class ModelConfig {
        private String modelId;
        private String endpoint;
        private String apiKey;
        private int weight = 1;
        private boolean enabled = true;
        private int maxConnections = 100;
        private long timeoutMs = 30000;
    }
    
    /**
     * 模型状态
     */
    @Data
    public static class ModelStatus {
        private String modelId;
        private int activeConnections;
        private long totalRequests;
        private long failedRequests;
        private long avgResponseTime;
        private long lastRequestTime;
        private boolean healthy;
    }
    
    /**
     * 已注册的模型
     */
    private final Map<String, ModelConfig> models = new ConcurrentHashMap<>();
    
    /**
     * 模型状态
     */
    private final Map<String, ModelStatus> statuses = new ConcurrentHashMap<>();
    
    /**
     * 轮询计数器
     */
    private final AtomicLong roundRobinCounter = new AtomicLong(0);
    
    /**
     * 注册模型
     */
    public void registerModel(ModelConfig config) {
        log.info("注册 AI 模型：{} - {}", config.getModelId(), config.getEndpoint());
        
        models.put(config.getModelId(), config);
        
        ModelStatus status = new ModelStatus();
        status.setModelId(config.getModelId());
        status.setActiveConnections(0);
        status.setTotalRequests(0);
        status.setFailedRequests(0);
        status.setAvgResponseTime(0);
        status.setHealthy(true);
        
        statuses.put(config.getModelId(), status);
    }
    
    /**
     * 选择模型（轮询）
     */
    public ModelConfig selectModelRoundRobin() {
        List<ModelConfig> availableModels = getAvailableModels();
        
        if (availableModels.isEmpty()) {
            throw new IllegalStateException("没有可用的 AI 模型");
        }
        
        int index = (int) (roundRobinCounter.getAndIncrement() % availableModels.size());
        ModelConfig selected = availableModels.get(index);
        
        log.debug("选择模型（轮询）: {}", selected.getModelId());
        incrementConnections(selected.getModelId());
        
        return selected;
    }
    
    /**
     * 选择模型（权重）
     */
    public ModelConfig selectModelWeighted() {
        List<ModelConfig> availableModels = getAvailableModels();
        
        if (availableModels.isEmpty()) {
            throw new IllegalStateException("没有可用的 AI 模型");
        }
        
        // 计算总权重
        int totalWeight = availableModels.stream()
            .mapToInt(ModelConfig::getWeight)
            .sum();
        
        // 随机选择
        Random random = new Random();
        int randomWeight = random.nextInt(totalWeight);
        
        int currentWeight = 0;
        for (ModelConfig model : availableModels) {
            currentWeight += model.getWeight();
            if (randomWeight < currentWeight) {
                log.debug("选择模型（权重）: {}", model.getModelId());
                incrementConnections(model.getModelId());
                return model;
            }
        }
        
        // fallback
        ModelConfig fallback = availableModels.get(0);
        incrementConnections(fallback.getModelId());
        return fallback;
    }
    
    /**
     * 选择模型（最少连接）
     */
    public ModelConfig selectModelLeastConnections() {
        List<ModelConfig> availableModels = getAvailableModels();
        
        if (availableModels.isEmpty()) {
            throw new IllegalStateException("没有可用的 AI 模型");
        }
        
        ModelConfig selected = availableModels.stream()
            .min(Comparator.comparingInt(m -> {
                ModelStatus status = statuses.get(m.getModelId());
                return status != null ? status.getActiveConnections() : 0;
            }))
            .orElse(availableModels.get(0));
        
        log.debug("选择模型（最少连接）: {}", selected.getModelId());
        incrementConnections(selected.getModelId());
        
        return selected;
    }
    
    /**
     * 获取可用模型列表
     */
    private List<ModelConfig> getAvailableModels() {
        return models.values().stream()
            .filter(ModelConfig::isEnabled)
            .filter(config -> {
                ModelStatus status = statuses.get(config.getModelId());
                return status != null && status.isHealthy() && 
                       status.getActiveConnections() < config.getMaxConnections();
            })
            .toList();
    }
    
    /**
     * 增加连接数
     */
    void incrementConnections(String modelId) {
        ModelStatus status = statuses.get(modelId);
        if (status != null) {
            status.setActiveConnections(status.getActiveConnections() + 1);
            status.setTotalRequests(status.getTotalRequests() + 1);
            status.setLastRequestTime(System.currentTimeMillis());
        }
    }
    
    /**
     * 减少连接数
     */
    public void decrementConnections(String modelId) {
        ModelStatus status = statuses.get(modelId);
        if (status != null) {
            status.setActiveConnections(Math.max(0, status.getActiveConnections() - 1));
        }
    }
    
    /**
     * 记录请求失败
     */
    public void recordFailure(String modelId) {
        ModelStatus status = statuses.get(modelId);
        if (status != null) {
            status.setFailedRequests(status.getFailedRequests() + 1);
            
            // 如果失败率超过 50%，标记为不健康
            double failureRate = (double) status.getFailedRequests() / status.getTotalRequests();
            if (failureRate > 0.5) {
                status.setHealthy(false);
                log.warn("模型 {} 失败率过高 ({:.2f}%)，标记为不健康", modelId, failureRate * 100);
            }
        }
    }
    
    /**
     * 记录响应时间
     */
    public void recordResponseTime(String modelId, long responseTimeMs) {
        ModelStatus status = statuses.get(modelId);
        if (status != null) {
            // 简单移动平均
            long totalRequests = status.getTotalRequests();
            long avg = status.getAvgResponseTime();
            status.setAvgResponseTime((avg * (totalRequests - 1) + responseTimeMs) / totalRequests);
        }
    }
    
    /**
     * 获取模型状态
     */
    public ModelStatus getModelStatus(String modelId) {
        return statuses.get(modelId);
    }
    
    /**
     * 获取所有模型状态
     */
    public Collection<ModelStatus> getAllModelStatuses() {
        return statuses.values();
    }
    
    /**
     * 重置模型健康状态
     */
    public void resetHealth(String modelId) {
        ModelStatus status = statuses.get(modelId);
        if (status != null) {
            status.setHealthy(true);
            status.setFailedRequests(0);
            log.info("重置模型 {} 健康状态", modelId);
        }
    }
    
    /**
     * 禁用模型
     */
    public void disableModel(String modelId) {
        ModelConfig config = models.get(modelId);
        if (config != null) {
            config.setEnabled(false);
            log.info("禁用模型：{}", modelId);
        }
    }
    
    /**
     * 启用模型
     */
    public void enableModel(String modelId) {
        ModelConfig config = models.get(modelId);
        if (config != null) {
            config.setEnabled(true);
            log.info("启用模型：{}", modelId);
        }
    }
}
