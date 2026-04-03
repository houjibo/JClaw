# PostgreSQL 技术选型报告

> **任务**: 任务 2.1 - 关系数据库选型  
> **日期**: 2026-04-03  
> **作者**: 可乐 🥤  
> **决策**: MySQL → PostgreSQL

---

## 📊 选型对比：MySQL vs PostgreSQL

### 核心对比

| 维度 | MySQL 8.0 | PostgreSQL 16 | 胜出 |
|------|-----------|---------------|------|
| **开源协议** | GPL | PostgreSQL (类 BSD) | PG |
| **JSON 支持** | 基础 | 完整 (JSONB) | PG ✅ |
| **复杂查询** | 一般 | 优秀 | PG ✅ |
| **图查询** | 不支持 | 支持 (扩展) | PG ✅ |
| **全文搜索** | 基础 | 完整 | PG ✅ |
| **事务支持** | InnoDB 支持 | 完整支持 | 平手 |
| **并发性能** | 读优 | 读写均衡 | PG ✅ |
| **扩展性** | 一般 | 优秀 (扩展) | PG ✅ |
| **学习成本** | 低 | 中 | MySQL |
| **社区规模** | 最大 | 大 | MySQL |

---

## ✅ 选择 PostgreSQL 的理由

### 1. JSONB 支持 - 适合记忆系统

**需求**: 记忆系统需要存储非结构化数据（每日日志、知识萃取）

**PostgreSQL 优势**:
```sql
-- JSONB 存储记忆数据
CREATE TABLE memories (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50),
    content JSONB,  -- JSONB 支持索引和查询
    created_at TIMESTAMP
);

-- JSONB 查询
SELECT * FROM memories 
WHERE content->>'date' = '2026-04-03'
  AND content @> '{"tags": ["重要"]}'::jsonb;
```

**MySQL 劣势**:
- JSON 支持基础，无 JSONB 类型
- JSON 查询性能较差
- 无法建立 GIN 索引

---

### 2. 复杂查询 - 适合代码追溯

**需求**: 代码追溯需要递归查询调用链

**PostgreSQL 优势**:
```sql
-- 递归查询调用链
WITH RECURSIVE call_chain AS (
    SELECT * FROM call_relationships WHERE method_id = 1
    UNION ALL
    SELECT cr.* FROM call_relationships cr
    INNER JOIN call_chain cc ON cr.callee_id = cc.caller_id
)
SELECT * FROM call_chain;
```

**MySQL 劣势**:
- 8.0 才支持 CTE（公用表表达式）
- 递归查询性能不如 PG

---

### 3. 图查询扩展 - 适合意图图谱

**需求**: 意图图谱需要图查询能力

**PostgreSQL 优势**:
- **Age 扩展**: 原生图数据库扩展，支持 Cypher
- **pg_graph**: 图查询扩展
- **灵活**: 关系 + 图混合查询

```sql
-- 使用 Age 扩展（Cypher 查询）
SELECT * FROM cypher('intent_graph', $$
    MATCH (i:Intent)-[:REALIZES]->(t:Task)
    RETURN i, t
$$) AS (i agtype, t agtype);
```

**MySQL 劣势**:
- 无图查询扩展
- 需要额外部署 Neo4j/FalkorDB

---

### 4. 全文搜索 - 适合记忆检索

**需求**: 记忆检索需要全文搜索

**PostgreSQL 优势**:
```sql
-- 全文搜索
SELECT * FROM memories
WHERE to_tsvector('simple', content->>'text') 
      @@ to_tsquery('simple', '意图 & 驱动');
```

**MySQL 劣势**:
- 全文搜索功能基础
- 中文分词需要额外配置

---

### 5. 扩展性 - 未来需求

**PostgreSQL 扩展生态**:
- **Age**: 图数据库
- **PostGIS**: 地理信息
- **pgvector**: 向量搜索（AI  embeddings）
- **pg_cron**: 定时任务
- **TimescaleDB**: 时序数据

**MySQL 扩展**: 较少

---

## 🎯 JClaw 融合计划需求匹配

### 使用场景

| 场景 | PostgreSQL 能力 | 匹配度 |
|------|----------------|--------|
| **记忆系统** | JSONB 存储 + 全文搜索 | ✅ 完美 |
| **意图图谱** | Age 扩展（图查询） | ✅ 完美 |
| **代码追溯** | 递归查询 + 复杂 JOIN | ✅ 完美 |
| **影响分析** | 复杂分析查询 | ✅ 完美 |
| **业务数据** | 事务 + ACID | ✅ 完美 |

### 性能要求

| 指标 | PostgreSQL | 满足 |
|------|-----------|------|
| 查询延迟 | <100ms (P95) | ✅ |
| 并发连接 | 100+ | ✅ |
| 数据量 | 100GB+ | ✅ |
| 备份恢复 | pg_dump / WAL | ✅ |

---

## 📋 PostgreSQL 集成方案

### Docker Compose 配置

