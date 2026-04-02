package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 切换和管理 AI 模型
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class ModelCommand extends Command {
    
    // 支持的模型列表
    private static final List<Map<String, Object>> MODELS = new ArrayList<>();
    static {
        Map<String, Object> model1 = new HashMap<>();
        model1.put("id", "qwen3.5-plus");
        model1.put("name", "Qwen 3.5 Plus");
        model1.put("provider", "ModelStudio");
        model1.put("contextWindow", "1M");
        model1.put("speed", "快");
        MODELS.add(model1);
        
        Map<String, Object> model2 = new HashMap<>();
        model2.put("id", "glm-4.7");
        model2.put("name", "GLM 4.7");
        model2.put("provider", "Zhipu");
        model2.put("contextWindow", "200K");
        model2.put("speed", "快");
        MODELS.add(model2);
        
        Map<String, Object> model3 = new HashMap<>();
        model3.put("id", "kimi-k2.5");
        model3.put("name", "Kimi K2.5");
        model3.put("provider", "Moonshot");
        model3.put("contextWindow", "256K");
        model3.put("speed", "中");
        MODELS.add(model3);
    }
    
    private static String currentModel = "qwen3.5-plus";
    
    public ModelCommand() {
        this.name = "model";
        this.description = "模型切换和管理";
        this.aliases = Arrays.asList("models", "llm");
        this.category = CommandCategory.CONFIG;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (args == null || args.isBlank()) {
            return listModels();
        }
        
        String[] parts = args.trim().split("\\s+");
        String action = parts[0];
        
        return switch (action) {
            case "set", "use", "switch" -> setModel(parts.length > 1 ? parts[1] : null);
            case "info" -> showModelInfo(parts.length > 1 ? parts[1] : null);
            case "compare" -> compareModels();
            default -> setModel(action);
        };
    }
    
    private CommandResult listModels() {
        StringBuilder sb = new StringBuilder();
        sb.append("## 可用模型列表\n\n");
        sb.append("当前模型：**").append(currentModel).append("**\n\n");
        sb.append("| ID | 名称 | 提供商 | 上下文 | 速度 |\n");
        sb.append("|----|------|--------|--------|------|\n");
        
        for (Map<String, Object> model : MODELS) {
            String marker = model.get("id").equals(currentModel) ? "👉" : "  ";
            sb.append(String.format("| %s %s | %s | %s | %s | %s |\n",
                    marker,
                    model.get("id"),
                    model.get("name"),
                    model.get("provider"),
                    model.get("contextWindow"),
                    model.get("speed")));
        }
        
        sb.append("\n使用 `model <模型 ID>` 切换模型");
        
        return CommandResult.success("模型列表")
                .withData("models", MODELS)
                .withData("currentModel", currentModel)
                .withDisplayText(sb.toString());
    }
    
    private CommandResult setModel(String modelId) {
        if (modelId == null) {
            return CommandResult.error("请指定模型 ID，使用 `model` 查看可用模型");
        }
        
        boolean found = MODELS.stream().anyMatch(m -> m.get("id").equals(modelId));
        if (!found) {
            return CommandResult.error("未知模型：" + modelId);
        }
        
        String oldModel = currentModel;
        currentModel = modelId;
        
        return CommandResult.success("已切换模型：" + oldModel + " → " + modelId)
                .withData("oldModel", oldModel)
                .withData("newModel", modelId);
    }
    
    private CommandResult showModelInfo(String modelId) {
        if (modelId == null) {
            modelId = currentModel;
        }
        
        Map<String, Object> model = null;
        for (Map<String, Object> m : MODELS) {
            if (m.get("id").equals(modelId)) {
                model = m;
                break;
            }
        }
        
        if (model == null) {
            return CommandResult.error("未知模型：" + modelId);
        }
        
        boolean isCurrent = model.get("id").equals(currentModel);
        
        String report = String.format("""
            ## 模型信息：%s
            
            | 属性 | 值 |
            |------|------|
            | 名称 | %s |
            | 提供商 | %s |
            | 上下文窗口 | %s |
            | 推理速度 | %s |
            | 当前使用 | %s |
            
            ### 适用场景
            
            - 代码生成：✅ 推荐
            - 代码审查：✅ 推荐
            - 文本创作：✅ 推荐
            - 数据分析：✅ 推荐
            """,
                model.get("id"),
                model.get("name"),
                model.get("provider"),
                model.get("contextWindow"),
                model.get("speed"),
                isCurrent ? "✅ 当前使用" : "❌"
        );
        
        return CommandResult.success("模型信息")
                .withData("model", model)
                .withDisplayText(report);
    }
    
    private CommandResult compareModels() {
        String report = """
            ## 模型对比
            
            ### 性能对比
            
            | 模型 | 代码能力 | 文本能力 | 推理速度 | 成本 |
            |------|---------|---------|---------|------|
            | Qwen 3.5 Plus | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | 中 |
            | GLM 4.7 | ⭐⭐⭐⭐☆ | ⭐⭐⭐⭐☆ | ⭐⭐⭐⭐⭐ | 低 |
            | Kimi K2.5 | ⭐⭐⭐⭐☆ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐☆☆ | 中 |
            
            ### 推荐场景
            
            | 场景 | 推荐模型 |
            |------|---------|
            | 代码生成 | Qwen 3.5 Plus |
            | 代码审查 | Qwen 3.5 Plus |
            | 长文本分析 | Kimi K2.5 |
            | 快速响应 | GLM 4.7 |
            | 复杂推理 | Qwen 3.5 Plus |
            
            ### 上下文窗口
            
            - **Qwen 3.5 Plus**: 1M tokens - 适合超大项目
            - **Kimi K2.5**: 256K tokens - 适合长文档
            - **GLM 4.7**: 200K tokens - 适合中等项目
            """;
        
        return CommandResult.success("模型对比")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：model
            别名：models, llm
            描述：模型切换和管理
            
            用法：
              model                     # 列出所有模型
              model <模型 ID>           # 切换模型
              model info [模型 ID]      # 查看模型信息
              model compare             # 对比模型
            
            示例：
              model
              model qwen3.5-plus
              model info glm-4.7
              model compare
            """;
    }
}
