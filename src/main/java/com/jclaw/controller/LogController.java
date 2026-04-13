package com.jclaw.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * 日志 REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {
    
    /**
     * 列出日志文件
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listLogs() {
        List<Map<String, Object>> logs = new ArrayList<>();
        
        try {
            Path logDir = Paths.get("logs");
            if (!Files.exists(logDir)) {
                return ResponseEntity.ok(logs);
            }
            
            try (Stream<Path> paths = Files.list(logDir)) {
                paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".log"))
                    .forEach(p -> {
                        try {
                            Map<String, Object> info = new HashMap<>();
                            info.put("name", p.getFileName().toString());
                            info.put("path", p.toString());
                            info.put("size", Files.size(p));
                            info.put("lastModified", Files.getLastModifiedTime(p).toString());
                            logs.add(info);
                        } catch (IOException e) {
                            log.warn("读取日志文件失败：{}", p, e);
                        }
                    });
            }
            
            // 按修改时间排序
            logs.sort((a, b) -> {
                String aTime = (String) a.get("lastModified");
                String bTime = (String) b.get("lastModified");
                return bTime.compareTo(aTime);
            });
            
        } catch (IOException e) {
            log.error("列出日志失败", e);
        }
        
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 查看日志内容
     */
    @GetMapping("/{filename}")
    public ResponseEntity<Map<String, Object>> viewLog(
            @PathVariable String filename,
            @RequestParam(defaultValue = "100") int lines,
            @RequestParam(required = false) String search) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Path logFile = Paths.get("logs", filename);
            
            // 安全检查：防止路径遍历
            if (!logFile.toAbsolutePath().startsWith(Paths.get("logs").toAbsolutePath())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "非法文件名"));
            }
            
            if (!Files.exists(logFile)) {
                return ResponseEntity.notFound().build();
            }
            
            List<String> logLines = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(logFile)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 搜索过滤
                    if (search != null && !line.contains(search)) {
                        continue;
                    }
                    logLines.add(line);
                }
            }
            
            // 限制行数
            int start = Math.max(0, logLines.size() - lines);
            List<String> limitedLines = logLines.subList(start, logLines.size());
            
            result.put("filename", filename);
            result.put("totalLines", logLines.size());
            result.put("returnedLines", limitedLines.size());
            result.put("content", String.join("\n", limitedLines));
            
            if (search != null) {
                result.put("search", search);
                result.put("matchedLines", limitedLines.size());
            }
            
            return ResponseEntity.ok(result);
            
        } catch (IOException e) {
            log.error("查看日志失败：{}", filename, e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "读取失败：" + e.getMessage()));
        }
    }
    
    /**
     * 搜索日志（跨文件）
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchLogs(
            @RequestParam String query,
            @RequestParam(defaultValue = "50") int maxResults) {
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> matches = new ArrayList<>();
        
        try {
            Path logDir = Paths.get("logs");
            if (!Files.exists(logDir)) {
                return ResponseEntity.ok(Map.of("matches", Collections.emptyList()));
            }
            
            int count = 0;
            try (Stream<Path> paths = Files.walk(logDir)) {
                for (Path path : paths.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".log"))
                        .limit(10).toList()) {
                    
                    try (BufferedReader reader = Files.newBufferedReader(path)) {
                        String line;
                        int lineNum = 0;
                        while ((line = reader.readLine()) != null && count < maxResults) {
                            lineNum++;
                            if (line.toLowerCase().contains(query.toLowerCase())) {
                                Map<String, String> match = new HashMap<>();
                                match.put("file", path.getFileName().toString());
                                match.put("line", String.valueOf(lineNum));
                                match.put("content", line);
                                matches.add(match);
                                count++;
                            }
                        }
                    }
                }
            }
            
            result.put("query", query);
            result.put("totalMatches", matches.size());
            result.put("matches", matches);
            
            return ResponseEntity.ok(result);
            
        } catch (IOException e) {
            log.error("搜索日志失败", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "搜索失败：" + e.getMessage()));
        }
    }
    
    /**
     * 下载日志文件
     */
    @GetMapping("/{filename}/download")
    public ResponseEntity<Resource> downloadLog(@PathVariable String filename) {
        try {
            Path logFile = Paths.get("logs", filename);
            
            // 安全检查
            if (!logFile.toAbsolutePath().startsWith(Paths.get("logs").toAbsolutePath())) {
                return ResponseEntity.badRequest().build();
            }
            
            if (!Files.exists(logFile)) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new PathMatchingResourcePatternResolver()
                .getResource("file:" + logFile.toAbsolutePath());
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(resource);
            
        } catch (Exception e) {
            log.error("下载日志失败：{}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
