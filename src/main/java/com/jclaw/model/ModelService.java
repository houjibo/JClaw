package com.jclaw.model;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

/**
 * 大模型服务 - 支持多种模型接口
 * 支持：Qwen/DeepSeek/Claude/OpenAI 等
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Service
public class ModelService {
    
    private final HttpClient httpClient;
    private ModelConfig currentModel;
    
    // 支持的模型列表
    private static final Map<String, ModelConfig> SUPPORTED_MODELS = new HashMap<>();
    
    static {
        // 阿里云百炼 API（推荐，国内可用）
        SUPPORTED_MODELS.put("bailian", new ModelConfig(
                "bailian",
                "阿里云百炼 - 通义千问",
                "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation",
                "qwen-plus",
                "DASHSCOPE_API_KEY"
        ));
        
        // Qwen (阿里云 DashScope)
        SUPPORTED_MODELS.put("qwen", new ModelConfig(
                "qwen",
                "Qwen 2.5 Coder",
                "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation",
                "qwen-coder-plus",
                "DASHSCOPE_API_KEY"
        ));
        
        // DeepSeek
        SUPPORTED_MODELS.put("deepseek", new ModelConfig(
                "deepseek",
                "DeepSeek Coder",
                "https://api.deepseek.com/v1/chat/completions",
                "deepseek-coder",
                "DEEPSEEK_API_KEY"
        ));
        
        // OpenAI
        SUPPORTED_MODELS.put("openai", new ModelConfig(
                "openai",
                "GPT-4",
                "https://api.openai.com/v1/chat/completions",
                "gpt-4-turbo",
                "OPENAI_API_KEY"
        ));
        
        // Claude (Anthropic)
        SUPPORTED_MODELS.put("claude", new ModelConfig(
                "claude",
                "Claude 3.5",
                "https://api.anthropic.com/v1/messages",
                "claude-3-5-sonnet-20241022",
                "ANTHROPIC_API_KEY"
        ));
        
        // 阿里云百炼 - Qwen Max
        SUPPORTED_MODELS.put("bailian-max", new ModelConfig(
                "bailian-max",
                "阿里云百炼 - Qwen Max",
                "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation",
                "qwen-max",
                "DASHSCOPE_API_KEY"
        ));
        
        // 阿里云百炼 - Qwen Turbo
        SUPPORTED_MODELS.put("bailian-turbo", new ModelConfig(
                "bailian-turbo",
                "阿里云百炼 - Qwen Turbo",
                "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation",
                "qwen-turbo",
                "DASHSCOPE_API_KEY"
        ));
    }
    
    public ModelService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        // 默认使用 Qwen
        this.currentModel = SUPPORTED_MODELS.get("qwen");
    }
    
    /**
     * 切换模型
     */
    public void switchModel(String modelName) {
        ModelConfig config = SUPPORTED_MODELS.get(modelName.toLowerCase());
        if (config != null) {
            this.currentModel = config;
        } else {
            throw new IllegalArgumentException("不支持的模型：" + modelName);
        }
    }
    
    /**
     * 获取支持的模型列表
     */
    public List<ModelConfig> getSupportedModels() {
        return new ArrayList<>(SUPPORTED_MODELS.values());
    }
    
    /**
     * 调用大模型 - 代码生成
     */
    public String generateCode(String prompt, String language) {
        String systemPrompt = String.format("""
                你是一个专业的 %s 程序员助手。
                请根据用户需求生成高质量、可运行的代码。
                
                要求：
                1. 代码完整、可运行
                2. 遵循最佳实践
                3. 添加必要的注释
                4. 考虑边界情况
                5. 包含错误处理
                
                语言：%s
                """, language, language);
        
        return callModel(systemPrompt, prompt);
    }
    
    /**
     * 调用大模型 - 代码审查
     */
    public String reviewCode(String code, String language) {
        String systemPrompt = String.format("""
                你是一个资深的 %s 代码审查专家。
                请审查以下代码，指出：
                1. 潜在 bug
                2. 性能问题
                3. 安全问题
                4. 代码风格问题
                5. 改进建议
                
                语言：%s
                """, language, language);
        
        return callModel(systemPrompt, "请审查以下代码：\n\n```" + language + "\n" + code + "\n```");
    }
    
    /**
     * 调用大模型 - 问题解答
     */
    public String answerQuestion(String question) {
        String systemPrompt = """
                你是一个专业的编程助手。
                请准确、清晰地回答用户的编程问题。
                
                要求：
                1. 回答准确
                2. 提供示例代码
                3. 解释原理
                4. 给出最佳实践
                """;
        
        return callModel(systemPrompt, question);
    }
    
    /**
     * 调用大模型 - 通用对话
     */
    public String chat(String message) {
        String systemPrompt = """
                你是 JClaw AI 助手，一个专业的编程助手。
                你可以帮助用户：
                1. 编写代码
                2. 审查代码
                3. 调试问题
                4. 学习编程
                5. 架构设计
                
                请友好、专业地回答用户问题。
                """;
        
        return callModel(systemPrompt, message);
    }
    
    /**
     * 实际调用模型 API
     */
    private String callModel(String systemPrompt, String userPrompt) {
        try {
            String apiKey = System.getenv(currentModel.apiKeyEnv());
            if (apiKey == null || apiKey.isEmpty()) {
                return "❌ 错误：未配置 API Key\n\n" +
                       "请设置环境变量：" + currentModel.apiKeyEnv() + "\n" +
                       "例如：export " + currentModel.apiKeyEnv() + "=your-api-key";
            }
            
            String requestBody = buildRequestBody(systemPrompt, userPrompt);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(currentModel.apiUrl()))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                return "❌ API 调用失败：" + response.statusCode() + "\n" + response.body();
            }
            
            return parseResponse(response.body());
            
        } catch (Exception e) {
            return "❌ 调用失败：" + e.getMessage();
        }
    }
    
    /**
     * 构建请求体（Qwen 格式）
     */
    private String buildRequestBody(String systemPrompt, String userPrompt) {
        return String.format("""
                {
                    "model": "%s",
                    "input": {
                        "messages": [
                            {
                                "role": "system",
                                "content": "%s"
                            },
                            {
                                "role": "user",
                                "content": "%s"
                            }
                        ]
                    },
                    "parameters": {
                        "max_tokens": 2048,
                        "temperature": 0.7,
                        "top_p": 0.9
                    }
                }
                """, 
                currentModel.modelName(),
                escapeJson(systemPrompt),
                escapeJson(userPrompt)
        );
    }
    
    /**
     * 解析响应（Qwen 格式）
     */
    private String parseResponse(String responseBody) {
        // 简单解析 JSON 响应
        int contentStart = responseBody.indexOf("\"content\":\"");
        if (contentStart == -1) {
            return "❌ 响应解析失败：" + responseBody;
        }
        
        contentStart += 11; // "content":" 长度
        int contentEnd = responseBody.indexOf("\"", contentStart);
        
        if (contentEnd == -1) {
            return "❌ 响应解析失败";
        }
        
        String content = responseBody.substring(contentStart, contentEnd);
        // 处理转义字符
        content = content.replace("\\n", "\n").replace("\\\"", "\"");
        
        return content;
    }
    
    /**
     * 转义 JSON 字符串
     */
    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    /**
     * 获取当前模型信息
     */
    public ModelConfig getCurrentModel() {
        return currentModel;
    }
}
