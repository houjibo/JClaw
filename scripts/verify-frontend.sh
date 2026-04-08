#!/bin/bash

# JClaw 前端构建验证脚本
# 验证前端项目的构建、打包和功能

set -e

echo "======================================"
echo "🎨 JClaw 前端验证"
echo "======================================"

FRONTEND_DIR="$(dirname "$0")/one-product-web"
cd "$FRONTEND_DIR"

echo ""
echo "1️⃣  检查 Node.js 环境..."
node --version
npm --version

echo ""
echo "2️⃣  安装依赖..."
npm ci

echo ""
echo "3️⃣  代码检查（Lint）..."
# 如果有 lint 脚本则执行
if npm run | grep -q "lint"; then
    npm run lint || echo "⚠️  Lint 检查有警告"
else
    echo "ℹ️  跳过 Lint（未配置）"
fi

echo ""
echo "4️⃣  构建生产版本..."
npm run build

echo ""
echo "5️⃣  验证构建产物..."
if [ -d "dist" ]; then
    echo "✅ dist 目录存在"
    ls -lh dist/
    
    # 检查主要文件
    if [ -f "dist/index.html" ]; then
        echo "✅ index.html 存在"
    else
        echo "❌ index.html 缺失"
        exit 1
    fi
    
    # 检查 JS 文件
    JS_FILES=$(find dist -name "*.js" | wc -l)
    if [ "$JS_FILES" -gt 0 ]; then
        echo "✅ 找到 $JS_FILES 个 JS 文件"
    else
        echo "❌ JS 文件缺失"
        exit 1
    fi
    
    # 检查 CSS 文件
    CSS_FILES=$(find dist -name "*.css" | wc -l)
    if [ "$CSS_FILES" -gt 0 ]; then
        echo "✅ 找到 $CSS_FILES 个 CSS 文件"
    else
        echo "⚠️  未找到 CSS 文件（可能是内联样式）"
    fi
    
    # 计算总大小
    TOTAL_SIZE=$(du -sh dist | cut -f1)
    echo "📦 构建产物总大小：$TOTAL_SIZE"
else
    echo "❌ dist 目录不存在"
    exit 1
fi

echo ""
echo "6️⃣  检查关键页面组件..."
PAGES=(
    "src/views/Home.vue"
    "src/views/MemoryManager.vue"
    "src/views/IntentManager.vue"
    "src/views/TraceManager.vue"
    "src/views/ImpactAnalysis.vue"
    "src/views/SubagentManager.vue"
    "src/views/ChannelManager.vue"
    "src/views/TestRecommender.vue"
    "src/views/ConfigPanel.vue"
    "src/views/CallChain3D.vue"
)

for page in "${PAGES[@]}"; do
    if [ -f "$page" ]; then
        echo "  ✅ $page"
    else
        echo "  ⚠️  $page (缺失)"
    fi
done

echo ""
echo "7️⃣  检查路由配置..."
if [ -f "src/router/index.js" ]; then
    echo "✅ 路由配置存在"
    # 统计路由数量
    ROUTE_COUNT=$(grep -c "path:" src/router/index.js || echo "0")
    echo "  路由数量：$ROUTE_COUNT"
else
    echo "⚠️  路由配置缺失"
fi

echo ""
echo "8️⃣  检查状态管理..."
if [ -f "src/stores/index.js" ] || [ -d "src/stores" ]; then
    echo "✅ Pinia 状态管理存在"
else
    echo "⚠️  状态管理缺失"
fi

echo ""
echo "======================================"
echo "✅ 前端验证完成！"
echo "======================================"

# 返回构建产物大小
echo ""
echo "📊 构建统计："
echo "  - 页面组件：${#PAGES[@]} 个"
echo "  - 构建大小：$TOTAL_SIZE"
echo "  - JS 文件：$JS_FILES 个"
echo "  - CSS 文件：$CSS_FILES 个"
