package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CatCommand 单元测试
 */
@DisplayName("CatCommand 测试")
class CatCommandTest {
    
    private CatCommand catCommand;
    private CommandContext context;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        catCommand = new CatCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("cat", catCommand.getName());
        assertEquals("查看文件内容", catCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, catCommand.getCategory());
    }
    
    @Test
    @DisplayName("查看文件内容")
    void testCatFile() throws Exception {
        Path testFile = tempDir.resolve("test.txt");
        Files.write(testFile, "line1\nline2\nline3".getBytes());
        
        CommandResult result = catCommand.execute(testFile.toString(), context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("line1"));
    }
    
    @Test
    @DisplayName("错误处理 - 文件不存在")
    void testFileNotFound() {
        CommandResult result = catCommand.execute("/nonexistent/file.txt", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("错误处理 - 无参数")
    void testNoArgs() {
        CommandResult result = catCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
