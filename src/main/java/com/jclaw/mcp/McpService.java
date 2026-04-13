package com.jclaw.mcp;

import com.jclaw.skills.Skill;
import com.jclaw.skills.SkillEngine;
import com.jclaw.skills.SkillResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * MCP 服务 - Model Context Protocol
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class McpService {
    
    private final SkillEngine skillEngine;
    
    /**
     * 列出所有工具（技能）
     */
    public List<McpProtocol.Tool> listTools() {
        List<McpProtocol.Tool> tools = new ArrayList<>();
        
        for (Skill skill : skillEngine.listSkills()) {
            McpProtocol.Tool tool = new McpProtocol.Tool();
            tool.setName(skill.getName());
            tool.setDescription(skill.getDescription());
            
            // 简化：所有工具都接受任意参数
            McpProtocol.Tool.InputSchema schema = new McpProtocol.Tool.InputSchema();
            schema.setProperties(new HashMap<>());
            schema.setRequired(new ArrayList<>());
            tool.setInputSchema(schema);
            
            tools.add(tool);
        }
        
        log.info("MCP 工具列表：{} 个", tools.size());
        return tools;
    }
    
    /**
     * 调用工具
     */
    public McpProtocol.Response callTool(String toolName, Map<String, Object> args) {
        log.info("MCP 调用工具：{} 参数：{}", toolName, args);
        
        SkillResult result = skillEngine.execute(toolName, args);
        
        McpProtocol.Response response = new McpProtocol.Response();
        response.setJsonrpc("2.0");
        response.setId(UUID.randomUUID().toString());
        
        if (result.isSuccess()) {
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("content", result.getContent());
            if (result.getData() != null) {
                resultData.putAll(result.getData());
            }
            response.setResult(resultData);
        } else {
            McpProtocol.Response.Error error = new McpProtocol.Response.Error();
            error.setCode(400);
            error.setMessage(result.getError());
            response.setError(error);
        }
        
        return response;
    }
    
    /**
     * 列出所有资源
     */
    public List<McpProtocol.Resource> listResources() {
        List<McpProtocol.Resource> resources = new ArrayList<>();
        
        // 添加系统资源
        resources.add(createResource("system://health", "系统健康", "检查系统状态", "application/json"));
        resources.add(createResource("system://metrics", "系统指标", "性能监控指标", "application/json"));
        resources.add(createResource("system://logs", "系统日志", "最近日志", "text/plain"));
        
        log.info("MCP 资源列表：{} 个", resources.size());
        return resources;
    }
    
    /**
     * 读取资源
     */
    public String readResource(String uri) {
        log.info("MCP 读取资源：{}", uri);
        
        switch (uri) {
            case "system://health":
                return "{\"status\": \"UP\"}";
            case "system://metrics":
                return "{\"threads\": " + Runtime.getRuntime().availableProcessors() + "}";
            case "system://logs":
                return "最近日志内容...";
            default:
                return "未知资源：" + uri;
        }
    }
    
    /**
     * 列出所有提示词
     */
    public List<McpProtocol.Prompt> listPrompts() {
        List<McpProtocol.Prompt> prompts = new ArrayList<>();
        
        // 添加内置提示词
        prompts.add(createPrompt("code-review", "代码审查", "请审查这段代码"));
        prompts.add(createPrompt("explain-code", "代码解释", "请解释这段代码的功能"));
        prompts.add(createPrompt("optimize-code", "代码优化", "请优化这段代码的性能"));
        
        log.info("MCP 提示词列表：{} 个", prompts.size());
        return prompts;
    }
    
    private McpProtocol.Resource createResource(String uri, String name, String desc, String mime) {
        McpProtocol.Resource resource = new McpProtocol.Resource();
        resource.setUri(uri);
        resource.setName(name);
        resource.setDescription(desc);
        resource.setMimeType(mime);
        return resource;
    }
    
    private McpProtocol.Prompt createPrompt(String name, String desc, String defaultText) {
        McpProtocol.Prompt prompt = new McpProtocol.Prompt();
        prompt.setName(name);
        prompt.setDescription(desc);
        prompt.setArguments(new ArrayList<>());
        return prompt;
    }
}
