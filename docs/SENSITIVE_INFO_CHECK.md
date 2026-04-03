# 敏感信息检查清单

## ✅ 已检查项目

### 1. API Key 检查
- [x] 检查 `sk-` 开头的 API Key
- [x] 检查 `api-key` 配置
- [x] 检查环境变量引用

**结果**: ✅ 未发现硬编码 API Key

### 2. 密码检查
- [x] 检查数据库密码
- [x] 检查配置文件中明文密码
- [x] 检查 placeholder 使用

**结果**: ⚠️ 发现默认密码，已修复

### 3. Token 检查
- [x] 检查 JWT Secret
- [x] 检查访问 Token
- [x] 检查认证 Token

**结果**: ✅ 未发现硬编码 Token

### 4. 密钥检查
- [x] 检查私钥文件
- [x] 检查加密密钥
- [x] 检查证书文件

**结果**: ✅ 未发现硬编码密钥

---

## 🔧 已修复问题

### 问题 1: 默认数据库密码
**位置**: `src/main/resources/application.yml`
**问题**: 使用固定密码 `jclaw_password`
**修复**: 改为环境变量 `${DB_PASSWORD:}`

**修复前**:
```yaml
password: jclaw_password
```

**修复后**:
```yaml
password: ${DB_PASSWORD:}
```

---

## 📋 .gitignore 配置

### 已忽略的敏感文件
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
```

---

## ✅ 最佳实践

### 1. 使用环境变量
```yaml
# ✅ 正确
password: ${DB_PASSWORD:}
api-key: ${API_KEY:}

# ❌ 错误
password: jclaw_password
api-key: sk-xxxxx
```

### 2. 使用配置模板
```bash
# 创建配置模板
cp application.yml application.yml.template

# 在模板中使用占位符
password: ${DB_PASSWORD:}
```

### 3. 本地开发配置
```bash
# 创建 .env.local 文件（已加入.gitignore）
DB_PASSWORD=your_local_password
API_KEY=your_api_key
```

### 4. 生产环境配置
```bash
# 使用 Kubernetes Secrets 或云服务商密钥管理
kubectl create secret generic jclaw-secrets \
  --from-literal=DB_PASSWORD=xxx \
  --from-literal=API_KEY=xxx
```

---

## 🔍 验证命令

### 提交前检查
```bash
# 检查是否有敏感信息
git diff --cached | grep -iE "(password|secret|api.*key|token)" | grep -v "placeholder"

# 检查是否有硬编码密钥
grep -r "sk-[a-zA-Z0-9]\{20,\}" src/ --include="*.java" --include="*.js" --include="*.vue"

# 检查配置文件
find src/main/resources -name "*.yml" | xargs grep -E "(password|secret)" | grep -v "\${"
```

### 使用 Git 钩子
```bash
# .git/hooks/pre-commit
#!/bin/bash
if git diff --cached | grep -iE "password.*=.*['\"][^'\"]+['\"]"; then
  echo "❌ 错误：检测到硬编码密码！"
  exit 1
fi
```

---

## 📊 检查结果

| 检查项 | 状态 | 说明 |
|--------|------|------|
| API Key | ✅ 通过 | 无硬编码 |
| 数据库密码 | ✅ 已修复 | 使用环境变量 |
| JWT Secret | ✅ 通过 | 使用环境变量 |
| Token | ✅ 通过 | 无硬编码 |
| 私钥/证书 | ✅ 通过 | 无硬编码 |
| .gitignore | ✅ 通过 | 配置完整 |

**总体状态**: ✅ **安全，可以提交**

---

*检查时间：2026-04-03 22:30*  
*检查者：可乐 🥤*
