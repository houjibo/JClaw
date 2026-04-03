package com.jclaw.memory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jclaw.memory.entity.DailyLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 每日日志 Mapper 接口
 */
@Mapper
public interface DailyLogMapper extends BaseMapper<DailyLog> {
}
