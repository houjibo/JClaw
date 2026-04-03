package com.jclaw.memory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jclaw.memory.entity.Knowledge;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识 Mapper 接口
 */
@Mapper
public interface KnowledgeMapper extends BaseMapper<Knowledge> {
}
