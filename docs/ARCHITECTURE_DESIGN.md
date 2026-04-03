# JClaw 融合架构详细设计

> **任务**: 任务 6 - 融合架构详细设计  
> **日期**: 2026-04-03  
> **作者**: 可乐 🥤  
> **版本**: v1.0

---

## 📋 目录

1. [架构概览](#1-架构概览)
2. [分层设计](#2-分层设计)
3. [模块划分](#3-模块划分)
4. [数据流设计](#4-数据流设计)
5. [接口设计](#5-接口设计)
6. [部署架构](#6-部署架构)

---

## 1. 架构概览

### 1.1 整体架构

```
┌──────────────────────────────────────────────────────────────────┐
│                          JClaw                                    │
│              (85,500 行代码 - 融合三大系统)                        │
├──────────────────────────────────────────────────────────────────┤
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                    用户交互层                               │  │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────────────┐  │  │
│  │  │  CLI    │ │  Web UI │ │  飞书   │ │  其他通道       │  │  │
│  │  │ (已有)  │ │ (已有)  │ │ (新增)  │ │ (可扩展)        │  │  │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────────────┘  │  │
│  └────────────────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                    意图驱动引擎                            │  │
│  │  意图识别 → AI 澄清 → 任务分解 → Agent 调度 → 执行 → 追溯   │  │
│  └────────────────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                    核心服务层                              │  │
│  │  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐  │  │
│  │  │ 记忆系统  │ │ 代码追溯  │ │ 影响分析  │ │ 多通道    │  │  │
│  │  │ (新增)    │ │ (新增)    │ │ (新增)    │ │ (新增)    │  │  │
│  │  └───────────┘ └───────────┘ └───────────┘ └───────────┘  │  │
│  │  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐  │  │
│  │  │ MCP 服务  │ │ 技能系统  │ │ Agent 系统 │ │ 工具系统  │  │  │
│  │  │ (已有)    │ │ (已有)    │ │ (已有)    │ │ (已有)    │  │  │
│  │  └───────────┘ └───────────┘ └───────────┘ └───────────┘  │  │
│  └────────────────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                    数据持久层                              │  │
│  │  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐  │  │
│  │  │PostgreSQL │ │FalkorDB   │ │  Redis    │ │  文件系统 │  │  │
│  │  │ (新增)    │ │ (新增)    │ │ (新增)    │ │ (已有)    │  │  │
│  │  └───────────┘ └───────────┘ └───────────┘ └───────────┘  │  │
│  └────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘
```

### 1.2 技术栈

| 层级 | 技术 | 版本 | 用途 |
|------|------|------|------|
| **后端框架** | Spring Boot | 3.2.4 | 核心框架 |
| **关系数据库** | PostgreSQL | 16 | 业务数据 + 记忆 + 追溯 |
| **图数据库** | FalkorDB | edge | 意图图谱 + 调用链 |
| **缓存** | Redis | 7 | 工作记忆 + 会话缓存 |
| **ORM** | MyBatis-Plus | 3.5.5 | 数据访问层 |
| **前端框架** | Vue 3 | 3.4 | Web UI |
| **3D 可视化** | Three.js | r183 | 调用链图谱 |

---

## 2. 分层设计

### 2.1 用户交互层

**职责**: 接收用户输入，展示执行结果

**组件**:
- **CLI**: Picocli + JLine（已有）
- **Web UI**: Vue 3 + Vite（已有）
- **ChannelAdapter**: 统一通道接口（新增）
  - FeishuChannelAdapter（新增）
  - QQChannelAdapter（新增）

**接口设计**:
```java
public interface ChannelAdapter {
    String getName();
    void sendMessage(String target, Message message);
    void registerListener(MessageListener listener);
}
```

### 2.2 意图驱动引擎

**职责**: 意图识别 → 任务分解 → 执行调度

**组件**:
- **IntentRecognitionService**: 意图识别（新增）
- **ClarificationService**: AI 澄清（新增）
- **TaskDecompositionEngine**: 任务分解（新增）
- **AgentCoordinator**: Agent 调度（已有）

**流程**:
```
用户输入 → 意图识别 → AI 澄清 → 任务分解 → Agent 调度 → 执行 → 追溯
```

### 2.3 核心服务层

**职责**: 提供核心业务能力

**模块**:

| 模块 | 状态 | 说明 |
|------|------|------|
| **记忆系统** | 新增 | MEMORY.md + 每日日志 + 知识萃取 |
| **代码追溯** | 新增 | AST 解析 + 调用链分析 |
| **影响分析** | 新增 | 变更影响 + 风险评分 |
| **多通道** | 新增 | 飞书/QQ 集成 |
| **MCP 服务** | 已有 | 远程工具调用 |
| **技能系统** | 已有 | 8 个技能 |
| **Agent 系统** | 已有 | 多 Agent 协调 |
| **工具系统** | 已有 | 45 个工具 |

### 2.4 数据持久层

**职责**: 数据存储和访问

**数据库设计**:

```
┌─────────────────────────────────────────┐
│         数据持久层                       │
├─────────────────────────────────────────┤
│  PostgreSQL:16                          │
│  ├─ memories (记忆表)                   │
│  ├─ intent_nodes (意图节点)             │
│  ├─ intent_relations (意图关系)         │
│  ├─ code_units (代码单元)               │
│  └─ call_relationships (调用关系)       │
├─────────────────────────────────────────┤
│  FalkorDB:edge                          │
│  ├─ intent_graph (意图图谱)             │
│  └─ call_graph (调用图谱)               │
├─────────────────────────────────────────┤
│  Redis:7                                │
│  ├─ working_memory (工作记忆)           │
│  ├─ session_cache (会话缓存)            │
│  └─ api_cache (API 响应缓存)             │
├─────────────────────────────────────────┤
│  文件系统                                │
│  ├─ MEMORY.md (长期记忆)                │
│  └─ memory/YYYY-MM-DD.md (每日日志)     │
└─────────────────────────────────────────┘
```

---

## 3. 模块划分

### 3.1 记忆系统模块

**包结构**:
```
com.jclaw.memory/
├── service/
│   ├── MemoryService.java        # 记忆服务接口
│   ├── MemoryServiceImpl.java    # 记忆服务实现
│   ├── DailyLogService.java      # 每日日志服务
│   └── KnowledgeExtractor.java   # 知识萃取引擎
├── entity/
│   ├── Memory.java               # 记忆实体
│   ├── DailyLog.java             # 每日日志实体
│   └── Knowledge.java            # 知识实体
├── mapper/
│   ├── MemoryMapper.java         # 记忆 Mapper
│   └── DailyLogMapper.java       # 每日日志 Mapper
├── controller/
│   └── MemoryController.java     # 记忆 REST API
└── cache/
    └── CacheUtils.java           # 缓存工具类
```

**核心接口**:
```java
public interface MemoryService {
    Memory getMemory(String id);
    List<Memory> searchMemories(String query);
    Memory createMemory(Memory memory);
    void updateMemory(Memory memory);
    void deleteMemory(String id);
}
```

### 3.2 代码追溯模块

**包结构**:
```
com.jclaw.trace/
├── service/
│   ├── TraceService.java         # 追溯服务接口
│   ├── TraceServiceImpl.java     # 追溯服务实现
│   ├── ImpactAnalyzer.java       # 影响分析引擎
│   └── RiskScorer.java           # 风险评分器
├── ast/
│   ├── JavaAstParser.java        # Java AST 解析器
│   └── CodeUnitExtractor.java   # 代码单元提取器
├── entity/
│   ├── CodeUnit.java             # 代码单元实体
│   ├── CallRelationship.java     # 调用关系实体
│   └── ImpactAnalysis.java       # 影响分析结果
├── mapper/
│   ├── CodeUnitMapper.java       # 代码单元 Mapper
│   └── CallRelationshipMapper.java
└── controller/
    └── TraceController.java      # 追溯 REST API
```

### 3.3 多通道模块

**包结构**:
```
com.jclaw.channel/
├── adapter/
│   ├── ChannelAdapter.java       # 通道接口
│   ├── FeishuChannelAdapter.java # 飞书适配器
│   └── QQChannelAdapter.java     # QQ 适配器
├── router/
│   └── MessageRouter.java        # 消息路由器
├── entity/
│   └── Message.java              # 消息实体
└── config/
    └── ChannelConfig.java        # 通道配置
```

---

## 4. 数据流设计

### 4.1 意图驱动流程

```
用户输入
    │
    ▼
┌─────────────────┐
│  ChannelAdapter │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ IntentRecognition│
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Clarification   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ TaskDecomposition│
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ AgentCoordinator│
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Tool Execution  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Trace Recording │
└─────────────────┘
```

### 4.2 记忆系统数据流

```
写入流程:
用户对话/执行结果
    │
    ▼
┌─────────────────┐
│ MemoryService   │
└────────┬────────┘
         │
         ├──────────────┐
         │              │
         ▼              ▼
┌─────────────┐ ┌─────────────┐
│  Redis L1   │ │  文件 L2    │
│ (工作记忆)  │ │ (MEMORY.md) │
└─────────────┘ └─────────────┘

读取流程:
查询请求
    │
    ▼
┌─────────────────┐
│ MemoryService   │
└────────┬────────┘
         │
         ├──────────────┐
         │              │
         ▼              ▼
┌─────────────┐ ┌─────────────┐
│  Redis L1   │ │  文件 L2    │
│ (快速缓存)  │ │ (完整数据)  │
└─────────────┘ └─────────────┘
         │              │
         └──────┬───────┘
                │
                ▼
         合并结果返回
```

---

## 5. 接口设计

### 5.1 REST API 清单

#### 记忆系统 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/memories | 查询记忆列表 |
| GET | /api/memories/{id} | 获取记忆详情 |
| POST | /api/memories | 创建记忆 |
| PUT | /api/memories/{id} | 更新记忆 |
| DELETE | /api/memories/{id} | 删除记忆 |
| GET | /api/memories/search | 搜索记忆 |
| GET | /api/daily-logs/{date} | 获取每日日志 |
| POST | /api/daily-logs | 创建每日日志 |

#### 代码追溯 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/trace/code-units | 查询代码单元 |
| GET | /api/trace/code-units/{id} | 获取代码单元详情 |
| GET | /api/trace/callchain/{id} | 查询调用链 |
| POST | /api/trace/impact/{id} | 影响分析 |
| GET | /api/trace/relations | 查询追溯关系 |
| POST | /api/trace/relations | 创建追溯关系 |

#### 多通道 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/channels | 查询通道列表 |
| POST | /api/channels/{name}/send | 发送消息 |
| GET | /api/channels/{name}/status | 通道状态 |

### 5.2 数据库 Schema

#### 记忆表

```sql
CREATE TABLE memories (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200),
    content JSONB NOT NULL,
    tags TEXT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_memories_content ON memories USING GIN (content);
CREATE INDEX idx_memories_tags ON memories USING GIN (tags);
```

#### 代码单元表

```sql
CREATE TABLE code_units (
    id BIGSERIAL PRIMARY KEY,
    file_path VARCHAR(500) NOT NULL,
    unit_type VARCHAR(50) NOT NULL,
    unit_name VARCHAR(200) NOT NULL,
    signature TEXT,
    metrics JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_code_units_path ON code_units(file_path);
```

---

## 6. 部署架构

### 6.1 开发环境

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:16-alpine
    ports: ["5432:5432"]
  
  falkordb:
    image: falkordb/falkordb:edge
    ports: ["6379:6379"]
  
  redis:
    image: redis:7-alpine
    ports: ["6380:6379"]
  
  jclaw:
    build: .
    ports: ["8080:8080"]
    depends_on: [postgres, falkordb, redis]
```

### 6.2 生产环境

```
┌─────────────────────────────────────────┐
│         生产环境部署                     │
├─────────────────────────────────────────┤
│  Nginx (反向代理)                       │
│         │                               │
│         ▼                               │
│  JClaw 实例 x 3 (Docker)                │
│         │                               │
│         ▼                               │
│  PostgreSQL 主从 (Docker)               │
│  FalkorDB 集群 (Docker)                 │
│  Redis 哨兵 (Docker)                    │
│         │                               │
│         ▼                               │
│  Prometheus + Grafana (监控)            │
└─────────────────────────────────────────┘
```

---

## ✅ 验收标准

- [x] 架构设计文档完整
- [x] 模块划分清晰
- [x] 接口设计完整
- [x] 数据流设计合理
- [x] 部署架构可行

---

*文档版本：v1.0*  
*完成时间：2026-04-03*  
*作者：可乐 🥤*  
*状态：✅ 完成*
