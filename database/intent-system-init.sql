-- JClaw 意图驱动数据库初始化脚本 (PostgreSQL 16)

-- 意图表
CREATE TABLE IF NOT EXISTS intents (
    id VARCHAR(50) PRIMARY KEY DEFAULT 'intent_' || uuid_generate_v4(),
    project_id BIGINT,
    type VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    context JSONB,
    status VARCHAR(50) DEFAULT 'pending',
    priority INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_intents_type ON intents(type);
CREATE INDEX IF NOT EXISTS idx_intents_status ON intents(status);
CREATE INDEX IF NOT EXISTS idx_intents_priority ON intents(priority);
CREATE INDEX IF NOT EXISTS idx_intents_context ON intents USING GIN (context);
CREATE INDEX IF NOT EXISTS idx_intents_created_at ON intents(created_at DESC);

-- 任务表
CREATE TABLE IF NOT EXISTS tasks (
    id VARCHAR(50) PRIMARY KEY DEFAULT 'task_' || uuid_generate_v4(),
    intent_id VARCHAR(50) REFERENCES intents(id),
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(50) DEFAULT 'pending',
    priority INT DEFAULT 1,
    assigned_agent VARCHAR(50),
    complexity INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_tasks_intent_id ON tasks(intent_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);
CREATE INDEX IF NOT EXISTS idx_tasks_assigned_agent ON tasks(assigned_agent);

-- 插入示例数据
INSERT INTO intents (id, type, name, description, status, priority, context) VALUES
('intent_001', 'task', '创建用户管理系统', '需要一个完整的用户管理系统，包括登录、注册、权限管理', 'pending', 1, 
 '{"input": "创建用户管理系统", "clarified": false}'::jsonb)
ON CONFLICT (id) DO NOTHING;

INSERT INTO tasks (intent_id, title, description, status, priority, assigned_agent) VALUES
('intent_001', '设计数据库 schema', '设计用户、角色、权限表', 'pending', 1, 'architect'),
('intent_001', '实现用户 API', '实现用户 CRUD API', 'pending', 2, 'fullstack'),
('intent_001', '开发前端页面', '开发用户管理前端页面', 'pending', 3, 'fullstack')
ON CONFLICT (id) DO NOTHING;
