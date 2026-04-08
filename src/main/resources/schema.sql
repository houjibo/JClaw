-- JClaw H2 数据库初始化脚本

-- 用户表
CREATE TABLE IF NOT EXISTS "user" (
  id VARCHAR(64) PRIMARY KEY,
  username VARCHAR(64) NOT NULL UNIQUE,
  email VARCHAR(128),
  password VARCHAR(256) NOT NULL,
  phone VARCHAR(32),
  status VARCHAR(32) DEFAULT 'active',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_username ON "user"(username);
CREATE INDEX IF NOT EXISTS idx_user_email ON "user"(email);

-- 意图表
CREATE TABLE IF NOT EXISTS intent (
  id VARCHAR(64) PRIMARY KEY,
  project_id BIGINT,
  type VARCHAR(32),
  name VARCHAR(256) NOT NULL,
  description TEXT,
  context TEXT,
  status VARCHAR(32),
  priority INTEGER,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_intent_status ON intent(status);

-- 需求表
CREATE TABLE IF NOT EXISTS requirement (
  id VARCHAR(64) PRIMARY KEY,
  title VARCHAR(256) NOT NULL,
  description TEXT,
  status VARCHAR(32) DEFAULT 'draft',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_requirement_status ON requirement(status);

-- 意图需求关联表
CREATE TABLE IF NOT EXISTS intent_requirement (
  id VARCHAR(64) PRIMARY KEY,
  intent_id VARCHAR(64) NOT NULL,
  requirement_id VARCHAR(64) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (intent_id) REFERENCES intent(id),
  FOREIGN KEY (requirement_id) REFERENCES requirement(id)
);

CREATE INDEX IF NOT EXISTS idx_intent_req_intent ON intent_requirement(intent_id);
CREATE INDEX IF NOT EXISTS idx_intent_req_requirement ON intent_requirement(requirement_id);

-- 任务表
CREATE TABLE IF NOT EXISTS task (
  id VARCHAR(64) PRIMARY KEY,
  intent_id VARCHAR(64),
  title VARCHAR(256) NOT NULL,
  description TEXT,
  type VARCHAR(32),
  priority VARCHAR(16) DEFAULT 'medium',
  status VARCHAR(32) DEFAULT 'pending',
  agent VARCHAR(64),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (intent_id) REFERENCES intent(id)
);

CREATE INDEX IF NOT EXISTS idx_task_intent ON task(intent_id);
CREATE INDEX IF NOT EXISTS idx_task_status ON task(status);
CREATE INDEX IF NOT EXISTS idx_task_agent ON task(agent);

-- 澄清问题表
CREATE TABLE IF NOT EXISTS clarification (
  id VARCHAR(64) PRIMARY KEY,
  intent_id VARCHAR(64) NOT NULL,
  question TEXT NOT NULL,
  answer TEXT,
  status VARCHAR(32) DEFAULT 'pending',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (intent_id) REFERENCES intent(id)
);

CREATE INDEX IF NOT EXISTS idx_clarification_intent ON clarification(intent_id);
CREATE INDEX IF NOT EXISTS idx_clarification_status ON clarification(status);

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

CREATE INDEX IF NOT EXISTS idx_memory_type ON memory(type);

-- 代码单元表
CREATE TABLE IF NOT EXISTS code_unit (
  id VARCHAR(64) PRIMARY KEY,
  file_path VARCHAR(512) NOT NULL,
  unit_name VARCHAR(256) NOT NULL,
  unit_type VARCHAR(32) NOT NULL,
  signature TEXT,
  metrics TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_code_unit_file ON code_unit(file_path);

-- 调用关系表
CREATE TABLE IF NOT EXISTS call_relationship (
  id VARCHAR(64) PRIMARY KEY,
  caller_id VARCHAR(64) NOT NULL,
  callee_id VARCHAR(64) NOT NULL,
  call_type VARCHAR(32),
  call_count INTEGER DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (caller_id) REFERENCES code_unit(id),
  FOREIGN KEY (callee_id) REFERENCES code_unit(id)
);

CREATE INDEX IF NOT EXISTS idx_call_caller ON call_relationship(caller_id);
CREATE INDEX IF NOT EXISTS idx_call_callee ON call_relationship(callee_id);

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

CREATE INDEX IF NOT EXISTS idx_subsystem_name ON subsystem(name);

-- 子系统依赖表
CREATE TABLE IF NOT EXISTS subsystem_dependency (
  id VARCHAR(64) PRIMARY KEY,
  source_subsystem_id VARCHAR(64) NOT NULL,
  target_subsystem_id VARCHAR(64) NOT NULL,
  dependency_type VARCHAR(32),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (source_subsystem_id) REFERENCES subsystem(id),
  FOREIGN KEY (target_subsystem_id) REFERENCES subsystem(id)
);

CREATE INDEX IF NOT EXISTS idx_sub_dep_source ON subsystem_dependency(source_subsystem_id);
CREATE INDEX IF NOT EXISTS idx_sub_dep_target ON subsystem_dependency(target_subsystem_id);

-- OpenClaw Agent 任务表
CREATE TABLE IF NOT EXISTS agent_task (
  id VARCHAR(64) PRIMARY KEY,
  task_id VARCHAR(100) UNIQUE NOT NULL,
  role VARCHAR(50) NOT NULL,
  intent_id VARCHAR(64),
  requirement_id VARCHAR(64),
  description TEXT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'pending',
  result TEXT,
  error_message TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  started_at TIMESTAMP,
  completed_at TIMESTAMP,
  FOREIGN KEY (intent_id) REFERENCES intent(id),
  FOREIGN KEY (requirement_id) REFERENCES requirement(id)
);

CREATE INDEX IF NOT EXISTS idx_agent_task_status ON agent_task(status);
CREATE INDEX IF NOT EXISTS idx_agent_task_role ON agent_task(role);

-- 通道表
CREATE TABLE IF NOT EXISTS channel (
  id VARCHAR(64) PRIMARY KEY,
  name VARCHAR(64) NOT NULL UNIQUE,
  type VARCHAR(32) NOT NULL,
  config TEXT,
  status VARCHAR(32) DEFAULT 'active',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_channel_name ON channel(name);
CREATE INDEX IF NOT EXISTS idx_channel_type ON channel(type);

-- 消息表
CREATE TABLE IF NOT EXISTS message (
  id VARCHAR(64) PRIMARY KEY,
  channel_id VARCHAR(64) NOT NULL,
  content TEXT NOT NULL,
  sender VARCHAR(64),
  receiver VARCHAR(64),
  status VARCHAR(32) DEFAULT 'pending',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (channel_id) REFERENCES channel(id)
);

CREATE INDEX IF NOT EXISTS idx_message_channel ON message(channel_id);
CREATE INDEX IF NOT EXISTS idx_message_status ON message(status);
