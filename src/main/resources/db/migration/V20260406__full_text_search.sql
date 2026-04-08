-- ===========================================
-- PostgreSQL 全文搜索优化
-- 执行时间：2026-04-06
-- 说明：为 memory 表添加全文搜索索引
-- ===========================================

-- 1. 为 memory 表添加 tsvector 列（用于存储全文索引）
ALTER TABLE memory ADD COLUMN IF NOT EXISTS search_vector tsvector;

-- 2. 创建触发器函数，自动更新 search_vector
CREATE OR REPLACE FUNCTION update_search_vector() RETURNS trigger AS $$
BEGIN
  NEW.search_vector := to_tsvector('simple', COALESCE(NEW.title, '') || ' ' || COALESCE(NEW.content, ''));
  RETURN NEW;
END
$$ LANGUAGE plpgsql;

-- 3. 创建触发器
DROP TRIGGER IF EXISTS update_memory_search_vector ON memory;
CREATE TRIGGER update_memory_search_vector
  BEFORE INSERT OR UPDATE ON memory
  FOR EACH ROW
  EXECUTE FUNCTION update_search_vector();

-- 4. 为现有数据创建索引
UPDATE memory SET search_vector = to_tsvector('simple', COALESCE(title, '') || ' ' || COALESCE(content, ''));

-- 5. 创建 GIN 索引（加速全文搜索）
CREATE INDEX IF NOT EXISTS idx_memory_search_vector ON memory USING GIN(search_vector);

-- 6. 创建普通索引（加速排序）
CREATE INDEX IF NOT EXISTS idx_memory_created_at ON memory(created_at DESC);

-- ===========================================
-- 测试查询
-- ===========================================
-- 测试全文搜索
-- SELECT * FROM memory WHERE search_vector @@ plainto_tsquery('simple', '关键词');

-- 测试带排名的搜索
-- SELECT *, ts_rank(search_vector, plainto_tsquery('simple', '关键词')) as rank
-- FROM memory WHERE search_vector @@ plainto_tsquery('simple', '关键词')
-- ORDER BY rank DESC;

-- ===========================================
-- 性能优化说明
-- ===========================================
-- 1. GIN 索引适合全文搜索，支持快速匹配
-- 2. 触发器确保数据变更时自动更新索引
-- 3. 使用'simple'配置，不支持中文分词
--    如需中文分词，需安装 pg_jieba 扩展
-- ===========================================
