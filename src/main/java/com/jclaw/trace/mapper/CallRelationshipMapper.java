package com.jclaw.trace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jclaw.trace.entity.CallRelationship;
import org.apache.ibatis.annotations.Mapper;

/**
 * 调用关系 Mapper 接口
 */
@Mapper
public interface CallRelationshipMapper extends BaseMapper<CallRelationship> {
}
