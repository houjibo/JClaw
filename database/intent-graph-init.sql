-- JClaw 意图图谱数据库初始化脚本 (PostgreSQL 16 + Age 扩展)

-- 启用 Age 扩展（图数据库）
CREATE EXTENSION IF NOT EXISTS age;

-- 加载 Age
LOAD 'age';

-- 创建意图图谱
SELECT create_graph('intent_graph');

-- 插入示例数据（使用 Cypher 语法）
SELECT * FROM cypher('intent_graph', $$
    // 创建意图节点
    CREATE (i1:Intent {id: 'intent_001', name: '创建用户管理系统', type: 'task', priority: 1})
    CREATE (i2:Intent {id: 'intent_002', name: '设计数据库 schema', type: 'subtask', priority: 1})
    CREATE (i3:Intent {id: 'intent_003', name: '实现用户 API', type: 'subtask', priority: 2})
    
    // 创建关系
    CREATE (i1)-[:CONTAINS]->(i2)
    CREATE (i1)-[:CONTAINS]->(i3)
    
    RETURN i1, i2, i3
$$) AS (i1 agtype, i2 agtype, i3 agtype);

-- 查询示例
SELECT * FROM cypher('intent_graph', $$
    MATCH (i:Intent)-[:CONTAINS]->(sub:Intent)
    RETURN i.name, sub.name
$$) AS (intent_name varchar, subtask_name varchar);
