package com.jclaw.controller;

import com.jclaw.memory.MemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    private final com.jclaw.memory.MemorySearchService searchService;
    private final com.jclaw.memory.MemoryExtractionService extractionService;
    
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
    
    /**
     * 语义搜索
     */
    @GetMapping("/semantic-search")
    public ResponseEntity<Map<String, Object>> semanticSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "./memory") String path) {
        log.info("语义搜索：{}", query);
        
        List<com.jclaw.memory.MemorySearchService.SearchResult> results = searchService.search(query, path);
        
        return ResponseEntity.ok(Map.of(
            "query", query,
            "count", results.size(),
            "results", results
        ));
    }
    
    /**
     * 从对话萃取记忆
     */
    @PostMapping("/extract")
    public ResponseEntity<Map<String, Object>> extractMemory(@RequestBody ExtractRequest request) {
        log.info("萃取记忆");
        
        com.jclaw.memory.MemoryExtractionService.ExtractionResult result = 
            extractionService.extract(request.getContent(), request.getPath());
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "preferences", result.getPreferences(),
            "decisions", result.getDecisions(),
            "lessons", result.getLessons()
        ));
    }
    
    /**
     * 从日志萃取
     */
    @PostMapping("/extract-from-log")
    public ResponseEntity<Map<String, String>> extractFromLog(
            @RequestParam(defaultValue = "./memory") String path) {
        log.info("从日志萃取记忆");
        
        extractionService.extractFromDailyLog(path);
        
        return ResponseEntity.ok(Map.of("status", "success"));
    }
    
    public static class ExtractRequest {
        private String content;
        private String path = "./memory";
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
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
