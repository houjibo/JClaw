#!/bin/bash
# JClaw 前端构建脚本

set -e

echo "🔨 开始构建 JClaw 前端..."

cd "$(dirname "$0")/.."
FRONTEND_DIR="frontend"

# 检查 Node.js
if ! command -v node &> /dev/null; then
    echo "❌ 错误：Node.js 未安装"
    exit 1
fi

echo "✅ Node.js 版本：$(node -v)"

# 进入前端目录
cd "$FRONTEND_DIR"

# 安装依赖（如果 node_modules 不存在）
if [ ! -d "node_modules" ]; then
    echo "📦 安装依赖..."
    npm install
fi

# 构建
echo "🚀 构建前端..."
npm run build

echo "✅ 前端构建完成！"
echo "📁 构建产物位置：src/main/resources/static/"
