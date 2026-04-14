package com.jclaw.tools;

import com.jclaw.core.ToolResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Notebook 编辑工具测试
 */
@DisplayName("Notebook 编辑工具测试")
class NotebookEditToolTest {
    
    private NotebookEditTool tool;
    private Path tempNotebook;
    
    @BeforeEach
    void setUp() throws IOException {
        tool = new NotebookEditTool();
        tempNotebook = Files.createTempFile("test", ".ipynb");
    }
    
    @Test
    @DisplayName("测试读取 Notebook")
    void testReadNotebook() throws Exception {
        // 创建测试 Notebook
        String notebookContent = """
            {
                "cells": [
                    {
                        "cell_type": "code",
                        "source": ["print('Hello')"],
                        "outputs": []
                    }
                ],
                "metadata": {},
                "nbformat": 4,
                "nbformat_minor": 5
            }
            """;
        Files.writeString(tempNotebook, notebookContent);
        
        Map<String, Object> params = new HashMap<>();
        params.put("action", "read");
        params.put("path", tempNotebook.toString());
        
        ToolResult result = tool.execute(params, null);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(tempNotebook.toString(), ((Map<?, ?>) result.getData()).get("path"));
    }
    
    @Test
    @DisplayName("测试写入 Notebook")
    void testWriteNotebook() {
        Path newNotebook = tempNotebook.getParent().resolve("new_notebook.ipynb");
        
        Map<String, Object> params = new HashMap<>();
        params.put("action", "write");
        params.put("path", newNotebook.toString());
        
        List<Map<String, Object>> cells = List.of(
            Map.of(
                "cell_type", "code",
                "source", List.of("print('Hello World')"),
                "outputs", List.of()
            )
        );
        params.put("cells", cells);
        
        ToolResult result = tool.execute(params, null);
        
        assertTrue(result.isSuccess());
        assertTrue(Files.exists(newNotebook));
        
        // 清理
        try {
            Files.deleteIfExists(newNotebook);
        } catch (IOException e) {
            // 忽略
        }
    }
    
    @Test
    @DisplayName("测试列单元格")
    void testListCells() throws Exception {
        String notebookContent = """
            {
                "cells": [
                    {"cell_type": "code", "source": ["code1"]},
                    {"cell_type": "markdown", "source": ["text1"]},
                    {"cell_type": "code", "source": ["code2"]}
                ],
                "metadata": {},
                "nbformat": 4,
                "nbformat_minor": 5
            }
            """;
        Files.writeString(tempNotebook, notebookContent);
        
        Map<String, Object> params = new HashMap<>();
        params.put("action", "list");
        params.put("path", tempNotebook.toString());
        
        ToolResult result = tool.execute(params, null);
        
        assertTrue(result.isSuccess());
        Map<?, ?> data = (Map<?, ?>) result.getData();
        assertEquals(3, (int) data.get("total"));
    }
    
    @Test
    @DisplayName("测试读取不存在的文件")
    void testReadNonExistent() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "read");
        params.put("path", "/non/existent/path.ipynb");
        
        ToolResult result = tool.execute(params, null);
        
        assertFalse(result.isSuccess());
    }
    
    @Test
    @DisplayName("测试不支持的操作")
    void testUnsupportedAction() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "unsupported");
        params.put("path", tempNotebook.toString());
        
        ToolResult result = tool.execute(params, null);
        
        assertFalse(result.isSuccess());
    }
    
    @AfterEach
    void tearDown() {
        try {
            Files.deleteIfExists(tempNotebook);
        } catch (IOException e) {
            // 忽略
        }
    }
}
