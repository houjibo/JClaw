package com.jclaw.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 向量相似度计算（简化版 TF-IDF + 余弦相似度）
 */
@Slf4j
@Service
public class VectorSimilarityService {
    
    /**
     * 计算两个文本的余弦相似度
     */
    public double cosineSimilarity(String text1, String text2) {
        Map<String, Integer> vector1 = computeTF(text1);
        Map<String, Integer> vector2 = computeTF(text2);
        
        return cosineSimilarity(vector1, vector2);
    }
    
    /**
     * 计算两个词频向量的余弦相似度
     */
    public double cosineSimilarity(Map<String, Integer> vector1, Map<String, Integer> vector2) {
        // 计算点积
        double dotProduct = 0.0;
        for (String term : vector1.keySet()) {
            if (vector2.containsKey(term)) {
                dotProduct += vector1.get(term) * vector2.get(term);
            }
        }
        
        // 计算模长
        double norm1 = Math.sqrt(vector1.values().stream()
            .mapToDouble(v -> v * v)
            .sum());
        double norm2 = Math.sqrt(vector2.values().stream()
            .mapToDouble(v -> v * v)
            .sum());
        
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        
        return dotProduct / (norm1 * norm2);
    }
    
    /**
     * 计算词频（TF）
     */
    public Map<String, Integer> computeTF(String text) {
        Map<String, Integer> tf = new HashMap<>();
        String[] words = tokenize(text);
        
        for (String word : words) {
            tf.put(word, tf.getOrDefault(word, 0) + 1);
        }
        
        return tf;
    }
    
    /**
     * 分词（简化版）
     */
    private String[] tokenize(String text) {
        if (text == null) {
            return new String[0];
        }
        
        // 中文按字符分词，英文按空格分词
        return text.toLowerCase()
            .replaceAll("[^\\w\\s\\u4e00-\\u9fa5]", "")
            .split("\\s+");
    }
    
    /**
     * 搜索最相似的文档
     */
    public List<SearchResult> searchSimilar(String query, List<Document> documents, int topK) {
        List<SearchResult> results = new ArrayList<>();
        
        for (Document doc : documents) {
            double similarity = cosineSimilarity(query, doc.getContent());
            if (similarity > 0.1) { // 阈值
                results.add(new SearchResult(doc.getId(), doc.getTitle(), similarity));
            }
        }
        
        // 按相似度排序
        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        
        // 返回前 K 个
        return results.subList(0, Math.min(topK, results.size()));
    }
    
    /**
     * 搜索结果
     */
    public static class SearchResult {
        private final String id;
        private final String title;
        private final double score;
        
        public SearchResult(String id, String title, double score) {
            this.id = id;
            this.title = title;
            this.score = score;
        }
        
        public String getId() { return id; }
        public String getTitle() { return title; }
        public double getScore() { return score; }
    }
    
    /**
     * 文档
     */
    public static class Document {
        private final String id;
        private final String title;
        private final String content;
        
        public Document(String id, String title, String content) {
            this.id = id;
            this.title = title;
            this.content = content;
        }
        
        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getContent() { return content; }
    }
}
