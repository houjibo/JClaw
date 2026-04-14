# JClaw MCP 协议集成示例

展示如何集成 Model Context Protocol (MCP) 与 JClaw。

## 什么是 MCP？

Model Context Protocol (MCP) 是一个开放协议，用于 AI 模型与外部工具/资源的标准化通信。

## 前置要求

- JClaw 1.0.0+
- MCP Server（任何兼容 MCP 的服务）

## 快速开始

### 1. 配置 MCP Server

创建 `mcp-config.json`：

```json
{
  "mcpServers": {
    "filesystem": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-filesystem", "/tmp"]
    },
    "github": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-github"],
      "env": {
        "GITHUB_TOKEN": "your_github_token"
      }
    }
  }
}
```

### 2. 在 JClaw 中启用 MCP

```java
import com.jclaw.client.JClawClient;
import com.jclaw.mcp.McpConfig;

JClawClient client = JClawClient.create(apiKey);

// 配置 MCP
McpConfig config = McpConfig.fromFile("mcp-config.json");
client.enableMcp(config);

// 使用 MCP 资源
var files = client.mcpResource("filesystem://files/tmp");
System.out.println(files);

// 使用 MCP 工具
var result = client.mcpTool("github-create-issue", 
    Map.of("repo", "owner/repo", "title", "Bug Report"));
System.out.println(result);
```

## MCP 资源示例

### 读取文件

```java
// 读取本地文件
var content = client.mcpResource("file:///path/to/file.txt");

// 读取 GitHub 文件
var ghFile = client.mcpResource("github://file/owner/repo/main/README.md");
```

### 查询数据库

```java
// SQL 查询
var results = client.mcpResource("postgres://query/SELECT * FROM users");
```

## MCP 工具示例

### GitHub 操作

```java
// 创建 Issue
client.mcpTool("github-create-issue", Map.of(
    "repo", "owner/repo",
    "title", "Bug Report",
    "body", "Description..."
));

// 创建 PR
client.mcpTool("github-create-pr", Map.of(
    "repo", "owner/repo",
    "title", "Feature: New API",
    "head", "feature-branch",
    "base", "main"
));
```

### 文件系统操作

```java
// 读取文件
client.mcpTool("filesystem-read_file", Map.of(
    "path", "/tmp/test.txt"
));

// 写入文件
client.mcpTool("filesystem-write_file", Map.of(
    "path", "/tmp/output.txt",
    "content", "Hello World"
));
```

## 自定义 MCP Server

### 1. 创建 Server

```typescript
// my-mcp-server.ts
import { Server } from '@modelcontextprotocol/sdk/server';

const server = new Server({
  name: 'my-server',
  version: '1.0.0'
});

// 定义资源
server.registerResource('my-resource', async (uri) => {
  return {
    contents: [{ uri: uri.href, text: 'Hello from MCP!' }]
  };
});

// 定义工具
server.registerTool('my-tool', async (params) => {
  return {
    content: [{ type: 'text', text: 'Tool executed!' }]
  };
});
```

### 2. 在 JClaw 中使用

```java
var result = client.mcpTool("my-tool", Map.of());
```

## 最佳实践

1. **安全配置**：不要硬编码敏感信息
2. **错误处理**：优雅处理 MCP 服务异常
3. **资源缓存**：缓存频繁访问的资源
4. **超时设置**：设置合理的超时时间
5. **日志记录**：记录 MCP 调用日志

## 资源

- [MCP 协议规范](https://modelcontextprotocol.io)
- [MCP Servers 列表](https://github.com/modelcontextprotocol/servers)
- [JClaw MCP 文档](https://docs.jclaw.dev/mcp)
