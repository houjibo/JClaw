package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

/**
 * HTTP 请求工具命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class CurlCommand extends Command {
    
    private final HttpClient httpClient;
    
    public CurlCommand() {
        this.name = "curl";
        this.description = "HTTP 请求工具";
        this.aliases = Arrays.asList("http", "request");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
        
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (args == null || args.isBlank()) {
            return showHelp();
        }
        
        // 解析参数
        String method = "GET";
        String url = null;
        Map<String, String> headers = new HashMap<>();
        String body = null;
        int timeout = 30;
        
        String[] parts = args.split("\\s+");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            
            if ("-X".equals(part) || "--request".equals(part)) {
                if (i + 1 < parts.length) {
                    method = parts[++i].toUpperCase();
                }
            } else if ("-H".equals(part) || "--header".equals(part)) {
                if (i + 1 < parts.length) {
                    String header = parts[++i];
                    String[] kv = header.split(":", 2);
                    if (kv.length == 2) {
                        headers.put(kv[0].trim(), kv[1].trim());
                    }
                }
            } else if ("-d".equals(part) || "--data".equals(part)) {
                if (i + 1 < parts.length) {
                    body = parts[++i];
                }
            } else if ("-t".equals(part) || "--timeout".equals(part)) {
                if (i + 1 < parts.length) {
                    timeout = Integer.parseInt(parts[++i]);
                }
            } else if (!part.startsWith("-")) {
                url = part;
            }
        }
        
        if (url == null) {
            return CommandResult.error("请指定 URL");
        }
        
        return executeRequest(method, url, headers, body, timeout);
    }
    
    private CommandResult executeRequest(String method, String url, Map<String, String> headers, String body, int timeout) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(timeout));
            
            // 添加 headers
            headers.forEach(builder::header);
            
            // 设置方法和 body
            if ("GET".equals(method)) {
                builder.GET();
            } else if ("POST".equals(method)) {
                builder.POST(body != null ? HttpRequest.BodyPublishers.ofString(body) : HttpRequest.BodyPublishers.noBody());
            } else if ("PUT".equals(method)) {
                builder.PUT(body != null ? HttpRequest.BodyPublishers.ofString(body) : HttpRequest.BodyPublishers.noBody());
            } else if ("DELETE".equals(method)) {
                builder.DELETE();
            } else if ("PATCH".equals(method)) {
                builder.method("PATCH", body != null ? HttpRequest.BodyPublishers.ofString(body) : HttpRequest.BodyPublishers.noBody());
            } else {
                builder.method(method, body != null ? HttpRequest.BodyPublishers.ofString(body) : HttpRequest.BodyPublishers.noBody());
            }
            
            HttpRequest request = builder.build();
            
            long startTime = System.currentTimeMillis();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            return CommandResult.success("请求完成")
                    .withDisplayText(formatResponse(response, duration, url));
                    
        } catch (IOException e) {
            return CommandResult.error("请求失败：" + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CommandResult.error("请求中断");
        } catch (Exception e) {
            return CommandResult.error("请求异常：" + e.getMessage());
        }
    }
    
    private String formatResponse(HttpResponse<String> response, long duration, String url) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("## HTTP 响应\n\n");
        sb.append(String.format("**URL**: %s\n\n", url));
        
        sb.append("### 响应信息\n\n");
        sb.append("| 属性 | 值 |\n");
        sb.append("|------|------|\n");
        sb.append(String.format("| 状态码 | %d %s |\n", response.statusCode(), getStatusText(response.statusCode())));
        sb.append(String.format("| 耗时 | %d ms |\n", duration));
        sb.append(String.format("| 版本 | %s |\n", response.version()));
        
        // Headers
        sb.append("\n### 响应头\n\n");
        sb.append("```\n");
        response.headers().map().forEach((key, values) -> {
            values.forEach(value -> sb.append(key).append(": ").append(value).append("\n"));
        });
        sb.append("```\n");
        
        // Body
        String body = response.body();
        if (body != null && !body.isEmpty()) {
            sb.append("\n### 响应体\n\n");
            
            // 判断是否是 JSON
            if (body.trim().startsWith("{") || body.trim().startsWith("[")) {
                sb.append("```json\n");
                sb.append(formatJson(body));
                sb.append("\n```\n");
            } else {
                sb.append("```\n");
                sb.append(truncate(body, 5000));
                sb.append("\n```\n");
            }
        }
        
        return sb.toString();
    }
    
    private String getStatusText(int statusCode) {
        return switch (statusCode) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 204 -> "No Content";
            case 301 -> "Moved Permanently";
            case 302 -> "Found";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "";
        };
    }
    
    private String formatJson(String json) {
        // 简单的 JSON 格式化
        try {
            // 使用缩进格式化
            int indent = 0;
            StringBuilder formatted = new StringBuilder();
            boolean inString = false;
            
            for (int i = 0; i < json.length(); i++) {
                char c = json.charAt(i);
                
                if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                    inString = !inString;
                }
                
                if (!inString) {
                    if (c == '{' || c == '[') {
                        formatted.append(c).append("\n");
                        indent++;
                        formatted.append("  ".repeat(indent));
                    } else if (c == '}' || c == ']') {
                        formatted.append("\n");
                        indent--;
                        formatted.append("  ".repeat(indent)).append(c);
                    } else if (c == ',') {
                        formatted.append(c).append("\n");
                        formatted.append("  ".repeat(indent));
                    } else if (c == ':') {
                        formatted.append(c).append(" ");
                    } else if (Character.isWhitespace(c)) {
                        continue;
                    } else {
                        formatted.append(c);
                    }
                } else {
                    formatted.append(c);
                }
            }
            
            return formatted.toString();
        } catch (Exception e) {
            return json;
        }
    }
    
    private String truncate(String str, int maxLen) {
        if (str.length() <= maxLen) {
            return str;
        }
        return str.substring(0, maxLen) + "\n... (已截断)";
    }
    
    private CommandResult showHelp() {
        String report = """
            ## curl 命令帮助
            
            ### 用法
            
            ```
            curl <URL> [-X METHOD] [-H HEADER] [-d DATA] [-t TIMEOUT]
            ```
            
            ### 参数
            
            - URL: 请求地址（必填）
            - -X, --request: HTTP 方法（GET/POST/PUT/DELETE/PATCH）
            - -H, --header: 请求头（格式："Key: Value"）
            - -d, --data: 请求体数据
            - -t, --timeout: 超时时间（秒，默认 30）
            
            ### 示例
            
            ```
            curl https://api.example.com/users
            curl -X POST https://api.example.com/users -H "Content-Type: application/json" -d '{"name":"test"}'
            curl https://api.example.com/users/1 -X PUT -d '{"name":"updated"}'
            curl https://api.example.com/users/1 -X DELETE
            ```
            
            ### 支持的 HTTP 方法
            
            - GET（默认）
            - POST
            - PUT
            - DELETE
            - PATCH
            - HEAD
            - OPTIONS
            """;
        
        return CommandResult.success("curl 帮助")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：curl
            别名：http, request
            描述：HTTP 请求工具
            
            用法：
              curl <URL> [选项]
            
            选项：
              -X METHOD    HTTP 方法（GET/POST/PUT/DELETE）
              -H HEADER    请求头
              -d DATA      请求体
              -t TIMEOUT   超时时间（秒）
            
            示例：
              curl https://api.example.com
              curl -X POST https://api.example.com -d '{"key":"value"}'
            """;
    }
}
