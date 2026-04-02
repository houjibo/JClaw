package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.*;

/**
 * 意图识别工具
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class IntentRecognitionTool extends Tool {
    
    /**
     * 意图类型
     */
    public enum IntentType {
        CODE_GENERATION("代码生成"),
        CODE_REVIEW("代码审查"),
        BUG_FIX("Bug 修复"),
        FEATURE_REQUEST("功能请求"),
        REFACTORING("代码重构"),
        TEST_GENERATION("测试生成"),
        DOCUMENTATION("文档编写"),
        EXPLANATION("代码解释"),
        OPTIMIZATION("性能优化"),
        SECURITY_AUDIT("安全审计"),
        DEPLOYMENT("部署操作"),
        DEBUGGING("调试问题"),
        UNKNOWN("未知意图");
        
        private final String displayName;
        
        IntentType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * 意图识别结果
     */
    public static class IntentResult {
        private final IntentType type;
        private final double confidence;
        private final String description;
        private final List<String> keywords;
        private final Map<String, Object> metadata;
        
        public IntentResult(IntentType type, double confidence, String description) {
            this.type = type;
            this.confidence = confidence;
            this.description = description;
            this.keywords = new ArrayList<>();
            this.metadata = new HashMap<>();
        }
        
        public IntentType getType() {
            return type;
        }
        
        public double getConfidence() {
            return confidence;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void addKeyword(String keyword) {
            this.keywords.add(keyword);
        }
        
        public List<String> getKeywords() {
            return keywords;
        }
        
        public void putMetadata(String key, Object value) {
            this.metadata.put(key, value);
        }
        
        public Map<String, Object> getMetadata() {
            return metadata;
        }
        
        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("type", type.name());
            map.put("typeDisplay", type.getDisplayName());
            map.put("confidence", confidence);
            map.put("description", description);
            map.put("keywords", keywords);
            map.put("metadata", metadata);
            return map;
        }
    }
    
    public IntentRecognitionTool() {
        this.name = "intent_recognition";
        this.description = "意图识别工具 - 分析用户输入，识别意图类型";
        this.category = ToolCategory.SYSTEM;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String input = params != null ? (String) params.get("input") : null;
        
        if (input == null || input.toString().isBlank()) {
            return ToolResult.error("请提供输入文本");
        }
        
        IntentResult result = recognizeIntent(input.toString());
        
        return ToolResult.success("意图识别完成");
    }
    
    /**
     * 识别意图
     */
    public IntentResult recognizeIntent(String input) {
        String lowerInput = input.toLowerCase();
        
        // 关键词匹配
        Map<IntentType, Integer> scores = new HashMap<>();
        List<String> matchedKeywords = new ArrayList<>();
        
        // 代码生成
        if (matches(lowerInput, "创建", "新建", "生成", "write", "create", "generate", "implement")) {
            addScore(scores, IntentType.CODE_GENERATION, 3);
            matchedKeywords.add("创建/生成");
        }
        
        // 代码审查
        if (matches(lowerInput, "审查", "review", "check", "audit", "分析代码")) {
            addScore(scores, IntentType.CODE_REVIEW, 3);
            matchedKeywords.add("审查");
        }
        
        // Bug 修复
        if (matches(lowerInput, "bug", "错误", "修复", "fix", "问题", "报错", "异常")) {
            addScore(scores, IntentType.BUG_FIX, 3);
            matchedKeywords.add("Bug 修复");
        }
        
        // 功能请求
        if (matches(lowerInput, "功能", "feature", "需求", "添加", "add")) {
            addScore(scores, IntentType.FEATURE_REQUEST, 2);
            matchedKeywords.add("功能请求");
        }
        
        // 代码重构
        if (matches(lowerInput, "重构", "refactor", "优化结构", "整理")) {
            addScore(scores, IntentType.REFACTORING, 3);
            matchedKeywords.add("重构");
        }
        
        // 测试生成
        if (matches(lowerInput, "测试", "test", "单元测试", "unit test", "spec")) {
            addScore(scores, IntentType.TEST_GENERATION, 3);
            matchedKeywords.add("测试");
        }
        
        // 文档编写
        if (matches(lowerInput, "文档", "doc", "readme", "注释", "comment")) {
            addScore(scores, IntentType.DOCUMENTATION, 2);
            matchedKeywords.add("文档");
        }
        
        // 代码解释
        if (matches(lowerInput, "解释", "explain", "什么意思", "理解", "understand")) {
            addScore(scores, IntentType.EXPLANATION, 3);
            matchedKeywords.add("解释");
        }
        
        // 性能优化
        if (matches(lowerInput, "性能", "performance", "优化", "optimize", "加速", "快")) {
            addScore(scores, IntentType.OPTIMIZATION, 3);
            matchedKeywords.add("性能优化");
        }
        
        // 安全审计
        if (matches(lowerInput, "安全", "security", "漏洞", "vulnerability", "审计")) {
            addScore(scores, IntentType.SECURITY_AUDIT, 3);
            matchedKeywords.add("安全");
        }
        
        // 部署操作
        if (matches(lowerInput, "部署", "deploy", "发布", "release", "上线")) {
            addScore(scores, IntentType.DEPLOYMENT, 3);
            matchedKeywords.add("部署");
        }
        
        // 调试问题
        if (matches(lowerInput, "调试", "debug", "排查", "追踪", "trace")) {
            addScore(scores, IntentType.DEBUGGING, 3);
            matchedKeywords.add("调试");
        }
        
        // 找出最高分
        IntentType bestType = IntentType.UNKNOWN;
        int maxScore = 0;
        
        for (Map.Entry<IntentType, Integer> entry : scores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                bestType = entry.getKey();
            }
        }
        
        // 计算置信度
        double confidence = Math.min(1.0, maxScore / 5.0);
        
        // 创建结果
        IntentResult result = new IntentResult(
                bestType,
                confidence,
                String.format("识别为：%s（置信度：%.0f%%）", bestType.getDisplayName(), confidence * 100)
        );
        
        for (String keyword : matchedKeywords) {
            result.addKeyword(keyword);
        }
        
        result.putMetadata("inputLength", input.length());
        result.putMetadata("matchedCount", matchedKeywords.size());
        
        return result;
    }
    
    private boolean matches(String input, String... keywords) {
        for (String keyword : keywords) {
            if (input.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    private void addScore(Map<IntentType, Integer> scores, IntentType type, int score) {
        scores.merge(type, score, Integer::sum);
    }
    
    private String formatIntentReport(IntentResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 意图识别结果\n\n");
        sb.append(String.format("**识别类型**: %s\n", result.getType().getDisplayName()));
        sb.append(String.format("**置信度**: %.0f%%\n", result.getConfidence() * 100));
        sb.append(String.format("**描述**: %s\n\n", result.getDescription()));
        
        if (!result.getKeywords().isEmpty()) {
            sb.append("**匹配关键词**:\n");
            for (String keyword : result.getKeywords()) {
                sb.append(String.format("- %s\n", keyword));
            }
            sb.append("\n");
        }
        
        sb.append("### 建议操作\n\n");
        sb.append(getSuggestedActions(result.getType()));
        
        return sb.toString();
    }
    
    private String getSuggestedActions(IntentType type) {
        return switch (type) {
            case CODE_GENERATION -> "- 使用 FileWriteTool 创建文件\n- 使用 BashTool 运行代码\n- 使用 TestGenerationTool 生成测试";
            case CODE_REVIEW -> "- 使用 FileReadTool 读取代码\n- 使用 ReviewTool 进行审查\n- 生成审查报告";
            case BUG_FIX -> "- 使用 DebugTool 调试\n- 使用 FileReadTool 查看相关代码\n- 使用 FileEditTool 修复";
            case TEST_GENERATION -> "- 使用 FileReadTool 读取源代码\n- 使用 TestGenerationTool 生成测试\n- 使用 BashTool 运行测试";
            case DOCUMENTATION -> "- 使用 FileReadTool 读取代码\n- 使用 DocumentationTool 生成文档\n- 使用 FileWriteTool 保存";
            default -> "- 请提供更多详细信息\n- 或选择其他工具";
        };
    }
}