```yaml
version: '3.8'

services:
  # PostgreSQL - 关系数据库（业务数据 + 记忆 + 追溯）
  postgres:
    image: postgres:16-alpine
    container_name: jclaw-postgres
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: jclaw
      POSTGRES_USER: jclaw
      POSTGRES_PASSWORD: jclaw_password
      TZ: Asia/Shanghai
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init:/docker-entrypoint-initdb.d
    networks:
      - jclaw-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U jclaw -d jclaw"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s
    restart: unless-stopped

  # FalkorDB - 图数据库（意图图谱 + 代码追溯图谱）
  falkordb:
    image: falkordb/falkordb:edge
    container_name: jclaw-falkordb
    ports:
      - "6379:6379"
    environment:
      - FALKORDB_MAX_QUEUED_QUERIES=1000
    volumes:
      - falkordb_data:/data
    networks:
      - jclaw-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  # Redis - 缓存（记忆 L1 层/会话缓存）
  redis:
    image: redis:7-alpine
    container_name: jclaw-redis
    ports:
      - "6380:6379"
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data
    networks:
      - jclaw-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

networks:
  jclaw-network:
    driver: bridge

volumes:
  postgres_data:
  falkordb_data:
  redis_data:
```

---

### Spring Boot 集成

**依赖**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

**配置文件**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:jclaw}
    username: ${DB_USER:jclaw}
    password: ${DB_PASSWORD:jclaw_password}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: true
```

---

### 数据库 Schema 设计

**记忆系统表**:
```sql
-- 记忆表（JSONB 存储非结构化数据）
CREATE TABLE memories (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,  -- 'long_term', 'daily_log', 'knowledge'
    title VARCHAR(200),
    content JSONB NOT NULL,     -- JSONB 存储灵活数据
    tags TEXT[],                -- 数组类型
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- GIN 索引（JSONB 查询加速）
CREATE INDEX idx_memories_content ON memories USING GIN (content);
CREATE INDEX idx_memories_tags ON memories USING GIN (tags);

-- 全文搜索索引
CREATE INDEX idx_memories_search ON memories 
USING GIN (to_tsvector('simple', content->>'text'));
```

**意图图谱表**:
```sql
-- 意图节点
CREATE TABLE intent_nodes (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,  -- 'intent', 'task', 'requirement'
    title VARCHAR(200) NOT NULL,
    description TEXT,
    properties JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 意图关系
CREATE TABLE intent_relations (
    id SERIAL PRIMARY KEY,
    source_id INTEGER REFERENCES intent_nodes(id),
    target_id INTEGER REFERENCES intent_nodes(id),
    type VARCHAR(50) NOT NULL,  -- 'realizes', 'depends', 'related'
    properties JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 递归查询索引
CREATE INDEX idx_relations_source ON intent_relations(source_id);
CREATE INDEX idx_relations_target ON intent_relations(target_id);
```

**代码追溯表**:
```sql
-- 代码单元表
CREATE TABLE code_units (
    id SERIAL PRIMARY KEY,
    file_path VARCHAR(500) NOT NULL,
    unit_type VARCHAR(50) NOT NULL,  -- 'class', 'method', 'function'
    unit_name VARCHAR(200) NOT NULL,
    signature TEXT,
    metrics JSONB,  -- 圈复杂度等指标
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 调用关系表
CREATE TABLE call_relationships (
    id SERIAL PRIMARY KEY,
    caller_id INTEGER REFERENCES code_units(id),
    callee_id INTEGER REFERENCES code_units(id),
    call_type VARCHAR(50),  -- 'direct', 'indirect'
    call_count INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 递归查询索引
CREATE INDEX idx_calls_caller ON call_relationships(caller_id);
CREATE INDEX idx_calls_callee ON call_relationships(callee_id);
```

---

## 🚀 迁移计划（MySQL → PostgreSQL）

### 阶段 1: 环境搭建 (1 天)
- [ ] 更新 Docker Compose 配置
- [ ] 创建初始化 SQL 脚本
- [ ] 测试连接

### 阶段 2: Schema 迁移 (2 天)
- [ ] 设计 PostgreSQL schema
- [ ] 创建表和索引
- [ ] 数据迁移脚本

### 阶段 3: 代码适配 (2 天)
- [ ] 更新 Spring Boot 配置
- [ ] 修改 SQL 方言
- [ ] 测试 CRUD 操作

### 阶段 4: 性能优化 (1 天)
- [ ] 查询性能测试
- [ ] 索引优化
- [ ] 连接池配置

---

## 📊 性能基准（预期）

### 查询性能对比

| 查询类型 | MySQL 8.0 | PostgreSQL 16 | 提升 |
|----------|-----------|---------------|------|
| 简单查询 | 10ms | 8ms | 20% ↑ |
| 复杂 JOIN | 50ms | 30ms | 40% ↑ |
| 递归查询 | 100ms | 50ms | 50% ↑ |
| JSON 查询 | 80ms | 30ms | 62% ↑ |
| 全文搜索 | 60ms | 25ms | 58% ↑ |

---

## ✅ 验收标准

- [ ] Docker Compose 一键启动
- [ ] Spring Boot 正常连接
- [ ] CRUD 操作正常
- [ ] JSONB 查询性能<50ms
- [ ] 递归查询性能<100ms
- [ ] 全文搜索性能<50ms

---

## 📚 参考资料

- [PostgreSQL 官方文档](https://www.postgresql.org/docs/)
- [PostgreSQL JSONB 文档](https://www.postgresql.org/docs/current/datatype-json.html)
- [Age 图数据库扩展](https://age.apache.org/)
- [PostgreSQL vs MySQL 对比](https://www.postgresql.org/about/compare/)

---

*报告完成时间：2026-04-03*  
*作者：可乐 🥤*  
*决策：✅ 使用 PostgreSQL 16*
