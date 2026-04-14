# 核心概念

> 理解 JClaw 的核心概念和架构

## 架构概览

```
┌─────────────────────────────────────────┐
│           JClaw Gateway                 │
│  ┌─────────────────────────────────┐    │
│  │         API Layer               │    │
│  │  REST API | CLI | SDK           │    │
│  └─────────────────────────────────┘    │
│  ┌─────────────────────────────────┐    │
│  │        Core Engine              │    │
│  │  Intent | Agent | Task | Memory │    │
│  └─────────────────────────────────┘    │
│  ┌─────────────────────────────────┐    │
│  │       Tool & Skill Layer        │    │
│  │  43+ Tools | Skill Market       │    │
│  └─────────────────────────────────┘    │
└─────────────────────────────────────────┘
```

## 核心概念

### 1. 意图 (Intent)

意图是用户想要完成的目标或任务。JClaw 通过意图识别理解用户需求。

**示例**：
- "帮我创建一个用户管理系统" → 意图：创建项目
- "这段代码有什么问题" → 意图：代码审查
- "搜索最新的 AI 编码工具" → 意图：信息检索

### 2. Agent

Agent 是执行任务的人工智能实体。JClaw 支持多种专业角色的 Agent。

**五大核心角色**：

| 角色 | 职责 | 模型 |
|------|------|------|
| PM-QA | 需求分析 + 质量测试 | Kimi K2.5 |
| Architect | 技术决策 + 代码审查 | Qwen3.5-Plus |
| FullStack | 编码实现 | Qwen3-Coder-Plus |
| DevOps | CI/CD + 部署 | Qwen3.5-Plus |
| Analyst | 技术调研 + 竞品分析 | Kimi K2.5 |

### 3. 工具 (Tool)

工具是 JClaw 可以执行的具体操作。内置 43+ 工具。

**工具分类**：

- **文件操作**：file_read, file_write, file_edit, notebook_edit
- **搜索工具**：grep, glob, tool_search
- **系统工具**：bash, config, sleep, schedule_cron
- **Git 工具**：git
- **网络工具**：web_search, web_fetch
- **任务管理**：todo_write, task_create, task_list, task_update
- **MCP 协议**：mcp, mcp_resources
- **代码工具**：review, explain, optimize, security, document, test, build, deploy, debug
- **Agent 工具**：agent_create, agent_list, agent_message
- **技能系统**：skill
- **上下文**：context_manage
- **记忆**：memory_search, knowledge_add

### 4. 技能 (Skill)

技能是可扩展的功能模块，用户可以开发和发布自定义技能。

**技能结构**：
```
my-skill/
├── skill.json      # 元数据
├── Skill.java      # 实现
├── README.md       # 文档
└── test/           # 测试
```

### 5. 会话 (Session)

会话是用户与 JClaw 的一次完整交互过程。

**会话类型**：
- **main**：主会话，与用户直接交互
- **isolated**：独立会话，用于后台任务
- **current**：当前会话，绑定到特定上下文

### 6. 记忆 (Memory)

记忆系统让 JClaw 能够记住历史交互和重要信息。

**记忆层次**：
- **短期记忆**：当前会话上下文
- **长期记忆**：MEMORY.md 文件
- **技能记忆**：已安装技能和配置

### 7. 协调器 (Coordinator)

协调器负责管理多 Agent 协作。

**协作模式**：
- **顺序模式**：Agent 按顺序执行
- **并行模式**：Agent 并行执行
- **讨论模式**：Agent 互相讨论

## 工作流程

```
用户输入
   ↓
意图识别
   ↓
任务分解
   ↓
Agent 选择
   ↓
工具/技能执行
   ↓
结果生成
   ↓
用户反馈
```

## 数据流

```
┌─────────┐     ┌─────────┐     ┌─────────┐
│  用户   │ ──→ │ Gateway │ ──→ │  Engine │
└─────────┘     └─────────┘     └─────────┘
                                     │
                              ┌──────┴──────┐
                              │             │
                         ┌────▼────┐  ┌────▼────┐
                         │  Tools  │  │ Skills  │
                         └─────────┘  └─────────┘
```

## 配置系统

JClaw 使用分层配置：

1. **系统配置**：`~/.jclaw/config.yml`
2. **项目配置**：`.jclaw.yml`（项目根目录）
3. **环境变量**：`JCLAW_*`

配置优先级：环境变量 > 项目配置 > 系统配置

## 安全模型

- **权限控制**：工具执行需要用户授权
- **沙箱隔离**：技能在沙箱中运行
- **审计日志**：所有操作都有日志记录

## 扩展点

- **技能开发**：创建自定义技能
- **插件系统**：扩展核心功能
- **MCP 集成**：连接外部工具/资源
- **通道适配**：支持多种通信渠道

---

*最后更新：2026-04-15*
