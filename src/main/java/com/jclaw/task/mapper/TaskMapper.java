package com.jclaw.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jclaw.task.entity.Task;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务 Mapper
 * 
 * @author JClaw
 * @since 2026-04-13
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}
