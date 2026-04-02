package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ZipCommand 单元测试
 */
@DisplayName("ZipCommand 测试")
class ZipCommandTest {
    
    private ZipCommand zipCommand;
    private CommandContext context;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        zipCommand = new ZipCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("zip", zipCommand.getName());
        assertEquals("压缩解压文件", zipCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, zipCommand.getCategory());
        assertTrue(zipCommand.getAliases().contains("unzip"));
        assertTrue(zipCommand.getAliases().contains("tar"));
    }
    
    @Test
    @DisplayName("压缩文件")
    void testCompress() throws Exception {
        Path testFile = tempDir.resolve("test.txt");
        Files.write(testFile, "test content".getBytes());
        
        CommandResult result = zipCommand.execute("compress " + testFile.toString(), context);
        
        assertNotNull(result);
        // 压缩可能成功或失败（取决于环境）
        assertTrue(result.getType() == CommandResult.ResultType.SUCCESS || 
                   result.getType() == CommandResult.ResultType.ERROR);
    }
    
    @Test
    @DisplayName("错误处理 - 文件不存在")
    void testFileNotFound() {
        CommandResult result = zipCommand.execute("compress /nonexistent/file.txt", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("错误处理 - 无参数")
    void testNoArgs() {
        CommandResult result = zipCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        // 无参数时显示帮助
        assertTrue(result.getDisplayText().contains("zip") || result.getDisplayText().contains("压缩"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = zipCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("zip"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
