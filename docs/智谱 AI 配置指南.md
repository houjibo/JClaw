# 🤖 智谱 AI 配置指南

**日期**: 2026-04-05  
**模型**: glm-4-flash  
**提供商**: 智谱 AI（Zhipu AI）

---

## 📋 目录

1. [获取 API Key](#获取-api-key)
2. [配置方式](#配置方式)
3. [使用示例](#使用示例)
4. [模型对比](#模型对比)
5. [故障排查](#故障排查)

---

## 🔑 获取 API Key

### 步骤

1. **访问智谱 AI 开放平台**
   - 网址：https://open.bigmodel.cn/
   - 注册/登录账号

2. **创建 API Key**
   - 进入控制台：https://open.bigmodel.cn/userapi/project
   - 创建新项目或选择现有项目
   - 点击"生成 API Key"
   - 复制并保存 API Key（只显示一次）

3. **查看额度**
   - 新用户赠送免费额度
   - glm-4-flash 价格：约 ¥0.005/1K tokens
   - 查看余额：https://open.bigmodel.cn/userapi/balance

---

## ⚙️ 配置方式

### 方式 1：环境变量（推荐）

```bash
# Linux/Mac
export ZHIPU_API_KEY=your_api_key_here

# Windows (PowerShell)
$env:ZHIPU_API_KEY="your_api_key_here"

# Windows (CMD)
set ZHIPU_API_KEY=your_api_key_here
```

### 方式 2：application.yml 配置

编辑 `src/main/resources/application.yml`：

```yaml
ai:
  provider: zhipu  # 使用智谱 AI
  zhipu:
    api:
      key: sk-xxxxxxxxxxxxxxxx  # 替换为你的 API Key
      url: https://open.bigmodel.cn/api/paas/v4/chat/completions
    model: glm-4-flash
    temperature: 0.7
    max-tokens: 2000
```

### 方式 3：启动参数

```bash
java -jar jclaw.jar \
  --ai.provider=zhipu \
  --ai.zhipu.api.key=sk-xxxxxxxxxxxxxxxx \
  --ai.zhipu.model=glm-4-flash
```

---

## 📝 使用示例

### 1. AI 意图识别

```java
@Autowired
private AiIntentRecognitionService aiIntentRecognitionService;

// 识别意图
String userInput = "我想创建一个用户管理系统，支持注册、登录和权限管理";
Intent intent = aiIntentRecognitionService.recognizeWithAI(userInput);

System.out.println("意图名称：" + intent.getName());
System.out.println("意图类型：" + intent.getType());  // feature
System.out.println("优先级：" + intent.getPriority());
```

### 2. AI 任务分解

```java
@Autowired
private AiTaskDecompositionService aiTaskDecompositionService;

Intent intent = Intent.builder()
    .name("电商订单系统")
    .description("实现一个完整的电商订单系统")
    .type("feature")
    .build();

// 分解任务
List<Map<String, Object>> tasks = aiTaskDecompositionService.decomposeWithAI(intent);

tasks.forEach(task -> {
    System.out.println("任务：" + task.get("title"));
    System.out.println("类型：" + task.get("type"));
    System.out.println("执行者：" + task.get("agent"));
    System.out.println("优先级：" + task.get("priority"));
});
```

### 3. AI 复杂度评估

```java
Map<String, Object> task = Map.of(
    "title", "实现分布式事务",
    "description", "使用 Seata 实现分布式事务管理",
    "type", "coding"
);

int complexity = aiTaskDecompositionService.estimateComplexityWithAI(task);
System.out.println("复杂度评分：" + complexity + "/10");
```

---

## 📊 模型对比

### glm-4-flash（推荐）

| 特性 | 说明 |
|------|------|
| **速度** | ⚡⚡⚡⚡⚡ 极快 |
| **准确性** | ⭐⭐⭐⭐ 高 |
| **价格** | 💰💰 便宜（¥0.005/1K tokens） |
| **适用场景** | 日常任务、快速响应 |

### glm-4

| 特性 | 说明 |
|------|------|
| **速度** | ⚡⚡⚡ 中等 |
| **准确性** | ⭐⭐⭐⭐⭐ 极高 |
| **价格** | 💰💰💰 中等（¥0.02/1K tokens） |
| **适用场景** | 复杂任务、高精度需求 |

### glm-4-flash vs Qwen-plus

| 指标 | glm-4-flash | Qwen-plus |
|------|-------------|-----------|
| 速度 | 更快 | 快 |
| 价格 | 更便宜 | 便宜 |
| 中文理解 | 优秀 | 优秀 |
| 代码能力 | 强 | 强 |

---

## 🔧 故障排查

### 问题 1：API Key 无效

**错误**: `401 Unauthorized`

**解决**:
1. 检查 API Key 是否正确复制
2. 确认 API Key 未过期
3. 确认项目有足够额度

### 问题 2：网络超时

**错误**: `ConnectTimeout` / `ReadTimeout`

**解决**:
```yaml
# 增加超时配置
ai:
  zhipu:
    timeout: 30000  # 30 秒
```

### 问题 3：额度不足

**错误**: `402 Payment Required`

**解决**:
1. 访问 https://open.bigmodel.cn/userapi/balance
2. 充值或等待下月额度

### 问题 4：模型不可用

**错误**: `400 Bad Request - model not found`

**解决**:
```yaml
# 切换模型
ai:
  zhipu:
    model: glm-4  # 或 glm-3-turbo
```

---

## 📈 性能优化

### 1. 缓存 AI 响应

```yaml
jclaw:
  cache-enabled: true
  cache-expire-minutes: 10
```

### 2. 降低 temperature

```yaml
ai:
  zhipu:
    temperature: 0.3  # 更稳定，更少随机性
```

### 3. 限制 max-tokens

```yaml
ai:
  zhipu:
    max-tokens: 1000  # 减少输出长度
```

---

## 🔗 相关资源

- **智谱 AI 官网**: https://www.zhipuai.cn/
- **开放平台**: https://open.bigmodel.cn/
- **API 文档**: https://open.bigmodel.cn/dev/api
- **模型介绍**: https://open.bigmodel.cn/dev/howuse/model
- **价格说明**: https://open.bigmodel.cn/dev/howuse/price

---

## 🎯 最佳实践

1. **使用环境变量存储 API Key** - 避免硬编码
2. **设置合理的超时时间** - 建议 30 秒
3. **实现降级方案** - AI 失败时使用规则匹配
4. **监控 API 调用** - 记录日志和错误
5. **定期清理缓存** - 避免缓存过期数据

---

*文档生成时间：2026-04-05 23:25*  
*生成者：可乐 🥤*  
*JClaw 项目 - Java 编码智能体*
