package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Git 工具 - 支持 Git 操作
 */
@Component
public class GitTool extends Tool {
    
    public GitTool() {
        this.name = "git";
        this.description = "Git 版本控制操作";
        this.category = ToolCategory.GIT;
        this.requiresConfirmation = true;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String action = (String) params.get("action");
        String workdir = (String) params.get("workdir");
        
        if (action == null || action.isBlank()) {
            return ToolResult.error("action 参数不能为空");
        }
        
        Path repoPath = workdir != null 
                ? context.getWorkingDirectory().resolve(workdir)
                : context.getWorkingDirectory();
        
        try {
            return switch (action.toLowerCase()) {
                case "status" -> getStatus(repoPath);
                case "log" -> getLog(repoPath, (Number) params.get("maxCount"));
                case "diff" -> getDiff(repoPath);
                case "branch" -> getBranch(repoPath);
                default -> ToolResult.error("不支持的 Git 操作：" + action);
            };
        } catch (IOException | GitAPIException e) {
            System.err.println("[GitTool] Git 操作失败：" + action + " - " + e.getMessage());
            return ToolResult.error("Git 操作失败：" + e.getMessage());
        }
    }
    
    private ToolResult getStatus(Path repoPath) throws IOException, GitAPIException {
        try (Repository repository = getRepository(repoPath)) {
            org.eclipse.jgit.api.Status status = Git.wrap(repository).status().call();
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== Git Status ===\n");
            
            if (!status.hasUncommittedChanges() && status.isClean()) {
                sb.append("工作区干净，无修改\n");
            } else {
                if (!status.getAdded().isEmpty()) {
                    sb.append("新增: ").append(status.getAdded()).append("\n");
                }
                if (!status.getModified().isEmpty()) {
                    sb.append("修改: ").append(status.getModified()).append("\n");
                }
                if (!status.getRemoved().isEmpty()) {
                    sb.append("删除: ").append(status.getRemoved()).append("\n");
                }
                if (!status.getUncommittedChanges().isEmpty()) {
                    sb.append("未暂存: ").append(status.getUncommittedChanges()).append("\n");
                }
            }
            
            return ToolResult.success("Git 状态查询成功", sb.toString());
        }
    }
    
    private ToolResult getLog(Path repoPath, Number maxCount) throws IOException, GitAPIException {
        try (Repository repository = getRepository(repoPath)) {
            Iterable<RevCommit> commits = Git.wrap(repository)
                    .log()
                    .setMaxCount(maxCount != null ? maxCount.intValue() : 10)
                    .call();
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== Git Log ===\n");
            
            for (RevCommit commit : commits) {
                sb.append(String.format("%s %s\n", 
                        commit.getId().abbreviate(7).name(),
                        commit.getShortMessage()));
            }
            
            return ToolResult.success("Git 日志查询成功", sb.toString());
        }
    }
    
    private ToolResult getDiff(Path repoPath) throws IOException, GitAPIException {
        // 简化实现，返回状态信息
        return getStatus(repoPath);
    }
    
    private ToolResult getBranch(Path repoPath) throws IOException {
        try (Repository repository = getRepository(repoPath)) {
            String branch = repository.getBranch();
            return ToolResult.success("当前分支：" + branch);
        }
    }
    
    private Repository getRepository(Path repoPath) throws IOException {
        return new FileRepositoryBuilder()
                .setWorkTree(repoPath.toFile())
                .findGitDir()
                .build();
    }
    
    @Override
    public boolean validate(Map<String, Object> params) {
        return params.containsKey("action");
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 action   - Git 操作：status, log, diff, branch (必填)
                 workdir  - 仓库目录，相对路径 (可选)
                 maxCount - 日志条数，默认 10 (可选)
               示例:
                 git action="status" workdir="my-project"
                 git action="log" maxCount=5
               """.formatted(name, description);
    }
}
