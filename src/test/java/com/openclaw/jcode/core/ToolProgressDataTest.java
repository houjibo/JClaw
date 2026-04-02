package com.openclaw.jcode.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * ToolProgressData 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("ToolProgressData 测试")
class ToolProgressDataTest {
    
    @Test
    @DisplayName("创建进度数据")
    void testCreateProgress() {
        ToolProgressData progress = new ToolProgressData("BashTool", ToolProgress.ProgressType.BASH_EXECUTION);
        
        assertEquals("BashTool", progress.getToolName());
        assertEquals(ToolProgress.ProgressType.BASH_EXECUTION, progress.getType());
        assertEquals(0, progress.getPercentComplete());
        assertEquals("初始化...", progress.getStatusMessage());
    }
    
    @Test
    @DisplayName("更新进度")
    void testUpdateProgress() {
        ToolProgressData progress = new ToolProgressData("FileReadTool", ToolProgress.ProgressType.FILE_READ);
        
        progress.update(50, "读取中...");
        
        assertEquals(50, progress.getPercentComplete());
        assertEquals("读取中...", progress.getStatusMessage());
    }
    
    @Test
    @DisplayName("设置百分比边界")
    void testPercentBounds() {
        ToolProgressData progress = new ToolProgressData("Test", ToolProgress.ProgressType.OTHER);
        
        progress.setPercentComplete(-10);
        assertEquals(0, progress.getPercentComplete());
        
        progress.setPercentComplete(150);
        assertEquals(100, progress.getPercentComplete());
    }
    
    @Test
    @DisplayName("标记完成")
    void testComplete() {
        ToolProgressData progress = new ToolProgressData("Test", ToolProgress.ProgressType.OTHER);
        
        progress.complete();
        
        assertEquals(100, progress.getPercentComplete());
        assertEquals("完成", progress.getStatusMessage());
    }
    
    @Test
    @DisplayName("标记失败")
    void testFail() {
        ToolProgressData progress = new ToolProgressData("Test", ToolProgress.ProgressType.OTHER);
        
        progress.fail("文件不存在");
        
        assertEquals(0, progress.getPercentComplete());
        assertTrue(progress.getStatusMessage().contains("失败"));
        assertTrue(progress.getStatusMessage().contains("文件不存在"));
    }
    
    @Test
    @DisplayName("添加元数据")
    void testMetadata() {
        ToolProgressData progress = new ToolProgressData("Test", ToolProgress.ProgressType.OTHER);
        
        progress.putMetadata("filePath", "/tmp/test.txt");
        progress.putMetadata("fileSize", 1024);
        
        Map<String, Object> metadata = progress.getMetadata();
        
        assertEquals("/tmp/test.txt", metadata.get("filePath"));
        assertEquals(1024, metadata.get("fileSize"));
    }
    
    @Test
    @DisplayName("转换为 Map")
    void testToMap() {
        ToolProgressData progress = new ToolProgressData("WebSearchTool", ToolProgress.ProgressType.WEB_SEARCH);
        progress.update(75, "搜索中...");
        progress.putMetadata("query", "test");
        
        Map<String, Object> map = progress.toMap();
        
        assertEquals("WebSearchTool", map.get("toolName"));
        assertEquals("WEB_SEARCH", map.get("type"));
        assertEquals("网络搜索", map.get("typeDisplay"));
        assertEquals(75, map.get("percentComplete"));
        assertEquals("搜索中...", map.get("statusMessage"));
        assertNotNull(map.get("metadata"));
    }
    
    @Test
    @DisplayName("toString 测试")
    void testToString() {
        ToolProgressData progress = new ToolProgressData("BashTool", ToolProgress.ProgressType.BASH_EXECUTION);
        progress.update(50, "执行中");
        
        String str = progress.toString();
        
        assertTrue(str.contains("Bash 执行"));
        assertTrue(str.contains("BashTool"));
        assertTrue(str.contains("50%"));
        assertTrue(str.contains("执行中"));
    }
    
    @Test
    @DisplayName("所有进度类型")
    void testAllProgressTypes() {
        for (ToolProgress.ProgressType type : ToolProgress.ProgressType.values()) {
            assertNotNull(type.getDisplayName());
        }
    }
}
