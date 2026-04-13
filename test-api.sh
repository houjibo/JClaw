#!/bin/bash
# JClaw 快速测试脚本
# 用法：./test-api.sh

BASE_URL="http://localhost:18790"

echo "=========================================="
echo "  JClaw API 快速测试"
echo "=========================================="
echo ""

# 检查服务是否运行
echo "1. 检查服务状态..."
if curl -s --connect-timeout 2 "$BASE_URL/api/health" > /dev/null; then
    echo "✅ 服务运行中"
else
    echo "❌ 服务未运行，请先执行：./jclaw start"
    exit 1
fi
echo ""

# 健康检查
echo "2. 健康检查..."
curl -s "$BASE_URL/api/health" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/api/health"
echo ""

# 系统信息
echo "3. 系统信息..."
curl -s "$BASE_URL/api/system/info" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/api/system/info"
echo ""

# 配置状态
echo "4. 配置状态..."
curl -s "$BASE_URL/api/config/status" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/api/config/status"
echo ""

# 技能列表
echo "5. 技能列表..."
curl -s "$BASE_URL/api/skills" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/api/skills"
echo ""

# 测试 Bash 技能
echo "6. 测试 Bash 技能..."
curl -s -X POST "$BASE_URL/api/skills/execute" \
  -H "Content-Type: application/json" \
  -d '{"skill": "bash", "params": {"command": "echo Hello from JClaw"}}' | python3 -m json.tool 2>/dev/null
echo ""

# 测试 AI 对话
echo "7. 测试 AI 对话..."
curl -s -X POST "$BASE_URL/api/ai/chat" \
  -H "Content-Type: application/json" \
  -d '{"prompt": "你好，请自我介绍"}' | python3 -m json.tool 2>/dev/null
echo ""

# 测试 Git 技能
echo "8. 测试 Git 技能..."
curl -s -X POST "$BASE_URL/api/skills/execute" \
  -H "Content-Type: application/json" \
  -d '{"skill": "git", "params": {"command": "status", "repoPath": "."}}' | python3 -m json.tool 2>/dev/null
echo ""

# 日志文件列表
echo "9. 日志文件列表..."
curl -s "$BASE_URL/api/logs" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/api/logs"
echo ""

# 通道列表
echo "10. 通道列表..."
curl -s "$BASE_URL/api/channels" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/api/channels"
echo ""

echo "=========================================="
echo "  测试完成！"
echo "=========================================="
