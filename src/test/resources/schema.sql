-- 记忆表
CREATE TABLE IF NOT EXISTS memory (
    id VARCHAR(64) PRIMARY KEY,
    type VARCHAR(32) NOT NULL,
    title VARCHAR(256),
    content TEXT,
    tags VARCHAR(512),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 意图表
CREATE TABLE IF NOT EXISTS intent (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    description TEXT,
    status VARCHAR(32) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 代码单元表
CREATE TABLE IF NOT EXISTS code_unit (
    id VARCHAR(64) PRIMARY KEY,
    file_path VARCHAR(512) NOT NULL,
    unit_name VARCHAR(256) NOT NULL,
    unit_type VARCHAR(32) NOT NULL,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 调用关系表
CREATE TABLE IF NOT EXISTS call_relationship (
    id VARCHAR(64) PRIMARY KEY,
    caller_id VARCHAR(64) NOT NULL,
    callee_id VARCHAR(64) NOT NULL,
    call_type VARCHAR(32),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 子系统表
CREATE TABLE IF NOT EXISTS subsystem (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    description TEXT,
    version VARCHAR(32),
    health_status VARCHAR(32) DEFAULT 'unknown',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 子系统依赖表
CREATE TABLE IF NOT EXISTS subsystem_dependency (
    id VARCHAR(64) PRIMARY KEY,
    source_subsystem_id VARCHAR(64) NOT NULL,
    target_subsystem_id VARCHAR(64) NOT NULL,
    dependency_type VARCHAR(32),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
