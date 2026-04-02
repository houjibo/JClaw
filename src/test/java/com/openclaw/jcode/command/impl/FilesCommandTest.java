package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * FilesCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("FilesCommand 测试")
class FilesCommandTest {
    
    private FilesCommand filesCommand;
    private CommandContext context;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        filesCommand = new FilesCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("files", filesCommand.getName());
        assertEquals("文件浏览/管理", filesCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, filesCommand.getCategory());
        assertTrue(filesCommand.getAliases().contains("file"));
        assertTrue(filesCommand.getAliases().contains("fs"));
    }
    
    @Test
    @DisplayName("列出文件")
    void testListFiles() {
        CommandResult result = filesCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("文件列表"));
    }
    
    @Test
    @DisplayName("创建文件")
    void testCreateFile() {
        String testFile = tempDir.resolve("test.txt").toString();
        CommandResult result = filesCommand.execute("create " + testFile, context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(Files.exists(Paths.get(testFile)));
    }
    
    @Test
    @DisplayName("创建目录")
    void testCreateDirectory() {
        String testDir = tempDir.resolve("testdir").toString();
        CommandResult result = filesCommand.execute("mkdir " + testDir, context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(Files.exists(Paths.get(testDir)));
        assertTrue(Files.isDirectory(Paths.get(testDir)));
    }
    
    @Test
    @DisplayName("删除文件")
    void testDeleteFile() {
        String testFile = tempDir.resolve("test.txt").toString();
        
        // 先创建
        try {
            Files.createFile(Paths.get(testFile));
        } catch (Exception e) {
            // 忽略
        }
        
        CommandResult result = filesCommand.execute("delete " + testFile, context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertFalse(Files.exists(Paths.get(testFile)));
    }
    
    @Test
    @DisplayName("移动文件")
    void testMoveFile() {
        String source = tempDir.resolve("source.txt").toString();
        String target = tempDir.resolve("target.txt").toString();
        
        // 先创建源文件
        try {
            Files.createFile(Paths.get(source));
        } catch (Exception e) {
            // 忽略
        }
        
        CommandResult result = filesCommand.execute("move " + source + " " + target, context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertFalse(Files.exists(Paths.get(source)));
        assertTrue(Files.exists(Paths.get(target)));
    }
    
    @Test
    @DisplayName("复制文件")
    void testCopyFile() {
        String source = tempDir.resolve("source.txt").toString();
        String target = tempDir.resolve("target.txt").toString();
        
        // 先创建源文件
        try {
            Files.createFile(Paths.get(source));
        } catch (Exception e) {
            // 忽略
        }
        
        CommandResult result = filesCommand.execute("copy " + source + " " + target, context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(Files.exists(Paths.get(source)));
        assertTrue(Files.exists(Paths.get(target)));
    }
    
    @Test
    @DisplayName("文件信息")
    void testFileInfo() {
        String testFile = tempDir.resolve("test.txt").toString();
        
        // 先创建
        try {
            Files.createFile(Paths.get(testFile));
        } catch (Exception e) {
            // 忽略
        }
        
        CommandResult result = filesCommand.execute("info " + testFile, context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("文件详情"));
    }
    
    @Test
    @DisplayName("目录树")
    void testTreeView() {
        CommandResult result = filesCommand.execute("tree " + tempDir.toString(), context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("目录树"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = filesCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("files"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(filesCommand.getAliases().contains("file"));
        assertTrue(filesCommand.getAliases().contains("fs"));
    }
    
    @Test
    @DisplayName("错误处理 - 文件不存在")
    void testFileNotFound() {
        CommandResult result = filesCommand.execute("info /nonexistent/file.txt", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("错误处理 - 参数不足")
    void testInsufficientArgs() {
        CommandResult result = filesCommand.execute("move", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
