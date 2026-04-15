package com.jclaw.integration.github;

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
 * GitHub 集成服务
 * 
 * 功能：
 * - Issues 管理（创建/查询/更新/关闭）
 * - PRs 管理（创建/查询/合并）
 * - Actions 触发
 * - 仓库管理
 * - Webhook 接收
 * 
 * @author JClaw
 * @since 2026-04-15
 */
@Slf4j
@Service
public class GitHubService {
    
    private static final String API_BASE = "https://api.github.com";
    
    private String token;
    private String owner;
    private String repo;
    private boolean enabled = false;
    
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 缓存
     */
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private final long cacheTtlMs = 60000; // 1 分钟
    private final Map<String, Long> cacheTime = new ConcurrentHashMap<>();
    
    public GitHubService() {
        log.info("GitHub 集成服务初始化");
    }
    
    /**
     * 初始化配置
     */
    public void initialize(Map<String, String> config) {
        this.token = config.get("token");
        this.owner = config.get("owner");
        this.repo = config.get("repo");
        this.enabled = config.get("enabled") != null && Boolean.parseBoolean(config.get("enabled"));
        
        if (enabled && token != null) {
            log.info("GitHub 集成已启用：{}/{}", owner, repo);
        } else {
            log.warn("GitHub 集成未启用或缺少配置");
        }
    }
    
    // ==================== Issues 管理 ====================
    
