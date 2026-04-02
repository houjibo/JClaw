# 🔐 JClaw 敏感信息保护规范

> **原则**: 零容忍泄露 - API Key、密码、Token 等敏感信息绝不提交到代码仓  
> **策略**: 本地环境变量桥接 + .gitignore 严格过滤 + 代码审查

---

## ⚠️ 严禁提交的敏感信息

### 绝对禁止提交的文件

| 文件类型 | 示例 | 风险等级 |
|----------|------|----------|
| **API Key 文件** | `api-keys.md`, `secrets.md`, `.env` | 🔴 高危 |
| **配置文件（含密码）** | `application-local.yml`, `config.local.json` | 🔴 高危 |
| **IDE 配置** | `.idea/`, `.vscode/settings.json` (可能含路径) | 🟡 中危 |
| **构建产物** | `target/`, `build/`, `dist/` | 🟢 低危 |
| **日志文件** | `*.log`, `logs/` | 🟡 中危 |

### 严禁硬编码的敏感信息

```java
// ❌ 绝对禁止
String apiKey = "sk-xxxxx";
String password = "mypassword123";
String token = "ghp_xxxxx";

// ✅ 正确做法
String apiKey = System.getenv("API_KEY");
String password = System.getenv("DB_PASSWORD");
String token = System.getenv("GITHUB_TOKEN");
```

---

## ✅ 正确做法：本地环境变量桥接

### 1. 创建本地环境变量文件（不提交）

**文件**: `.env.local`（已加入 .gitignore）

```bash
# 数据库
DB_HOST=localhost
DB_PORT=3306
DB_NAME=jclaw
DB_USER=root
DB_PASSWORD=jclaw_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6380

# FalkorDB
FALKORDB_HOST=localhost
FALKORDB_PORT=6379

# AI API Keys（按需配置）
ZHIPU_API_KEY=your_zhipu_key_here
MOONSHOT_API_KEY=your_moonshot_key_here
ALIYUN_API_KEY=your_aliyun_key_here

# JWT
JWT_SECRET=your_jwt_secret_key_must_be_at_least_256_bits

# 其他敏感配置
ENCRYPTION_KEY=your_encryption_key
```

### 2. 在代码中读取环境变量

**Spring Boot 方式**:

```java
// 方式 1: @Value 注解
@Value("${DB_PASSWORD:}")
private String dbPassword;

// 方式 2: @ConfigurationProperties
@Configuration
@ConfigurationProperties(prefix = "db")
public class DatabaseProperties {
    private String host;
    private String port;
    private String username;
    private String password;
    // getters/setters
}

// 方式 3: System.getenv()
String apiKey = System.getenv("ZHIPU_API_KEY");
```

**配置文件引用**:

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:jclaw}
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:}  # 从环境变量读取

ai:
  zhipu:
    api-key: ${ZHIPU_API_KEY:}
  moonshot:
    api-key: ${MOONSHOT_API_KEY:}

jwt:
  secret: ${JWT_SECRET:}
```

### 3. 本地启动脚本

**脚本**: `scripts/start-dev.sh`

```bash
#!/bin/bash

# 加载本地环境变量
if [ -f .env.local ]; then
    export $(cat .env.local | grep -v '^#' | xargs)
    echo "✅ 已加载 .env.local 环境变量"
else
    echo "⚠️  警告：.env.local 不存在，请创建并配置敏感信息"
    exit 1
fi

# 启动应用
mvn spring-boot:run
```

**使用**:
```bash
chmod +x scripts/start-dev.sh
./scripts/start-dev.sh
```

---

## 🛡️ .gitignore 严格过滤

### 当前 .gitignore 已包含

```gitignore
# 敏感信息
.env
.env.local
.env.*.local
api-keys.md
secrets.md
*.key
*.pem

# 配置文件（可能含敏感信息）
application-local.yml
application-local.properties
config.local.json

# 构建产物
target/
build/
*.class
*.log

# IDE
.idea/
.vscode/
*.iml

# 操作系统
.DS_Store
Thumbs.db
```

### 验证 .gitignore 是否生效

```bash
# 检查敏感文件是否被忽略
git check-ignore -v .env.local
git check-ignore -v api-keys.md
git check-ignore -v target/

# 应该输出规则匹配信息
```

---

## 🔍 提交前检查清单

### 每次 git commit 前必查

```bash
# 1. 检查将要提交的文件
git status

# 2. 检查 diff 中是否有敏感信息
git diff --cached | grep -i "key\|password\|secret\|token"

# 3. 使用 git-secrets 扫描（推荐安装）
git secrets --scan

# 4. 手动检查新增文件
git diff --cached --name-only
```

### 自动化检查（推荐）

**Git 预提交钩子**: `.git/hooks/pre-commit`

```bash
#!/bin/bash

echo "🔐 运行敏感信息检查..."

