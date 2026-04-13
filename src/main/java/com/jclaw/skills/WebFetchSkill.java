package com.jclaw.skills;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * 网页抓取技能
 */
@Slf4j
@Service
public class WebFetchSkill implements Skill {
    
    private final WebClient webClient;
    
    public WebFetchSkill() {
        this.webClient = WebClient.builder()
            .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (compatible; JClaw/1.0)")
            .build();
    }
    
    @Override
    public String getName() {
        return "web_fetch";
    }
    
    @Override
    public String getDescription() {
        return "抓取网页内容";
    }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        try {
            String url = (String) params.get("url");
            
            if (url == null || url.isEmpty()) {
                return SkillResult.error("缺少参数：url");
            }
            
            // 安全检查：只允许 HTTP/HTTPS
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                return SkillResult.error("只支持 HTTP/HTTPS 协议");
            }
            
            // 抓取网页
            String content = webClient.get()
                .uri(url)
                .accept(MediaType.TEXT_HTML, MediaType.TEXT_PLAIN)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            
            if (content == null) {
                return SkillResult.error("无法获取网页内容");
            }
            
            // 简单清理 HTML 标签
            String text = content
                .replaceAll("<script[^>]*>.*?</script>", "")
                .replaceAll("<style[^>]*>.*?</style>", "")
                .replaceAll("<[^>]+>", "")
                .replaceAll("&nbsp;", " ")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&")
                .replaceAll("\\s+", " ")
                .trim();
            
            // 限制返回长度
            int maxLength = 5000;
            if (text.length() > maxLength) {
                text = text.substring(0, maxLength) + "\n\n...（内容过长，已截断）";
            }
            
            log.info("抓取网页：{} ({} 字符)", url, text.length());
            
            return SkillResult.success(text, Map.of(
                "url", url,
                "length", text.length(),
                "truncated", text.length() >= maxLength
            ));
            
        } catch (Exception e) {
            log.error("网页抓取失败", e);
            return SkillResult.error("抓取失败：" + e.getMessage());
        }
    }
}
