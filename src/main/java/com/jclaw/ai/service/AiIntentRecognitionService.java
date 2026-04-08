package com.jclaw.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jclaw.intent.entity.Intent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 意图识别服务
 * 对接智谱 AI（glm-4-flash）和阿里云 Qwen 进行智能意图识别
 */
@Service
@Slf4j
public class AiIntentRecognitionService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ai.provider:zhipu}")
    private String provider;

    @Value("${ai.zhipu.api.key:}")
    private String zhipuApiKey;

    @Value("${ai.zhipu.api.url:https://open.bigmodel.cn/api/paas/v4/chat/completions}")
    private String zhipuApiUrl;

    @Value("${ai.zhipu.model:glm-4-flash}")
    private String zhipuModel;

    @Value("${ai.dashscope.api.key:}")
    private String dashscopeApiKey;

    @Value("${ai.dashscope.api.url:https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation}")
    private String dashscopeApiUrl;

    @Value("${ai.dashscope.model:qwen-plus}")
    private String dashscopeModel;

    /**
     * 使用 AI 识别意图
     */
    public Intent recognizeWithAI(String userInput) {
        log.info("使用 AI 识别意图：{}", userInput);

        try {
            String response = callLLM(buildPrompt(userInput));
            return parseAIResponse(response, userInput);
        } catch (Exception e) {
            log.error("AI 意图识别失败，使用默认逻辑", e);
            return recognizeWithDefault(userInput);
        }
    }

    /**
     * 生成澄清问题
     */
    public List<String> generateClarificationQuestions(Intent intent) {
        log.info("使用 AI 生成澄清问题：{}", intent.getName());

        String prompt = String.format("""
            请为以下意图生成 3-5 个澄清问题，帮助更好地理解用户需求：
            
            意图名称：%s
            意图描述：%s
            用户输入：%s
            
            请直接返回问题列表，每个问题一行，不要其他说明。
            """,
            intent.getName(),
            intent.getDescription(),
            intent.getContext()
        );

        try {
            String response = callLLM(prompt);
            return parseQuestions(response);
        } catch (Exception e) {
            log.error("AI 生成澄清问题失败，使用默认问题", e);
            return getDefaultClarificationQuestions();
        }
    }

    /**
     * 调用大模型 API（支持智谱 AI 和阿里云）
     */
    public String callLLM(String prompt) {
        if ("zhipu".equalsIgnoreCase(provider)) {
            return callZhipuLLM(prompt);
        } else {
            return callDashscopeLLM(prompt);
        }
    }

    /**
     * 调用智谱 AI API（glm-4-flash）
     */
    private String callZhipuLLM(String prompt) {
        log.info("调用智谱 AI：model={}", zhipuModel);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (zhipuApiKey != null && !zhipuApiKey.isEmpty()) {
            headers.set("Authorization", "Bearer " + zhipuApiKey);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", zhipuModel);
        
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        requestBody.put("messages", List.of(message));
        requestBody.put("stream", false);
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(zhipuApiUrl, entity, String.class);
            return parseZhipuResponse(response.getBody());
        } catch (Exception e) {
            log.error("调用智谱 AI 失败", e);
            throw new RuntimeException("调用智谱 AI 失败：" + e.getMessage(), e);
        }
    }

    /**
     * 调用阿里云 DashScope API
     */
    private String callDashscopeLLM(String prompt) {
        log.info("调用阿里云 DashScope：model={}", dashscopeModel);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (dashscopeApiKey != null && !dashscopeApiKey.isEmpty()) {
            headers.set("Authorization", "Bearer " + dashscopeApiKey);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", dashscopeModel);
        
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        requestBody.put("messages", List.of(message));
        requestBody.put("stream", false);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(dashscopeApiUrl, entity, String.class);
            return parseDashscopeResponse(response.getBody());
        } catch (Exception e) {
            log.error("调用阿里云 DashScope 失败", e);
            throw new RuntimeException("调用阿里云 DashScope 失败：" + e.getMessage(), e);
        }
    }

    /**
     * 解析智谱 AI 响应
     */
    private String parseZhipuResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0).path("message").path("content").asText();
            }
            return responseBody;
        } catch (Exception e) {
            log.error("解析智谱 AI 响应失败", e);
            return responseBody;
        }
    }

    /**
     * 解析阿里云 DashScope 响应
     */
    private String parseDashscopeResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode output = root.path("output");
            JsonNode text = output.path("text");
            return text.asText();
        } catch (Exception e) {
            log.error("解析阿里云 DashScope 响应失败", e);
            return responseBody;
        }
    }

    /**
     * 构建意图识别提示词
     */
    private String buildPrompt(String userInput) {
        return String.format("""
            你是一个智能需求分析助手。请分析用户的输入，识别其意图。
            
            用户输入：%s
            
            请返回 JSON 格式，包含以下字段：
            - name: 意图名称（简短描述）
            - type: 意图类型（task/feature/bug/query 等）
            - description: 详细描述
            - priority: 优先级（1-5，5 最高）
            
            只返回 JSON，不要其他说明。
            """, userInput);
    }

    /**
     * 解析 AI 返回的意图
     */
    private Intent parseAIResponse(String response, String userInput) {
        try {
            // 提取 JSON 部分
            String jsonStr = extractJson(response);
            JsonNode json = objectMapper.readTree(jsonStr);

            return Intent.builder()
                .name(json.path("name").asText("未命名意图"))
                .type(json.path("type").asText("task"))
                .description(json.path("description").asText(userInput))
                .priority(json.path("priority").asInt(3))
                .status("pending")
                .context("{\"input\":\"" + userInput + "\",\"ai_parsed\":true}")
                .build();
        } catch (Exception e) {
            log.error("解析 AI 意图失败，使用默认逻辑", e);
            return recognizeWithDefault(userInput);
        }
    }

    /**
     * 提取 JSON 字符串
     */
    private String extractJson(String text) {
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }

    /**
     * 解析问题列表
     */
    private List<String> parseQuestions(String response) {
        return List.of(response.split("\n"));
    }

    /**
     * 默认意图识别（降级方案）
     */
    private Intent recognizeWithDefault(String userInput) {
        String lower = userInput.toLowerCase();

        String type = "task";
        if (lower.contains("创建") || lower.contains("开发") || lower.contains("实现")) {
            type = "feature";
        } else if (lower.contains("修复") || lower.contains("解决") || lower.contains("bug")) {
            type = "bug";
        } else if (lower.contains("查询") || lower.contains("查看") || lower.contains("搜索")) {
            type = "query";
        }

        return Intent.builder()
            .name("用户意图")
            .type(type)
            .description(userInput)
            .priority(3)
            .status("pending")
            .context("{\"input\":\"" + userInput + "\",\"ai_parsed\":false}")
            .build();
    }

    /**
     * 默认澄清问题（降级方案）
     */
    private List<String> getDefaultClarificationQuestions() {
        return List.of(
            "能详细描述一下您的需求吗？",
            "这个功能的使用场景是什么？",
            "有什么特殊的约束条件或要求吗？",
            "期望的完成时间是什么时候？",
            "有哪些相关方会参与到这个需求中？"
        );
    }
}