# 检查是否有敏感文件
SENSITIVE_FILES=(
    ".env"
    ".env.local"
    "api-keys.md"
    "secrets.md"
)

for file in "${SENSITIVE_FILES[@]}"; do
    if git ls-files --error-unmatch "$file" 2>/dev/null; then
        echo "❌ 错误：检测到敏感文件 $file，禁止提交！"
        exit 1
    fi
done

# 检查代码中是否有硬编码的 API Key
if git diff --cached | grep -E "(sk-[a-zA-Z0-9]{20,}|ghp_[a-zA-Z0-9]{30,})"; then
    echo "❌ 错误：检测到可能的 API Key，禁止提交！"
    exit 1
fi

echo "✅ 敏感信息检查通过"
exit 0
```

**启用**:
```bash
chmod +x .git/hooks/pre-commit
```

---

## 🚨 泄露应急响应

### 如果发现敏感信息已提交

**立即执行**:

```bash
# 1. 立即撤销提交（如果还没 push）
git reset --hard HEAD~1

# 2. 如果已经 push，立即删除远程分支
git push origin :main

# 3. 修改 git history（彻底删除敏感信息）
git filter-branch --force --index-filter \
  "git rm --cached --ignore-unmatch path/to/sensitive/file" \
  --prune-empty --tag-name-filter cat -- --all

# 4. 强制推送到远程
git push origin --force --all

# 5. 立即更换所有泄露的 API Key/密码！
# 6. 通知团队成员检查本地仓库
```

### 更换 API Key 流程

1. **立即撤销**泄露的 API Key（到对应平台）
2. **生成新的** API Key
3. **更新** `.env.local` 文件
4. **通知**团队成员更新
5. **检查**代码仓历史确保无泄露

---

## 📋 开发环境安全配置

### IntelliJ IDEA 配置

**文件**: `.idea/runConfigurations/JClawApplication.xml`

```xml
<component name="ProjectRunConfigurationManager">
  <configuration default="false" name="JClawApplication" type="SpringBootApplicationConfigurationType" factoryName="Spring Boot">
    <envs>
      <env name="DB_PASSWORD" value="jclaw_password" />
      <env name="JWT_SECRET" value="your_secret" />
      <!-- 不要硬编码，使用 .env 文件 -->
    </envs>
    <module name="jclaw" />
    <option name="SPRING_BOOT_MAIN_CLASS" value="com.jclaw.JClawApplication" />
  </configuration>
</component>
```

**注意**: `.idea/` 已在 .gitignore 中

### VS Code 配置

**文件**: `.vscode/launch.json`

```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "JClawApplication",
            "request": "launch",
            "mainClass": "com.jclaw.JClawApplication",
            "env": {
                "DB_PASSWORD": "${env:DB_PASSWORD}",
                "JWT_SECRET": "${env:JWT_SECRET}"
            }
        }
    ]
}
```

**注意**: `.vscode/` 已在 .gitignore 中

---

## ✅ 安全验证清单

### 迭代验收时必须检查

- [ ] 代码中无硬编码 API Key
- [ ] `.env.local` 未提交到代码仓
- [ ] `.gitignore` 包含所有敏感文件
- [ ] 配置文件使用环境变量占位符
- [ ] 日志中无敏感信息输出
- [ ] 错误信息不泄露敏感数据

### 自动化测试

```java
@Test
public void testNoHardcodedApiKey() {
    // 扫描代码中的硬编码 API Key
    String apiKeyPattern = "(sk-[a-zA-Z0-9]{20,}|ghp_[a-zA-Z0-9]{30,})";
    
    // 如果匹配到，测试失败
    // 确保所有 API Key 都从环境变量读取
}
```

---

## 📚 最佳实践总结

### ✅ DO（应该做的）

1. ✅ 使用环境变量读取敏感信息
2. ✅ 本地 `.env.local` 文件存储配置
3. ✅ `.gitignore` 严格过滤敏感文件
4. ✅ 提交前检查 diff
5. ✅ 使用 Git Hooks 自动检查
6. ✅ 日志中脱敏敏感信息
7. ✅ 定期更换 API Key

### ❌ DON'T（禁止做的）

1. ❌ 硬编码 API Key 到代码
2. ❌ 提交 `.env` 或 `api-keys.md`
3. ❌ 在日志中打印完整 API Key
4. ❌ 在错误信息中泄露敏感数据
5. ❌ 使用默认密码
6. ❌ 分享个人 API Key 给他人
7. ❌ 在公开场合讨论敏感配置

---

## 🔐 责任声明

**所有开发者必须遵守**:

1. **个人责任**: 每个开发者对自己的 API Key 安全负责
2. **代码审查**: PR 必须检查敏感信息
3. **零容忍**: 发现泄露立即处理并报告
4. **持续教育**: 定期学习安全最佳实践

---

*创建时间：2026-04-03*  
*创建者：可乐 🥤*  
*版本：v1.0*

**安全红线，绝不触碰！** 🔐
