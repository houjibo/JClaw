package com.jclaw.docs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * API 文档自动生成器
 * 
 * 功能：
 * - Swagger/OpenAPI 解析
 * - Markdown 文档生成
 * - 示例代码生成
 * - 文档更新
 * 
 * @author JClaw
 * @since 2026-04-15
 */
@Slf4j
@Service
public class ApiDocGenerator {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 从 Swagger JSON 生成 Markdown 文档
     */
    public void generateFromSwagger(String swaggerUrl, Path outputDir) throws IOException {
        log.info("开始从 Swagger 生成 API 文档：{}", swaggerUrl);
        
        // 读取 Swagger JSON
        String swaggerJson = Files.readString(Paths.get(swaggerUrl));
        JsonNode root = objectMapper.readTree(swaggerJson);
        
        // 创建输出目录
        Files.createDirectories(outputDir);
        
        // 生成概述文档
        generateOverview(root, outputDir);
        
        // 生成各分组文档
        generateEndpoints(root, outputDir);
        
        // 生成认证文档
        generateAuthDoc(root, outputDir);
        
        log.info("API 文档生成完成：{}", outputDir);
    }
    
    /**
     * 生成概述文档
     */
    private void generateOverview(JsonNode swagger, Path outputDir) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        // 标题
        JsonNode info = swagger.path("info");
        sb.append("# ").append(info.path("title").asText()).append("\n\n");
        sb.append("> ").append(info.path("description").asText()).append("\n\n");
        sb.append("**版本**: ").append(info.path("version").asText()).append("\n\n");
        
        // 服务器
        sb.append("## 服务器\n\n");
        for (JsonNode server : swagger.path("servers")) {
            sb.append("- ").append(server.path("url").asText());
            if (server.has("description")) {
                sb.append(" - ").append(server.path("description").asText());
            }
            sb.append("\n");
        }
        sb.append("\n");
        
        // 认证
        sb.append("## 认证\n\n");
        sb.append("所有 API 请求需要在 Header 中包含认证信息：\n\n");
        sb.append("```bash\n");
        sb.append("Authorization: Bearer <YOUR_API_KEY>\n");
        sb.append("```\n\n");
        
        // 统计
        JsonNode paths = swagger.path("paths");
        int totalEndpoints = 0;
        Set<String> tags = new HashSet<>();
        
        paths.fields().forEachRemaining(entry -> {
            entry.getValue().fields().forEachRemaining(method -> {
                totalEndpoints++;
                method.getValue().path("tags").forEach(tag -> tags.add(tag.asText()));
            });
        });
        
        sb.append("## API 统计\n\n");
        sb.append("| 指标 | 数量 |\n");
        sb.append("|------|------|\n");
        sb.append("| 端点总数 | ").append(totalEndpoints).append(" |\n");
        sb.append("| 分组数量 | ").append(tags.size()).append(" |\n");
        sb.append("\n");
        
        // 分组列表
        sb.append("## API 分组\n\n");
        for (String tag : tags) {
            sb.append("- [").append(tag).append("](./").append(tag.toLowerCase()).append(".md)\n");
        }
        sb.append("\n");
        
