# 安装指南

> JClaw 安装与配置完整指南

---

## 前置要求

| 依赖 | 最低版本 | 推荐版本 |
|------|---------|---------|
| Java | 17 | 21 |
| Maven | 3.6 | 3.9 |
| Node.js (文档) | 18 | 20 |

---

## 步骤 1: 下载 JClaw

### 方式 1: Git 克隆 (推荐)

```bash
git clone https://github.com/houjibo/JClaw.git
cd JClaw
```

### 方式 2: 下载源码包

```bash
# 访问 GitHub 下载最新 release
wget https://github.com/houjibo/JClaw/archive/refs/tags/v1.0.0.zip
unzip v1.0.0.zip
cd JClaw-1.0.0
```

---

## 步骤 2: 编译项目

```bash
# 使用 Maven 编译
mvn clean install

# 跳过测试 (加快编译)
mvn clean install -DskipTests
```

编译成功后，在 `target/` 目录下生成 `jclaw-1.0.0.jar`。

---

## 步骤 3: 配置 API Key

### 获取智谱 API Key

1. 访问 https://open.bigmodel.cn
2. 注册/登录账号
3. 进入 API 控制台
4. 创建新的 API Key
5. 复制并保存 Key

### 配置方式

**方式 1: 环境变量 (推荐)**

```bash
export JCLAW_API_KEY="your_api_key_here"
```

**方式 2: 配置文件**

创建 `~/.jclaw/config.yml`:

```yaml
api:
  key: "your_api_key_here"
  endpoint: "https://api.bigmodel.cn"

model:
  provider: "zhipu"
  name: "glm-4.7"
```

**方式 3: 启动参数**

```bash
java -jar target/jclaw-1.0.0.jar --api-key="your_api_key_here"
```

---

## 步骤 4: 运行 JClaw

### 交互模式

```bash
java -jar target/jclaw-1.0.0.jar
```

启动后看到：

```
🤖 JClaw v1.0.0 (Phoenix)
输入指令或输入 'help' 查看帮助

> 
```

### 命令行模式

```bash
# 执行单条指令
java -jar target/jclaw-1.0.0.jar "读取当前目录的文件"

# 使用工具
java -jar target/jclaw-1.0.0.jar tool file_read --path README.md
```

---

## 步骤 5: 验证安装

### 测试基本功能

```bash
# 查看帮助
jclaw help

# 查看状态
jclaw status

# 列出工具
jclaw tools
```

### 测试 AI 功能

```bash
# 简单对话
jclaw "你好，请介绍一下自己"

# 代码分析
jclaw "分析这个文件：README.md"
```

---

## 可选配置

### 通道集成

**Telegram**:
```yaml
# ~/.jclaw/channels/telegram.yml
telegram:
  enabled: true
  botToken: "YOUR_BOT_TOKEN"
```

**Discord**:
```yaml
# ~/.jclaw/channels/discord.yml
discord:
  enabled: true
  botToken: "YOUR_BOT_TOKEN"
  guildId: "YOUR_GUILD_ID"
```

### 第三方集成

**GitHub**:
```yaml
# ~/.jclaw/integrations/github.yml
github:
  enabled: true
  token: "YOUR_GITHUB_TOKEN"
  owner: "your-username"
  repo: "your-repo"
```

**Slack**:
```yaml
# ~/.jclaw/integrations/slack.yml
slack:
  enabled: true
  token: "xoxb-YOUR_SLACK_TOKEN"
```

---

## 故障排除

### 问题 1: 编译失败

**错误**: `Unsupported class file major version`

**解决**: 确认 Java 版本 >= 17
```bash
java -version
```

### 问题 2: API Key 无效

**错误**: `401 Unauthorized`

**解决**:
1. 检查 API Key 是否正确
2. 确认 API Key 未过期
3. 验证网络连接

### 问题 3: 内存不足

**错误**: `OutOfMemoryError`

**解决**: 增加 JVM 堆内存
```bash
java -Xmx2g -jar target/jclaw-1.0.0.jar
```

---

## 下一步

- 📚 阅读 [快速开始](./02-quickstart.md)
- 🛠️ 学习 [工具使用](../guides/tools/intro.md)
- 🎯 探索 [技能开发](../guides/skills/intro.md)

---

*最后更新：2026-04-15*
