# JClaw Spring Boot 集成示例

展示如何在 Spring Boot 项目中集成 JClaw。

## 前置要求

- Java 17+
- Spring Boot 3.0+
- Maven 3.6+

## 快速开始

### 1. 添加依赖

在 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.jclaw</groupId>
    <artifactId>jclaw-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置应用

在 `application.yml` 中配置：

```yaml
jclaw:
  enabled: true
  api-key: ${JCLAW_API_KEY}
  endpoint: https://api.jclaw.dev
  timeout: 30s
  retry:
    max-attempts: 3
    backoff: 1000ms
```

### 3. 注入客户端

```java
import com.jclaw.spring.JClawTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyService {
    
    @Autowired
    private JClawTemplate jclaw;
    
    public String analyzeCode(String code) {
        return jclaw.execute("分析这段代码：" + code);
    }
    
    public Map<String, Object> readFile(String path) {
        return jclaw.tool("file_read", Map.of("path", path));
    }
}
```

### 4. 创建 REST API

```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {
    
    @Autowired
    private JClawTemplate jclaw;
    
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeCode(@RequestBody CodeRequest request) {
        try {
            String result = jclaw.execute("分析代码：" + request.getCode());
            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequest request) {
        String response = jclaw.execute(request.getMessage());
        return ResponseEntity.ok(Map.of("response", response));
    }
}
```

## 高级用法

### 异步调用

```java
@Service
public class AsyncService {
    
    @Autowired
    private JClawTemplate jclaw;
    
    @Async
    public CompletableFuture<String> analyzeAsync(String code) {
        return CompletableFuture.supplyAsync(() -> 
            jclaw.execute("分析代码：" + code)
        );
    }
}
```

### 流式输出

```java
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<String>> streamResponse(@RequestParam String query) {
    return jclaw.stream(query)
        .map(chunk -> ServerSentEvent.builder(chunk).build());
}
```

### 错误处理

```java
@ControllerAdvice
public class JClawExceptionHandler {
    
    @ExceptionHandler(JClawException.class)
    public ResponseEntity<?> handleJClawException(JClawException e) {
        return ResponseEntity.status(e.getStatusCode())
            .body(Map.of(
                "error", e.getMessage(),
                "code", e.getErrorCode()
            ));
    }
}
```

### 自定义配置

```java
@Configuration
public class JClawConfig {
    
    @Bean
    public JClawTemplate jclawTemplate(@Value("${jclaw.api-key}") String apiKey) {
        return JClawTemplate.builder()
            .apiKey(apiKey)
            .timeout(Duration.ofSeconds(60))
            .retryPolicy(RetryPolicy.exponentialBackoff(3, 1000))
            .build();
    }
}
```

## 完整示例

### 代码审查服务

```java
@Service
public class CodeReviewService {
    
    @Autowired
    private JClawTemplate jclaw;
    
    public CodeReviewResult review(String code, String language) {
        String prompt = """
            请审查以下 %s 代码：
            
            %s
            
            请从以下角度审查：
            1. 代码质量
            2. 安全性
            3. 性能
            4. 最佳实践
            
            输出结构化的审查报告。
            """.formatted(language, code);
        
        String result = jclaw.execute(prompt);
        return parseReviewResult(result);
    }
}
```

### 智能搜索服务

```java
@Service
public class SearchService {
    
    @Autowired
    private JClawTemplate jclaw;
    
    public List<SearchResult> intelligentSearch(String query) {
        // 使用 JClaw 理解搜索意图
        String intent = jclaw.execute("分析搜索意图：" + query);
        
        // 根据意图执行搜索
        var results = jclaw.tool("web_search", Map.of("query", query));
        
        // 使用 JClaw 总结结果
        String summary = jclaw.execute("总结搜索结果：" + results);
        
        return parseSearchResults(summary);
    }
}
```

## 测试

```java
@SpringBootTest
class MyServiceTest {
    
    @Autowired
    private JClawTemplate jclaw;
    
    @Test
    void testAnalyzeCode() {
        String code = "public class Hello {}";
        String result = jclaw.execute("分析代码：" + code);
        assertNotNull(result);
    }
    
    @Test
    void testTool() {
        var result = jclaw.tool("file_read", Map.of("path", "test.txt"));
        assertNotNull(result);
    }
}
```

## 资源

- [Spring Boot 集成文档](https://docs.jclaw.dev/integration/spring-boot)
- [JClawTemplate API](https://docs.jclaw.dev/api/jclaw-template)
- [示例项目源码](https://github.com/houjibo/JClaw/tree/main/examples/spring-boot-demo)
