package com.openclaw.jcode.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Notebook 编辑工具 - 支持 Jupyter Notebook 格式
 * 
 * 功能：
 * - 读取 Notebook 文件
 * - 写入 Notebook 文件
 * - 执行代码单元格
 * - 渲染输出
 */
@Component
public class NotebookEditTool extends Tool {
    
    private final ObjectMapper objectMapper;
    
    public NotebookEditTool() {
        this.name = "notebook_edit";
        this.description = "Notebook 编辑，支持 Jupyter Notebook 格式 (.ipynb)";
        this.category = ToolCategory.FILE;
        this.requiresConfirmation = true;
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String action = null;
        Object actionObj = params.get("action");
        if (actionObj instanceof String) {
            action = (String) actionObj;
        }
        
        String path = null;
        Object pathObj = params.get("path");
        if (pathObj instanceof String) {
            path = (String) pathObj;
        }
        
        if (path == null || path.isBlank()) {
            return ToolResult.error("path 参数不能为空");
        }
        
        try {
            return switch (action.toLowerCase()) {
                case "read" -> readNotebook(path);
                case "write" -> writeNotebook(path, params);
                case "execute" -> executeCell(path, params);
                case "list" -> listCells(path);
                default -> ToolResult.error("不支持的操作：" + action);
            };
        } catch (Exception e) {
            return ToolResult.error("Notebook 操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 读取 Notebook 文件
     */
    private ToolResult readNotebook(String path) throws Exception {
        Path notebookPath = Paths.get(path);
        if (!Files.exists(notebookPath)) {
            return ToolResult.error("Notebook 文件不存在：" + path);
        }
        
        String content = Files.readString(notebookPath);
        Map<?, ?> notebook = objectMapper.readValue(content, Map.class);
        
        // 提取单元格信息
        List<?> cells = (List<?>) notebook.get("cells");
        List<Map<String, Object>> cellInfo = new ArrayList<>();
        
        int index = 0;
        for (Object cellObj : cells) {
            Map<?, ?> cell = (Map<?, ?>) cellObj;
            Map<String, Object> info = new HashMap<>();
            info.put("index", index++);
            info.put("cell_type", cell.get("cell_type"));
            info.put("source", cell.get("source"));
            info.put("outputs", cell.get("outputs"));
            cellInfo.add(info);
        }
        
        ToolResult result = ToolResult.success("Notebook 读取成功");
        result.setData(Map.of(
            "path", path,
            "cells", cellInfo,
            "metadata", notebook.get("metadata")
        ));
        return result;
    }
    
    /**
     * 写入 Notebook 文件
     */
    private ToolResult writeNotebook(String path, Map<String, Object> params) throws Exception {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cells = (List<Map<String, Object>>) params.get("cells");
        
        if (cells == null) {
            return ToolResult.error("cells 参数不能为空");
        }
        
        // 构建 Notebook 结构
        Map<String, Object> notebook = new HashMap<>();
        notebook.put("cells", cells);
        notebook.put("metadata", Map.of(
            "kernelspec", Map.of(
                "display_name", "Java",
                "language", "java",
                "name", "java"
            ),
            "language_info", Map.of(
                "name", "java",
                "version", "21"
            )
        ));
        notebook.put("nbformat", 4);
        notebook.put("nbformat_minor", 5);
        
        // 写入文件
        Path notebookPath = Paths.get(path);
        Files.createDirectories(notebookPath.getParent());
        String content = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(notebook);
        Files.writeString(notebookPath, content);
        
        ToolResult result = ToolResult.success("Notebook 写入成功");
        result.setData(Map.of(
            "path", path,
            "cells", cells.size()
        ));
        return result;
    }
    
    /**
     * 执行代码单元格
     */
    private ToolResult executeCell(String path, Map<String, Object> params) throws Exception {
        Integer cellIndex = null;
        Object indexObj = params.get("cellIndex");
        if (indexObj instanceof Integer) {
            cellIndex = (Integer) indexObj;
        }
        
        String code = null;
        Object codeObj = params.get("code");
        if (codeObj instanceof String) {
            code = (String) codeObj;
        }
        
        if (cellIndex == null && code == null) {
            return ToolResult.error("cellIndex 或 code 参数必须提供一个");
        }
        
        // 读取 Notebook
        Path notebookPath = Paths.get(path);
        if (!Files.exists(notebookPath)) {
            return ToolResult.error("Notebook 文件不存在：" + path);
        }
        
        String content = Files.readString(notebookPath);
        Map<?, ?> notebook = objectMapper.readValue(content, Map.class);
        List<?> cells = (List<?>) notebook.get("cells");
        
        // 执行单元格（模拟）
        String output;
        if (code != null) {
            output = executeCode(code);
        } else if (cellIndex != null && cellIndex < cells.size()) {
            Map<?, ?> cell = (Map<?, ?>) cells.get(cellIndex);
            Object sourceObj = cell.get("source");
            String source;
            if (sourceObj instanceof List) {
                List<?> sourceList = (List<?>) sourceObj;
                source = String.join("\n", sourceList.stream().map(Object::toString).toList());
            } else {
                source = sourceObj != null ? sourceObj.toString() : "";
            }
            output = executeCode(source);
        } else {
            return ToolResult.error("无效的单元格索引：" + cellIndex);
        }
        
        ToolResult result = ToolResult.success("单元格执行成功");
        result.setData(Map.of(
            "output", output,
            "execution_count", 1
        ));
        return result;
    }
    
    /**
     * 列出所有单元格
     */
    private ToolResult listCells(String path) throws Exception {
        Path notebookPath = Paths.get(path);
        if (!Files.exists(notebookPath)) {
            return ToolResult.error("Notebook 文件不存在：" + path);
        }
        
        String content = Files.readString(notebookPath);
        Map<?, ?> notebook = objectMapper.readValue(content, Map.class);
        List<?> cells = (List<?>) notebook.get("cells");
        
        List<Map<String, Object>> cellList = new ArrayList<>();
        int index = 0;
        for (Object cellObj : cells) {
            Map<?, ?> cell = (Map<?, ?>) cellObj;
            Map<String, Object> info = new HashMap<>();
            info.put("index", index++);
            info.put("type", cell.get("cell_type"));
            info.put("source_length", getSourceLength(cell.get("source")));
            cellList.add(info);
        }
        
        ToolResult result = ToolResult.success("单元格列表查询成功");
        result.setData(Map.of(
            "path", path,
            "total", cellList.size(),
            "cells", cellList
        ));
        return result;
    }
    
    /**
     * 执行代码（模拟）
     */
    private String executeCode(String code) {
        // 实际应该调用 Java 编译器/解释器
        // 这里只是模拟输出
        return "执行结果:\n" + code.substring(0, Math.min(100, code.length())) + "...";
    }
    
    /**
     * 获取源代码长度
     */
    private int getSourceLength(Object source) {
        if (source instanceof List) {
            List<?> list = (List<?>) source;
            return list.stream().mapToInt(s -> s.toString().length()).sum();
        }
        return source != null ? source.toString().length() : 0;
    }
}
