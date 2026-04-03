-- JClaw 记忆系统数据库初始化脚本 (PostgreSQL 16)
-- 创建数据库：jclaw

-- 启用扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 记忆表
CREATE TABLE IF NOT EXISTS memories (
    id VARCHAR(50) PRIMARY KEY DEFAULT 'mem_' || uuid_generate_v4(),
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200),
    content JSONB NOT NULL,
    tags TEXT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- GIN 索引（JSONB 查询加速）
CREATE INDEX IF NOT EXISTS idx_memories_content ON memories USING GIN (content);
CREATE INDEX IF NOT EXISTS idx_memories_tags ON memories USING GIN (tags);
CREATE INDEX IF NOT EXISTS idx_memories_type ON memories(type);
CREATE INDEX IF NOT EXISTS idx_memories_created_at ON memories(created_at DESC);

-- 每日日志表
CREATE TABLE IF NOT EXISTS daily_logs (
    id VARCHAR(50) PRIMARY KEY DEFAULT 'log_' || uuid_generate_v4(),
    date DATE NOT NULL UNIQUE,
    content JSONB NOT NULL,
    tags TEXT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_daily_logs_date ON daily_logs(date DESC);
CREATE INDEX IF NOT EXISTS idx_daily_logs_content ON daily_logs USING GIN (content);

-- 知识表
CREATE TABLE IF NOT EXISTS knowledge (
    id VARCHAR(50) PRIMARY KEY DEFAULT 'know_' || uuid_generate_v4(),
    title VARCHAR(200) NOT NULL,
    category VARCHAR(50),
    content TEXT NOT NULL,
    metadata JSONB,
    tags TEXT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_knowledge_category ON knowledge(category);
CREATE INDEX IF NOT EXISTS idx_knowledge_tags ON knowledge USING GIN (tags);

-- 插入示例数据
INSERT INTO memories (id, type, title, content, tags) VALUES
('mem_001', 'long_term', 'JClaw 融合计划', 
 '{"description": "融合 OpenClaw + One Product 核心能力", "status": "进行中"}'::jsonb,
 ARRAY['JClaw', '融合', '规划'])
ON CONFLICT (id) DO NOTHING;

INSERT INTO daily_logs (id, date, content, tags) VALUES
('log_001', '2026-04-03',
 '{"summary": "迭代 0 完成，迭代 1 启动", "tasks_completed": 6}'::jsonb,
 ARRAY['迭代 0', '完成'])
ON CONFLICT (id) DO NOTHING;
