#!/bin/bash

# JCode AI 助手测试脚本
# 使用 DeepSeek API 演示 AI 编码能力

API_KEY="sk-c0c89088c3ec49af8e6aa34d14abad76"
API_URL="https://api.deepseek.com/v1/chat/completions"

echo "╔════════════════════════════════════════╗"
echo "║       🤖 JCode AI 助手测试              ║"
echo "╚════════════════════════════════════════╝"
echo ""
echo "模型：DeepSeek Coder"
echo "API: $API_URL"
echo ""

# 测试 1: 代码生成
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📝 测试 1: 代码生成"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

REQUEST_BODY='{
  "model": "deepseek-coder",
  "messages": [
    {
      "role": "system",
      "content": "你是一个专业的 Java 程序员助手。请生成高质量、可运行的代码。"
    },
    {
      "role": "user",
      "content": "用 Java 实现一个线程安全的单例模式，包含双重检查锁定"
    }
  ],
  "max_tokens": 1024,
  "temperature": 0.7
}'

echo "请求：用 Java 实现线程安全的单例模式..."
echo ""

RESPONSE=$(curl -s -X POST "$API_URL" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $API_KEY" \
  -d "$REQUEST_BODY")

# 解析响应
CONTENT=$(echo "$RESPONSE" | grep -o '"content":"[^"]*"' | head -1 | sed 's/"content":"//;s/"$//' | sed 's/\\n/\n/g')

if [ -n "$CONTENT" ]; then
    echo "✅ AI 生成结果:"
    echo ""
    echo "$CONTENT"
else
    echo "❌ 请求失败"
    echo "响应：$RESPONSE"
fi

echo ""
echo ""

# 测试 2: 代码审查
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🔍 测试 2: 代码审查"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

REQUEST_BODY='{
  "model": "deepseek-coder",
  "messages": [
    {
      "role": "system",
      "content": "你是一个资深的代码审查专家。请审查代码并指出问题。"
    },
    {
      "role": "user",
      "content": "请审查以下 Java 代码：\n\npublic class UserService {\n    public User getUserById(Long id) {\n        return userRepository.findById(id);\n    }\n}"
    }
  ],
  "max_tokens": 1024,
  "temperature": 0.7
}'

echo "请求：审查 UserService 代码..."
echo ""

RESPONSE=$(curl -s -X POST "$API_URL" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $API_KEY" \
  -d "$REQUEST_BODY")

CONTENT=$(echo "$RESPONSE" | grep -o '"content":"[^"]*"' | head -1 | sed 's/"content":"//;s/"$//' | sed 's/\\n/\n/g')

if [ -n "$CONTENT" ]; then
    echo "✅ AI 审查结果:"
    echo ""
    echo "$CONTENT"
else
    echo "❌ 请求失败"
fi

echo ""
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ 测试完成！"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "提示：配置阿里云百炼 API Key 后可以使用更多模型"
echo "参考：ai bailian"
echo ""
