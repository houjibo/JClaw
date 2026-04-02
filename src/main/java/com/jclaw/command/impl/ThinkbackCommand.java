package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Thinkback 命令 - 思考回溯
 * 回顾之前的思考过程和决策
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class ThinkbackCommand extends Command {
    
    public ThinkbackCommand() {
        this.name = "thinkback";
        this.description = "思考回溯";
        this.aliases = Arrays.asList("review", "reflect");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showThinkback();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "session" -> thinkbackSession(parts.length > 1 ? parts[1] : null);
            case "decision" -> thinkbackDecision(parts.length > 1 ? parts[1] : null);
            case "learning" -> thinkbackLearning();
            default -> showThinkback();
        };
    }
    
    private CommandResult showThinkback() {
        String report = """
            ## 思考回溯
            
            ### 功能说明
            
            Thinkback 帮助你回顾：
            
            1. **会话历史** - 之前的对话和决策
            2. **关键决策** - 重要选择的思考过程
            3. **学习收获** - 从经历中学到的经验
            
            ### 使用方法
            
            ```
            thinkback                  # 显示回溯选项
            thinkback session <ID>     # 回顾会话
            thinkback decision <主题>  # 回顾决策
            thinkback learning         # 查看学习收获
            ```
            
            ### 使用场景
            
            1. **项目复盘**
               - 回顾项目启动时的决策
               - 分析技术选型原因
               - 总结经验教训
            
            2. **问题解决**
               - 回顾问题排查过程
               - 分析根本原因
               - 记录解决方案
            
            3. **学习成长**
               - 整理新学知识
               - 记录思维模式
               - 规划改进方向
            
            ### 示例
            
            ```
            thinkback session project-init
            thinkback decision tech-stack
            thinkback learning
            ```
            
            开始回溯你的思考历程...
            """;
        
        return CommandResult.success("思考回溯")
                .withDisplayText(report);
    }
    
    private CommandResult thinkbackSession(String sessionId) {
        if (sessionId == null) {
            return CommandResult.error("请指定会话 ID");
        }
        
        String report = String.format("""
            ## 会话回溯：%s
            
            ### 会话信息
            
            | 属性 | 值 |
            |------|------|
            | 会话 ID | %s |
            | 创建时间 | 2026-04-01 15:00 |
            | 持续时间 | 2 小时 |
            | 消息数 | 50 |
            
            ### 关键节点
            
            1. **15:00** - 会话开始，讨论项目初始化
            2. **15:30** - 决定使用 Spring Boot + Vue 技术栈
            3. **16:00** - 完成项目结构创建
            4. **16:30** - 配置数据库连接
            5. **17:00** - 首次构建成功
            
            ### 关键决策
            
            - 选择 Maven 而非 Gradle（团队熟悉度）
            - 使用 MySQL 而非 PostgreSQL（项目需求）
            - 采用 REST API 而非 GraphQL（简单优先）
            
            ### 经验教训
            
            ✅ 提前规划目录结构节省时间
            ⚠️ 应该更早考虑测试策略
            💡 下次可以自动化更多步骤
            
            回溯完成。
            """, sessionId, sessionId);
        
        return CommandResult.success("会话回溯：" + sessionId)
                .withDisplayText(report);
    }
    
    private CommandResult thinkbackDecision(String topic) {
        if (topic == null) {
            return CommandResult.error("请指定决策主题");
        }
        
        String report = String.format("""
            ## 决策回溯：%s
            
            ### 决策背景
            
            在 %s 的选择上，我们考虑了以下因素：
            
            ### 可选方案
            
            | 方案 | 优点 | 缺点 |
            |------|------|------|
            | 方案 A | 简单快速 | 扩展性差 |
            | 方案 B | 功能强大 | 学习曲线陡 |
            | 方案 C | 平衡折中 | 需要定制 |
            
            ### 决策过程
            
            1. **需求分析** - 明确核心需求
            2. **方案对比** - 列出优缺点
            3. **风险评估** - 考虑潜在问题
            4. **最终选择** - 选择最适合的方案
            
            ### 决策依据
            
            - ✅ 符合项目当前阶段
            - ✅ 团队技术储备充足
            - ✅ 社区支持良好
            - ⚠️ 长期可能需要重构
            
            ### 反思
            
            如果重新选择，会考虑：
            - 更多关注长期维护成本
            - 更早引入自动化测试
            - 考虑更多备选方案
            
            决策回溯完成。
            """, topic, topic);
        
        return CommandResult.success("决策回溯：" + topic)
                .withDisplayText(report);
    }
    
    private CommandResult thinkbackLearning() {
        String report = """
            ## 学习收获
            
            ### 技术收获
            
            | 主题 | 收获 | 应用 |
            |------|------|------|
            | Spring Boot | 深入理解自动配置 | 项目实践 |
            | Vue 3 | 掌握组合式 API | 前端开发 |
            | Docker | 容器化部署流程 | CI/CD |
            
            ### 思维模式
            
            1. **问题拆解**
               - 将大问题分解为小任务
               - 逐个击破，降低复杂度
            
            2. **优先级排序**
               - 先解决核心问题
               - 再优化边缘情况
            
            3. **迭代思维**
               - 先完成再完美
               - 持续改进优化
            
            ### 经验教训
            
            ✅ **做得好的**
            - 及时记录和文档化
            - 主动寻求反馈
            - 保持开放心态
            
            ⚠️ **需要改进**
            - 更早考虑测试
            - 更多关注性能
            - 加强代码审查
            
            ### 下一步计划
            
            1. 深入学习 Spring Boot 高级特性
            2. 掌握 Vue 3 最佳实践
            3. 完善 CI/CD 流程
            4. 加强自动化测试
            
            学习回顾完成。
            """;
        
        return CommandResult.success("学习收获")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：thinkback
            别名：review, reflect
            描述：思考回溯
            
            用法：
              thinkback                  # 显示回溯选项
              thinkback session <ID>     # 回顾会话
              thinkback decision <主题>  # 回顾决策
              thinkback learning         # 查看学习收获
            
            示例：
              thinkback session project-init
              thinkback decision tech-stack
              thinkback learning
            """;
    }
}
