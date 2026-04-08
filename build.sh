#!/bin/bash
# JClaw 一键构建脚本

set -e

echo "🔨 开始构建 JClaw..."

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

# 1. 构建前端
echo "📦 构建前端..."
cd src/main/resources/frontend
if [ ! -d "node_modules" ]; then
    npm install
fi
npm run build
cd "$PROJECT_DIR"

# 2. 构建后端
echo "☕ 构建后端..."
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
mvn clean package -DskipTests

echo "✅ 构建完成！"
echo "📦 JAR 文件位置：target/jclaw-1.0.0-SNAPSHOT.jar"
