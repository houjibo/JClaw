package com.jclaw.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * JClaw 配置属性
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "jclaw")
public class JClawProperties {
    
    /**
     * AI 配置
     */
    private AiConfig ai = new AiConfig();
    
    /**
     * 记忆系统配置
     */
    private MemoryConfig memory = new MemoryConfig();
    
    /**
     * 通道配置
     */
    private ChannelsConfig channels = new ChannelsConfig();
    
    /**
     * 技能配置
     */
    private SkillsConfig skills = new SkillsConfig();
    
    @PostConstruct
    public void validate() {
        log.info("JClaw 配置加载完成");
        
        // 验证必要配置
        if (ai.getZhipu() == null || ai.getZhipu().getApiKey() == null || 
            ai.getZhipu().getApiKey().isEmpty() || ai.getZhipu().getApiKey().equals("your-api-key-here")) {
            log.warn("⚠️  智谱 AI API Key 未配置，AI 功能将不可用");
            log.warn("   请在 application.yml 中配置 jclaw.ai.zhipu.api-key");
        } else {
            log.info("✅ 智谱 AI API Key 已配置");
        }
        
        if (channels.getFeishu() != null && channels.getFeishu().isEnabled()) {
            if (channels.getFeishu().getAppId() == null || channels.getFeishu().getAppId().isEmpty()) {
                log.warn("⚠️  飞书通道已启用但 App ID 未配置");
            }
        }
        
        log.info("配置验证完成");
    }
    
    @Data
    public static class AiConfig {
        private String model = "glm-4-flash";
        private String defaultModel = "glm-4-flash";
        private List<String> fallbackModels = new ArrayList<>();
        private ZhipuConfig zhipu = new ZhipuConfig();
    }
    
    @Data
    public static class ZhipuConfig {
        private String apiKey;
        private String baseUrl = "https://open.bigmodel.cn/api/paas/v4";
    }
    
    @Data
    public static class MemoryConfig {
        private boolean enabled = true;
        private String path = "./memory";
        private boolean dailyLogEnabled = true;
    }
    
    @Data
    public static class ChannelsConfig {
        private boolean enabled = true;
        private FeishuConfig feishu = new FeishuConfig();
    }
    
    @Data
    public static class FeishuConfig {
        private boolean enabled = false;
        private String appId;
        private String appSecret;
        private String verificationToken;
    }
    
    @Data
    public static class SkillsConfig {
        private boolean enabled = true;
        private List<String> scanPackages = new ArrayList<>();
    }
}
