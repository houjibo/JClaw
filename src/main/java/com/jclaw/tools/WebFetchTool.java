package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 抓取 URL 内容并提取文本
 */
@Component
public class WebFetchTool extends Tool {
    
    private final HttpClient httpClient;
    
    public WebFetchTool() {
        this.name = "web_fetch";
        this.description = "抓取网页内容并提取文本";
        this.category = ToolCategory.NETWORK;
        this.requiresConfirmation = false;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String url = (String) params.get("url");
        String prompt = (String) params.get("prompt");
        Integer maxChars = (Integer) params.getOrDefault("max_chars", 50000);
        
        if (url == null || url.isBlank()) {
            return ToolResult.error("url 参数不能为空");
        }
        
        // URL 验证
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return ToolResult.error("URL 必须以 http:// 或 https:// 开头");
        }
        
        // 安全检查：阻止内网访问
        if (isInternalUrl(url)) {
            return ToolResult.error("不允许访问内网地址");
        }
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("User-Agent", "Mozilla/5.0 (compatible; JClaw/1.0; +https://github.com/openclaw/jclaw)")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                return ToolResult.error("HTTP " + response.statusCode() + ": " + response.body().substring(0, Math.min(200, response.body().length())));
            }
            
            String html = response.body();
            
            // 限制内容大小
            if (html.length() > maxChars) {
                html = html.substring(0, maxChars);
            }
            
            // 提取文本（去除 HTML 标签）
            String text = extractTextFromHtml(html);
            
            // 如果有 prompt，提取相关内容
            if (prompt != null && !prompt.isBlank()) {
                text = extractRelevantContent(text, prompt);
            }
            
            System.out.println("[WebFetchTool] 抓取成功：" + url + " (" + text.length() + " 字符)");
            
            String result = String.format(
                    "URL: %s\n状态码：%d\n内容长度：%d 字符\n\n%s",
                    url, response.statusCode(), text.length(), text
            );
            
            return ToolResult.success("网页抓取成功", result);
            
        } catch (Exception e) {
            System.err.println("[WebFetchTool] 抓取失败：" + url + " - " + e.getMessage());
            return ToolResult.error("抓取失败：" + e.getMessage());
        }
    }
    
    /**
     * 从 HTML 提取文本
     */
    private String extractTextFromHtml(String html) {
        // 移除 script 和 style 标签
        html = html.replaceAll("(?i)<script[^>]*>.*?</script>", " ");
        html = html.replaceAll("(?i)<style[^>]*>.*?</style>", " ");
        
        // 移除 HTML 标签
        String text = html.replaceAll("<[^>]+>", " ");
        
        // 解码 HTML 实体
        text = decodeHtmlEntities(text);
        
        // 清理空白
        text = text.replaceAll("\\s+", " ").trim();
        
        // 格式化段落
        text = formatParagraphs(text);
        
        return text;
    }
    
    /**
     * 解码 HTML 实体
     */
    private String decodeHtmlEntities(String text) {
        return text
                .replace("&nbsp;", " ")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&apos;", "'")
                .replace("&copy;", "©")
                .replace("&reg;", "®")
                .replace("&trade;", "™");
    }
    
    /**
     * 格式化段落
     */
    private String formatParagraphs(String text) {
        // 在句号后添加换行（如果是大写字母开头）
        text = text.replaceAll("(?<=[.!?])\\s+(?=[A-Z])", "\n\n");
        
        // 限制每行长度
        StringBuilder sb = new StringBuilder();
        String[] words = text.split(" ");
        int lineLength = 0;
        
        for (String word : words) {
            if (lineLength + word.length() > 100) {
                sb.append("\n");
                lineLength = 0;
            }
            sb.append(word).append(" ");
            lineLength += word.length() + 1;
        }
        
        return sb.toString().trim();
    }
    
    /**
     * 提取相关内容
     */
    private String extractRelevantContent(String text, String prompt) {
        // 简单的关键词匹配
        String[] keywords = prompt.toLowerCase().split("\\s+");
        String[] sentences = text.split("(?<=[.!?])\\s+");
        
        StringBuilder sb = new StringBuilder();
        sb.append("根据提示 \"").append(prompt).append("\" 提取的相关内容:\n\n");
        
        int matchCount = 0;
        for (String sentence : sentences) {
            String lowerSentence = sentence.toLowerCase();
            int keywordMatches = 0;
            
            for (String keyword : keywords) {
                if (keyword.length() > 2 && lowerSentence.contains(keyword)) {
                    keywordMatches++;
                }
            }
            
            if (keywordMatches > 0) {
                sb.append("- ").append(sentence.trim()).append("\n");
                matchCount++;
                
                // 限制输出长度
                if (matchCount >= 20 || sb.length() > 5000) {
                    break;
                }
            }
        }
        
        if (matchCount == 0) {
            sb.append("\n未找到与提示直接相关的内容，返回全文摘要:\n");
            sb.append(text.substring(0, Math.min(2000, text.length())));
            sb.append("...");
        }
        
        return sb.toString();
    }
    
    /**
     * 检查是否为内网 URL
     */
    private boolean isInternalUrl(String url) {
        // 阻止内网地址
        String[] internalPatterns = {
                "localhost",
                "127\\.\\d+\\.\\d+\\.\\d+",
                "10\\.\\d+\\.\\d+\\.\\d+",
                "192\\.168\\.\\d+\\.\\d+",
                "172\\.(1[6-9]|2\\d|3[01])\\.\\d+\\.\\d+",
                "::1",
                "fe80::",
                "fc00::",
                "fd00::"
        };
        
        for (String pattern : internalPatterns) {
            if (Pattern.compile(pattern).matcher(url).find()) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean validate(Map<String, Object> params) {
        return params.containsKey("url");
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 url      - 要抓取的 URL (必填)
                 prompt   - 提取内容的提示词 (可选)
                 max_chars - 最大字符数，默认 50000 (可选)
               
               示例:
                 web_fetch url="https://example.com"
                 web_fetch url="https://github.com/openclaw/jclaw" prompt="项目介绍"
               """.formatted(name, description);
    }
}
