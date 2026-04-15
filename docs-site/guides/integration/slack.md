# Slack 集成指南

> 将 JClaw 接入 Slack，实现团队通知和协作

## 前置要求

- Slack 工作空间
- JClaw 1.0.0+

## 步骤 1：创建 Slack App

### 1.1 访问 Slack API

打开 https://api.slack.com/apps

### 1.2 创建新 App

1. 点击"Create New App"
2. 选择"From scratch"
3. 填写 App 名称（如"JClaw Bot"）
4. 选择工作空间
5. 点击"Create App"

### 1.3 配置权限

1. 左侧菜单选择"OAuth & Permissions"
2. 在"Scopes" → "Bot Token Scopes"中添加：
   - `chat:write` - 发送消息
   - `files:write` - 上传文件
   - `channels:read` - 读取频道
   - `users:read` - 读取用户
   - `conversations:write` - 管理频道

3. 点击"Install to Workspace"
4. 授权 App
5. **复制 Bot User OAuth Token**（格式：`xoxb-...`）

### 1.4 邀请 Bot 到频道

在 Slack 中：
```
/invite @JClaw Bot
```

## 步骤 2：配置 JClaw

### 2.1 创建配置文件

创建 `~/.jclaw/integrations/slack.yml`：

```yaml
slack:
  enabled: true
  token: "${SLACK_BOT_TOKEN}"  # 从环境变量读取
  defaultChannel: "C0123456789"  # 默认频道 ID（可选）
```

### 2.2 重启 JClaw

```bash
jclaw restart
```

## 步骤 3：使用 Slack 集成

### 3.1 发送消息

```bash
# 发送消息到频道
jclaw slack send --channel C0123456789 --message "Hello from JClaw!"

# 发送线程回复
jclaw slack send --channel C0123456789 --message "回复内容" --thread 1234567890.123456

# 发送文件
jclaw slack upload --channel C0123456789 --file /path/to/file.pdf --title "报告"
```

### 3.2 在技能中使用

```java
@Autowired
private SlackService slack;

// 发送消息
slack.sendMessage("C0123456789", "任务完成！");

// 发送带附件的消息
List<Map<String, Object>> attachments = List.of(Map.of(
    "color", "good",
    "title", "任务报告",
    "text", "所有测试通过",
    "fields", List.of(
        Map.of("title", "通过", "value", "100", "short", true),
        Map.of("title", "失败", "value", "0", "short", true)
    )
));

slack.sendMessageWithAttachments("C0123456789", "构建完成", attachments);
```

### 3.3 发送互动消息

```java
// 创建按钮
List<Map<String, Object>> actions = List.of(
    Map.of(
        "type", "button",
        "text", Map.of("type", "plain_text", "text", "✅ 确认"),
        "action_id", "confirm"
    ),
    Map.of(
        "type", "button",
        "text", Map.of("type", "plain_text", "text", "❌ 取消"),
        "action_id", "cancel"
    )
);

slack.sendInteractiveMessage("C0123456789", "请确认操作：", actions);
```

## 高级用法

### 构建通知机器人

```java
@Service
public class NotificationService {
    
    @Autowired
    private SlackService slack;
    
    public void notifyDeployment(String env, String status) {
        String color = "success".equals(status) ? "good" : "danger";
        
        List<Map<String, Object>> attachments = List.of(Map.of(
            "color", color,
            "title", "部署通知",
            "fields", List.of(
                Map.of("title", "环境", "value", env, "short", true),
                Map.of("title", "状态", "value", status, "short", true)
            ),
            "footer", "JClaw Deployment"
        ));
        
        slack.sendMessageWithAttachments("C0123456789", "部署完成", attachments);
    }
}
```

### 每日站会机器人

```java
@Scheduled(cron = "0 9 * * 1-5")  // 工作日 9:00
public void sendDailyStandup() {
    String text = """
        *🌞 每日站会*
        
        请大家回复：
        1. 昨天完成了什么
        2. 今天计划做什么
        3. 有什么阻碍
        
        直接在此频道回复即可！
        """;
    
    slack.sendMessage("C0123456789", text);
}
```

### 错误告警

```java
@EventListener
public void handleError(AppErrorEvent event) {
    String text = """
        *🚨 错误告警*
        
        *服务*: %s
        *错误*: %s
        *时间*: %s
        
        [查看详情](%s)
        """.formatted(
            event.getService(),
            event.getMessage(),
            event.getTimestamp(),
            event.getDetailUrl()
        );
    
    slack.sendMessage("C0123456789", text);
}
```

## 故障排除

### 问题 1：消息发送失败

**原因**：Token 无效或 Bot 不在频道中

**解决**：
1. 检查 Token 是否正确
2. 确认 Bot 已加入目标频道
3. 确认 Token 权限足够

### 问题 2：无法上传文件

**原因**：缺少 `files:write` 权限

**解决**：
1. 在 App 设置中添加 `files:write` 权限
2. 重新安装 App 到工作空间

### 问题 3：互动按钮无响应

**原因**：未配置互动端点

**解决**：
1. 在 App 设置中配置"Interactivity & Shortcuts"
2. 设置 Request URL 为你的服务器地址

## 最佳实践

1. **Token 安全**：使用环境变量存储 Token
2. **频道规范**：使用专用频道发送通知
3. **消息格式**：使用 Mrkdwn 格式化消息
4. **速率限制**：Slack API 限制 1 次/秒
5. **错误处理**：捕获并记录发送异常

## 资源

- [Slack API 文档](https://api.slack.com/)
- [Slack Block Kit](https://app.slack.com/block-kit-builder)
- [JClaw Slack 集成 API](../../api/integration/slack.md)

---

*最后更新：2026-04-15*
