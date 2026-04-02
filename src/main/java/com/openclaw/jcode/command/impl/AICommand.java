package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * AI 助手命令 - 大模型配置和使用指南
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class AICommand extends Command {
    
    public AICommand() {
        this.name = "ai";
        this.description = "AI 编程助手（大模型集成）";
        this.aliases = Arrays.asList("assistant", "llm", "code");
        this.category = CommandCategory.AI;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+", 2);
        
        if (parts.length == 0) {
            return showAIInfo();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "info", "help" -> showAIInfo();
            case "config" -> showConfigGuide();
            case "bailian" -> showBailianGuide();
            default -> showAIInfo();
        };
    }
    
    private CommandResult showAIInfo() {
        String report = """
            ## 🤖 JClaw AI 助手
            
            JClaw 已集成大模型 API，支持多种模型提供商。
            
            ### 支持的模型平台
            
            | 平台 | 模型 | 环境 变量 | 推荐度 |
            |------|------|---------|--------|
            | **阿里云百炼** | Qwen 系列 | DASHSCOPE_API_KEY | ⭐⭐⭐⭐⭐ 国内推荐 |
            | DeepSeek | DeepSeek Coder | DEEPSEEK_API_KEY | ⭐⭐⭐⭐⭐ 代码专用 |
            | Kimi | Kimi K2.5 | KIMI_API_KEY | ⭐⭐⭐⭐ 国内 |
            | GLM/Zhipu | GLM-4 | ZHIPU_API_KEY | ⭐⭐⭐⭐ 国内 |
            | OpenAI | GPT-4 | OPENAI_API_KEY | ⭐⭐⭐ 国际 |
            | Claude | Claude 3.5 | ANTHROPIC_API_KEY | ⭐⭐⭐ 国际 |
            
            ### 快速开始
            
            **1. 配置 API Key**
            ```bash
            # 阿里云百炼（推荐）
            export DASHSCOPE_API_KEY=sk-xxxxxxxx
            
            # DeepSeek
            export DEEPSEEK_API_KEY=sk-xxxxxxxx
            ```
            
            **2. 查看配置指南**
            ```bash
            ai config      # 查看所有配置
            ai bailian     # 查看百炼配置
            ```
            
            ### 功能列表
            
            | 功能 | 命令 | 说明 |
            |------|------|------|
            | 代码生成 | `ai code` | 根据需求生成代码 |
            | 代码审查 | `ai review` | 审查代码质量 |
            | 问题解答 | `ai ask` | 回答编程问题 |
            | 对话聊天 | `ai chat` | AI 助手对话 |
            
            ### 配置文档
            
            - 百炼 API: `BAILIAN_API_CONFIG.md`
            - AI 演示：`AI_DEMO.md`
            - 审查演示：`AI_CODE_REVIEW_DEMO.md`
            
            ### 下一步
            
            1. 获取 API Key（参考 ai config）
            2. 设置环境变量
            3. 开始使用 AI 功能
            
            ---
            *提示：运行 `ai config` 查看详细配置指南*
            """;
        
        return CommandResult.success("AI 助手信息")
                .withDisplayText(report);
    }
    
    private CommandResult showConfigGuide() {
        String report = """
            ## 🔑 API Key 配置指南
            
            ### 1. 阿里云百炼（推荐）
            
            **获取 API Key**:
            1. 访问 https://bailian.console.aliyun.com/
            2. 登录/注册阿里云账号
            3. 开通百炼服务
            4. 创建 API Key
            
            **配置**:
            ```bash
            export DASHSCOPE_API_KEY=sk-xxxxxxxxxxxxxxxx
            ```
            
            **价格**:
            - qwen-turbo: ¥0.002/千 tokens
            - qwen-plus: ¥0.004/千 tokens ⭐推荐
            - qwen-max: ¥0.04/千 tokens
            
            **免费额度**: 新用户 ¥20-100
            
            ---
            
            ### 2. DeepSeek
            
            **获取 API Key**:
            1. 访问 https://platform.deepseek.com/
            2. 注册账号
            3. 创建 API Key
            
            **配置**:
            ```bash
            export DEEPSEEK_API_KEY=sk-xxxxxxxxxxxxxxxx
            ```
            
            **价格**: ¥0.002/千 tokens
            
            ---
            
            ### 3. Kimi（月之暗面）
            
            **获取 API Key**:
            1. 访问 https://platform.moonshot.cn/
            2. 注册账号
            3. 创建 API Key
            
            **配置**:
            ```bash
            export KIMI_API_KEY=sk-xxxxxxxxxxxxxxxx
            ```
            
            ---
            
            ### 4. GLM（智谱 AI）
            
            **获取 API Key**:
            1. 访问 https://open.bigmodel.cn/
            2. 注册账号
            3. 创建 API Key
            
            **配置**:
            ```bash
            export ZHIPU_API_KEY=xxxxxxxxxxxxxxxx
            ```
            
            ---
            
            ### 5. OpenAI
            
            **配置**:
            ```bash
            export OPENAI_API_KEY=sk-xxxxxxxxxxxxxxxx
            ```
            
            ---
            
            ### 6. Claude
            
            **配置**:
            ```bash
            export ANTHROPIC_API_KEY=sk-ant-xxxxxxxxxxxxxxxx
            ```
            
            ---
            
            ## ✅ 验证配置
            
            ```bash
            # 检查环境变量
            echo $DASHSCOPE_API_KEY
            
            # 应该输出：sk-xxxxxxxx
            ```
            
            ## 📚 详细文档
            
            - 百炼配置：`BAILIAN_API_CONFIG.md`
            - AI 演示：`AI_DEMO.md`
            
            ## 🚀 下一步
            
            配置好 API Key 后，可以使用：
            - `ai code <需求>` - 生成代码
            - `ai review <代码>` - 审查代码
            - `ai ask <问题>` - 问题解答
            """;
        
        return CommandResult.success("配置指南")
                .withDisplayText(report);
    }
    
    private CommandResult showBailianGuide() {
        String report = """
            ## ☁️ 阿里云百炼配置详解
            
            ### 什么是阿里云百炼？
            
            阿里云百炼（Bailian）是阿里云的大模型服务平台，提供：
            - 通义千问（Qwen）系列模型
            - 通义万相（图像生成）
            - 通义听悟（语音处理）
            
            **官网**: https://bailian.console.aliyun.com/
            
            ---
            
            ### 步骤 1: 注册阿里云
            
            1. 访问 https://www.aliyun.com/
            2. 注册或登录账号
            
            ---
            
            ### 步骤 2: 开通百炼服务
            
            1. 访问 https://bailian.console.aliyun.com/
            2. 点击「开通服务」
            3. 同意服务协议
            
            ---
            
            ### 步骤 3: 创建 API Key
            
            1. 访问 https://dashscope.console.aliyun.com/apiKey
            2. 点击「创建新的 API Key」
            3. 复制 API Key（格式：`sk-xxxxxxxx`）
            
            ⚠️ **注意**: API Key 只显示一次，请妥善保存！
            
            ---
            
            ### 步骤 4: 配置到 JClaw
            
            ```bash
            # 添加到 ~/.zshrc 或 ~/.bashrc
            export DASHSCOPE_API_KEY=sk-xxxxxxxxxxxxxxxx
            
            # 使配置生效
            source ~/.zshrc
            ```
            
            ---
            
            ### 步骤 5: 验证配置
            
            ```bash
            # 检查环境变量
            echo $DASHSCOPE_API_KEY
            
            # 应该输出：sk-xxxxxxxxxxxxxxxx
            ```
            
            ---
            
            ### 可用模型
            
            | 模型 | 速度 | 质量 | 价格 | 推荐场景 |
            |------|------|------|------|---------|
            | qwen-turbo | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ¥0.002/千 | 快速响应 |
            | qwen-plus | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ¥0.004/千 | 日常开发 ⭐ |
            | qwen-max | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ¥0.04/千 | 高质量 |
            | qwen-coder-plus | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ¥0.035/千 | 代码专用 ⭐ |
            
            ---
            
            ### 价格说明
            
            **免费额度**: 新用户 ¥20-100
            
            **日常使用估算**:
            - 每天 10 次代码生成：约 ¥0.1-0.5
            - 每月成本：约 ¥3-15
            
            ---
            
            ### 常见问题
            
            **Q: API Key 无效？**
            A: 检查是否正确复制，是否已开通服务
            
            **Q: 额度不足？**
            A: 访问 https://usercenter2.aliyun.com/ 充值
            
            **Q: 选择哪个模型？**
            A: 日常开发用 qwen-plus，代码专用 qwen-coder-plus
            
            ---
            
            ### 相关文档
            
            - 完整配置：`BAILIAN_API_CONFIG.md`
            - AI 演示：`AI_DEMO.md`
            - 审查演示：`AI_CODE_REVIEW_DEMO.md`
            """;
        
        return CommandResult.success("百炼配置指南")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：ai
            别名：assistant, llm, code
            描述：AI 编程助手（大模型集成）
            
            用法：
              ai                  # 显示 AI 助手信息
              ai config           # 查看配置指南
              ai bailian          # 查看百炼配置
              ai help             # 显示帮助
            
            示例：
              ai
              ai config
              ai bailian
            """;
    }
}
