-- JClaw 代码追溯系统数据库初始化脚本 (PostgreSQL 16)

-- 代码单元表
CREATE TABLE IF NOT EXISTS code_units (
    id VARCHAR(50) PRIMARY KEY DEFAULT 'code_' || uuid_generate_v4(),
    file_path VARCHAR(500) NOT NULL,
    unit_type VARCHAR(50) NOT NULL,
    unit_name VARCHAR(200) NOT NULL,
    signature TEXT,
    metrics JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_code_units_path ON code_units(file_path);
CREATE INDEX IF NOT EXISTS idx_code_units_type ON code_units(unit_type);
CREATE INDEX IF NOT EXISTS idx_code_units_metrics ON code_units USING GIN (metrics);

-- 调用关系表
CREATE TABLE IF NOT EXISTS call_relationships (
    id VARCHAR(50) PRIMARY KEY DEFAULT 'call_' || uuid_generate_v4(),
    caller_id VARCHAR(50) REFERENCES code_units(id),
    callee_id VARCHAR(50) REFERENCES code_units(id),
    call_type VARCHAR(50),
    call_count INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 递归查询索引
CREATE INDEX IF NOT EXISTS idx_calls_caller ON call_relationships(caller_id);
CREATE INDEX IF NOT EXISTS idx_calls_callee ON call_relationships(callee_id);

-- 插入示例数据
INSERT INTO code_units (id, file_path, unit_type, unit_name, signature, metrics) VALUES
('code_001', '/src/main/java/com/jclaw/memory/service/MemoryService.java', 'interface', 'MemoryService', 
 'public interface MemoryService', '{"complexity": 1, "lines": 30}'::jsonb),
('code_002', '/src/main/java/com/jclaw/memory/service/impl/MemoryServiceImpl.java', 'class', 'MemoryServiceImpl',
 'public class MemoryServiceImpl implements MemoryService', '{"complexity": 15, "lines": 85}'::jsonb)
ON CONFLICT (id) DO NOTHING;

INSERT INTO call_relationships (caller_id, callee_id, call_type, call_count) VALUES
('code_002', 'code_001', 'implements', 1)
ON CONFLICT (id) DO NOTHING;
