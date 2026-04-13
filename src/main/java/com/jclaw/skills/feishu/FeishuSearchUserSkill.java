package com.jclaw.skills.feishu;

import com.jclaw.skills.Skill;
import com.jclaw.skills.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * 飞书用户搜索技能
 */
@Slf4j
@Service
public class FeishuSearchUserSkill implements Skill {
    
    @Value("${jclaw.channels.feishu.app-id:}")
    private String appId;
    
    @Value("${jclaw.channels.feishu.app-secret:}")
    private String appSecret;
    
    private final WebClient webClient;
    private String accessToken;
    
    public FeishuSearchUserSkill() {
        this.webClient = WebClient.builder()
            .baseUrl("https://open.feishu.cn/open-apis")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
    
    @Override
    public String getName() {
        return "feishu_search_user";
    }
    
    @Override
    public String getDescription() {
        return "搜索飞书用户（姓名/手机号/邮箱）";
    }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        try {
            String query = (String) params.get("query");
            
            if (query == null || query.isEmpty()) {
                return SkillResult.error("缺少参数：query");
            }
            
            String token = getAccessToken();
            if (token == null) {
                return SkillResult.error("获取飞书令牌失败");
            }
            
            // 搜索用户
            FeishuUserResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/contact/v3/users/search")
                    .queryParam("user_id_type", "open_id")
                    .queryParam("page_size", 20)
                    .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(FeishuUserResponse.class)
                .block();
            
            if (response != null && response.getCode() == 0) {
                List<FeishuUserResponse.UserInfo> users = response.getData().getItems();
                
                StringBuilder sb = new StringBuilder("找到 ");
                sb.append(users.size()).append(" 个用户:\n\n");
                
                for (FeishuUserResponse.UserInfo user : users) {
                    sb.append("👤 ").append(user.getName())
                      .append(" (").append(user.getOpenId()).append(")\n")
                      .append("   部门：").append(user.getDepartmentIds() != null ? user.getDepartmentIds().size() : 0)
                      .append(" 职位：").append(user.getJobNumber() != null ? user.getJobNumber() : "N/A")
                      .append("\n\n");
                }
                
                return SkillResult.success(sb.toString(), Map.of(
                    "count", users.size(),
                    "users", users
                ));
            } else {
                return SkillResult.error("搜索失败：" + (response != null ? response.getMsg() : "未知错误"));
            }
            
        } catch (Exception e) {
            log.error("搜索飞书用户失败", e);
            return SkillResult.error("搜索失败：" + e.getMessage());
        }
    }
    
    private String getAccessToken() {
        if (accessToken != null) {
            return accessToken;
        }
        
        try {
            FeishuTokenResponse response = webClient.post()
                .uri("/auth/v3/tenant_access_token/internal")
                .bodyValue(Map.of(
                    "app_id", appId,
                    "app_secret", appSecret
                ))
                .retrieve()
                .bodyToMono(FeishuTokenResponse.class)
                .block();
            
            if (response != null && response.getCode() == 0) {
                accessToken = response.getTenantAccessToken();
                return accessToken;
            }
            
            return null;
            
        } catch (Exception e) {
            log.error("获取飞书令牌失败", e);
            return null;
        }
    }
    
    private static class FeishuTokenResponse {
        private Integer code;
        private String msg;
        private String tenantAccessToken;
        public Integer getCode() { return code; }
        public void setCode(Integer code) { this.code = code; }
        public String getMsg() { return msg; }
        public void setMsg(String msg) { this.msg = msg; }
        public String getTenantAccessToken() { return tenantAccessToken; }
        public void setTenantAccessToken(String tenantAccessToken) { this.tenantAccessToken = tenantAccessToken; }
    }
    
    private static class FeishuUserResponse {
        private Integer code;
        private String msg;
        private Data data;
        public Integer getCode() { return code; }
        public void setCode(Integer code) { this.code = code; }
        public String getMsg() { return msg; }
        public void setMsg(String msg) { this.msg = msg; }
        public Data getData() { return data; }
        public void setData(Data data) { this.data = data; }
        
        private static class Data {
            private java.util.List<UserInfo> items;
            public java.util.List<UserInfo> getItems() { return items; }
            public void setItems(java.util.List<UserInfo> items) { this.items = items; }
        }
        
        private static class UserInfo {
            private String name;
            private String openId;
            private String userId;
            private java.util.List<String> departmentIds;
            private String jobNumber;
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            public String getOpenId() { return openId; }
            public void setOpenId(String openId) { this.openId = openId; }
            public String getUserId() { return userId; }
            public void setUserId(String userId) { this.userId = userId; }
            public java.util.List<String> getDepartmentIds() { return departmentIds; }
            public void setDepartmentIds(java.util.List<String> departmentIds) { this.departmentIds = departmentIds; }
            public String getJobNumber() { return jobNumber; }
            public void setJobNumber(String jobNumber) { this.jobNumber = jobNumber; }
        }
    }
}
