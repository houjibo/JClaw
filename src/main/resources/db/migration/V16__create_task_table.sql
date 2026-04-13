-- JClaw 任务管理表
-- 创建日期：2026-04-13

CREATE TABLE IF NOT EXISTS jclaw_task (
    id VARCHAR(64) PRIMARY KEY COMMENT '任务 ID',
    title VARCHAR(256) NOT NULL COMMENT '任务标题',
    description TEXT COMMENT '任务描述',
    status VARCHAR(32) DEFAULT 'pending' COMMENT '任务状态：pending, running, completed, stopped, failed',
    assignee VARCHAR(64) COMMENT '负责人',
    priority VARCHAR(16) DEFAULT 'medium' COMMENT '优先级：low, medium, high, critical',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_assignee (assignee),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='JClaw 任务表';
