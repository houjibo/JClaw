package com.jclaw.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jclaw.intent.entity.Intent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 任务分解服务
 * 使用大模型将意图智能分解为可执行任务
 */
@Service
@Slf4j
public class AiTaskDecompositionService {

    @Autowired
    private AiIntentRecognitionService aiIntentRecognitionService;

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
     * 使用 AI 分解任务
     */
    public List<Map<String, Object>> decomposeWithAI(Intent intent) {
        log.info("使用 AI 分解意图为任务：{}", intent.getName());

        String prompt = buildDecompositionPrompt(intent);

        try {
            String response = callLLM(prompt);
            return parseTasks(response);
        } catch (Exception e) {
            log.error("AI 任务分解失败，使用默认分解", e);
            return decomposeWithDefault(intent);
        }
    }

    /**
     * 评估任务复杂度
     */
    public int estimateComplexityWithAI(Map<String, Object> task) {
        log.info("使用 AI 评估任务复杂度：{}", task.get("title"));

        String prompt = String.format("""
            请评估以下任务的复杂度，分数 1-10（10 最复杂）：
            
            任务标题：%s
            任务描述：%s
            任务类型：%s
            
            只返回数字，不要其他说明。
            """,
            task.get("title"),
            task.get("description"),
            task.get("type")
        );

        try {
            String response = callLLM(prompt);
            return Integer.parseInt(response.trim());
        } catch (Exception e) {
            log.error("AI 复杂度评估失败，使用默认算法", e);
            return estimateComplexityWithDefault(task);
        }
    }

    /**
     * 分配 Agent 角色
     */
    public String assignAgentWithAI(Map<String, Object> task) {
        log.info("使用 AI 分配 Agent 角色：{}", task.get("title"));

        String prompt = String.format("""
            请为以下任务分配最合适的 Agent 角色：
            
            任务标题：%s
            任务描述：%s
            任务类型：%s
            
            可选角色：architect, fullstack, qa, devops, analyst
            
            只返回角色名称，不要其他说明。
            """,
            task.get("title"),
            task.get("description"),
            task.get("type")
        );

        try {
            String response = callLLM(prompt).trim().toLowerCase();
            return validateAgentRole(response);
        } catch (Exception e) {
            log.error("AI Agent 分配失败，使用默认规则", e);
            return assignAgentWithDefault(task);
        }
    }

    /**
     * 调用大模型 API（支持智谱 AI 和阿里云）
     */
    private String callLLM(String prompt) {
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
        requestBody.put("temperature", 0.3);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(zhipuApiUrl, entity, String.class);
        return parseZhipuResponse(response.getBody());
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

        ResponseEntity<String> response = restTemplate.postForEntity(dashscopeApiUrl, entity, String.class);
        return parseDashscopeResponse(response.getBody());
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
            return responseBody;
        }
    }

    /**
     * 构建任务分解提示词
     */
    private String buildDecompositionPrompt(Intent intent) {
        return String.format("""
            你是一个资深的项目经理。请将以下意图分解为可执行的任务列表。
            
            意图名称：%s
            意图描述：%s
            意图类型：%s
            
            要求：
            1. 分解为 3-8 个具体任务
            2. 每个任务包含：title（标题）、description（描述）、type（类型）、priority（优先级 1-5）、agent（执行角色）
            3. 任务类型可选：design, coding, testing, deploy, documentation
            4. Agent 角色可选：architect, fullstack, qa, devops, analyst
            5. 按优先级排序
            
            返回 JSON 数组格式，例如：
            [
              {"title": "设计数据库 schema", "description": "...", "type": "design", "priority": 1, "agent": "architect"},
              {"title": "实现核心功能", "description": "...", "type": "coding", "priority": 2, "agent": "fullstack"}
            ]
            
            只返回 JSON 数组，不要其他说明。
            """,
            intent.getName(),
            intent.getDescription(),
            intent.getType()
        );
    }

    /**
     * 解析任务列表
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseTasks(String response) {
        try {
            String jsonStr = extractJsonArray(response);
            JsonNode array = objectMapper.readTree(jsonStr);

            List<Map<String, Object>> tasks = new ArrayList<>();
            for (JsonNode node : array) {
                Map<String, Object> task = new HashMap<>();
                task.put("title", node.path("title").asText());
                task.put("description", node.path("description").asText());
                task.put("type", node.path("type").asText("coding"));
                task.put("priority", node.path("priority").asInt(3));
                task.put("agent", validateAgentRole(node.path("agent").asText("fullstack")));
                tasks.add(task);
            }
            return tasks;
        } catch (Exception e) {
            log.error("解析任务列表失败", e);
            throw new RuntimeException("解析任务列表失败", e);
        }
    }

    /**
     * 提取 JSON 数组
     */
    private String extractJsonArray(String text) {
        int start = text.indexOf("[");
        int end = text.lastIndexOf("]");
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }

    /**
     * 验证 Agent 角色
     */
    private String validateAgentRole(String role) {
        List<String> validRoles = List.of("architect", "fullstack", "qa", "devops", "analyst");
        String normalized = role.toLowerCase().trim();
        return validRoles.contains(normalized) ? normalized : "fullstack";
    }

    /**
     * 默认任务分解（降级方案）
     */
    private List<Map<String, Object>> decomposeWithDefault(Intent intent) {
        List<Map<String, Object>> tasks = new ArrayList<>();

        tasks.add(Map.of(
            "title", "需求分析",
            "description", "详细分析需求，明确功能边界",
            "type", "design",
            "priority", 1,
            "agent", "analyst"
        ));

        tasks.add(Map.of(
            "title", "架构设计",
            "description", "设计系统架构和数据库 schema",
            "type", "design",
            "priority", 2,
            "agent", "architect"
        ));

        tasks.add(Map.of(
            "title", "核心功能实现",
            "description", "实现主要业务逻辑",
            "type", "coding",
            "priority", 3,
            "agent", "fullstack"
        ));

        tasks.add(Map.of(
            "title", "单元测试编写",
            "description", "编写单元测试和集成测试",
            "type", "testing",
            "priority", 4,
            "agent", "qa"
        ));

        tasks.add(Map.of(
            "title", "部署配置",
            "description", "配置 CI/CD 和生产环境",
            "type", "deploy",
            "priority", 5,
            "agent", "devops"
        ));

        return tasks;
    }

    /**
     * 默认复杂度评估（降级方案）
     */
    private int estimateComplexityWithDefault(Map<String, Object> task) {
        String description = (String) task.getOrDefault("description", "");
        int baseScore = description.length() / 50;

        String type = (String) task.getOrDefault("type", "general");
        int typeMultiplier = switch (type) {
            case "coding", "deploy" -> 2;
            case "design", "testing" -> 1;
            default -> 1;
        };

        return Math.min(10, baseScore * typeMultiplier);
    }

    /**
     * 默认 Agent 分配（降级方案）
     */
    private String assignAgentWithDefault(Map<String, Object> task) {
        String type = (String) task.getOrDefault("type", "general");

        return switch (type) {
            case "design" -> "architect";
            case "testing" -> "qa";
            case "deploy" -> "devops";
            case "documentation" -> "analyst";
            default -> "fullstack";
        };
    }
}
