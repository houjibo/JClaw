package com.jclaw.controller;

import com.jclaw.memory.MemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 记忆 REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/memory")
@RequiredArgsConstructor
public class MemoryController {
    
    private final MemoryService memoryService;
    
    /**
     * 保存记忆
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> saveMemory(@RequestBody MemoryRequest request) {
        log.info("保存记忆：{}", request.getContent());
        memoryService.save(request.getContent());
        return ResponseEntity.ok(Map.of("status", "success"));
    }
    
    /**
     * 搜索记忆
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, String>> searchMemory(@RequestParam String query) {
        log.info("搜索记忆：{}", query);
        String result = memoryService.search(query);
        return ResponseEntity.ok(Map.of("result", result));
    }
    
    /**
     * 今日日志
     */
    @GetMapping("/today")
    public ResponseEntity<Map<String, String>> getTodayLog() {
        String log = memoryService.getTodayLog();
        return ResponseEntity.ok(Map.of("log", log));
    }
    
    /**
     * 追加到今日日志
     */
    @PostMapping("/today")
    public ResponseEntity<Map<String, String>> appendToTodayLog(@RequestBody LogRequest request) {
        log.info("追加日志：{} - {}", request.getSection(), request.getContent());
        memoryService.appendToTodayLog(request.getSection(), request.getContent());
        return ResponseEntity.ok(Map.of("status", "success"));
    }
    
    public static class MemoryRequest {
        private String content;
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    public static class LogRequest {
        private String section;
        private String content;
        public String getSection() { return section; }
        public void setSection(String section) { this.section = section; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
