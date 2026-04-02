package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 */
@Component
public class WebSearchTool extends Tool {
    
    private final HttpClient httpClient;
    
    public WebSearchTool() {
        this.name = "web_search";
        this.description = "搜索互联网内容";
        this.category = ToolCategory.NETWORK;
        this.requiresConfirmation = false;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String query = (String) params.get("query");
        Integer maxResults = (Integer) params.getOrDefault("maxResults", 10);
        
        if (query == null || query.isBlank()) {
            return ToolResult.error("query 参数不能为空");
        }
        
        try {
            // 使用 DuckDuckGo HTML 搜索（无需 API Key）
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://html.duckduckgo.com/html/?q=" + encodedQuery;
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("User-Agent", "Mozilla/5.0 (compatible; JClaw/1.0)")
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                String html = response.body();
                String results = parseSearchResults(html, maxResults);
                System.out.println("[WebSearchTool] 搜索完成：" + query);
                return ToolResult.success("搜索完成", results);
            } else {
                return ToolResult.error("搜索失败：HTTP " + response.statusCode());
            }
            
        } catch (Exception e) {
            System.err.println("[WebSearchTool] 搜索失败：" + e.getMessage());
            return ToolResult.error("搜索失败：" + e.getMessage());
        }
    }
    
    private String parseSearchResults(String html, int maxResults) {
        // 简化解析，提取标题和链接
        StringBuilder sb = new StringBuilder();
        sb.append("搜索结果:\n\n");
        
        String[] lines = html.split("\n");
        int count = 0;
        for (String line : lines) {
            if (line.contains("result__a") && count < maxResults) {
                int start = line.indexOf("href=");
                if (start > 0) {
                    int end = line.indexOf("\"", start + 6);
                    String link = line.substring(start + 6, end);
                    int textStart = line.indexOf(">", start) + 1;
                    int textEnd = line.indexOf("<", textStart);
                    String title = line.substring(textStart, textEnd).trim();
                    sb.append(count + 1).append(". ").append(title).append("\n");
                    sb.append("   ").append(link).append("\n\n");
                    count++;
                }
            }
        }
        
        if (count == 0) {
            sb.append("未找到结果，尝试直接访问：https://duckduckgo.com/?q=").append(URLEncoder.encode(html.substring(0, Math.min(50, html.length())), StandardCharsets.UTF_8));
        }
        
        return sb.toString();
    }
    
    @Override
    public boolean validate(Map<String, Object> params) {
        return params.containsKey("query");
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 query      - 搜索关键词 (必填)
                 maxResults - 最大结果数，默认 10 (可选)
               示例:
                 web_search query="Java Spring Boot 教程"
               """.formatted(name, description);
    }
}
