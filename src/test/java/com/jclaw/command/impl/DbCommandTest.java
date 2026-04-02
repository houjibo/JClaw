package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * DbCommand 单元测试
 */
@DisplayName("DbCommand 测试")
class DbCommandTest {
    
    private DbCommand dbCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        dbCommand = new DbCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("db", dbCommand.getName());
        assertEquals("数据库操作", dbCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, dbCommand.getCategory());
        assertTrue(dbCommand.getAliases().contains("database"));
        assertTrue(dbCommand.getAliases().contains("sql"));
    }
    
    @Test
    @DisplayName("显示数据库信息")
    void testShowDbInfo() {
        CommandResult result = dbCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("数据库"));
    }
    
    @Test
    @DisplayName("连接数据库")
    void testConnect() {
        CommandResult result = dbCommand.execute("connect mysql://localhost/test", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("连接"));
    }
    
    @Test
    @DisplayName("SQL 查询")
    void testQuery() {
        CommandResult result = dbCommand.execute("query SELECT * FROM users", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("查询"));
    }
    
    @Test
    @DisplayName("查看表列表")
    void testListTables() {
        CommandResult result = dbCommand.execute("tables", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("表"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = dbCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("db"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
