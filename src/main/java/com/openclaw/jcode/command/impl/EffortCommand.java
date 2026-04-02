package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 评估任务所需时间和复杂度
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class EffortCommand extends Command {
    
    public EffortCommand() {
        this.name = "effort";
        this.description = "评估任务工作量";
        this.aliases = Arrays.asList("estimate", "time");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (args == null || args.isBlank()) {
            return estimateDefault();
        }
        
        // 简单的关键词分析
        String lowerArgs = args.toLowerCase();
        
        if (lowerArgs.contains("bug") || lowerArgs.contains("fix") || lowerArgs.contains("修复")) {
            return estimateBugFix(args);
        } else if (lowerArgs.contains("feature") || lowerArgs.contains("new") || lowerArgs.contains("功能") || lowerArgs.contains("新增")) {
            return estimateFeature(args);
        } else if (lowerArgs.contains("test") || lowerArgs.contains("测试")) {
            return estimateTest(args);
        } else if (lowerArgs.contains("refactor") || lowerArgs.contains("重构")) {
            return estimateRefactor(args);
        }
        
        return estimateGeneric(args);
    }
    
    private CommandResult estimateDefault() {
        String report = """
            ## 工作量评估指南
            
            ### 任务类型和估算
            
            | 类型 | 时间 | 复杂度 |
            |------|------|--------|
            | Bug 修复 | 1-4 小时 | 低 - 中 |
            | 新功能 | 1-5 天 | 中 - 高 |
            | 测试编写 | 2-8 小时 | 低 - 中 |
            | 代码重构 | 4 小时 -2 天 | 中 |
            | 文档更新 | 0.5-2 小时 | 低 |
            
            ### 使用方法
            
            ```
            effort 修复登录 bug
            effort 新增用户管理功能
            effort 编写单元测试
            effort 重构支付模块
            ```
            
            ### 评估因素
            
            - 代码复杂度
            - 依赖关系
            - 测试覆盖率
            - 文档需求
            - 风险等级
            """;
        
        return CommandResult.success("工作量评估指南")
                .withDisplayText(report);
    }
    
    private CommandResult estimateBugFix(String description) {
        Map<String, Object> estimate = new HashMap<>();
        estimate.put("type", "Bug 修复");
        estimate.put("timeRange", "1-4 小时");
        estimate.put("complexity", "低 - 中");
        estimate.put("risk", "低");
        estimate.put("description", description);
        
        String report = String.format("""
            ## 工作量评估：Bug 修复
            
            **任务**: %s
            
            | 指标 | 评估 |
            |------|------|
            | 预计时间 | 1-4 小时 |
            | 复杂度 | 低 - 中 |
            | 风险等级 | 低 |
            | 建议优先级 | 中 |
            
            ### 步骤
            
            1. 复现问题 (15-30 分钟)
            2. 定位原因 (30-60 分钟)
            3. 实施修复 (30-90 分钟)
            4. 测试验证 (15-30 分钟)
            
            **建议**: 尽快修复，避免影响扩大
            """, description);
        
        return CommandResult.success("Bug 修复评估")
                .withData("estimate", estimate)
                .withDisplayText(report);
    }
    
    private CommandResult estimateFeature(String description) {
        Map<String, Object> estimate = new HashMap<>();
        estimate.put("type", "新功能");
        estimate.put("timeRange", "1-5 天");
        estimate.put("complexity", "中 - 高");
        estimate.put("risk", "中");
        
        String report = String.format("""
            ## 工作量评估：新功能
            
            **任务**: %s
            
            | 指标 | 评估 |
            |------|------|
            | 预计时间 | 1-5 天 |
            | 复杂度 | 中 - 高 |
            | 风险等级 | 中 |
            | 建议优先级 | 按业务价值定 |
            
            ### 步骤
            
            1. 需求分析 (2-4 小时)
            2. 方案设计 (2-4 小时)
            3. 编码实现 (4-16 小时)
            4. 测试编写 (2-8 小时)
            5. 代码审查 (1-2 小时)
            6. 文档更新 (1-2 小时)
            
            **建议**: 拆分为小任务，逐步交付
            """, description);
        
        return CommandResult.success("新功能评估")
                .withData("estimate", estimate)
                .withDisplayText(report);
    }
    
    private CommandResult estimateTest(String description) {
        Map<String, Object> estimate = new HashMap<>();
        estimate.put("type", "测试编写");
        estimate.put("timeRange", "2-8 小时");
        estimate.put("complexity", "低 - 中");
        
        String report = String.format("""
            ## 工作量评估：测试编写
            
            **任务**: %s
            
            | 指标 | 评估 |
            |------|------|
            | 预计时间 | 2-8 小时 |
            | 复杂度 | 低 - 中 |
            | 测试类型 | 单元测试/集成测试 |
            
            ### 步骤
            
            1. 理解业务逻辑 (30-60 分钟)
            2. 设计测试用例 (30-60 分钟)
            3. 编写测试代码 (1-4 小时)
            4. 运行和优化 (30-60 分钟)
            
            **建议**: 优先覆盖核心逻辑和边界情况
            """, description);
        
        return CommandResult.success("测试编写评估")
                .withData("estimate", estimate)
                .withDisplayText(report);
    }
    
    private CommandResult estimateRefactor(String description) {
        Map<String, Object> estimate = new HashMap<>();
        estimate.put("type", "代码重构");
        estimate.put("timeRange", "4 小时 -2 天");
        estimate.put("complexity", "中");
        estimate.put("risk", "中 - 高");
        
        String report = String.format("""
            ## 工作量评估：代码重构
            
            **任务**: %s
            
            | 指标 | 评估 |
            |------|------|
            | 预计时间 | 4 小时 -2 天 |
            | 复杂度 | 中 |
            | 风险等级 | 中 - 高 |
            
            ### 步骤
            
            1. 理解现有代码 (1-2 小时)
            2. 设计重构方案 (1-2 小时)
            3. 实施重构 (2-8 小时)
            4. 运行测试 (30-60 分钟)
            5. 性能验证 (30-60 分钟)
            
            **建议**: 小步重构，确保测试覆盖
            """, description);
        
        return CommandResult.success("代码重构评估")
                .withData("estimate", estimate)
                .withDisplayText(report);
    }
    
    private CommandResult estimateGeneric(String description) {
        String report = String.format("""
            ## 工作量评估
            
            **任务**: %s
            
            ### 通用评估
            
            | 规模 | 时间 | 建议 |
            |------|------|------|
            | 小 | < 4 小时 | 立即开始 |
            | 中 | 4 小时 -2 天 | 规划后执行 |
            | 大 | 2-5 天 | 拆分任务 |
            | 超大 | > 5 天 | 分阶段交付 |
            
            **建议**: 请提供更多任务细节以获得准确评估
            """, description);
        
        return CommandResult.success("通用评估")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：effort
            别名：estimate, time
            描述：评估任务工作量
            
            用法：
              effort <任务描述>
            
            示例：
              effort 修复登录 bug
              effort 新增用户管理功能
              effort 编写单元测试
              effort 重构支付模块
            """;
    }
}
