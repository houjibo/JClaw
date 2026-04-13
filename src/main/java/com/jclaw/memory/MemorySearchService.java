package com.jclaw.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 记忆搜索服务 - 语义搜索（简化版：关键词 + 相似度）
 */
@Slf4j
@Service
public class MemorySearchService {
    
    /**
     * 语义搜索（简化实现：关键词匹配 + TF-IDF 相似度）
     */
    public List<SearchResult> search(String query, String memoryPath) {
        try {
            Path path = Paths.get(memoryPath);
            if (!Files.exists(path)) {
                return Collections.emptyList();
            }
            
            // 读取所有记忆文件
            List<Path> files = new ArrayList<>();
            try (var stream = Files.walk(path)) {
                stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".md"))
                    .forEach(files::add);
            }
            
            List<SearchResult> results = new ArrayList<>();
            
            for (Path file : files) {
                String content = Files.readString(file);
                double score = calculateSimilarity(query, content);
                
                if (score > 0.3) { // 阈值
                    SearchResult result = new SearchResult();
                    result.setFile(file.getFileName().toString());
                    result.setScore(score);
                    result.setSnippet(extractSnippet(content, query));
                    results.add(result);
                }
            }
            
            // 按相似度排序
            results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
            
            log.info("语义搜索：'{}' 找到 {} 个结果", query, results.size());
            
            return results;
            
        } catch (IOException e) {
            log.error("搜索记忆失败", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 计算相似度（简化 TF-IDF）
     */
    private double calculateSimilarity(String query, String content) {
        // 分词
        Set<String> queryTerms = tokenize(query);
        Set<String> contentTerms = tokenize(content);
        
        if (queryTerms.isEmpty() || contentTerms.isEmpty()) {
            return 0.0;
        }
        
        // Jaccard 相似度
        Set<String> intersection = new HashSet<>(queryTerms);
        intersection.retainAll(contentTerms);
        
        Set<String> union = new HashSet<>(queryTerms);
        union.addAll(contentTerms);
        
        return (double) intersection.size() / union.size();
    }
    
    /**
     * 分词（简化：按空格和标点分割）
     */
    private Set<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase().split("[\\s\\p{Punct}]+"))
            .filter(word -> word.length() > 1) // 过滤单字符
            .filter(word -> !isStopWord(word)) // 过滤停用词
            .collect(Collectors.toSet());
    }
    
    /**
     * 停用词
     */
    private boolean isStopWord(String word) {
        return Set.of("的", "了", "是", "在", "和", "与", "或", "等", "the", "a", "an", "is", "are").contains(word);
    }
    
    /**
     * 提取摘要
     */
    private String extractSnippet(String content, String query) {
        String[] lines = content.split("\n");
        Set<String> queryTerms = tokenize(query);
        
        // 找到包含最多关键词的行
        String bestLine = "";
        int maxMatches = 0;
        
        for (String line : lines) {
            Set<String> lineTerms = tokenize(line);
            lineTerms.retainAll(queryTerms);
            
            if (lineTerms.size() > maxMatches) {
                maxMatches = lineTerms.size();
                bestLine = line;
            }
        }
        
        // 截取长度
        if (bestLine.length() > 200) {
            bestLine = bestLine.substring(0, 200) + "...";
        }
        
        return bestLine;
    }
    
    /**
     * 搜索结果
     */
    public static class SearchResult {
        private String file;
        private double score;
        private String snippet;
        
        public String getFile() { return file; }
        public void setFile(String file) { this.file = file; }
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
        public String getSnippet() { return snippet; }
        public void setSnippet(String snippet) { this.snippet = snippet; }
    }
}
