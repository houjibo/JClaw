package com.jclaw.skill.remote;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 技能注册中心服务（远程版）
 * 
 * 功能：
 * - 技能注册到远程中心
 * - 技能搜索/发现
 * - 技能下载
 * - 版本管理
 * - 流行度统计
 * 
 * @author JClaw
 * @since 2026-04-15
 */
@Slf4j
@Service
public class SkillRegistryService {
    
    /**
     * 远程注册中心配置
     */
    private String registryUrl = "https://skills.jclaw.dev/api";
    private boolean enabled = false;
    
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 技能缓存
     */
    private final Map<String, SkillInfo> skillCache = new ConcurrentHashMap<>();
    private final long cacheTtlMs = 300000; // 5 分钟
    private final Map<String, Long> cacheTime = new ConcurrentHashMap<>();
    
    /**
     * 技能信息
     */
    public static class SkillInfo {
        public String id;
        public String name;
        public String version;
        public String description;
        public String author;
        public List<String> tags;
        public long downloads;
        public double rating;
        public String downloadUrl;
        public long createdAt;
        public long updatedAt;
    }
    
    public SkillRegistryService() {
        log.info("技能注册中心服务初始化");
    }
    
    /**
     * 初始化配置
     */
    public void initialize(Map<String, String> config) {
        this.registryUrl = config.getOrDefault("registryUrl", "https://skills.jclaw.dev/api");
        this.enabled = config.get("enabled") != null && Boolean.parseBoolean(config.get("enabled"));
        
        if (enabled) {
            log.info("技能注册中心已启用：{}", registryUrl);
        } else {
            log.info("技能注册中心未启用，使用本地模式");
        }
    }
    
    /**
     * 注册技能到远程中心
     */
    public boolean registerSkill(String name, String version, String description, String author, List<String> tags) {
        if (!enabled) {
            log.warn("技能注册中心未启用");
            return false;
        }
        
        try {
            String url = registryUrl + "/skills";
            
            Map<String, Object> request = new HashMap<>();
            request.put("name", name);
            request.put("version", version);
            request.put("description", description);
            request.put("author", author);
            request.put("tags", tags);
            
            HttpRequest request_ = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(request)))
                .build();
            
            HttpResponse<String> response = httpClient.send(request_, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 201) {
                log.info("技能注册成功：{} v{}", name, version);
                return true;
            } else {
                log.error("技能注册失败：{} - {}", response.statusCode(), response.body());
                return false;
            }
        } catch (Exception e) {
            log.error("技能注册异常", e);
            return false;
        }
    }
    
    /**
     * 搜索技能
     */
    public List<SkillInfo> searchSkills(String query, List<String> tags) {
        // 检查缓存
        String cacheKey = "search_" + query + "_" + (tags != null ? tags.hashCode() : 0);
        if (skillCache.containsKey(cacheKey) && 
            System.currentTimeMillis() - cacheTime.get(cacheKey) < cacheTtlMs) {
            log.debug("命中缓存：{}", cacheKey);
            return new ArrayList<>(skillCache.values());
        }
        
        if (!enabled) {
            log.warn("技能注册中心未启用");
            return Collections.emptyList();
        }
        
        try {
            String url = registryUrl + "/skills/search?q=" + URLEncoder.encode(query, "UTF-8");
            if (tags != null && !tags.isEmpty()) {
                url += "&tags=" + String.join(",", tags);
            }
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonNode skills = objectMapper.readTree(response.body());
                List<SkillInfo> result = new ArrayList<>();
                
                for (JsonNode skill : skills) {
                    SkillInfo info = new SkillInfo();
                    info.id = skill.path("id").asText();
                    info.name = skill.path("name").asText();
                    info.version = skill.path("version").asText();
                    info.description = skill.path("description").asText();
                    info.author = skill.path("author").asText();
                    info.tags = objectMapper.convertValue(skill.path("tags"), new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
                    info.downloads = skill.path("downloads").asLong();
                    info.rating = skill.path("rating").asDouble();
                    info.downloadUrl = skill.path("downloadUrl").asText();
                    
                    result.add(info);
                    skillCache.put(info.id, info);
                }
                
                cacheTime.put(cacheKey, System.currentTimeMillis());
                log.info("搜索到 {} 个技能", result.size());
                return result;
            } else {
                log.error("技能搜索失败：{} - {}", response.statusCode(), response.body());
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("技能搜索异常", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 获取技能详情
     */
    public SkillInfo getSkillInfo(String skillId) {
        // 检查缓存
        if (skillCache.containsKey(skillId) && 
            System.currentTimeMillis() - cacheTime.get(skillId) < cacheTtlMs) {
            return skillCache.get(skillId);
        }
        
        if (!enabled) {
            return null;
        }
        
        try {
            String url = registryUrl + "/skills/" + skillId;
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonNode skill = objectMapper.readTree(response.body());
                SkillInfo info = objectMapper.convertValue(skill, SkillInfo.class);
                
                skillCache.put(skillId, info);
                cacheTime.put(skillId, System.currentTimeMillis());
                
                return info;
            } else {
                log.error("获取技能详情失败：{} - {}", response.statusCode(), response.body());
                return null;
            }
        } catch (Exception e) {
            log.error("获取技能详情异常", e);
            return null;
        }
    }
    
    /**
     * 下载技能
     */
    public byte[] downloadSkill(String skillId, String version) {
        if (!enabled) {
            log.warn("技能注册中心未启用");
            return null;
        }
        
        try {
            String url = registryUrl + "/skills/" + skillId + "/download?version=" + version;
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
            
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            
            if (response.statusCode() == 200) {
                log.info("技能下载成功：{} v{}", skillId, version);
                return response.body();
            } else {
                log.error("技能下载失败：{} - {}", response.statusCode(), new String(response.body()));
                return null;
            }
        } catch (Exception e) {
            log.error("技能下载异常", e);
            return null;
        }
    }
    
    /**
     * 获取热门技能
     */
    public List<SkillInfo> getPopularSkills(int limit) {
        String cacheKey = "popular_" + limit;
        
        // 检查缓存
        if (skillCache.containsKey(cacheKey) && 
            System.currentTimeMillis() - cacheTime.get(cacheKey) < cacheTtlMs) {
            return new ArrayList<>(skillCache.values());
        }
        
        if (!enabled) {
            return Collections.emptyList();
        }
        
        try {
            String url = registryUrl + "/skills/popular?limit=" + limit;
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonNode skills = objectMapper.readTree(response.body());
                List<SkillInfo> result = new ArrayList<>();
                
                for (JsonNode skill : skills) {
                    SkillInfo info = objectMapper.convertValue(skill, SkillInfo.class);
                    result.add(info);
                }
                
                cacheTime.put(cacheKey, System.currentTimeMillis());
                return result;
            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("获取热门技能异常", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 检查服务状态
     */
    public boolean isConnected() {
        if (!enabled) {
            return false;
        }
        
        try {
            String url = registryUrl + "/health";
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            log.error("检查注册中心连接失败", e);
            return false;
        }
    }
}
