package com.jclaw.memory.service;

import com.jclaw.memory.entity.DailyLog;

import java.time.LocalDate;
import java.util.List;

/**
 * 每日日志服务接口
 */
public interface DailyLogService {
    
    /**
     * 获取指定日期的日志
     */
    DailyLog getLogByDate(LocalDate date);
    
    /**
     * 查询日志列表
     */
    List<DailyLog> listLogs(int page, int size);
    
    /**
     * 创建每日日志
     */
    DailyLog createLog(DailyLog log);
    
    /**
     * 更新每日日志
     */
    void updateLog(DailyLog log);
    
    /**
     * 删除每日日志
     */
    void deleteLog(String id);
}
