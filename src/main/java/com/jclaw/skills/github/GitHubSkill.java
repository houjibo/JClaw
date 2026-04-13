package com.jclaw.skills.github;

import com.jclaw.skills.Skill;
import com.jclaw.skills.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * GitHub 技能 - Issue/PR 管理
 */
@Slf4j
@Service
public class GitHubSkill implements Skill {
    
    @Value("${jclaw.skills.github.token:#{null}}")
    private String githubToken;
    
    private final WebClient webClient;
    
    public GitHubSkill() {
        this.webClient = WebClient.builder()
            .baseUrl("https://api.github.com")
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
    
    @Override
    public String getName() {
        return "github";
    }
    
    @Override
    public String getDescription() {
        return "GitHub 操作（Issue/PR 管理）";
    }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        if (githubToken == null || githubToken.isEmpty()) {
            return SkillResult.error("GitHub Token 未配置，请设置 jclaw.skills.github.token");
        }
        
        String action = (String) params.get("action");
        
        if (action == null) {
            return SkillResult.error("缺少参数：action (issues/pr/list)");
        }
        
        switch (action.toLowerCase()) {
            case "issues":
                return listIssues(params);
            case "pr":
            case "pull_requests":
                return listPullRequests(params);
            case "create_issue":
                return createIssue(params);
            case "repo":
                return getRepoInfo(params);
            default:
                return SkillResult.error("未知操作：" + action);
        }
    }
    
    private SkillResult listIssues(Map<String, Object> params) {
        String owner = (String) params.get("owner");
        String repo = (String) params.get("repo");
        String state = (String) params.getOrDefault("state", "open");
        
        if (owner == null || repo == null) {
            return SkillResult.error("缺少参数：owner, repo");
        }
        
        try {
            String url = String.format("/repos/%s/%s/issues?state=%s&per_page=10", 
                owner, repo, state);
            
            List<Map<String, Object>> issues = webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .retrieve()
                .bodyToMono(List.class)
                .block();
            
            if (issues != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Issues (").append(issues.size()).append("):\n\n");
                for (Map<String, Object> issue : issues.subList(0, Math.min(10, issues.size()))) {
                    sb.append("#").append((int)issue.get("number"))
                      .append(" ").append(issue.get("title"))
                      .append(" (").append(issue.get("state")).append(")\n");
                }
                return SkillResult.success(sb.toString(), Map.of("count", issues.size()));
            }
            
            return SkillResult.success("无 Issues");
            
        } catch (Exception e) {
            log.error("获取 Issues 失败", e);
            return SkillResult.error("获取失败：" + e.getMessage());
        }
    }
    
    private SkillResult listPullRequests(Map<String, Object> params) {
        String owner = (String) params.get("owner");
        String repo = (String) params.get("repo");
        
        if (owner == null || repo == null) {
            return SkillResult.error("缺少参数：owner, repo");
        }
        
        try {
            String url = String.format("/repos/%s/%s/pulls?state=open&per_page=10", owner, repo);
            
            List<Map<String, Object>> prs = webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .retrieve()
                .bodyToMono(List.class)
                .block();
            
            if (prs != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Pull Requests (").append(prs.size()).append("):\n\n");
                for (Map<String, Object> pr : prs.subList(0, Math.min(10, prs.size()))) {
                    sb.append("#").append((int)pr.get("number"))
                      .append(" ").append(pr.get("title"))
                      .append(" by @").append(((Map)pr.get("user")).get("login"))
                      .append("\n");
                }
                return SkillResult.success(sb.toString(), Map.of("count", prs.size()));
            }
            
            return SkillResult.success("无 Pull Requests");
            
        } catch (Exception e) {
            log.error("获取 PRs 失败", e);
            return SkillResult.error("获取失败：" + e.getMessage());
        }
    }
    
    private SkillResult createIssue(Map<String, Object> params) {
        String owner = (String) params.get("owner");
        String repo = (String) params.get("repo");
        String title = (String) params.get("title");
        String body = (String) params.get("body");
        
        if (owner == null || repo == null || title == null) {
            return SkillResult.error("缺少参数：owner, repo, title");
        }
        
        try {
            Map<String, String> requestBody = Map.of(
                "title", title,
                "body", body != null ? body : ""
            );
            
            String url = String.format("/repos/%s/%s/issues", owner, repo);
            
            Map<String, Object> result = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
            
            if (result != null) {
                return SkillResult.success(
                    "Issue 创建成功 #" + result.get("number"),
                    Map.of("number", result.get("number"), "url", result.get("html_url"))
                );
            }
            
            return SkillResult.error("创建失败");
            
        } catch (Exception e) {
            log.error("创建 Issue 失败", e);
            return SkillResult.error("创建失败：" + e.getMessage());
        }
    }
    
    private SkillResult getRepoInfo(Map<String, Object> params) {
        String owner = (String) params.get("owner");
        String repo = (String) params.get("repo");
        
        if (owner == null || repo == null) {
            return SkillResult.error("缺少参数：owner, repo");
        }
        
        try {
            String url = String.format("/repos/%s/%s", owner, repo);
            
            Map<String, Object> repoInfo = webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
            
            if (repoInfo != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("📦 ").append(repoInfo.get("full_name")).append("\n");
                sb.append(repoInfo.get("description")).append("\n\n");
                sb.append("⭐ ").append(repoInfo.get("stargazers_count")).append(" stars\n");
                sb.append("🍴 ").append(repoInfo.get("forks_count")).append(" forks\n");
                sb.append("📌 ").append(repoInfo.get("language")).append("\n");
                return SkillResult.success(sb.toString(), repoInfo);
            }
            
            return SkillResult.error("获取仓库信息失败");
            
        } catch (Exception e) {
            log.error("获取仓库信息失败", e);
            return SkillResult.error("获取失败：" + e.getMessage());
        }
    }
}
