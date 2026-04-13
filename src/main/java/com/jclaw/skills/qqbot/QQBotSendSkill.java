package com.jclaw.skills.qqbot;

import com.jclaw.skills.Skill;
import com.jclaw.skills.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * QQ Bot 消息发送技能
 */
@Slf4j
@Service
public class QQBotSendSkill implements Skill {
    
    @Value("${jclaw.skills.qqbot.api-url:#{null}}")
    private String apiUrl;
    
    @Value("${jclaw.skills.qqbot.token:#{null}}")
    private String token;
    
    private final WebClient webClient;
    
    public QQBotSendSkill() {
        this.webClient = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
    
    @Override
    public String getName() {
        return "qqbot_send";
    }
    
    @Override
    public String getDescription() {
        return "发送 QQ 消息（群聊/私聊）";
    }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        if (apiUrl == null || token == null) {
            return SkillResult.error("QQ Bot 未配置，请设置 jclaw.skills.qqbot.api-url 和 token");
        }
        
        String messageType = (String) params.getOrDefault("message_type", "group");
        String groupId = (String) params.get("group_id");
        String userId = (String) params.get("user_id");
        String message = (String) params.get("message");
        
        if (message == null) {
            return SkillResult.error("缺少参数：message");
        }
        
        if ("group".equals(messageType) && groupId == null) {
            return SkillResult.error("群消息需要参数：group_id");
        }
        
        if ("private".equals(messageType) && userId == null) {
            return SkillResult.error("私聊消息需要参数：user_id");
        }
        
        try {
            Map<String, Object> requestBody = Map.of(
                "message_type", messageType,
                messageType.equals("group") ? "group_id" : "user_id", 
                    messageType.equals("group") ? groupId : userId,
                "message", message
            );
            
            Map<String, Object> response = webClient.post()
                .uri(apiUrl + "/send_msg")
                .header("Authorization", "Bearer " + token)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
            
            if (response != null && "ok".equals(response.get("status"))) {
                return SkillResult.success("消息发送成功", Map.of(
                    "message_id", response.get("message_id")
                ));
            } else {
                return SkillResult.error("发送失败：" + response);
            }
            
        } catch (Exception e) {
            log.error("发送 QQ 消息失败", e);
            return SkillResult.error("发送失败：" + e.getMessage());
        }
    }
}
