package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * LessCommand 单元测试
 */
@DisplayName("LessCommand 测试")
class LessCommandTest {
    
    private LessCommand lessCommand;
    private CommandContext context;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        lessCommand = new LessCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("less", lessCommand.getName());
        assertEquals("分页查看文件", lessCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, lessCommand.getCategory());
        assertTrue(lessCommand.getAliases().contains("more"));
        assertTrue(lessCommand.getAliases().contains("pager"));
    }
    
    @Test
    @DisplayName("分页查看文件")
    void testLessFile() throws Exception {
        Path testFile = tempDir.resolve("test.txt");
        Files.write(testFile, "line1\nline2\nline3".getBytes());
        
        CommandResult result = lessCommand.execute(testFile.toString(), context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("分页"));
    }
    
    @Test
    @DisplayName("错误处理 - 文件不存在")
    void testFileNotFound() {
        CommandResult result = lessCommand.execute("/nonexistent/file.txt", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("错误处理 - 无参数")
    void testNoArgs() {
        CommandResult result = lessCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = lessCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("less"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
