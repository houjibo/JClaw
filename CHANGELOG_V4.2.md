# JClaw v4.2 更新说明

**日期**: 2026-04-13  
**版本**: v4.2  
**主题**: 消息通道集成

---

## 🎉 新增功能

### 1. 飞书消息通道

#### 配置
```yaml
jclaw:
  channels:
    feishu:
      enabled: true
      app-id: your-app-id
      app-secret: your-app-secret
      verification-token: your-verification-token
```

#### 飞书回调配置
- 回调地址：`http://your-domain/api/channels/feishu/callback`
- 事件类型：接收消息

#### 可用命令
```bash
/help              # 查看帮助
/skills            # 列出技能
/bash <命令>       # 执行 Bash 命令
/search <关键词>   # 网络搜索
/todo create <内容> # 创建 TODO
/git status        # Git 状态
```

#### 直接使用
直接发送文字消息，JClaw 会自动调用 AI 回复。

---

### 2. REST API 增强

#### 通道管理 API
```bash
# 列出所有通道
GET /api/channels

# 发送消息
POST /api/channels/send
{
  "channel": "feishu",
  "receiverId": "oc_xxx",
  "content": "Hello"
}

# 飞书回调
POST /api/channels/feishu/callback
```

---

## 📊 功能对比

| 功能 | v4.1 | v4.2 | 改进 |
|------|------|------|------|
| **消息通道** | ❌ | ✅ 飞书 | 重大 |
| **命令解析** | ❌ | ✅ 内置 | 重大 |
| **AI 对话** | ✅ API | ✅ 集成通道 | 中等 |
| **技能执行** | ✅ API | ✅ 集成通道 | 中等 |

---

## 🧪 使用示例

### 1. 飞书群聊使用

在飞书群中添加 JClaw 机器人，然后：

```
用户：/help
JClaw: 显示帮助信息

用户：/bash ls -la
JClaw: 执行命令并返回结果

用户：今天天气怎么样？
JClaw: 调用 AI 回答
```

### 2. API 调用

```bash
# 发送消息
curl -X POST http://localhost:18790/api/channels/send \
  -H "Content-Type: application/json" \
  -d '{
    "channel": "feishu",
    "receiverId": "oc_xxx",
    "content": "Hello from API"
  }'
```

---

## 🔧 配置说明

### 飞书应用创建

1. 访问 [飞书开放平台](https://open.feishu.cn/)
2. 创建企业自建应用
3. 获取 App ID 和 App Secret
4. 配置事件订阅（接收消息）
5. 配置机器人（发送消息）

### 环境变量

```bash
export FEISHU_APP_ID=cli_xxx
export FEISHU_APP_SECRET=xxx
export FEISHU_VERIFICATION_TOKEN=xxx
```

---

## 📝 下一步

### v4.3 计划
- [ ] QQ 通道集成
- [ ] 更多技能（web_fetch, file_edit）
- [ ] 语义搜索（向量数据库）
- [ ] 记忆萃取

### v5.0 计划
- [ ] 多 Agent 协作
- [ ] 容器化部署
- [ ] CI/CD 流水线

---

**版本**: v4.2  
**提交**: `a35b0b1`  
**GitHub**: https://github.com/houjibo/JClaw
