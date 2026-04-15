# Discord 通道集成指南

> 将 JClaw 接入 Discord，实现消息收发

## 前置要求

- Discord 账号
- Discord 服务器（Guild）
- JClaw 1.0.0+

## 步骤 1：创建 Discord Bot

### 1.1 访问 Discord Developer Portal

打开 https://discord.com/developers/applications

### 1.2 创建新应用

点击"New Application"，输入应用名称。

### 1.3 创建 Bot

1. 左侧菜单选择"Bot"
2. 点击"Add Bot"
3. 确认"Add Bot"

### 1.4 获取 Token

在"Token"区域点击"Reset Token"，复制 Token。

**⚠️ 重要**：妥善保管 Token，不要泄露！

### 1.5 配置权限

在"OAuth2" → "URL Generator" 中：

1. Scopes 选择：`bot`
2. Bot Permissions 选择：
   - Send Messages
   - Embed Links
   - Attach Files
   - Read Message History
   - Add Reactions

3. 复制生成的 URL

### 1.6 邀请 Bot 到服务器

在浏览器中打开上一步的 URL，选择服务器并授权。

## 步骤 2：配置 JClaw

### 2.1 获取服务器 ID

在 Discord 中：
1. 设置 → 高级模式 → 开启"开发者模式"
2. 右键服务器 → 复制服务器 ID

### 2.2 创建配置文件

创建 `~/.jclaw/channels/discord.yml`：

```yaml
discord:
  enabled: true
  botToken: "YOUR_BOT_TOKEN"
  guildId: "YOUR_GUILD_ID"
```

### 2.3 重启 JClaw

```bash
jclaw restart
```

## 步骤 3：获取频道 ID

### 3.1 获取文本频道 ID

在 Discord 中右键文本频道 → 复制频道 ID。

### 3.2 验证配置

查看 JClaw 日志：
```bash
tail -f ~/.jclaw/logs/jclaw.log | grep "Discord"
```

应该看到：
```
Discord 通道已启用并开始接收消息
```

## 步骤 4：发送消息

### 4.1 使用 CLI

```bash
# 发送消息
jclaw channel discord send --channel <CHANNEL_ID> --message "Hello from JClaw!"

# 发送文件
jclaw channel discord send-file --channel <CHANNEL_ID> --file /path/to/file.pdf

# 发送 Embed 消息
jclaw channel discord send-embed --channel <CHANNEL_ID> --title "标题" --description "描述"
```

### 4.2 使用 API

```java
import com.jclaw.channel.ChannelService;

@Autowired
private ChannelService channelService;

// 发送消息
channelService.send("discord", channelId, "Hello from JClaw!");

// 发送文件
channelService.sendFile("discord", channelId, "/path/to/file.pdf", "文件说明");
```

### 4.3 发送 Embed 消息

```java
DiscordChannelAdapter adapter = context.getBean(DiscordChannelAdapter.class);

Map<String, Object> embed = new HashMap<>();
embed.put("title", "代码审查报告");
embed.put("description", "发现 3 个问题，5 个建议");
embed.put("color", 0x00AA00);  // 绿色
embed.put("url", "https://example.com/report");

Map<String, Object> footer = Map.of(
    "text", "JClaw Code Review",
    "icon_url", "https://example.com/icon.png"
);
embed.put("footer", footer);

adapter.sendEmbed(channelId, embed);
```

## 高级功能

### 内联组件（按钮）

```java
DiscordChannelAdapter adapter = context.getBean(DiscordChannelAdapter.class);

// 构建按钮组件
List<Map<String, Object>> components = new ArrayList<>();

Map<String, Object> actionRow = new HashMap<>();
actionRow.put("type", 1);  // Action Row

List<Map<String, Object>> buttons = new ArrayList<>();

// 确认按钮
Map<String, Object> confirmButton = Map.of(
    "type", 2,  // Button
    "style", 3, // Success (green)
    "label", "✅ 确认",
    "custom_id", "confirm"
);

// 取消按钮
Map<String, Object> cancelButton = Map.of(
    "type", 2,
    "style", 4, // Danger (red)
    "label", "❌ 取消",
    "custom_id", "cancel"
);

buttons.add(confirmButton);
buttons.add(cancelButton);
actionRow.put("components", buttons);
components.add(actionRow);

adapter.sendMessageWithComponents(channelId, "请选择操作：", components);
```

### @提及用户

```java
String userId = "123456789";
String message = "你好 <@!" + userId + ">，代码审查已完成！";
channelService.send("discord", channelId, message);
```

### 富文本格式

Discord 支持 Markdown：

```java
String message = """
    **代码分析报告**
    
    ```java
    public class Hello {
        public static void main(String[] args) {
            System.out.println("Hello World!");
        }
    }
    ```
    
    - ✅ 代码质量：良好
    - ⚠️ 安全问题：1 个
    - 💡 优化建议：3 条
    
    [查看详细报告](https://example.com)
    """;

channelService.send("discord", channelId, message);
```

## 故障排除

### 问题 1：Bot 无法接收消息

**检查项**：
- Bot 是否在服务器中
- Bot 是否有读取消息权限
- Token 是否正确

### 问题 2：消息发送失败

**检查项**：
- 频道 ID 是否正确
- Bot 是否有发送消息权限
- 网络连接是否正常

### 问题 3：Embed 显示异常

**检查项**：
- Embed 字段是否符合规范
- 颜色值是否为整数（0xRRGGBB）
- URL 是否有效

## 最佳实践

1. **Token 安全**：使用环境变量存储 Token
2. **权限最小化**：只申请必要的权限
3. **错误处理**：捕获并记录 API 异常
4. **速率限制**：Discord 有 50 条/秒 的限制
5. **消息格式**：使用 Embed 提升可读性

## 资源

- [Discord Developer 文档](https://discord.com/developers/docs)
- [Discord API 参考](https://discord.com/developers/docs/intro)
- [JClaw 通道文档](../../api/channels/discord.md)

---

*最后更新：2026-04-15*