    /**
     * 创建 Issue
     */
    public Map<String, Object> createIssue(String title, String body, List<String> labels) throws IOException, InterruptedException {
        String url = API_BASE + "/repos/" + owner + "/" + repo + "/issues";
        
        Map<String, Object> request = new HashMap<>();
        request.put("title", title);
        request.put("body", body);
        if (labels != null) {
            request.put("labels", labels);
        }
        
        JsonNode response = postJson(url, request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", response.path("id").asLong());
        result.put("number", response.path("number").asInt());
        result.put("title", response.path("title").asText());
        result.put("url", response.path("html_url").asText());
        result.put("state", response.path("state").asText());
        
        log.info("GitHub Issue 创建成功：#{} {}", result.get("number"), title);
        return result;
    }
    
    /**
     * 查询 Issues
     */
    public List<Map<String, Object>> listIssues(String state, String labels, int page, int perPage) throws IOException, InterruptedException {
        String url = API_BASE + "/repos/" + owner + "/" + repo + "/issues" +
            "?state=" + (state != null ? state : "open") +
            "&page=" + page +
            "&per_page=" + perPage;
        
        if (labels != null) {
            url += "&labels=" + labels;
        }
        
        JsonNode response = getJson(url);
        
        List<Map<String, Object>> issues = new ArrayList<>();
        for (JsonNode issue : response) {
            Map<String, Object> item = new HashMap<>();
            item.put("number", issue.path("number").asInt());
            item.put("title", issue.path("title").asText());
            item.put("state", issue.path("state").asText());
            item.put("url", issue.path("html_url").asText());
            item.put("created_at", issue.path("created_at").asText());
            
            List<String> labels = new ArrayList<>();
            issue.path("labels").forEach(label -> labels.add(label.path("name").asText()));
            item.put("labels", labels);
            
            issues.add(item);
        }
        
        return issues;
    }
    
    /**
     * 获取 Issue 详情
     */
    public Map<String, Object> getIssue(int issueNumber) throws IOException, InterruptedException {
        String url = API_BASE + "/repos/" + owner + "/" + repo + "/issues/" + issueNumber;
        JsonNode response = getJson(url);
        
        Map<String, Object> result = new HashMap<>();
        result.put("number", response.path("number").asInt());
        result.put("title", response.path("title").asText());
        result.put("body", response.path("body").asText());
        result.put("state", response.path("state").asText());
        result.put("url", response.path("html_url").asText());
        result.put("author", response.path("user").path("login").asText());
        result.put("comments", response.path("comments").asInt());
        
        return result;
    }
    
    /**
     * 更新 Issue
     */
    public Map<String, Object> updateIssue(int issueNumber, String title, String body, String state) throws IOException, InterruptedException {
        String url = API_BASE + "/repos/" + owner + "/" + repo + "/issues/" + issueNumber;
        
        Map<String, Object> request = new HashMap<>();
        if (title != null) request.put("title", title);
        if (body != null) request.put("body", body);
        if (state != null) request.put("state", state);
        
        JsonNode response = patchJson(url, request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("number", response.path("number").asInt());
        result.put("title", response.path("title").asText());
        result.put("state", response.path("state").asText());
        
        log.info("GitHub Issue 更新成功：#{}", issueNumber);
        return result;
    }
    
    /**
     * 添加 Issue 评论
     */
    public Map<String, Object> addComment(int issueNumber, String body) throws IOException, InterruptedException {
        String url = API_BASE + "/repos/" + owner + "/" + repo + "/issues/" + issueNumber + "/comments";
        
        Map<String, String> request = Map.of("body", body);
        JsonNode response = postJson(url, request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", response.path("id").asLong());
        result.put("url", response.path("html_url").asText());
        result.put("created_at", response.path("created_at").asText());
        
        log.info("GitHub 评论添加成功：Issue #{}", issueNumber);
        return result;
    }
    
    // ==================== PRs 管理 ====================
    
    /**
     * 创建 Pull Request
     */
    public Map<String, Object> createPullRequest(String title, String body, String head, String base) throws IOException, InterruptedException {
        String url = API_BASE + "/repos/" + owner + "/" + repo + "/pulls";
        
        Map<String, String> request = new HashMap<>();
        request.put("title", title);
        request.put("body", body);
        request.put("head", head);
        request.put("base", base);
        
        JsonNode response = postJson(url, request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("number", response.path("number").asInt());
        result.put("title", response.path("title").asText());
        result.put("url", response.path("html_url").asText());
        result.put("state", response.path("state").asText());
        result.put("merged", response.path("merged").asBoolean());
        
        log.info("GitHub PR 创建成功：#{} {}", result.get("number"), title);
        return result;
    }
    
    /**
     * 查询 PRs
     */
    public List<Map<String, Object>> listPullRequests(String state, int page, int perPage) throws IOException, InterruptedException {
        String url = API_BASE + "/repos/" + owner + "/" + repo + "/pulls" +
            "?state=" + (state != null ? state : "open") +
            "&page=" + page +
            "&per_page=" + perPage;
        
        JsonNode response = getJson(url);
        
        List<Map<String, Object>> prs = new ArrayList<>();
        for (JsonNode pr : response) {
            Map<String, Object> item = new HashMap<>();
            item.put("number", pr.path("number").asInt());
            item.put("title", pr.path("title").asText());
            item.put("state", pr.path("state").asText());
            item.put("url", pr.path("html_url").asText());
            item.put("author", pr.path("user").path("login").asText());
            item.put("merged", pr.path("merged").asBoolean());
            
            prs.add(item);
        }
        
        return prs;
    }
    
    /**
     * 合并 PR
     */
    public Map<String, Object> mergePullRequest(int prNumber, String commitTitle, String commitMessage) throws IOException, InterruptedException {
        String url = API_BASE + "/repos/" + owner + "/" + repo + "/pulls/" + prNumber + "/merge";
        
        Map<String, String> request = new HashMap<>();
        if (commitTitle != null) request.put("commit_title", commitTitle);
        if (commitMessage != null) request.put("commit_message", commitMessage);
        request.put("merge_method", "merge");
        
        JsonNode response = putJson(url, request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("merged", response.path("merged").asBoolean());
        result.put("sha", response.path("sha").asText());
        result.put("message", response.path("message").asText());
        
        log.info("GitHub PR 合并成功：#{}", prNumber);
        return result;
    }
    
    // ==================== Actions 管理 ====================
    
    /**
     * 触发 Workflow
     */
    public Map<String, Object> triggerWorkflow(String workflowId, String ref, Map<String, Object> inputs) throws IOException, InterruptedException {
        String url = API_BASE + "/repos/" + owner + "/" + repo + "/actions/workflows/" + workflowId + "/dispatches";
        
        Map<String, Object> request = new HashMap<>();
        request.put("ref", ref != null ? ref : "main");
        if (inputs != null) {
            request.put("inputs", inputs);
        }
        
        postJson(url, request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("workflow", workflowId);
        result.put("ref", ref);
        result.put("triggered", true);
        
        log.info("GitHub Workflow 触发成功：{}", workflowId);
        return result;
    }
    
    /**
     * 查询 Workflow 运行
     */
    public List<Map<String, Object>> listWorkflowRuns(String workflowId, String branch, String status) throws IOException, InterruptedException {
        String url = API_BASE + "/repos/" + owner + "/" + repo + "/actions/workflows/" + workflowId + "/runs";
        
        if (branch != null) url += "?branch=" + branch;
        if (status != null) url += (branch != null ? "&" : "?") + "status=" + status;
        
        JsonNode response = getJson(url);
        
        List<Map<String, Object>> runs = new ArrayList<>();
        for (JsonNode run : response.path("workflow_runs")) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", run.path("id").asLong());
            item.put("status", run.path("status").asText());
            item.put("conclusion", run.path("conclusion").asText());
            item.put("branch", run.path("head_branch").asText());
            item.put("url", run.path("html_url").asText());
            item.put("created_at", run.path("created_at").asText());
            
            runs.add(item);
        }
        
        return runs;
    }
    
    // ==================== 仓库管理 ====================
    
    /**
     * 获取仓库信息
     */
    public Map<String, Object> getRepository() throws IOException, InterruptedException {
        String cacheKey = "repo_" + owner + "_" + repo;
        
        if (cache.containsKey(cacheKey) && 
            System.currentTimeMillis() - cacheTime.get(cacheKey) < cacheTtlMs) {
            return (Map<String, Object>) cache.get(cacheKey);
        }
        
        String url = API_BASE + "/repos/" + owner + "/" + repo;
        JsonNode response = getJson(url);
        
        Map<String, Object> result = new HashMap<>();
        result.put("name", response.path("name").asText());
        result.put("fullName", response.path("full_name").asText());
        result.put("description", response.path("description").asText());
        result.put("url", response.path("html_url").asText());
        result.put("stars", response.path("stargazers_count").asInt());
        result.put("forks", response.path("forks_count").asInt());
        result.put("openIssues", response.path("open_issues_count").asInt());
        result.put("defaultBranch", response.path("default_branch").asText());
        result.put("private", response.path("private").asBoolean());
        
        cache.put(cacheKey, result);
        cacheTime.put(cacheKey, System.currentTimeMillis());
        
        return result;
    }
    
    /**
     * 获取仓库文件
     */
    public Map<String, Object> getFileContent(String path, String ref) throws IOException, InterruptedException {
        String url = API_BASE + "/repos/" + owner + "/" + repo + "/contents/" + path;
        if (ref != null) url += "?ref=" + ref;
        
        JsonNode response = getJson(url);
        
        Map<String, Object> result = new HashMap<>();
        result.put("name", response.path("name").asText());
        result.put("path", response.path("path").asText());
        result.put("sha", response.path("sha").asText());
        result.put("size", response.path("size").asInt());
        result.put("content", response.path("content").asText());
        result.put("encoding", response.path("encoding").asText());
        result.put("downloadUrl", response.path("download_url").asText());
        
        return result;
    }
    
    // ==================== HTTP 辅助方法 ====================
    
    private JsonNode getJson(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + token)
            .header("Accept", "application/vnd.github.v3+json")
            .GET()
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("GitHub API 请求失败：" + response.statusCode() + " - " + response.body());
        }
        
        return objectMapper.readTree(response.body());
    }
    
    private JsonNode postJson(String url, Object body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + token)
            .header("Accept", "application/vnd.github.v3+json")
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 300) {
            throw new IOException("GitHub API 请求失败：" + response.statusCode() + " - " + response.body());
        }
        
        return objectMapper.readTree(response.body());
    }
    
    private JsonNode patchJson(String url, Object body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + token)
            .header("Accept", "application/vnd.github.v3+json")
            .header("Content-Type", "application/json")
            .method("PATCH", HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 300) {
            throw new IOException("GitHub API 请求失败：" + response.statusCode() + " - " + response.body());
        }
        
        return objectMapper.readTree(response.body());
    }
    
    private JsonNode putJson(String url, Object body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + token)
            .header("Accept", "application/vnd.github.v3+json")
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 300) {
            throw new IOException("GitHub API 请求失败：" + response.statusCode() + " - " + response.body());
        }
        
        return objectMapper.readTree(response.body());
    }
    
    /**
     * 检查连接状态
     */
    public boolean isConnected() {
        return enabled && token != null;
    }
    
    /**
     * 获取服务信息
     */
    public Map<String, Object> getServiceInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "github");
        info.put("enabled", enabled);
        info.put("connected", isConnected());
        if (enabled) {
            info.put("owner", owner);
            info.put("repo", repo);
        }
        return info;
    }
}
