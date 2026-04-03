package com.jclaw.memory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jclaw.memory.entity.Memory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 记忆 Mapper 接口
 */
@Mapper
public interface MemoryMapper extends BaseMapper<Memory> {
}
