# JClaw 文档站点

> JClaw 官方文档，使用 Docusaurus 构建

## 快速开始

### 1. 安装依赖

```bash
npm install
```

### 2. 本地开发

```bash
npm run start
```

访问 http://localhost:3000

### 3. 构建生产版本

```bash
npm run build
npm run serve
```

## 文档结构

```
docs-site/
├── docs/           # 文档内容
│   ├── intro.md    # 介绍
│   ├── quickstart.md  # 快速开始
│   └── ...
├── guides/         # 使用指南
│   ├── skills/     # 技能开发指南
│   ├── agents/     # Agent 配置指南
│   └── ...
├── api/            # API 文档
│   ├── rest/       # REST API
│   ├── sdk/        # SDK API
│   └── ...
└── examples/       # 示例代码
```

## 部署

### GitHub Pages

```bash
npm run deploy
```

### Vercel

自动部署，推送到 main 分支即可。

## 自定义配置

编辑 `docusaurus.config.js` 进行自定义配置。

## 资源

- [Docusaurus 文档](https://docusaurus.io)
- [JClaw 主仓库](https://github.com/houjibo/JClaw)
