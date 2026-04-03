# 迭代 0 - 技术预研任务清单

> **周期**: Week 1 (2026-04-03 ~ 2026-04-09)  
> **目标**: 完成技术预研和环境准备，为迭代 1 做准备  
> **状态**: 🔄 进行中

---

## 📋 任务列表

### 任务 1: Neo4j 技术预研 (#2)

**目标**: 确定图数据库技术选型并搭建环境

**子任务**:
- [ ] 1.1 调研 Neo4j vs FalkorDB vs NebulaGraph
- [ ] 1.2 确定技术选型
- [ ] 1.3 编写 Docker Compose 配置
- [ ] 1.4 测试基本 CRUD 操作
- [ ] 1.5 编写集成示例代码

**验收标准**:
- [ ] 技术选型报告（对比 3 种图数据库）
- [ ] Docker Compose 可一键启动
- [ ] Java 代码可连接并执行 Cypher 查询
- [ ] 性能测试：1000 节点/10000 关系查询<100ms

**预期产出**:
```
src/main/resources/docker/neo4j-compose.yml
src/test/java/com/jclaw/graph/Neo4jIntegrationTest.java
docs/NEO4J_SETUP.md
```

**预计时间**: 2 天

---

### 任务 2: PostgreSQL 技术预研 (#3)

**目标**: 设计数据库 schema 并搭建环境

**子任务**:
- [ ] 2.1 分析 One Product 数据库 schema
- [ ] 2.2 设计 JClaw 融合版 schema（记忆/意图/追溯）
- [ ] 2.3 编写 Docker Compose 配置
- [ ] 2.4 编写初始化 SQL 脚本
- [ ] 2.5 测试 MyBatis-Plus 集成

**验收标准**:
- [ ] 数据库 schema 设计文档
- [ ] Docker Compose 可一键启动
- [ ] MyBatis-Plus 可正常 CRUD
- [ ] 初始化脚本可执行

**预期产出**:
```
database/jclaw-postgres-init.sql
src/main/resources/docker/postgres-compose.yml
src/main/java/com/jclaw/config/MybatisPlusConfig.java
docs/POSTGRES_SCHEMA_DESIGN.md
```

**预计时间**: 2 天

---

### 任务 3: Redis 技术预研 (#4)

**目标**: 设计缓存策略并搭建环境

**子任务**:
- [ ] 3.1 分析缓存需求（记忆 L1 层/会话缓存）
- [ ] 3.2 设计 Redis 数据结构
- [ ] 3.3 编写 Docker Compose 配置
- [ ] 3.4 测试 Spring Data Redis 集成
- [ ] 3.5 编写缓存工具类

**验收标准**:
- [ ] 缓存策略设计文档
- [ ] Docker Compose 可一键启动
- [ ] Spring Data Redis 可正常操作
- [ ] 缓存工具类支持 String/Hash/List

**预期产出**:
```
src/main/resources/docker/redis-compose.yml
src/main/java/com/jclaw/cache/CacheUtils.java
src/test/java/com/jclaw/cache/CacheUtilsTest.java
docs/REDIS_CACHE_STRATEGY.md
```

**预计时间**: 1.5 天

---

### 任务 4: One Product 代码分析 (#5, #6)

**目标**: 深入理解意图驱动和代码追溯实现

**子任务**:
- [ ] 4.1 分析意图识别模块
- [ ] 4.2 分析任务分解引擎
- [ ] 4.3 分析 AST 解析器
- [ ] 4.4 分析调用链分析
- [ ] 4.5 分析影响分析引擎
- [ ] 4.6 编写代码分析报告

**验收标准**:
- [ ] 意图驱动代码分析报告
- [ ] 代码追溯代码分析报告
- [ ] 可复用代码清单
- [ ] 需要重写代码清单

**预期产出**:
```
docs/ONE_PRODUCT_INTENT_ANALYSIS.md
docs/ONE_PRODUCT_TRACE_ANALYSIS.md
docs/REUSABLE_CODE_LIST.md
```

**预计时间**: 2 天

**状态**: ✅ 完成（2026-04-03）

---

### 任务 5: 融合架构详细设计

**目标**: 完成融合架构详细设计

**子任务**:
- [ ] 5.1 架构分层设计
- [ ] 5.2 模块划分
- [ ] 5.3 接口设计
- [ ] 5.4 数据流设计
- [ ] 5.5 编写详细设计文档

**验收标准**:
- [ ] 架构设计文档
- [ ] 模块接口定义
- [ ] 数据流图

**预期产出**:
```
docs/ARCHITECTURE_DESIGN.md
docs/MODULE_INTERFACES.md
docs/DATA_FLOW_DIAGRAM.md
```

**预计时间**: 1.5 天

---

## 📊 进度追踪

### 任务进度

```
任务 1 (Neo4j):     ░░░░░░░░░░  0% (0/5)
任务 2 (MySQL):     ░░░░░░░░░░  0% (0/5)
任务 3 (Redis):     ░░░░░░░░░░  0% (0/5)
任务 4 (代码分析):  ░░░░░░░░░░  0% (0/6)
任务 5 (架构设计):  ░░░░░░░░░░  0% (0/5)
```

### 总体进度

| 指标 | 数值 | 百分比 |
|------|------|--------|
| **总子任务** | 26 | 100% |
| **已完成** | 0 | 0% |
| **进行中** | 0 | 0% |
| **待开始** | 26 | 100% |

---

## 📅 时间线

```
Week 1 (2026-04-03 ~ 2026-04-09)

周一 (04-03): ✅ 创建 Issue 追踪
              🔄 启动任务 1 (Neo4j)

周二 (04-04): 🔄 任务 1 (Neo4j 调研)
              🔄 任务 1 (Docker Compose)

周三 (04-05): ✅ 任务 1 完成
              🔄 启动任务 2 (MySQL)

周四 (04-06): 🔄 任务 2 (Schema 设计)
              🔄 任务 2 (Docker Compose)

周五 (04-07): ✅ 任务 2 完成
              🔄 启动任务 3 (Redis)

周六 (04-08): ✅ 任务 3 完成
              🔄 启动任务 4 (代码分析)

周日 (04-09): 🔄 任务 4 (代码分析)
              🔄 任务 5 (架构设计)
```

---

## 🚀 立即行动

### 今天 (2026-04-03)
- [x] 创建 Issue 追踪系统
- [ ] 启动任务 1.1：调研 Neo4j vs FalkorDB vs NebulaGraph

### 明天 (2026-04-04)
- [ ] 完成任务 1.1：技术选型报告
- [ ] 完成任务 1.3：Docker Compose 配置

---

## 📝 技术选型初步建议

### Neo4j vs FalkorDB vs NebulaGraph

| 维度 | Neo4j | FalkorDB | NebulaGraph | 推荐 |
|------|-------|----------|-------------|------|
| **开源协议** | GPL | AGPL | Apache 2.0 | NebulaGraph |
| **性能** | 中 | 高 (Redis 引擎) | 高 | FalkorDB |
| **易用性** | 高 (Cypher) | 高 (Cypher) | 中 (nGQL) | Neo4j/FalkorDB |
| **社区** | 最大 | 小 | 中 | Neo4j |
| **集成难度** | 低 | 低 | 中 | Neo4j/FalkorDB |
| **One Product 使用** | ❌ | ✅ | ❌ | FalkorDB |

**初步推荐**: **FalkorDB** (基于 Redis，性能好，One Product 已有经验)

---

*更新者：可乐 🥤*  
*最后更新：2026-04-03 07:45*
