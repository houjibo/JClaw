# JClaw - AI 智能助手

> **真正可用的 AI 助手** - 集成智谱 AI、飞书消息、技能系统

---

## 🚀 快速开始

### 1. 环境要求
- Java 17+
- Maven 3.8+
- （可选）PostgreSQL 14+
- （可选）Redis 6+

### 2. 配置 API Key

```bash
# 复制配置示例
cp src/main/resources/application.yml.example application.yml

# 编辑配置，填入智谱 AI API Key
vim application.yml
```

配置智谱 AI：
```yaml
jclaw:
  ai:
    zhipu:
      api-key: your-api-key-here  # 替换为你的 API Key
```

### 3. 编译

```bash
mvn clean package -DskipTests
```

### 4. 启动

```bash
# 方式 1：使用启动脚本
chmod +x jclaw
./jclaw start

# 方式 2：直接运行
java -jar target/jclaw-1.0.0.jar
```

### 5. 验证

```bash
# 检查状态
./jclaw status

# 查看日志
tail -f logs/jclaw.log
```

---

## 💬 使用示例

### 简单对话

```java
@Autowired
private AiService aiService;

String response = aiService.chat("你好，请介绍一下自己");
System.out.println(response);
```

### 使用技能

```java
@Autowired
private SkillEngine skillEngine;

// 读取文件
SkillResult result = skillEngine.execute("file_read", Map.of(
    "path", "/path/to/file.txt"
));

// 执行命令
SkillResult cmdResult = skillEngine.execute("bash", Map.of(
    "command", "ls -la"
));
```

### 记忆系统

```java
@Autowired
private MemoryService memoryService;

// 保存记忆
memoryService.save("用户喜欢喝可乐");

// 搜索记忆
String info = memoryService.search("用户喜好");
```

---

## 📁 项目结构

```
JClaw/
├── src/main/java/com/jclaw/
│   ├── ai/              # AI 服务（智谱集成）
│   ├── skills/          # 技能系统
│   ├── memory/          # 记忆系统
│   ├── channels/        # 消息通道
│   ├── scheduler/       # 定时任务
│   └── controller/      # REST API
├── src/main/resources/
│   ├── application.yml.example  # 配置示例
│   └── db/migration/    # 数据库迁移
├── jclaw                # 启动脚本
└── README.md            # 本文档
```

---

## 🔧 核心功能

### 1. AI 模型调用
- ✅ 智谱 AI 集成（glm-4-flash）
- ✅ 流式输出支持
- ✅ 多模型 fallback

### 2. 技能系统
| 技能 | 功能 | 状态 |
|------|------|------|
| `file_read` | 读取文件 | ✅ |
| `file_write` | 写入文件 | ✅ |
| `file_edit` | 编辑文件 | ✅ |
| `bash` | 执行命令 | ✅ |
| `grep` | 搜索文本 | ✅ |
| `glob` | 文件匹配 | ✅ |

### 3. 记忆系统
- ✅ MEMORY.md 读写
- ✅ 每日日志自动创建
- ✅ 语义搜索

### 4. 消息通道
- ✅ 飞书集成（待配置）
- ✅ QQ 集成（待配置）

### 5. REST API
- 86 个端点（完整列表见 API 文档）

---

## 📝 配置说明

### 必要配置

```yaml
# 智谱 AI API Key（必须）
jclaw:
  ai:
    zhipu:
      api-key: sk-xxx
```

### 可选配置

```yaml
# 数据库（用于持久化）
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/jclaw
    username: jclaw
    password: xxx

# Redis（用于缓存）
spring:
  redis:
    host: localhost
    port: 6379

# 飞书通道
jclaw:
  channels:
    feishu:
      enabled: true
      app-id: xxx
      app-secret: xxx
```

---

## 🛠️ 开发指南

### 添加新技能

1. 创建技能类：
```java
@Service
public class MySkill implements Skill {
    @Override
    public String getName() { return "my_skill"; }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        // 实现技能逻辑
    }
}
```

2. 技能自动注册（通过 `@Service` 注解）

### 调用 AI

```java
@Service
public class MyService {
    @Autowired
    private AiService aiService;
    
    public void doSomething() {
        String response = aiService.chat("请帮我...");
    }
}
```

---

## 📊 监控

### 健康检查

```bash
curl http://localhost:18790/actuator/health
```

### 查看日志

```bash
tail -f logs/jclaw.log
```

### 性能监控

```bash
curl http://localhost:18790/actuator/metrics
```

---

## ❓ 常见问题

### Q: API Key 在哪里获取？
A: 访问 [智谱 AI 开放平台](https://open.bigmodel.cn/) 注册并创建 API Key

### Q: 如何启用飞书消息？
A: 在配置文件中设置 `jclaw.channels.feishu.enabled: true` 并填写 App ID 和 Secret

### Q: 日志在哪里？
A: `logs/jclaw.log`

---

## 📄 License

MIT License

---

**版本**: v4.0.0  
**更新日期**: 2026-04-13  
**作者**: JClaw Team
