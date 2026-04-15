# Telegram 通道集成指南

> 将 JClaw 接入 Telegram，实现消息收发

## 前置要求

- Telegram 账号
- JClaw 1.0.0+

## 步骤 1：创建 Bot

### 1.1 联系 BotFather

在 Telegram 中搜索 `@BotFather` 并启动对话。

### 1.2 创建新 Bot

发送命令：
```
/newbot
```

按提示输入：
1. Bot 名称（显示名称）
2. Bot 用户名（必须以 `bot` 结尾）

### 1.3 获取 Token

创建成功后，BotFather 会返回 Token：
```
Use this token to access the HTTP API:
1234567890:ABCdefGHIjklMNOpqrsTUVwxyz
```

**⚠️ 重要**：妥善保管 Token，不要泄露！

## 步骤 2：配置 JClaw

### 2.1 创建配置文件

创建 `~/.jclaw/channels/telegram.yml`：

```yaml
telegram:
  enabled: true
  botToken: "1234567890:ABCdefGHIjklMNOpqrsTUVwxyz"
  webhookUrl: ""  # 可选，使用长轮询时留空
```

### 2.2 重启 JClaw

```bash
jclaw restart
```

## 步骤 3：获取 Chat ID

### 3.1 启动 Bot

在 Telegram 中搜索你的 Bot 用户名，点击"Start"。

### 3.2 获取 Chat ID

发送任意消息给 Bot，然后查看 JClaw 日志：

```bash
tail -f ~/.jclaw/logs/jclaw.log | grep "收到 Telegram 消息"
```

日志中会显示：
```
收到 Telegram 消息：your_username - 你好
```

或者访问：
```
https://api.telegram.org/bot<YOUR_TOKEN>/getUpdates
```

返回结果中的 `chat.id` 即为 Chat ID。

## 步骤 4：发送消息

### 4.1 使用 CLI

```bash
# 发送消息
jclaw channel telegram send --chat <CHAT_ID> --message "Hello from JClaw!"

# 发送文件
jclaw channel telegram send-file --chat <CHAT_ID> --file /path/to/file.pdf --caption "文件说明"
```

### 4.2 使用 API

```java
import com.jclaw.channel.ChannelService;

@Autowired
private ChannelService channelService;

// 发送消息
channelService.send("telegram", chatId, "Hello from JClaw!");

// 发送文件
channelService.sendFile("telegram", chatId, "/path/to/file.pdf", "文件说明");
```

### 4.3 在技能中使用

```java
// 在技能中发送通知
Map<String, Object> result = jclaw.execute("分析代码");
channelService.send("telegram", chatId, "代码分析完成：" + result.get("summary"));
```

## 高级功能

### 内联键盘

```java
TelegramChannelAdapter adapter = context.getBean(TelegramChannelAdapter.class);

Map<String, String> buttons = Map.of(
    "✅ 确认", "confirm",
    "❌ 取消", "cancel",
    "ℹ️ 详情", "details"
);

adapter.sendMessageWithKeyboard(chatId, "请选择操作：", buttons);
```

### Markdown 支持

Telegram 支持 Markdown 格式：

```java
String message = """
    **代码分析报告**
    
    - 文件：`Main.java`
    - 问题：3 个
    - 建议：5 条
    
    [查看详情](https://example.com)
    """;

channelService.send("telegram", chatId, message);
```

### 长消息处理

Telegram 消息长度限制为 4096 字符，超长需要分段：

```java
public void sendLongMessage(String chatId, String content) {
    int maxLength = 4000;
    
    for (int i = 0; i < content.length(); i += maxLength) {
        String chunk = content.substring(i, Math.min(i + maxLength, content.length()));
        channelService.send("telegram", chatId, chunk);
    }
}
```

## 故障排除

### 问题 1：Bot 无法接收消息

**检查项**：
- Bot 是否已启动（在 Telegram 中点击"Start"）
- Token 是否正确
- 网络连接是否正常

### 问题 2：消息发送失败

**检查项**：
- Chat ID 是否正确
- Bot 是否有发送权限
- 是否触达速率限制

### 问题 3：文件发送失败

**检查项**：
- 文件大小是否 < 50MB
- 文件路径是否正确
- 文件格式是否支持

## 最佳实践

1. **Token 安全**：不要将 Token 提交到代码仓库
2. **错误处理**：捕获并记录发送异常
3. **速率限制**：避免短时间内发送大量消息
4. **用户隐私**：不要泄露用户 Chat ID
5. **消息格式**：使用 Markdown 提升可读性

## 资源

- [Telegram Bot API 文档](https://core.telegram.org/bots/api)
- [Telegram Bot 示例](https://core.telegram.org/bots/samples)
- [JClaw 通道文档](../../api/channels/telegram.md)

---

*最后更新：2026-04-15*
