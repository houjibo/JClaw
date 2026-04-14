# JClaw Hello World 示例

最简单的 JClaw 使用示例，展示如何快速开始。

## 前置要求

- Java 17+
- Maven 3.6+

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/houjibo/JClaw.git
cd JClaw/examples/hello-world
```

### 2. 配置 API Key

创建 `.env` 文件：

```bash
JCLAW_API_KEY=your_api_key_here
```

### 3. 运行示例

```bash
# 编译
mvn clean compile

# 运行
mvn exec:java
```

### 4. 与 JClaw 交互

```java
import com.jclaw.client.JClawClient;

public class HelloWorld {
    public static void main(String[] args) {
        // 创建客户端
        JClawClient client = JClawClient.create("your_api_key");
        
        // 发送简单指令
        String result = client.execute("读取当前目录的文件");
        System.out.println(result);
        
        // 使用工具
        client.tool("file_read", Map.of("path", "README.md"));
        
        // 关闭客户端
        client.close();
    }
}
```

## 输出示例

```
## 文件列表

- README.md
- pom.xml
- src/

## 文件内容：README.md

# JClaw Hello World 示例

最简单的 JClaw 使用示例...
```

## 下一步

- 查看 [spring-boot-demo](../spring-boot-demo/) - Spring Boot 集成示例
- 查看 [skill-demo](../skill-demo/) - 技能开发示例
- 查看 [mcp-integration](../mcp-integration/) - MCP 协议集成示例
- 查看 [multi-agent-demo](../multi-agent-demo/) - 多 Agent 协作示例

## 资源

- [JClaw 文档](https://docs.jclaw.dev)
- [GitHub 仓库](https://github.com/houjibo/JClaw)
- [技能市场](https://skills.jclaw.dev)
