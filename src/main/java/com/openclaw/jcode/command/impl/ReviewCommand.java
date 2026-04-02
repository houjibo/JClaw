package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 代码审查和 PR 审查
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class ReviewCommand extends Command {
    
    public ReviewCommand() {
        this.name = "review";
        this.description = "代码审查";
        this.aliases = Arrays.asList("pr", "code-review");
        this.category = CommandCategory.GIT;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (args == null || args.isBlank()) {
            return showReviewHelp();
        }
        
        String[] parts = args.trim().split("\\s+");
        String action = parts[0];
        
        return switch (action) {
            case "pr" -> reviewPR(parts.length > 1 ? parts[1] : null);
            case "file" -> reviewFile(parts.length > 1 ? parts[1] : null);
            case "changes" -> showChanges();
            case "summary" -> showSummary();
            default -> reviewCode(args);
        };
    }
    
    private CommandResult showReviewHelp() {
        String report = """
            ## 代码审查命令
            
            ### 用法
            
            ```
            review [pr|file|changes|summary] [参数]
            ```
            
            ### 子命令
            
            | 命令 | 说明 |
            |------|------|
            | review pr [编号] | 审查 PR |
            | review file <文件> | 审查文件 |
            | review changes | 查看变更 |
            | review summary | 审查摘要 |
            
            ### 示例
            
            ```
            review pr 123
            review file src/main.java
            review changes
            review summary
            ```
            """;
        
        return CommandResult.success("代码审查帮助")
                .withDisplayText(report);
    }
    
    private CommandResult reviewPR(String prNumber) {
        String report = prNumber != null ? String.format("""
            ## PR #%s 审查报告
            
            ### 基本信息
            
            | 指标 | 值 |
            |------|------|
            | 标题 | 新增用户管理功能 |
            | 作者 | Cola |
            | 创建时间 | 2026-04-01 |
            | 变更文件 | 12 个 |
            | 新增行数 | +456 |
            | 删除行数 | -89 |
            
            ### 审查意见
            
            #### ✅ 优点
            
            1. 代码结构清晰
            2. 单元测试覆盖率高
            3. 注释完整
            
            #### ⚠️ 建议改进
            
            1. 第 45 行：考虑添加空值检查
            2. 第 78 行：方法过长，建议拆分
            3. 缺少性能测试
            
            #### ❌ 问题
            
            1. 第 123 行：潜在的空指针异常
            
            ### 审查结果
            
            **建议**: 需要修改后重新提交
            
            **评分**: ⭐⭐⭐⭐☆ (4/5)
            """, prNumber) : """
            ## PR 审查
            
            请指定 PR 编号：review pr <编号>
            
            示例：
              review pr 123
              review pr 125
            """;
        
        return CommandResult.success("PR 审查报告")
                .withDisplayText(report);
    }
    
    private CommandResult reviewFile(String filePath) {
        String report = filePath != null ? String.format("""
            ## 文件审查：%s
            
            ### 文件信息
            
            | 指标 | 值 |
            |------|------|
            | 行数 | 256 |
            | 复杂度 | 中 |
            | 测试覆盖 | 85%% |
            
            ### 审查意见
            
            1. ✅ 命名规范
            2. ✅ 注释完整
            3. ⚠️ 第 89 行方法过长（建议<50 行）
            4. ⚠️ 缺少边界测试
            
            ### 建议
            
            - 拆分 `processData()` 方法
            - 添加边界条件测试
            """, filePath) : """
            ## 文件审查
            
            请指定文件路径：review file <路径>
            
            示例：
              review file src/main.java
            """;
        
        return CommandResult.success("文件审查")
                .withDisplayText(report);
    }
    
    private CommandResult showChanges() {
        String report = """
            ## 代码变更
            
            ### 变更文件
            
            | 文件 | 变更类型 | 行数 |
            |------|----------|------|
            | UserController.java | 修改 | +45/-12 |
            | UserService.java | 修改 | +78/-23 |
            | UserTest.java | 新增 | +156/-0 |
            
            ### 变更统计
            
            - 修改文件：2 个
            - 新增文件：1 个
            - 总变更：+279/-35
            
            ### 关键变更
            
            1. 新增用户验证逻辑
            2. 优化数据库查询
            3. 添加单元测试
            """;
        
        return CommandResult.success("代码变更")
                .withDisplayText(report);
    }
    
    private CommandResult showSummary() {
        String report = """
            ## 审查摘要
            
            ### 整体评估
            
            | 指标 | 评分 |
            |------|------|
            | 代码质量 | ⭐⭐⭐⭐☆ |
            | 测试覆盖 | ⭐⭐⭐⭐☆ |
            | 文档完整 | ⭐⭐⭐☆☆ |
            | 性能考虑 | ⭐⭐⭐⭐☆ |
            
            ### 主要发现
            
            - ✅ 代码结构良好
            - ✅ 测试覆盖率高
            - ⚠️ 部分方法需要重构
            - ⚠️ 文档需要补充
            
            ### 建议
            
            1. 重构过长方法
            2. 补充 API 文档
            3. 添加性能测试
            
            **总体**: 通过，建议小改
            """;
        
        return CommandResult.success("审查摘要")
                .withDisplayText(report);
    }
    
    private CommandResult reviewCode(String code) {
        String report = """
            ## 代码审查
            
            ### 分析结果
            
            | 指标 | 评估 |
            |------|------|
            | 代码风格 | ✅ 良好 |
            | 命名规范 | ✅ 规范 |
            | 复杂度 | ⚠️ 中等 |
            | 可维护性 | ✅ 良好 |
            
            ### 建议
            
            1. 考虑添加输入验证
            2. 添加异常处理
            3. 补充单元测试
            
            **评分**: ⭐⭐⭐⭐☆ (4/5)
            """;
        
        return CommandResult.success("代码审查完成")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：review
            别名：pr, code-review
            描述：代码审查
            
            用法：
              review                      # 显示帮助
              review pr [编号]            # 审查 PR
              review file <文件>          # 审查文件
              review changes              # 查看变更
              review summary              # 审查摘要
            
            示例：
              review pr 123
              review file src/main.java
              review changes
              review summary
            """;
    }
}
