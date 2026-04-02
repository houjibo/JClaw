package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 支持 diff 补丁式编辑，精确修改文件内容
 */
@Component
public class FileEditTool extends Tool {
    
    public FileEditTool() {
        this.name = "file_edit";
        this.description = "使用 diff 补丁方式编辑文件，支持精确修改";
        this.category = ToolCategory.FILE;
        this.requiresConfirmation = true;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String path = (String) params.get("path");
        String oldString = (String) params.get("old_string");
        String newString = (String) params.get("new_string");
        Boolean replaceAll = (Boolean) params.getOrDefault("replace_all", false);
        String encoding = (String) params.getOrDefault("encoding", "UTF-8");
        
        if (path == null || path.isBlank()) {
            return ToolResult.error("path 参数不能为空");
        }
        
        if (oldString == null || oldString.isBlank()) {
            return ToolResult.error("old_string 参数不能为空");
        }
        
        if (newString == null) {
            return ToolResult.error("new_string 参数不能为空");
        }
        
        if (!context.isAllowWrite()) {
            return ToolResult.error("写操作未被允许");
        }
        
        // 检查是否有实际变化
        if (oldString.equals(newString)) {
            return ToolResult.error("没有变化：old_string 和 new_string 完全相同");
        }
        
        try {
            Path filePath = context.getWorkingDirectory().resolve(path);
            
            if (!Files.exists(filePath)) {
                return ToolResult.error("文件不存在：" + path);
            }
            
            // 检查文件大小（最大 1GB）
            long fileSize = Files.size(filePath);
            if (fileSize > 1024 * 1024 * 1024) {
                return ToolResult.error(String.format("文件过大 (%.2f GB)，最大支持 1GB", 
                        fileSize / 1024.0 / 1024.0 / 1024.0));
            }
            
            // 读取文件内容
            String content = Files.readString(filePath, java.nio.charset.Charset.forName(encoding));
            
            // 执行替换
            EditResult editResult = performEdit(content, oldString, newString, replaceAll);
            
            if (!editResult.success) {
                return ToolResult.error(editResult.message);
            }
            
            // 写回文件
            Files.writeString(filePath, editResult.newContent, 
                    java.nio.charset.Charset.forName(encoding));
            
            System.out.println("[FileEditTool] 文件编辑成功：" + path + 
                    " (" + editResult.replacements + " 处修改)");
            
            // 生成 diff 信息
            String diffInfo = generateDiffInfo(oldString, newString, editResult.replacements);
            
            return ToolResult.success("文件编辑成功：" + path, diffInfo);
            
        } catch (IOException e) {
            System.err.println("[FileEditTool] 文件编辑失败：" + path + " - " + e.getMessage());
            return ToolResult.error("编辑失败：" + e.getMessage());
        }
    }
    
    /**
     * 执行编辑操作
     */
    private EditResult performEdit(String content, String oldString, String newString, boolean replaceAll) {
        // 检查 old_string 是否存在
        int index = content.indexOf(oldString);
        if (index == -1) {
            // 尝试模糊匹配（忽略前后空白）
            String trimmedOld = oldString.trim();
            String fuzzyPattern = Pattern.quote(trimmedOld);
            Pattern pattern = Pattern.compile("\\s*" + fuzzyPattern + "\\s*");
            Matcher matcher = pattern.matcher(content);
            
            if (matcher.find()) {
                // 找到模糊匹配，使用精确位置
                index = matcher.start();
                String actualMatch = matcher.group();
                // 使用实际匹配的内容进行替换
                newString = preserveWhitespaceStyle(oldString, newString, actualMatch);
                oldString = actualMatch;
            } else {
                // 完全找不到，提供相似内容建议
                return findSimilarContent(content, oldString);
            }
        }
        
        // 检查是否有多个匹配
        int secondIndex = content.indexOf(oldString, index + 1);
        if (secondIndex != -1 && !replaceAll) {
            return new EditResult(false, 
                "找到多处匹配（至少 2 处），请确保 old_string 唯一，或使用 replace_all=true", 0, null);
        }
        
        // 执行替换
        String newContent;
        int replacements;
        
        if (replaceAll) {
            newContent = content.replace(oldString, newString);
            replacements = (content.length() - newContent.length()) / oldString.length() + 
                          (newContent.length() - content.length()) / newString.length();
            // 更准确的计数
            replacements = 0;
            int pos = 0;
            while ((pos = content.indexOf(oldString, pos)) != -1) {
                replacements++;
                pos += oldString.length();
            }
        } else {
            newContent = content.substring(0, index) + newString + content.substring(index + oldString.length());
            replacements = 1;
        }
        
        return new EditResult(true, "成功", replacements, newContent);
    }
    
