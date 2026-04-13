# 🎯 JClaw 核心功能实现计划

**日期**: 2026-04-13  
**目标**: 让 JClaw 真正可以使用  
**对标**: Claude Code + OpenClaw 核心能力

---

## 📋 核心功能清单（P0 - 必须实现）

### 1. 启动入口（4 小时）
**对标**: `openclaw gateway start`

**实现内容**:
- [ ] `JClawApplication.java` - Spring Boot 启动类
- [ ] `jclaw` 启动脚本（bash）
- [ ] `application.yml` 配置示例
- [ ] `.env.example` 环境变量示例
- [ ] `README.md` 快速开始指南

**验收标准**:
```bash
# 用户可以这样启动
./jclaw start
# 或者
java -jar jclaw.jar --config application.yml
```

---

### 2. 模型调用集成（4 小时）
**对标**: Claude Code 的模型调用

**实现内容**:
- [ ] `AiService` 实现（智谱 AI 集成）
- [ ] 流式输出支持
- [ ] 多模型 fallback
- [ ] Token 计数和限流

**验收标准**:
```java
// 可以调用 AI
String response = aiService.chat("你好");
System.out.println(response);
```

---

### 3. 技能系统（16 小时）
**对标**: Claude Code 的 43 个工具

**核心技能**（必须实现）:
| 技能 | 功能 | 优先级 |
|------|------|--------|
| `file_read` | 读取文件 | P0 |
| `file_write` | 写入文件 | P0 |
| `file_edit` | 编辑文件 | P0 |
| `bash` | 执行命令 | P0 |
| `grep` | 搜索文本 | P0 |
| `glob` | 文件匹配 | P0 |
| `web_search` | 网络搜索 | P1 |
| `web_fetch` | 抓取网页 | P1 |
| `todo_write` | 任务管理 | P1 |
| `git` | Git 操作 | P1 |

**实现内容**:
- [ ] `Skill` 接口定义
- [ ] 技能注册机制
- [ ] 技能执行引擎
- [ ] 技能权限控制
- [ ] 10 个核心技能实现

**验收标准**:
```java
// 可以执行技能
SkillResult result = skillEngine.execute("file_read", params);
```

---

### 4. 消息通道集成（8 小时）
**对标**: OpenClaw 的多通道

**实现内容**:
- [ ] 飞书通道完整集成
  - [ ] 消息接收（回调处理）
  - [ ] 消息发送
  - [ ] 卡片消息支持
- [ ] 消息路由器
- [ ] 命令解析器

**验收标准**:
```java
// 可以收发消息
channel.send("ou_xxx", "Hello");
channel.onMessage(message -> handle(message));
```

---

### 5. 记忆系统（8 小时）
**对标**: OpenClaw 的记忆系统

**实现内容**:
- [ ] `MEMORY.md` 读写
- [ ] 每日日志自动创建
- [ ] 记忆搜索（语义搜索）
- [ ] 记忆萃取（从对话中提取）

**验收标准**:
```java
// 可以记住上下文
memory.save("用户喜欢喝可乐");
String info = memory.search("用户喜好");
```

---

### 6. 任务调度（4 小时）
**对标**: OpenClaw 的 cron

**实现内容**:
- [ ] Cron 表达式解析
- [ ] 定时任务执行
- [ ] 任务管理 API

**验收标准**:
```java
// 可以设置定时任务
cron.schedule("0 9 * * *", () -> sendDailyReport());
```

---

## 📅 执行计划

### 第 1 天（04-13）
- [x] 批判性分析
- [ ] 启动入口
- [ ] 模型调用集成

### 第 2 天（04-14）
- [ ] 技能系统框架
- [ ] 3 个核心技能（file_read, file_write, bash）

### 第 3 天（04-15）
- [ ] 技能系统完善（7 个技能）
- [ ] 消息通道集成

### 第 4 天（04-16）
- [ ] 记忆系统
- [ ] 任务调度
- [ ] 集成测试

### 第 5 天（04-17）
- [ ] 文档编写
- [ ] 示例配置
- [ ] 发布 v4.0

---

## 🎯 验收标准

**JClaw v4.0 必须能够**:
1. ✅ 一键启动
2. ✅ 接收飞书消息
3. ✅ 调用 AI 模型
4. ✅ 执行技能（读写文件、执行命令）
5. ✅ 记住对话上下文
6. ✅ 回复消息

**不做**:
- ❌ 新增 REST API（除非必要）
- ❌ 新增前端页面（除非必要）
- ❌ 花哨但不实用的功能

---

**执行者**: 可乐 🥤  
**监督**: 波哥
