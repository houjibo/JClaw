# 常见问题 (FAQ)

> JClaw 使用常见问题解答

---

## 一般问题

### Q: JClaw 是什么？

A: JClaw 是一个 AI 编码助手，基于智谱 GLM 4.7 模型，提供代码理解、生成、优化、测试等能力。

### Q: JClaw 免费吗？

A: JClaw 本身开源免费，但使用智谱 AI 需要 API Key (有免费额度)。

### Q: 支持哪些操作系统？

A: 支持 Windows、macOS、Linux (需要 Java 17+)。

### Q: 需要联网吗？

A: 是的，需要联网调用智谱 AI API。

---

## 安装配置

### Q: 安装失败怎么办？

A: 检查以下几点：
1. Java 版本 >= 17 (`java -version`)
2. Maven 版本 >= 3.6 (`mvn -version`)
3. 网络连接正常
4. 查看完整错误日志

### Q: API Key 在哪里获取？

A: 访问 https://open.bigmodel.cn 注册账号后在 API 控制台创建。

### Q: API Key 安全吗？

A: 建议使用环境变量存储，不要提交到代码仓库：
```bash
export JCLAW_API_KEY="your_key"
```

### Q: 可以更换 AI 模型吗？

A: 当前支持智谱 GLM 4.7，后续会支持更多模型。

---

## 使用问题

### Q: 响应速度慢怎么办？

A: 可能原因：
1. 网络延迟 - 检查网络连接
2. AI 模型繁忙 - 稍后重试
3. 任务复杂 - 分解为小任务

### Q: 代码生成不准确怎么办？

A: 尝试：
1. 提供更详细的上下文
2. 分解复杂任务
3. 使用澄清模式
4. 人工审查生成的代码

### Q: 如何处理大文件？

A: JClaw 支持大文件，但建议：
1. 分块处理 (>10MB 文件)
2. 使用 grep 搜索特定内容
3. 指定行号范围读取

### Q: 可以处理私有仓库吗？

A: 可以，配置 GitHub Token 后支持私有仓库访问。

---

## 通道集成

### Q: Telegram Bot 无法接收消息？

A: 检查：
1. Bot Token 是否正确
2. 是否已启动 Bot (点击 Start)
3. 网络连接是否正常

### Q: Discord Bot 无法发送消息？

A: 检查：
1. Bot 是否在服务器中
2. Bot 权限是否足够
3. 频道 ID 是否正确

### Q: 文件上传失败？

A: 检查文件大小限制：
- Telegram: <50MB
- Discord: <25MB
- Slack: <100MB

---

## 技能系统

### Q: 如何开发自定义技能？

A: 参考 [技能开发指南](../guides/skills/develop.md)

### Q: 技能安装失败？

A: 检查：
1. 技能包是否完整 (skill.json)
2. 依赖是否满足
3. 版本是否兼容

### Q: 技能市场有哪些技能？

A: 使用 `skill search` 命令搜索，或访问技能市场 (待上线)。

---

## 性能问题

### Q: 内存占用高怎么办？

A: 调整 JVM 参数：
```bash
java -Xmx1g -Xms512m -jar jclaw-1.0.0.jar
```

### Q: 启动慢怎么办？

A: 正常启动时间约 0.8-1.5 秒，如过慢检查：
1. 磁盘性能
2. 网络延迟
3. 是否加载大量技能

### Q: 并发性能如何？

A: 基准测试显示支持 100+ 并发，实际性能取决于硬件配置。

---

## 错误处理

### Q: 遇到 500 错误怎么办？

A: 
1. 查看日志文件 (`~/.jclaw/logs/`)
2. 检查 API Key 是否有效
3. 重试请求
4. 如持续报错，提交 Issue

### Q: 网络超时怎么办？

A:
1. 检查网络连接
2. 增加超时配置
3. 使用代理 (如需要)

### Q: 如何查看日志？

A:
```bash
# 实时查看日志
tail -f ~/.jclaw/logs/jclaw.log

# 搜索错误
grep "ERROR" ~/.jclaw/logs/jclaw.log
```

---

## 贡献支持

### Q: 如何贡献代码？

A:
1. Fork 项目
2. 创建功能分支
3. 提交 PR
4. 参考 [贡献指南](https://github.com/houjibo/JClaw/blob/main/CONTRIBUTING.md)

### Q: 如何报告 Bug？

A: 在 GitHub Issues 提交：https://github.com/houjibo/JClaw/issues

### Q: 有社区吗？

A: 
- GitHub Discussions: https://github.com/houjibo/JClaw/discussions
- Discord: (待创建)
- 飞书群：(待创建)

---

## 其他问题

没有找到答案？欢迎提交 Issue 或在社区提问。

---

*最后更新：2026-04-15*
