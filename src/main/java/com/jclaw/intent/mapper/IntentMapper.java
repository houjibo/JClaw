package com.jclaw.intent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jclaw.intent.entity.Intent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 意图 Mapper 接口
 */
@Mapper
public interface IntentMapper extends BaseMapper<Intent> {
}
