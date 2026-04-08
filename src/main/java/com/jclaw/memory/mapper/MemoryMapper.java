package com.jclaw.memory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jclaw.memory.entity.Memory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 记忆 Mapper 接口
 */
@Mapper
public interface MemoryMapper extends BaseMapper<Memory> {
    
    /**
     * PostgreSQL 全文搜索
     * @param query 搜索关键词
     * @return 匹配的记忆列表
     */
    @Select("SELECT * FROM memory " +
            "WHERE to_tsvector('simple', title || ' ' || content) @@ plainto_tsquery('simple', #{query}) " +
            "ORDER BY ts_rank(to_tsvector('simple', title || ' ' || content), plainto_tsquery('simple', #{query})) DESC")
    List<Memory> fullTextSearch(@Param("query") String query);
}
