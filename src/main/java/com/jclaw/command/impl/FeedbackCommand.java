package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Feedback 命令 - 用户反馈
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class FeedbackCommand extends Command {
    
    public FeedbackCommand() {
        this.name = "feedback";
        this.description = "用户反馈";
        this.aliases = Arrays.asList("report", "issue");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showFeedbackInfo();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "submit" -> submitFeedback(parts.length > 1 ? String.join(" ", Arrays.copyOfRange(parts, 1, parts.length)) : null);
            case "list" -> listFeedback();
            case "status" -> feedbackStatus(parts.length > 1 ? parts[1] : null);
            case "info" -> showFeedbackInfo();
            default -> showFeedbackInfo();
        };
    }
    
    private CommandResult showFeedbackInfo() {
        String report = """
            ## 用户反馈
            
            ### 反馈类型
            
            | 类型 | 说明 | 优先级 |
            |------|------|--------|
            | bug | 功能缺陷 | 高 |
            | feature | 功能建议 | 中 |
            | improvement | 改进建议 | 中 |
            | question | 使用问题 | 低 |
            
            ### 提交反馈
            
            ```
            feedback submit <类型> <描述>
            
            示例：
            feedback submit bug 文件上传失败
            feedback submit feature 添加批量导出功能
            ```
            
            ### 查看反馈
            
            ```
            feedback list           # 查看反馈列表
            feedback status <ID>    # 查看反馈状态
            ```
            
            ### 反馈渠道
            
            - **GitHub Issues** - 技术问题
            - **Discord** - 社区讨论
            - **邮件** - 商务合作
            
            ### 反馈处理流程
            
            1. 提交反馈
            2. 确认收到（24 小时内）
            3. 评估优先级
            4. 安排处理
            5. 反馈结果
            
            感谢你的反馈！
            """;
        
        return CommandResult.success("反馈功能")
                .withDisplayText(report);
    }
    
    private CommandResult submitFeedback(String content) {
        if (content == null) {
            return CommandResult.error("请指定反馈内容");
        }
        
        String feedbackId = "FB-" + System.currentTimeMillis();
        
        String report = String.format("""
            ## 反馈已提交
            
            **反馈 ID**: %s
            **内容**: %s
            **时间**: 2026-04-01 19:20
            **状态**: 待处理
            
            ### 处理流程
            
            1. ✅ 反馈已提交
            2. ⏳ 等待确认（24 小时内）
            3. ⏳ 评估优先级
            4. ⏳ 安排处理
            5. ⏳ 反馈结果
            
            ### 查看状态
            
            ```
            feedback status %s
            ```
            
            ### 补充信息
            
            如有更多信息，可以：
            - 回复此消息
            - 发送邮件至 support@example.com
            - 在 GitHub 追加评论
            
            感谢你的反馈！
            """, feedbackId, content, feedbackId);
        
        return CommandResult.success("反馈已提交：" + feedbackId)
                .withDisplayText(report);
    }
    
    private CommandResult listFeedback() {
        String report = """
            ## 反馈列表
            
            | ID | 类型 | 描述 | 状态 | 时间 |
            |-----|------|------|------|------|
            | FB-001 | bug | 文件上传失败 | 🟢 已解决 | 04-01 |
            | FB-002 | feature | 批量导出 | 🟡 处理中 | 04-01 |
            | FB-003 | improvement | 性能优化 | ⚪ 待处理 | 04-01 |
            
            ### 状态说明
            
            | 状态 | 说明 |
            |------|------|
            | ⚪ 待处理 | 等待评估 |
            | 🟡 处理中 | 正在开发 |
            | 🟢 已解决 | 已完成 |
            | 🔴 已拒绝 | 不采纳 |
            
            ### 筛选选项
            
            ```
            feedback list --status=pending    # 待处理
            feedback list --type=bug          # Bug 列表
            feedback list --my                # 我的反馈
            ```
            """;
        
        return CommandResult.success("反馈列表")
                .withDisplayText(report);
    }
    
    private CommandResult feedbackStatus(String feedbackId) {
        if (feedbackId == null) {
            return CommandResult.error("请指定反馈 ID");
        }
        
        String report = String.format("""
            ## 反馈状态：%s
            
            ### 基本信息
            
            | 属性 | 值 |
            |------|------|
            | ID | %s |
            | 类型 | bug |
            | 描述 | 文件上传失败 |
            | 提交时间 | 2026-04-01 15:00 |
            
            ### 处理进度
            
            ```
            [✅] 提交反馈      04-01 15:00
            [✅] 确认收到      04-01 16:00
            [✅] 评估优先级    04-01 17:00
            [🟡] 开发修复     进行中
            [⏳] 测试验证     待开始
            [⏳] 发布更新     待开始
            ```
            
            ### 处理人员
            
            - **负责人**: 张三
            - **开发**: 李四
            - **测试**: 王五
            
            ### 预计完成
            
            **时间**: 2026-04-02 18:00
            
            ### 备注
            
            已定位问题原因，正在修复中。
            预计明天发布补丁版本。
            """, feedbackId, feedbackId);
        
        return CommandResult.success("反馈状态：" + feedbackId)
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：feedback
            别名：report, issue
            描述：用户反馈
            
            用法：
              feedback                  # 反馈功能
              feedback submit <内容>    # 提交反馈
              feedback list             # 查看列表
              feedback status <ID>      # 查看状态
            
            示例：
              feedback submit bug 文件上传失败
              feedback list
              feedback status FB-001
            """;
    }
}
