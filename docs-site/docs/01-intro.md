# JClaw 介绍

> JClaw - 下一代 AI 编码助手

## 什么是 JClaw？

JClaw 是一个强大的 AI 编码助手，基于智谱 GLM 4.7 模型，提供代码理解、生成、优化、测试等全方位能力。

## 核心特性

### 🧠 AI 驱动

- 基于智谱 GLM 4.7 模型
- 深度代码理解
- 智能代码生成
- 自动代码优化

### 🛠️ 丰富工具

- 43+ 内置工具
- 文件操作（读/写/编辑）
- 代码搜索（grep/glob）
- Git 集成
- 网络搜索
- 任务管理

### 🤖 多 Agent 协作

- 5 大专业角色
- 智能任务协调
- 人类评审集成
- 灵活协作模式

### 🎯 意图驱动

- 自然语言交互
- 意图识别与澄清
- 任务自动分解
- 结果可追溯

### 🔌 开放生态

- MCP 协议支持
- 技能市场
- 插件系统
- 第三方集成

## 快速开始

### 安装

```bash
# 克隆项目
git clone https://github.com/houjibo/JClaw.git
cd JClaw

# 编译
mvn clean install

# 运行
java -jar target/jclaw-1.0.0.jar
```

### 配置

创建配置文件 `~/.jclaw/config.yml`：

```yaml
api:
  key: your_api_key
  endpoint: https://api.jclaw.dev
  
model:
  provider: zhipu
  name: glm-4.7
  
features:
  streaming: true
  memory: true
```

### 使用

```bash
# 交互模式
jclaw

# 执行指令
jclaw "读取当前目录的文件"

# 使用工具
jclaw tool file_read --path README.md
```

## 能力对比

| 能力 | JClaw | Claude Code | 状态 |
|------|-------|-------------|------|
| 工具数量 | 46 | 43 | ✅ 超越 |
| 测试覆盖 | 85%+ | 75%+ | ✅ 超越 |
| 启动时间 | 0.865s | ~1.5s | ✅ +73% 快 |
| AI 模型 | GLM 4.7 | Claude 3.5 | ⚠️ 追赶中 |
| 生态建设 | 发展中 | 成熟 | ❌ 差距大 |

## 学习路线

1. **入门**：阅读 [快速开始](./02-quickstart.md)
2. **基础**：学习 [核心概念](./03-concepts.md)
3. **进阶**：掌握 [技能开发](../guides/skills/intro.md)
4. **高级**：探索 [多 Agent 协作](../guides/agents/coordinator.md)

## 资源

- [GitHub 仓库](https://github.com/houjibo/JClaw)
- [问题反馈](https://github.com/houjibo/JClaw/issues)
- [社区讨论](https://github.com/houjibo/JClaw/discussions)
- [技能市场](https://skills.jclaw.dev)

## 贡献

欢迎贡献！请参阅 [贡献指南](../CONTRIBUTING.md)。

---

*最后更新：2026-04-15*
