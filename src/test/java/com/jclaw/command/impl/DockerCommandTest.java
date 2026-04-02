package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * DockerCommand 单元测试
 */
@DisplayName("DockerCommand 测试")
class DockerCommandTest {
    
    private DockerCommand dockerCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        dockerCommand = new DockerCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("docker", dockerCommand.getName());
        assertEquals("Docker 容器管理", dockerCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, dockerCommand.getCategory());
    }
    
    @Test
    @DisplayName("显示 Docker 信息")
    void testShowDockerInfo() {
        CommandResult result = dockerCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("Docker"));
    }
    
    @Test
    @DisplayName("运行容器")
    void testRunContainer() {
        CommandResult result = dockerCommand.execute("run nginx", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("运行"));
    }
    
    @Test
    @DisplayName("错误处理 - 缺少镜像")
    void testMissingImage() {
        CommandResult result = dockerCommand.execute("run", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = dockerCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("docker"));
    }
    
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
