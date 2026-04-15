# GitHub Pages 部署指南

> 将 JClaw 文档部署到 GitHub Pages

## 前置要求

- GitHub 账号
- Node.js 18+
- JClaw 文档源码

## 步骤 1：启用 GitHub Pages

### 1.1 访问仓库设置

打开 https://github.com/houjibo/JClaw/settings/pages

### 1.2 配置 Pages

1. **Source**: GitHub Actions
2. 点击"Save"

## 步骤 2：安装依赖

```bash
cd docs-site
npm install
```

## 步骤 3：本地测试

```bash
# 启动开发服务器
npm run start

# 访问 http://localhost:3000
```

## 步骤 4：构建测试

```bash
# 生产构建
npm run build

# 本地预览
npm run serve
```

## 步骤 5：自动部署

推送到 main 分支会自动触发部署：

```bash
git add docs-site/
git commit -m "docs: 更新文档"
git push
```

GitHub Actions 会自动：
1. 安装依赖
2. 构建文档
3. 部署到 GitHub Pages

## 步骤 6：访问文档

部署完成后访问：
```
https://houjibo.github.io/JClaw/
```

## 自定义域名（可选）

### 6.1 配置 DNS

在域名服务商处添加 CNAME 记录：
```
docs  CNAME  houjibo.github.io
```

### 6.2 配置 GitHub Pages

1. 访问 https://github.com/houjibo/JClaw/settings/pages
2. 在"Custom domain"中输入 `docs.jclaw.dev`
3. 点击"Save"

### 6.3 添加 CNAME 文件

创建 `docs-site/static/CNAME`：
```
docs.jclaw.dev
```

## 故障排除

### 问题 1：部署失败

**检查项**：
- Node.js 版本是否 >= 18
- 依赖是否完整安装
- 构建是否有错误

### 问题 2：404 错误

**解决**：
- 等待几分钟（部署需要时间）
- 检查仓库是否公开
- 确认 Pages 已启用

### 问题 3：样式丢失

**解决**：
- 清除浏览器缓存
- 检查 base URL 配置
- 重新构建

## 手动部署

```bash
# 安装 docusaurus
npm install -g @docusaurus/core

# 构建
npm run build

# 部署（需要 gh-pages 包）
npm install --save-dev gh-pages
npx gh-pages -d build
```

## 资源

- [Docusaurus 部署文档](https://docusaurus.io/docs/deployment)
- [GitHub Pages 文档](https://docs.github.com/en/pages)
- [GitHub Actions 文档](https://docs.github.com/en/actions)

---

*最后更新：2026-04-15*
