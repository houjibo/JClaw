# JClaw Windows 部署指南

**版本：** 1.0.0-SNAPSHOT
**构建时间：** 2026-04-02
**JDK 要求：** 21+

---

## 📦 文件清单

| 文件 | 大小 | 说明 |
|------|------|------|
| `jcode-1.0.0-SNAPSHOT.jar` | 38MB | 主程序 |
| `src/main/resources/application.yml` | - | 配置文件 |
| `README.md` | - | 项目说明 |

---

## 🔧 环境要求

### 必需

- **JDK 21+**（推荐 Adoptium Temurin 21）
- **内存：** 最低 512MB，推荐 1GB+
- **磁盘：** 最低 100MB

### 可选

- **Maven 3.8+**（如需重新编译）
- **Git**（如需克隆源码）

---

## 🚀 快速开始

### 1. 安装 JDK 21

**下载：** https://adoptium.net/temurin/releases/?version=21

**验证：**
```powershell
java -version
```

应显示：
```
openjdk version "21.x.x"
```

### 2. 运行 JClaw

```powershell
# 进入项目目录
cd jcode

# 运行
java -jar target/jcode-1.0.0-SNAPSHOT.jar
```

### 3. 验证启动

启动后应看到：
```
========================================
  JClaw 编码智能体启动
========================================
✓ 已注册 46 个工具
✓ 已注册 200 个命令

📊 功能统计:
  工具：46 个
  命令：200 个
========================================

Started JClawApplication in 0.865 seconds
```

---

## 📋 配置说明

### 配置文件位置

```
用户目录/.openclaw/jcode/application.yml
```

### 常用配置

```yaml
# 服务端口
server:
  port: 8080

# JClaw 配置
jcode:
  workspace: ${user.home}/.openclaw/workspace
  max-read-size: 10485760  # 10MB
  allow-write: true
  allow-exec: true
  cache-enabled: true
  cache-expire-minutes: 10
  mcp-enabled: true
  agent-enabled: true
  streaming-timeout: 30000  # 30 秒
```

---

## 🌐 API 测试

### 健康检查

```powershell
# PowerShell
curl http://localhost:8080/api/health

# 或使用浏览器访问
http://localhost:8080/api/health
```

**预期响应：**
```json
{
  "status": "UP",
  "components": {
    "database": "UP",
    "cache": "UP",
    "api": "UP"
  },
  "version": "1.0.0"
}
```

### Swagger UI

访问：http://localhost:8080/swagger-ui.html

### 监控指标

访问：http://localhost:8080/actuator/health

---

## 🔍 功能验证清单

### 基础功能

- [ ] 服务正常启动
- [ ] 健康检查返回 UP
- [ ] Swagger UI 可访问
- [ ] 工具列表正常（46 个）
- [ ] 命令列表正常（200 个）

### API 测试

- [ ] `GET /api/health` - 健康检查
- [ ] `GET /api/health/ready` - 就绪检查
- [ ] `GET /api/health/live` - 存活检查
- [ ] `GET /api/features` - 特性列表
- [ ] `GET /api/cache/stats` - 缓存统计
- [ ] `GET /api/config` - 配置查询

### 高级功能（可选）

- [ ] 文件读取工具
- [ ] 代码搜索工具
- [ ] Git 工具
- [ ] MCP 工具调用
- [ ] Agent 协调

---

## ⚠️ 常见问题

### 1. 端口被占用

**错误：** `Port 8080 was already in use`

**解决：** 修改端口
```yaml
server:
  port: 8081  # 改为其他端口
```

### 2. JDK 版本过低

**错误：** `Unsupported class file major version`

**解决：** 安装 JDK 21+

### 3. 内存不足

**错误：** `Java heap space`

**解决：** 增加堆内存
```powershell
java -Xmx512m -jar target/jcode-1.0.0-SNAPSHOT.jar
```

### 4. 权限问题（文件操作）

**错误：** `Access denied`

**解决：** 以管理员身份运行，或检查工作目录权限

---

## 📝 使用示例

### 命令行交互

```powershell
# 启动后，可以通过 HTTP API 调用工具

# 1. 读取文件
curl -X POST http://localhost:8080/api/tools/file_read/execute ^
  -H "Content-Type: application/json" ^
  -d '{"path": "pom.xml"}'

# 2. 搜索代码
curl -X POST http://localhost:8080/api/tools/grep/execute ^
  -H "Content-Type: application/json" ^
  -d '{"pattern": "public class", "path": "src"}'

# 3. Git 状态
curl -X POST http://localhost:8080/api/tools/git/execute ^
  -H "Content-Type: application/json" ^
  -d '{"args": ["status"]}'
```

### 多 Agent 协作

```powershell
# 1. 创建 Agent
curl -X POST http://localhost:8080/api/agents ^
  -H "Content-Type: application/json" ^
  -d '{"role": "developer", "model": "qwen3.5-plus"}'

# 2. 创建任务
curl -X POST http://localhost:8080/api/agents/tasks ^
  -H "Content-Type: application/json" ^
  -d '{"description": "代码审查"}'

# 3. 分配任务
curl -X POST http://localhost:8080/api/agents/tasks/{taskId}/assign ^
  -H "Content-Type: application/json" ^
  -d '{"agentId": "{agentId}"}'
```

---

## 📊 性能基准

| 指标 | 标准 | Windows 预期 |
|------|------|-------------|
| 启动时间 | <2 秒 | <3 秒 |
| API 响应 | <100ms | <150ms |
| 内存占用 | <500MB | <600MB |
| 编译时间 | <30 秒 | <40 秒 |

---

## 📞 技术支持

**项目地址：** `~/.openclaw/workspace/projects/code/core/jcode/`

**文档位置：**
- `README.md` - 项目说明
- `COMPLETE_OPTIMIZATION_REPORT.md` - 优化报告
- `FINAL_ACCEPTANCE_REPORT.md` - 验收报告
- `JCODE_DEVELOPMENT_EXPERIENCE.md` - 开发经验

**问题反馈：** 飞书联系 波哥

---

*JClaw - 让 Java 开发更智能* 🤖

*构建时间：2026-04-02 08:29*
