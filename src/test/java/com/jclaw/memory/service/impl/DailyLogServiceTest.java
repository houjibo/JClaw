package com.jclaw.memory.service.impl;

import com.jclaw.memory.entity.DailyLog;
import com.jclaw.memory.mapper.DailyLogMapper;
import com.jclaw.memory.service.DailyLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 每日日志服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class DailyLogServiceTest {

    @Mock
    private DailyLogMapper dailyLogMapper;

    @InjectMocks
    private DailyLogServiceImpl dailyLogService;

    @Test
    void testGetLogByDate() {
        LocalDate testDate = LocalDate.of(2026, 4, 3);
        DailyLog mockLog = DailyLog.builder()
            .id("log_001")
            .date(testDate)
            .content(Map.of("summary", "测试日志"))
            .build();
        
        when(dailyLogMapper.selectOne(any())).thenReturn(mockLog);

        DailyLog result = dailyLogService.getLogByDate(testDate);

        assertNotNull(result);
        assertEquals("log_001", result.getId());
        verify(dailyLogMapper, times(1)).selectOne(any());
    }

    @Test
    void testCreateLog() {
        DailyLog mockLog = DailyLog.builder()
            .date(LocalDate.of(2026, 4, 3))
            .content(Map.of("summary", "新日志"))
            .build();
        
        when(dailyLogMapper.insert(any(DailyLog.class))).thenReturn(1);

        DailyLog result = dailyLogService.createLog(mockLog);

        assertNotNull(result);
        verify(dailyLogMapper, times(1)).insert(any(DailyLog.class));
    }

    @Test
    void testDeleteLog() {
        when(dailyLogMapper.deleteById("log_001")).thenReturn(1);

        dailyLogService.deleteLog("log_001");

        verify(dailyLogMapper, times(1)).deleteById("log_001");
    }
}
