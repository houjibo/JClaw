#!/bin/bash

# JClaw Git 预提交钩子 - 敏感信息检查
# 位置：.git/hooks/pre-commit
# 权限：chmod +x .git/hooks/pre-commit

echo "🔐 运行敏感信息安全检查..."

# 定义颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 错误计数器
ERROR_COUNT=0

# ===========================================
# 检查 1: 敏感文件
# ===========================================
echo "📁 检查敏感文件..."

SENSITIVE_FILES=(
    ".env"
    ".env.local"
    ".env.*.local"
    "api-keys.md"
    "secrets.md"
    "*.key"
    "*.pem"
)

for pattern in "${SENSITIVE_FILES[@]}"; do
    # 检查暂存区是否有敏感文件
    FILES=$(git diff --cached --name-only --diff-filter=ACM | grep -E "^$pattern$" || true)
    if [ -n "$FILES" ]; then
        echo -e "${RED}❌ 错误：检测到敏感文件 '$FILES'，禁止提交！${NC}"
        echo -e "${YELLOW}💡 提示：请将敏感文件加入 .gitignore 或从暂存区移除${NC}"
        echo -e "${YELLOW}   移除命令：git reset HEAD $FILES${NC}"
        ERROR_COUNT=$((ERROR_COUNT + 1))
    fi
done

# ===========================================
# 检查 2: 硬编码 API Key
# ===========================================
echo "🔑 检查硬编码 API Key..."

# API Key 模式
API_KEY_PATTERNS=(
    "sk-[a-zA-Z0-9]{20,}"           # OpenAI/Zhipu
    "ghp_[a-zA-Z0-9]{30,}"          # GitHub
    "glpat-[a-zA-Z0-9]{20,}"        # GitLab
    "xox[baprs]-[0-9a-zA-Z]{10,}"   # Slack
    "AIza[a-zA-Z0-9]{35}"           # Google
    "AKIA[0-9A-Z]{16}"              # AWS
)

for pattern in "${API_KEY_PATTERNS[@]}"; do
    MATCHES=$(git diff --cached | grep -oE "$pattern" || true)
    if [ -n "$MATCHES" ]; then
        echo -e "${RED}❌ 错误：检测到可能的 API Key，禁止提交！${NC}"
        echo -e "${YELLOW}💡 提示：请使用环境变量替代硬编码的 API Key${NC}"
        echo -e "${YELLOW}   示例：System.getenv(\"API_KEY\")${NC}"
        ERROR_COUNT=$((ERROR_COUNT + 1))
    fi
done

# ===========================================
# 检查 3: 硬编码密码
# ===========================================
echo "🔒 检查硬编码密码..."

PASSWORD_PATTERNS=(
    "password\s*=\s*\"[^\"]{4,}\""
    "passwd\s*=\s*\"[^\"]{4,}\""
    "secret\s*=\s*\"[^\"]{8,}\""
)

for pattern in "${PASSWORD_PATTERNS[@]}"; do
    MATCHES=$(git diff --cached | grep -oE "$pattern" || true)
    if [ -n "$MATCHES" ]; then
        echo -e "${RED}❌ 错误：检测到硬编码密码，禁止提交！${NC}"
        echo -e "${YELLOW}💡 提示：请使用环境变量或配置文件${NC}"
        ERROR_COUNT=$((ERROR_COUNT + 1))
    fi
done

# ===========================================
# 检查结果
# ===========================================
echo ""
if [ $ERROR_COUNT -gt 0 ]; then
    echo -e "${RED}╔════════════════════════════════════════╗${NC}"
    echo -e "${RED}║  🔐 安全检查失败：发现 $ERROR_COUNT 个问题          ║${NC}"
    echo -e "${RED}║  请修复后重新提交                    ║${NC}"
    echo -e "${RED}╚════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${YELLOW}💡 如需跳过此检查（不推荐），使用：${NC}"
    echo -e "${YELLOW}   git commit -m \"message\" --no-verify${NC}"
    echo ""
    exit 1
else
    echo -e "${GREEN}✅ 安全检查通过！${NC}"
    exit 0
fi
