package com.openclaw.jcode.model;

import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模型注册表 - 管理所有可用的大模型
 */
@Component
public class ModelRegistry {
    
    private final Map<String, ModelConfig> models = new ConcurrentHashMap<>();
    private String defaultModelId = "qwen3.5-plus";
    
    public ModelRegistry() {
        // 注册默认模型
        registerDefaultModels();
    }
    
    private void registerDefaultModels() {
        // Qwen 系列（阿里云百炼）
        register(new ModelConfig("qwen3.5-plus", "Qwen 3.5 Plus", 
            "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation", 
            "qwen-plus", "DASHSCOPE_API_KEY"));
        register(new ModelConfig("qwen3-max", "Qwen 3 Max", 
            "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation", 
            "qwen-max", "DASHSCOPE_API_KEY"));
        register(new ModelConfig("qwen3-coder-plus", "Qwen 3 Coder Plus", 
            "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation", 
            "qwen-coder-plus", "DASHSCOPE_API_KEY"));
        
        // Kimi 系列
        register(new ModelConfig("kimi-k2.5", "Kimi K2.5", 
            "https://api.moonshot.cn/v1/chat/completions", 
            "kimi-k2.5", "KIMI_API_KEY"));
        register(new ModelConfig("kimi-k2-thinking", "Kimi K2 Thinking", 
            "https://api.moonshot.cn/v1/chat/completions", 
            "kimi-k2-thinking", "KIMI_API_KEY"));
        
        // GLM 系列
        register(new ModelConfig("glm-4.7", "GLM 4.7", 
            "https://open.bigmodel.cn/api/paas/v4/chat/completions", 
            "glm-4.7", "ZHIPU_API_KEY"));
        register(new ModelConfig("glm-4.7-flash", "GLM 4.7 Flash", 
            "https://open.bigmodel.cn/api/paas/v4/chat/completions", 
            "glm-4.7-flash", "ZHIPU_API_KEY"));
        
        // MiniMax
        register(new ModelConfig("minimax-m2.5", "MiniMax M2.5", 
            "https://api.minimax.chat/v1/text/chatcompletion_v2", 
            "minimax-m2.5", "MINIMAX_API_KEY"));
    }
    
    public void register(ModelConfig config) {
        models.put(config.name(), config);
        System.out.println("[ModelRegistry] 注册模型：" + config.name() + " - " + config.displayName());
    }
    
    public Optional<ModelConfig> getModel(String modelId) {
        return Optional.ofNullable(models.get(modelId));
    }
    
    public ModelConfig getDefaultModel() {
        return models.get(defaultModelId);
    }
    
    public void setDefaultModel(String modelId) {
        if (models.containsKey(modelId)) {
            this.defaultModelId = modelId;
        }
    }
    
    public List<ModelConfig> listModels() {
        return new ArrayList<>(models.values());
    }
    
    public List<ModelConfig> listModelsByProvider(String provider) {
        // 简化实现，按名称过滤
        return models.values().stream()
                .filter(m -> m.name().contains(provider.toLowerCase()))
                .toList();
    }
    
    public int size() {
        return models.size();
    }
}
