# JClaw 多 Agent 协作示例

展示如何使用 JClaw 的多 Agent 协作功能。

## 架构概述

```
┌─────────────────┐
│  Coordinator    │
│  (协调器)       │
└────────┬────────┘
         │
    ┌────┴────┬───────────┬──────────┐
    │         │           │          │
┌───▼───┐ ┌──▼────┐ ┌───▼────┐ ┌──▼──────┐
│  PM   │ │Arch   │ │  Dev   │ │  QA     │
│ Agent │ │ Agent │ │ Agent  │ │  Agent  │
└───────┘ └───────┘ └────────┘ └─────────┘
```

## 快速开始

### 1. 创建协调器

```java
import com.jclaw.coordinator.ArmyCoordinator;
import com.jclaw.agent.AgentConfig;

// 创建协调器
ArmyCoordinator coordinator = new ArmyCoordinator();

// 配置 Agent 角色
coordinator.addRole("pm", AgentConfig.builder()
    .name("产品经理")
    .model("glm-4.7")
    .systemPrompt("你是一名资深产品经理，负责需求分析和产品设计...")
    .build());

coordinator.addRole("architect", AgentConfig.builder()
    .name("架构师")
    .model("glm-4.7")
    .systemPrompt("你是一名技术架构师，负责技术选型和架构设计...")
    .build());

coordinator.addRole("developer", AgentConfig.builder()
    .name("开发工程师")
    .model("glm-4.7")
    .systemPrompt("你是一名资深开发工程师，负责代码实现...")
    .build());

coordinator.addRole("qa", AgentConfig.builder()
    .name("测试工程师")
    .model("glm-4.7")
    .systemPrompt("你是一名测试工程师，负责质量保证...")
    .build());
```

### 2. 启动协作任务

```java
// 定义任务
String task = "开发一个用户管理系统，包括注册、登录、个人信息管理功能";

// 启动协作
var result = coordinator.execute(task, Map.of(
    "project", "user-management",
    "deadline", "2026-04-30"
));

System.out.println(result);
```

### 3. 查看协作过程

```java
// 获取任务状态
var status = coordinator.getTaskStatus(taskId);
System.out.println(status);

// 获取对话历史
var history = coordinator.getConversationHistory(taskId);
System.out.println(history);
```

## 协作模式

### 顺序模式

Agent 按顺序执行任务：

```java
coordinator.setMode(CoordinatorMode.SEQUENTIAL);
// PM → Architect → Developer → QA
```

### 并行模式

Agent 并行执行任务：

```java
coordinator.setMode(CoordinatorMode.PARALLEL);
// 所有 Agent 同时工作
```

### 讨论模式

Agent 之间可以互相讨论：

```java
coordinator.setMode(CoordinatorMode.DISCUSSION);
// Agent 可以互相提问、回答、辩论
```

## 实际示例

### 示例 1: 需求分析

```java
var result = coordinator.execute("""
    分析以下需求并输出产品文档：
    
    需求：开发一个在线书店系统
    - 用户可以浏览书籍
    - 用户可以购买书籍
    - 管理员可以管理书籍库存
    - 支持多种支付方式
    
    请输出：
    1. 用户故事
    2. 功能列表
    3. 技术建议
    """);
```

### 示例 2: 代码审查

```java
var result = coordinator.execute("""
    审查以下代码：
    
    [代码内容]
    
    请从以下角度审查：
    1. 代码质量
    2. 安全性
    3. 性能
    4. 可维护性
    
    输出审查报告和改进建议。
    """);
```

### 示例 3: 问题调试

```java
var result = coordinator.execute("""
    调试以下问题：
    
    问题描述：API 响应时间超过 5 秒
    环境：生产环境，1000 QPS
    日志：[日志内容]
    
    请分析原因并提供解决方案。
    """);
```

## 高级功能

### 人类评审

```java
// 在关键节点请求人类评审
coordinator.setHumanReview(true);
coordinator.addReviewer("houjibo@example.com");
```

### 自定义工作流

```java
// 定义自定义工作流
coordinator.defineWorkflow("custom", List.of(
    "pm:需求分析",
    "architect:技术设计",
    "developer:实现",
    "qa:测试",
    "pm:验收"
));
```

### 结果导出

```java
// 导出协作报告
var report = coordinator.exportReport(taskId, ReportFormat.MARKDOWN);
Files.writeString("report.md", report);
```

## 最佳实践

1. **明确角色**：每个 Agent 的职责要清晰
2. **充分上下文**：提供足够的背景信息
3. **合理分工**：根据任务特点选择协作模式
4. **及时干预**：必要时人工介入
5. **结果验证**：验证输出质量

## 资源

- [协调器 API 文档](https://docs.jclaw.dev/coordinator)
- [Agent 配置指南](https://docs.jclaw.dev/agents/config)
- [协作模式详解](https://docs.jclaw.dev/coordinator/modes)
