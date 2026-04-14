# 快速开始

> 5 分钟快速上手 JClaw

## 前置要求

- Java 17 或更高版本
- Maven 3.6+
- 智谱 API Key（申请地址：https://open.bigmodel.cn）

## 步骤 1：获取项目

```bash
# 克隆项目
git clone https://github.com/houjibo/JClaw.git
cd JClaw
```

## 步骤 2：配置 API Key

### 方式 1：环境变量

```bash
export JCLAW_API_KEY=your_api_key_here
```

### 方式 2：配置文件

创建 `~/.jclaw/config.yml`：

```yaml
api:
  key: your_api_key_here
  endpoint: https://api.bigmodel.cn
```

### 方式 3：启动参数

```bash
java -jar target/jclaw-1.0.0.jar --api-key=your_api_key_here
```

## 步骤 3：编译项目

```bash
# 编译
mvn clean install

# 跳过测试（加快编译）
mvn clean install -DskipTests
```

## 步骤 4：运行 JClaw

### 交互模式

```bash
# 启动交互模式
java -jar target/jclaw-1.0.0.jar

# 或使用命令
jclaw
```

进入交互模式后：

```
🤖 JClaw v1.0.0
输入指令或输入 'help' 查看帮助

> 读取当前目录的文件
```

### 命令行模式

```bash
# 执行单条指令
java -jar target/jclaw-1.0.0.jar "读取当前目录的文件"

# 使用工具
java -jar target/jclaw-1.0.0.jar tool file_read --path README.md

# 使用技能
java -jar target/jclaw-1.0.0.jar skill web_search --query "AI 编码助手"
```

## 步骤 5：第一个任务

尝试让 JClaw 帮你完成一个简单任务：

```
> 帮我创建一个 Spring Boot 项目，包含用户管理功能
```

JClaw 会：
1. 分析需求
2. 创建项目结构
3. 生成代码
4. 运行测试

## 常用命令

| 命令 | 说明 | 示例 |
|------|------|------|
| `help` | 查看帮助 | `help` |
| `status` | 查看状态 | `status` |
| `tools` | 列出工具 | `tools` |
| `skills` | 列出技能 | `skills` |
| `config` | 查看配置 | `config` |
| `quit` | 退出 | `quit` |

## 下一步

- 📚 阅读 [核心概念](./03-concepts.md)
- 🛠️ 学习 [工具使用](../guides/tools/intro.md)
- 🎯 探索 [技能开发](../guides/skills/intro.md)
- 🤖 了解 [多 Agent 协作](../guides/agents/coordinator.md)

## 遇到问题？

- 查看 [常见问题](./faq.md)
- 提交 [Issue](https://github.com/houjibo/JClaw/issues)
- 参与 [讨论](https://github.com/houjibo/JClaw/discussions)

---

*最后更新：2026-04-15*