        Files.writeString(outputDir.resolve("README.md"), sb.toString());
    }
    
    /**
     * 生成端点文档
     */
    private void generateEndpoints(JsonNode swagger, Path outputDir) throws IOException {
        Map<String, StringBuilder> docsByTag = new HashMap<>();
        
        JsonNode paths = swagger.path("paths");
        
        paths.fields().forEachRemaining(entry -> {
            String pathUrl = entry.getKey();
            JsonNode pathItem = entry.getValue();
            
            pathItem.fields().forEachRemaining(methodEntry -> {
                String method = methodEntry.getKey().toUpperCase();
                JsonNode operation = methodEntry.getValue();
                
                operation.path("tags").forEach(tagNode -> {
                    String tag = tagNode.asText();
                    docsByTag.computeIfAbsent(tag, k -> new StringBuilder());
                    StringBuilder sb = docsByTag.get(tag);
                    
                    // 端点
                    sb.append("### ").append(method).append(" ").append(pathUrl).append("\n\n");
                    sb.append(operation.path("summary").asText()).append("\n\n");
                    
                    // 描述
                    if (operation.has("description")) {
                        sb.append(operation.path("description").asText()).append("\n\n");
                    }
                    
                    // 请求参数
                    if (operation.has("parameters")) {
                        sb.append("#### 请求参数\n\n");
                        sb.append("| 参数 | 位置 | 类型 | 必填 | 说明 |\n");
                        sb.append("|------|------|------|------|------|\n");
                        
                        operation.path("parameters").forEach(param -> {
                            String required = param.path("required").asBoolean() ? "是" : "否";
                            sb.append("| ").append(param.path("name").asText())
                              .append(" | ").append(param.path("in").asText())
                              .append(" | ").append(param.path("schema").path("type").asText())
                              .append(" | ").append(required)
                              .append(" | ").append(param.path("description").asText())
                              .append(" |\n");
                        });
                        sb.append("\n");
                    }
                    
                    // 请求体
                    if (operation.has("requestBody")) {
                        sb.append("#### 请求体\n\n");
                        sb.append("```json\n");
                        try {
                            JsonNode schema = operation.path("requestBody")
                                .path("content").path("application/json").path("schema");
                            sb.append(objectMapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(generateExample(schema)));
                        } catch (Exception e) {
                            sb.append("// 示例生成失败");
                        }
                        sb.append("\n```\n\n");
                    }
                    
                    // 响应
                    if (operation.has("responses")) {
                        sb.append("#### 响应\n\n");
                        operation.path("responses").fields().forEachRemaining(responseEntry -> {
                            String code = responseEntry.getKey();
                            JsonNode response = responseEntry.getValue();
                            
                            sb.append("**").append(code).append("** - ")
                              .append(response.path("description").asText()).append("\n\n");
                        });
                        sb.append("\n");
                    }
                    
                    // 示例代码
                    sb.append("#### 示例\n\n");
                    sb.append("```bash\n");
                    sb.append("curl -X ").append(method)
                      .append(" 'https://api.jclaw.dev").append(pathUrl).append("' \\\n");
                    sb.append("  -H 'Authorization: Bearer YOUR_API_KEY' \\\n");
                    sb.append("  -H 'Content-Type: application/json'\n");
                    sb.append("```\n\n");
                    
                    sb.append("---\n\n");
                });
            });
        });
        
        // 写入各分组文档
        docsByTag.forEach((tag, content) -> {
            try {
                StringBuilder fullDoc = new StringBuilder();
                fullDoc.append("# ").append(tag).append(" API\n\n");
                fullDoc.append(content);
                
                String filename = tag.toLowerCase().replaceAll("\\s+", "-") + ".md";
                Files.writeString(outputDir.resolve(filename), fullDoc.toString());
            } catch (IOException e) {
                log.error("生成文档失败：{}", tag, e);
            }
        });
    }
    
    /**
     * 生成认证文档
     */
    private void generateAuthDoc(JsonNode swagger, Path outputDir) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        sb.append("# 认证指南\n\n");
        sb.append("## 概述\n\n");
        sb.append("JClaw API 使用 Bearer Token 进行认证。\n\n");
        
        sb.append("## 获取 API Key\n\n");
        sb.append("1. 登录 JClaw 控制台\n");
        sb.append("2. 进入设置 > API Keys\n");
        sb.append("3. 点击\"创建新 Key\"\n");
        sb.append("4. 复制并安全保存\n\n");
        
        sb.append("## 使用方式\n\n");
        sb.append("### HTTP Header\n\n");
        sb.append("```bash\n");
        sb.append("Authorization: Bearer YOUR_API_KEY\n");
        sb.append("```\n\n");
        
        sb.append("### Java SDK\n\n");
        sb.append("```java\n");
        sb.append("JClawClient client = JClawClient.create(\"YOUR_API_KEY\");\n");
        sb.append("```\n\n");
        
        sb.append("### Python SDK\n\n");
        sb.append("```python\n");
        sb.append("client = JClawClient(api_key=\"YOUR_API_KEY\")\n");
        sb.append("```\n\n");
        
        sb.append("## 安全提示\n\n");
        sb.append("- ⚠️ 不要将 API Key 提交到代码仓库\n");
        sb.append("- ⚠️ 使用环境变量存储 API Key\n");
        sb.append("- ⚠️ 定期轮换 API Key\n");
        sb.append("- ⚠️ 泄露后立即撤销并重新生成\n\n");
        
        Files.writeString(outputDir.resolve("AUTH.md"), sb.toString());
    }
    
    /**
     * 生成示例对象
     */
    private Object generateExample(JsonNode schema) {
        String type = schema.path("type").asText("object");
        
        return switch (type) {
            case "string" -> "example";
            case "integer" -> 123;
            case "number" -> 123.45;
            case "boolean" -> true;
            case "array" -> {
                JsonNode items = schema.path("items");
                yield List.of(generateExample(items));
            }
            case "object" -> {
                Map<String, Object> obj = new HashMap<>();
                schema.path("properties").fields().forEachRemaining(prop -> {
                    obj.put(prop.getKey(), generateExample(prop.getValue()));
                });
                yield obj;
            }
            default -> null;
        };
    }
    
    /**
     * 从注解生成 API 文档
     */
    public void generateFromAnnotations(String basePackage, Path outputDir) throws IOException {
        log.info("从注解生成 API 文档：{}", basePackage);
        
        // TODO: 实现扫描 @RestController 和 @RequestMapping
        // 使用 Reflections 库扫描类路径
        
        Files.writeString(outputDir.resolve("README.md"), "# API 文档\n\n待生成...");
    }
}
