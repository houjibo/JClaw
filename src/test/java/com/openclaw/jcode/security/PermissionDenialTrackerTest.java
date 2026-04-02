package com.openclaw.jcode.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * PermissionDenialTracker 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("PermissionDenialTracker 测试")
class PermissionDenialTrackerTest {
    
    private final PermissionDenialTracker tracker = new PermissionDenialTracker();
    
    @Test
    @DisplayName("记录拒绝")
    void testRecordDenial() {
        tracker.recordDenial("BashTool", "目录不允许", "/tmp");
        
        List<PermissionDenialTracker.DenialRecord> history = tracker.getHistory();
        
        assertEquals(1, history.size());
        assertEquals("BashTool", history.get(0).getToolName());
        assertEquals("/tmp", history.get(0).getWorkingDirectory().getPath());
    }
    
    @Test
    @DisplayName("多次拒绝计数")
    void testMultipleDenials() {
        tracker.recordDenial("FileWriteTool", "权限不足", "/etc");
        tracker.recordDenial("FileWriteTool", "权限不足", "/etc");
        tracker.recordDenial("FileWriteTool", "权限不足", "/etc");
        
        List<PermissionDenialTracker.DenialRecord> history = tracker.getHistoryByTool("FileWriteTool");
        
        assertEquals(1, history.size());
        assertEquals(3, history.get(0).getCount());
    }
    
    @Test
    @DisplayName("自动拒绝（达到阈值）")
    void testShouldAutoDenyThreshold() {
        // 拒绝 3 次
        tracker.recordDenial("BashTool", "拒绝", "/untrusted");
        tracker.recordDenial("BashTool", "拒绝", "/untrusted");
        tracker.recordDenial("BashTool", "拒绝", "/untrusted");
        
        assertTrue(tracker.shouldAutoDeny("BashTool", "/untrusted"));
    }
    
    @Test
    @DisplayName("不自动拒绝（未达阈值）")
    void testShouldNotAutoDenyBelowThreshold() {
        // 只拒绝 2 次
        tracker.recordDenial("BashTool", "拒绝", "/untrusted");
        tracker.recordDenial("BashTool", "拒绝", "/untrusted");
        
        assertFalse(tracker.shouldAutoDeny("BashTool", "/untrusted"));
    }
    
    @Test
    @DisplayName("信任目录不拒绝")
    void testTrustedDirectory() {
        tracker.addTrustedDirectory("/trusted");
        
        // 即使拒绝多次也不自动拒绝
        tracker.recordDenial("BashTool", "拒绝", "/trusted");
        tracker.recordDenial("BashTool", "拒绝", "/trusted");
        tracker.recordDenial("BashTool", "拒绝", "/trusted");
        
        assertFalse(tracker.shouldAutoDeny("BashTool", "/trusted"));
        assertTrue(tracker.isTrustedDirectory("/trusted"));
    }
    
    @Test
    @DisplayName("信任子目录")
    void testTrustedSubdirectory() {
        tracker.addTrustedDirectory("/projects");
        
        assertTrue(tracker.isTrustedDirectory("/projects/my-app"));
        assertTrue(tracker.isTrustedDirectory("/projects/my-app/src"));
    }
    
    @Test
    @DisplayName("添加自动拒绝模式")
    void testAutoDenyPattern() {
        tracker.addAutoDenyPattern("BashTool", ".*/sensitive/.*");
        
        assertTrue(tracker.shouldAutoDeny("BashTool", "/home/user/sensitive/data"));
        assertFalse(tracker.shouldAutoDeny("BashTool", "/home/user/public/data"));
    }
    
    @Test
    @DisplayName("清除历史")
    void testClearHistory() {
        tracker.recordDenial("Tool1", "原因", "/path1");
        tracker.recordDenial("Tool2", "原因", "/path2");
        
        assertEquals(2, tracker.getHistory().size());
        
        tracker.clearHistory("Tool1");
        
        assertEquals(1, tracker.getHistory().size());
        assertEquals("Tool2", tracker.getHistory().get(0).getToolName());
    }
    
    @Test
    @DisplayName("完全清除历史")
    void testClearAllHistory() {
        tracker.recordDenial("Tool1", "原因", "/path1");
        tracker.recordDenial("Tool2", "原因", "/path2");
        
        tracker.clearHistory();
        
        assertTrue(tracker.getHistory().isEmpty());
    }
    
    @Test
    @DisplayName("统计数据")
    void testStatistics() {
        tracker.addTrustedDirectory("/trusted");
        tracker.addAutoDenyPattern("BashTool", ".*/test/.*");
        tracker.recordDenial("BashTool", "拒绝", "/path");
        tracker.recordDenial("BashTool", "拒绝", "/path");
        
        Map<String, Object> stats = tracker.getStatistics();
        
        assertEquals(1, stats.get("totalDenials"));
        assertEquals(1, stats.get("trustedDirectories"));
        assertEquals(1, stats.get("autoDenyPatterns"));
        
        @SuppressWarnings("unchecked")
        Map<String, Integer> byTool = (Map<String, Integer>) stats.get("denialsByTool");
        assertEquals(2, byTool.get("BashTool"));
    }
    
    @Test
    @DisplayName("移除信任目录")
    void testRemoveTrustedDirectory() {
        tracker.addTrustedDirectory("/tmp");
        assertTrue(tracker.isTrustedDirectory("/tmp"));
        
        tracker.removeTrustedDirectory("/tmp");
        assertFalse(tracker.isTrustedDirectory("/tmp"));
    }
    
    @Test
    @DisplayName("DenialRecord toString")
    void testDenialRecordToString() {
        tracker.recordDenial("TestTool", "测试原因", "/test/path");
        
        List<PermissionDenialTracker.DenialRecord> history = tracker.getHistory();
        String str = history.get(0).toString();
        
        assertTrue(str.contains("TestTool"));
        assertTrue(str.contains("测试原因"));
    }
}
