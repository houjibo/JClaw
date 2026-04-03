package com.jclaw.intent.controller;

import com.jclaw.common.entity.Result;
import com.jclaw.intent.entity.Intent;
import com.jclaw.intent.service.IntentRecognitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 意图 REST API 控制器
 */
@RestController
@RequestMapping("/api/intents")
public class IntentController {

    private static final Logger log = LoggerFactory.getLogger(IntentController.class);
    @Autowired
    private IntentRecognitionService intentService;

    /**
     * 识别意图
     */
    @PostMapping("/recognize")
    public Result<Intent> recognizeIntent(@RequestBody Map<String, String> request) {
        String userInput = request.get("input");
        Intent intent = intentService.recognize(userInput);
        return Result.success(intent);
    }

    /**
     * 获取意图详情
     */
    @GetMapping("/{id}")
    public Result<Intent> getIntent(@PathVariable String id) {
        Intent intent = intentService.getIntent(id);
        if (intent == null) {
            return Result.error("意图不存在");
        }
        return Result.success(intent);
    }

    /**
     * 查询意图列表
     */
    @GetMapping
    public Result<List<Intent>> listIntents(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        List<Intent> intents = intentService.listIntents(page, size);
        return Result.success(intents);
    }

    /**
     * 生成澄清问题
     */
    @PostMapping("/{id}/clarify")
    public Result<List<String>> generateClarificationQuestions(@PathVariable String id) {
        Intent intent = intentService.getIntent(id);
        if (intent == null) {
            return Result.error("意图不存在");
        }
        List<String> questions = intentService.generateClarificationQuestions(intent);
        return Result.success(questions);
    }

    /**
     * 创建意图
     */
    @PostMapping
    public Result<Intent> createIntent(@RequestBody Intent intent) {
        Intent created = intentService.createIntent(intent);
        return Result.success(created);
    }

    /**
     * 更新意图
     */
    @PutMapping("/{id}")
    public Result<Void> updateIntent(
        @PathVariable String id,
        @RequestBody Intent intent
    ) {
        intent.setId(id);
        intentService.updateIntent(intent);
        return Result.success();
    }

    /**
     * 删除意图
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteIntent(@PathVariable String id) {
        intentService.deleteIntent(id);
        return Result.success();
    }
}
