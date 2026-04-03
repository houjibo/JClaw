-- JClaw Subagent 系统数据库初始化脚本 (PostgreSQL 16)

-- Subagent 表
CREATE TABLE IF NOT EXISTS subagents (
    id VARCHAR(50) PRIMARY KEY DEFAULT 'sub_' || uuid_generate_v4(),
    parent_agent_id VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL, -- pm-qa, architect, fullstack, devops, analyst
    task TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'pending', -- pending, running, completed, failed
    context JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    output TEXT
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_subagents_parent ON subagents(parent_agent_id);
CREATE INDEX IF NOT EXISTS idx_subagents_role ON subagents(role);
CREATE INDEX IF NOT EXISTS idx_subagents_status ON subagents(status);
CREATE INDEX IF NOT EXISTS idx_subagents_context ON subagents USING GIN (context);

-- 插入示例数据
INSERT INTO subagents (parent_agent_id, role, task, status, output) VALUES
('agent_001', 'architect', '设计数据库 schema', 'completed', '设计完成，包含 7 张表'),
('agent_001', 'fullstack', '实现用户 API', 'completed', '实现 5 个 REST API 端点'),
('agent_001', 'qa', '编写单元测试', 'running', NULL)
ON CONFLICT (id) DO NOTHING;
