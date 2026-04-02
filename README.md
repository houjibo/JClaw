# JClaw - Java 编码智能体


## 🎯 设计理念

- **工具系统**: 43 个工具的 Java 实现
- **命令系统**: 101 个命令的扩展框架
- **MCP 协议**: Model Context Protocol 支持
- **模块化**: 易于扩展和维护

## 📁 项目结构

```
jcode/
├── src/main/java/com/openclaw/jclaw/
│   ├── JClawApplication.java     # 主应用入口
│   ├── core/                      # 核心抽象
│   │   ├── Tool.java             # 工具基类
│   │   ├── ToolRegistry.java     # 工具注册表
│   │   ├── ToolResult.java       # 执行结果
│   │   ├── ToolContext.java      # 执行上下文
│   │   └── ToolCategory.java     # 工具分类
│   ├── tools/                     # 工具实现
│   │   ├── FileReadTool.java     # 文件读取
│   │   ├── FileWriteTool.java    # 文件写入
│   │   ├── GrepTool.java         # 代码搜索
│   │   ├── BashTool.java         # 命令执行
│   │   ├── GitTool.java          # Git 操作
│   │   ├── McpTool.java          # MCP 协议
│   │   ├── WebSearchTool.java    # 网络搜索
│   │   ├── GlobTool.java         # 文件匹配
│   │   └── TodoWriteTool.java    # TODO 管理
│   ├── controller/                # REST 控制器
│   │   └── ToolController.java    # 工具 API
│   └── config/                    # 配置类
│       ├── WebSocketConfig.java   # WebSocket 配置
│       └── WebSocketMessageHandler.java
└── src/main/resources/
    └── application.yml            # 配置文件
```

## 🛠️ 已实现工具（13 个）

| 工具 | 分类 | 说明 |
|------|------|------|
| `file_read` | FILE | 读取文件内容，支持行范围和编码 |
| `file_write` | FILE | 写入文件内容，自动创建目录 |
| `file_edit` | FILE | **diff 补丁式编辑，精确修改** 🔥 |
| `grep` | SEARCH | 代码搜索，支持正则表达式 |
| `glob` | SEARCH | 文件匹配，支持 * 和 ** 通配符 |
| `bash` | SYSTEM | 执行 Bash 命令，带安全检查 |
| `git` | GIT | Git 操作（status/log/diff/branch） |
| `web_search` | NETWORK | 网络搜索（DuckDuckGo） |
| `web_fetch` | NETWORK | **网页抓取，提取文本内容** 🔥 |
| `mcp` | MCP | MCP 协议调用，支持远程工具 |
| `todo_write` | TASK | TODO 管理（create/update/delete/list） |
| `task_create` | TASK | **创建任务，支持优先级和子任务** 🔥 |
| `task_list` | TASK | **列出任务，支持状态/优先级过滤** 🔥 |

## 🌐 REST API

### 列出所有工具
```bash
curl http://localhost:8080/api/tools
```

### 获取工具详情
```bash
curl http://localhost:8080/api/tools/file_read
```

### 执行工具
```bash
curl http://localhost:8080/api/tools/file_read/execute \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"path": "pom.xml"}'
```

### 健康检查
```bash
curl http://localhost:8080/api/health
```

## 🔌 WebSocket

### 连接
```javascript
const ws = new WebSocket('ws://localhost:8080/ws');
```

### 执行工具
```javascript
ws.send(JSON.stringify({
  toolName: 'file_read',
  params: { path: 'pom.xml' },
  sessionId: 'session-123'
}));
```

### 接收响应
```javascript
ws.onmessage = (event) => {
  const response = JSON.parse(event.data);
  console.log(response);
};
```

## 🚀 快速开始

### 环境要求
- JDK 21+
- Maven 3.8+

### 构建
```bash
cd ~/.openclaw/workspace/projects/code/core/JClaw
mvn clean package
```

### 运行
```bash
mvn spring-boot:run
```

应用启动后访问：
- REST API: http://localhost:8080/api
- WebSocket: ws://localhost:8080/ws

## 📋 待实现功能

### 工具扩展
- [ ] `file_edit` - 文件编辑（diff 补丁）
- [ ] `web_fetch` - 网页抓取
- [ ] `task_*` - 任务管理系列（create/list/update/stop）
- [ ] `notebook_edit` - Notebook 编辑
- [ ] `config` - 配置管理

### 命令系统
- [ ] `commit` - Git 提交命令
- [ ] `review` - 代码审查命令
- [ ] `config` - 配置管理命令

### 服务层
- [ ] MCP 客户端完整实现
- [ ] 多 Agent 协调器

## 🔧 配置说明

```yaml
server:
  port: 8080

jcode:
  workspace: ${user.home}/.openclaw/workspace  # JClaw 工作目录
  max-read-size: 10485760                       # 最大读取大小 (10MB)
  allow-write: true                             # 允许写操作
  allow-exec: true                              # 允许命令执行

mcp:
  enabled: true
  servers:
    - name: local
      url: http://localhost:3000/mcp
```


|------|-------------|-------|
| 语言 | TypeScript | Java |
| 工具数量 | 43 | 9 ✅ |
| 命令数量 | 101 | 0 (框架就绪) |
| MCP 协议 | ✅ | ✅ |
| Git 集成 | ✅ | ✅ |
| REST API | ❌ | ✅ |
| WebSocket | ✅ | ✅ |
| 终端 UI | Ink | 待实现 |
| 多 Agent | ✅ | 待实现 |

## 📝 开发计划

| Phase | 内容 | 状态 |
|-------|------|------|
| Phase 1 | 核心工具框架 | ✅ 完成 |
| Phase 2 | 完善工具集（13 个工具） | ✅ 完成 |
| Phase 3 | REST API + WebSocket | ✅ 完成 |
| Phase 4 | FileEditTool + Task 系统 | ✅ 完成 |
| Phase 5 | 命令系统实现 | 📋 计划 |
| Phase 6 | MCP 深度集成 | 📋 计划 |
| Phase 7 | 多 Agent 协调 | 📋 计划 |

## 🎓 学习来源

- 核心参考：`Tool.ts`, `tools/` 目录，`MCP` 服务

---

*JClaw - 让 Java 开发更智能* 🤖
