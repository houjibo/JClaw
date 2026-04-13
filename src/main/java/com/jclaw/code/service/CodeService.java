package com.jclaw.code.service;

import com.jclaw.code.dto.*;

import java.util.List;

/**
 * 代码工具服务接口
 * 
 * @author JClaw
 * @since 2026-04-13
 */
public interface CodeService {
    
    /**
     * 代码解释
     * 
     * @param filePath 文件路径
     * @param language 编程语言
     * @return 代码解释
     */
    CodeExplanation explainCode(String filePath, String language);
    
    /**
     * 代码优化建议
     * 
     * @param filePath 文件路径
     * @return 优化建议列表
     */
    List<CodeOptimization> optimizeCode(String filePath);
    
    /**
     * 安全漏洞扫描
     * 
     * @param filePath 文件路径
     * @return 安全报告
     */
    SecurityReport securityScan(String filePath);
    
    /**
     * 自动生成文档
     * 
     * @param filePath 文件路径
     * @return 生成的文档
     */
    CodeDocumentation generateDocs(String filePath);
    
    /**
     * 项目构建
     * 
     * @param projectPath 项目路径
     * @param buildTool 构建工具（maven/gradle）
     * @return 构建结果
     */
    BuildResult buildProject(String projectPath, String buildTool);
    
    /**
     * 调试信息提取
     * 
     * @param filePath 文件路径
     * @param lineNumber 行号
     * @return 调试信息
     */
    DebugInfo debugCode(String filePath, int lineNumber);
}