    /**
     * 保留空白风格
     */
    private String preserveWhitespaceStyle(String oldStr, String newStr, String actualMatch) {
        // 如果实际匹配有前导空白，添加到 newStr
        int oldLeading = countLeadingWhitespace(oldStr);
        int actualLeading = countLeadingWhitespace(actualMatch);
        
        if (actualLeading > oldLeading) {
            String padding = actualMatch.substring(0, actualLeading - oldLeading);
            return padding + newStr;
        }
        return newStr;
    }
    
    private int countLeadingWhitespace(String str) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (Character.isWhitespace(c)) count++;
            else break;
        }
        return count;
    }
    
    /**
     * 查找相似内容
     */
    private EditResult findSimilarContent(String content, String oldString) {
        // 简单的相似性检查：查找包含部分关键词的行
        String[] contentLines = content.split("\n");
        String[] searchLines = oldString.split("\n");
        
        List<String> similarLines = new ArrayList<>();
        for (String searchLine : searchLines) {
            if (searchLine.trim().length() > 5) {
                for (String contentLine : contentLines) {
                    if (contentLine.contains(searchLine.trim())) {
                        similarLines.add(contentLine.trim());
                    }
                }
            }
        }
        
        StringBuilder message = new StringBuilder("未找到 old_string 的匹配内容");
        if (!similarLines.isEmpty()) {
            message.append("\n\n相似内容:\n");
            for (int i = 0; i < Math.min(5, similarLines.size()); i++) {
                message.append("  - ").append(similarLines.get(i)).append("\n");
            }
        }
        
        return new EditResult(false, message.toString(), 0, null);
    }
    
    /**
     * 生成 diff 信息
     */
    private String generateDiffInfo(String oldStr, String newStr, int replacements) {
        StringBuilder sb = new StringBuilder();
        sb.append("编辑完成\n\n");
        sb.append("修改位置数：").append(replacements).append("\n\n");
        
        // 显示变更摘要
        int oldLines = oldStr.split("\n").length;
        int newLines = newStr.split("\n").length;
        
        sb.append("变更统计:\n");
        sb.append("  - 原行数：").append(oldLines).append("\n");
        sb.append("  - 新行数：").append(newLines).append("\n");
        sb.append("  - 行数变化：").append(newLines - oldLines).append("\n");
        
        // 显示变更预览
        sb.append("\n变更预览:\n");
        sb.append("--- 原内容（前 3 行）\n");
        String[] oldLinesArr = oldStr.split("\n");
        for (int i = 0; i < Math.min(3, oldLinesArr.length); i++) {
            sb.append("- ").append(oldLinesArr[i]).append("\n");
        }
        
        sb.append("+++ 新内容（前 3 行）\n");
        String[] newLinesArr = newStr.split("\n");
        for (int i = 0; i < Math.min(3, newLinesArr.length); i++) {
            sb.append("+ ").append(newLinesArr[i]).append("\n");
        }
        
        return sb.toString();
    }
    
    @Override
    public boolean validate(Map<String, Object> params) {
        return params.containsKey("path") && 
               params.containsKey("old_string") && 
               params.containsKey("new_string");
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 path        - 文件路径 (必填)
                 old_string  - 要替换的原始内容 (必填，必须精确匹配)
                 new_string  - 替换后的新内容 (必填)
                 replace_all - 是否替换所有匹配，默认 false (可选)
                 encoding    - 文件编码，默认 UTF-8 (可选)
               
               示例:
                 file_edit path="src/Main.java" old_string="public class Main {" new_string="public class MainApp {"
                 file_edit path="config.yml" old_string="debug: false" new_string="debug: true"
               """.formatted(name, description);
    }
    
    static class EditResult {
        boolean success;
        String message;
        int replacements;
        String newContent;
        
        EditResult(boolean success, String message, int replacements, String newContent) {
            this.success = success;
            this.message = message;
            this.replacements = replacements;
            this.newContent = newContent;
        }
    }
}
