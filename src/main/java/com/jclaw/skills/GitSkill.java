package com.jclaw.skills;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

/**
 * Git 操作技能（简化版）
 */
@Slf4j
@Service
public class GitSkill implements Skill {
    
    @Override
    public String getName() {
        return "git";
    }
    
    @Override
    public String getDescription() {
        return "Git 版本控制操作";
    }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        try {
            String command = (String) params.get("command");
            String repoPath = (String) params.getOrDefault("repoPath", ".");
            
            if (command == null || command.isEmpty()) {
                return SkillResult.error("缺少参数：command");
            }
            
            File repoDir = new File(repoPath);
            if (!repoDir.exists()) {
                return SkillResult.error("仓库路径不存在：" + repoPath);
            }
            
            StringBuilder output = new StringBuilder();
            
            try (Git git = Git.open(repoDir)) {
                switch (command.toLowerCase()) {
                    case "status":
                        output.append(gitStatus(git));
                        break;
                    case "log":
                        Integer maxCommits = (Integer) params.getOrDefault("maxCommits", 5);
                        output.append(gitLog(git, maxCommits));
                        break;
                    case "branch":
                        output.append(gitBranch(git));
                        break;
                    case "add":
                        String files = (String) params.get("files");
                        output.append(gitAdd(git, files));
                        break;
                    case "commit":
                        String message = (String) params.get("message");
                        output.append(gitCommit(git, message));
                        break;
                    default:
                        return SkillResult.error("不支持的命令：" + command + 
                            "，支持的命令：status, log, branch, add, commit");
                }
            }
            
            return SkillResult.success(output.toString(), Map.of(
                "command", command,
                "repoPath", repoPath
            ));
            
        } catch (GitAPIException e) {
            log.error("Git 操作失败", e);
            return SkillResult.error("Git 操作失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("Git 操作失败", e);
            return SkillResult.error("Git 操作失败：" + e.getMessage());
        }
    }
    
    private String gitStatus(Git git) throws GitAPIException, java.io.IOException {
        var status = git.status().call();
        StringBuilder sb = new StringBuilder();
        sb.append("当前分支：").append(git.getRepository().getBranch()).append("\n");
        if (!status.isClean()) {
            sb.append("\n修改的文件:\n");
            status.getModified().forEach(f -> sb.append("  M ").append(f).append("\n"));
            sb.append("\n未跟踪的文件:\n");
            status.getUntracked().forEach(f -> sb.append("  ? ").append(f).append("\n"));
        } else {
            sb.append("工作区干净");
        }
        return sb.toString();
    }
    
    private String gitLog(Git git, int maxCommits) throws GitAPIException {
        StringBuilder sb = new StringBuilder();
        for (var commit : git.log().setMaxCount(maxCommits).call()) {
            sb.append(commit.getId().abbreviate(7).name())
                .append(" ")
                .append(commit.getShortMessage())
                .append(" (")
                .append(commit.getAuthorIdent().getName())
                .append(", ")
                .append(commit.getCommitterIdent().getWhen())
                .append(")\n");
        }
        return sb.toString();
    }
    
    private String gitBranch(Git git) throws GitAPIException, java.io.IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("当前分支：").append(git.getRepository().getBranch()).append("\n");
        for (var branch : git.branchList().call()) {
            sb.append("  ").append(branch.getName().replace("refs/heads/", "")).append("\n");
        }
        return sb.toString();
    }
    
    private String gitAdd(Git git, String files) throws GitAPIException {
        git.add().addFilepattern(files != null ? files : ".").call();
        return "已添加文件：" + (files != null ? files : "所有修改");
    }
    
    private String gitCommit(Git git, String message) throws GitAPIException {
        if (message == null || message.isEmpty()) {
            return "错误：缺少提交消息";
        }
        var commit = git.commit().setMessage(message).call();
        return "提交成功：" + commit.getId().abbreviate(7).name();
    }
}
