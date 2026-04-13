package com.jclaw.skills;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 网络搜索技能
 */
@Slf4j
@Service
public class WebSearchSkill implements Skill {
    
    private final WebClient webClient;
    
    public WebSearchSkill() {
        this.webClient = WebClient.builder()
            .baseUrl("https://api.bing.microsoft.com/v7.0")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
    
    @Override
    public String getName() {
        return "web_search";
    }
    
    @Override
    public String getDescription() {
        return "搜索网络信息";
    }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        try {
            String query = (String) params.get("query");
            Integer count = (Integer) params.getOrDefault("count", 5);
            
            if (query == null || query.isEmpty()) {
                return SkillResult.error("缺少参数：query");
            }
            
            // 简单实现：返回模拟结果
            // TODO: 集成真实搜索 API（Bing/Google/Brave）
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            
            StringBuilder result = new StringBuilder();
            result.append("搜索结果：").append(query).append("\n\n");
            result.append("注意：需要配置搜索 API Key 才能使用真实搜索\n");
            result.append("支持配置:\n");
            result.append("- Bing Search API\n");
            result.append("- Google Custom Search API\n");
            result.append("- Brave Search API\n\n");
            result.append("示例搜索:\n");
            result.append("1. https://www.bing.com/search?q=").append(encodedQuery).append("\n");
            result.append("2. https://www.google.com/search?q=").append(encodedQuery).append("\n");
            
            log.info("网络搜索：{}", query);
            
            return SkillResult.success(result.toString(), Map.of(
                "query", query,
                "count", count,
                "note", "需要配置搜索 API"
            ));
            
        } catch (Exception e) {
            log.error("网络搜索失败", e);
            return SkillResult.error("搜索失败：" + e.getMessage());
        }
    }
}
