# GitHub 集成指南

> 将 JClaw 接入 GitHub，实现 Issues/PRs/Actions 自动化

## 前置要求

- GitHub 账号
- JClaw 1.0.0+

## 步骤 1：创建 Personal Access Token

### 1.1 访问 Token 设置

打开 https://github.com/settings/tokens

### 1.2 生成新 Token

1. 点击"Generate new token (classic)"
2. 填写备注（如"JClaw Integration"）
3. 选择权限：
   - ✅ `repo` (完整仓库权限)
   - ✅ `workflow` (触发 Actions)
   - ✅ `read:org` (读取组织信息)
   - ✅ `user:email` (读取用户邮箱)

4. 点击"Generate token"
5. **复制并保存 Token**（只显示一次！）

## 步骤 2：配置 JClaw

### 2.1 创建配置文件

创建 `~/.jclaw/integrations/github.yml`：

```yaml
github:
  enabled: true
  token: "${GITHUB_TOKEN}"  # 从环境变量读取
  owner: "your-username"
  repo: "your-repo"
```

### 2.2 重启 JClaw

```bash
jclaw restart
```

## 步骤 3：使用 GitHub 集成

### 3.1 查询 Issues

```bash
# 列出开放 Issues
jclaw github issues list --state open

# 查看 Issue 详情
jclaw github issues get --number 42

# 创建新 Issue
jclaw github issues create --title "Bug: xxx" --body "问题描述..." --labels "bug,high-priority"
```

### 3.2 管理 PRs

```bash
# 列出 PRs
jclaw github prs list

# 创建 PR
jclaw github prs create --title "Feature: xxx" --body "变更说明..." --head feature-branch --base main

# 合并 PR
jclaw github prs merge --number 42 --method merge
```

### 3.3 触发 Actions

```bash
# 触发 Workflow
jclaw github actions trigger --workflow deploy.yml --ref main

# 查看运行状态
jclaw github actions runs --workflow deploy.yml
```

### 3.4 在技能中使用

```java
@Autowired
private GitHubService github;

// 创建 Issue
Map<String, Object> issue = github.createIssue(
    "发现 Bug",
    "详细描述...",
    List.of("bug", "high-priority")
);

// 添加评论
github.addComment((Integer) issue.get("number"), "正在处理...");

// 触发部署
github.triggerWorkflow("deploy.yml", "main", Map.of("environment", "production"));
```

## 高级用法

### 自动创建 Issue

当 JClaw 检测到错误时自动创建 Issue：

```java
try {
    // 执行任务
} catch (Exception e) {
    github.createIssue(
        "错误：" + e.getMessage(),
        "堆栈：\n```\n" + getStackTrace(e) + "\n```",
        List.of("bug", "auto-generated")
    );
}
```

### PR 审查工作流

```java
// 获取 PR 变更
var pr = github.getPullRequest(42);

// AI 审查代码
var review = jclaw.execute("审查这个 PR 的代码变更：" + pr.get("diff"));

// 添加审查评论
github.addComment(42, "## AI 审查意见\n\n" + review);
```

### 自动合并 PR

```java
// 检查 CI 状态
var runs = github.listWorkflowRuns("ci.yml", "main", "success");

if (runs.stream().anyMatch(r -> "success".equals(r.get("conclusion")))) {
    // CI 通过，合并 PR
    github.mergePullRequest(42, "合并 PR #42", "由 JClaw 自动合并");
}
```

## 故障排除

### 问题 1：401 Unauthorized

**原因**：Token 无效或过期

**解决**：
1. 检查 Token 是否正确
2. 确认 Token 权限足够
3. 重新生成 Token

### 问题 2：403 Forbidden

**原因**：权限不足

**解决**：
1. 确认 Token 有 `repo` 权限
2. 确认用户有仓库访问权限

### 问题 3：404 Not Found

**原因**：仓库不存在或路径错误

**解决**：
1. 检查 owner/repo 配置
2. 确认仓库可见性（私有仓库需要额外权限）

## 最佳实践

1. **Token 安全**：使用环境变量或密钥管理服务
2. **权限最小化**：只申请必要的权限
3. **错误处理**：捕获并记录 API 异常
4. **速率限制**：GitHub API 限制 5000 次/小时
5. **Webhook**：使用 Webhook 代替轮询（实时性更好）

## 资源

- [GitHub REST API 文档](https://docs.github.com/en/rest)
- [GitHub Actions 文档](https://docs.github.com/en/actions)
- [JClaw GitHub 集成 API](../../api/integration/github.md)

---

*最后更新：2026-04-15*
