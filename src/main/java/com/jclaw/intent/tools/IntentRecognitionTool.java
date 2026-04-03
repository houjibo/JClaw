package com.jclaw.intent.tools;

import com.jclaw.core.Tool;
import com.jclaw.core.ToolContext;
import com.jclaw.core.ToolResult;
import com.jclaw.intent.entity.Intent;
import com.jclaw.intent.service.IntentRecognitionService;
import com.jclaw.intent.service.TaskDecompositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 意图识别工具
 */
@Component
@Slf4j
public class IntentRecognitionTool extends Tool {

    @Autowired
    private IntentRecognitionService intentService;

    @Autowired
    private TaskDecompositionService taskDecompositionService;

    @Override
    public String getName() {
        return "intent_recognition";
    }

    @Override
    public String getDescription() {
        return "识别用户意图并分解为可执行任务";
    }

    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String userInput = (String) params.get("input");
        
        try {
            // 1. 识别意图
            Intent intent = intentService.recognize(userInput);
            if (intent == null) {
                return ToolResult.error("无法识别意图");
            }

            // 2. 生成澄清问题
            List<String> questions = intentService.generateClarificationQuestions(intent);

            // 3. 分解任务
            List<Map<String, Object>> tasks = taskDecompositionService.decompose(intent);

            // 4. 为每个任务分配 Agent
            for (Map<String, Object> task : tasks) {
                String agent = taskDecompositionService.assignAgent(task);
                task.put("assignedAgent", agent);
            }

            return ToolResult.success("意图识别成功", tasks.toString());

        } catch (Exception e) {
            log.error("意图识别失败", e);
            return ToolResult.error("意图识别失败：" + e.getMessage());
        }
    }
}
