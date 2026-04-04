<template>
  <div id="app">
    <el-container class="main-container">
      <!-- 侧边栏导航 -->
      <el-aside width="220px" class="sidebar">
        <div class="logo">
          <h2>JClaw</h2>
          <p>AI 原生研发平台</p>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="home">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="memory">
            <el-icon><Folder /></el-icon>
            <span>记忆管理</span>
          </el-menu-item>
          <el-menu-item index="intent">
            <el-icon><Aim /></el-icon>
            <span>意图驱动</span>
          </el-menu-item>
          <el-menu-item index="trace">
            <el-icon><Connection /></el-icon>
            <span>代码追溯</span>
          </el-menu-item>
          <el-menu-item index="impact">
            <el-icon><Warning /></el-icon>
            <span>影响分析</span>
          </el-menu-item>
          <el-menu-item index="agent">
            <el-icon><User /></el-icon>
            <span>Agent 管理</span>
          </el-menu-item>
          <el-menu-item index="channel">
            <el-icon><ChatDotRound /></el-icon>
            <span>通道管理</span>
          </el-menu-item>
          <el-menu-item index="test">
            <el-icon><Checked /></el-icon>
            <span>测试推荐</span>
          </el-menu-item>
          <el-menu-item index="chat">
            <el-icon><ChatLineRound /></el-icon>
            <span>💬 聊天</span>
          </el-menu-item>
          <el-menu-item index="config">
            <el-icon><Setting /></el-icon>
            <span>系统配置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container class="main-content">
        <!-- 顶部栏 -->
        <el-header class="top-header">
          <div class="header-left">
            <h3>{{ pageTitle }}</h3>
          </div>
          <div class="header-right">
            <el-tag type="success" size="small">🟢 运行中</el-tag>
            <el-divider direction="vertical" />
            <span class="version">v1.0.0</span>
          </div>
        </el-header>

        <!-- 内容区域 -->
        <el-main class="content-area">
          <component :is="currentComponent" />
        </el-main>

        <!-- 底部 -->
        <el-footer class="footer">
          <p>JClaw v1.0.0 - Made with ❤️ by Cola</p>
        </el-footer>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, provide } from 'vue'
import {
  HomeFilled,
  Folder,
  Aim,
  Connection,
  Warning,
  User,
  ChatDotRound,
  ChatLineRound,
  Setting,
  Checked
} from '@element-plus/icons-vue'

import Home from './views/Home.vue'
import MemoryManager from './views/MemoryManager.vue'
import IntentManager from './views/IntentManager.vue'
import TraceManager from './views/TraceManager.vue'
import ImpactAnalysis from './views/ImpactAnalysis.vue'
import SubagentManager from './views/SubagentManager.vue'
import ChannelManager from './views/ChannelManager.vue'
import TestRecommender from './views/TestRecommender.vue'
import ChatPanel from './views/ChatPanel.vue'
import ConfigPanel from './views/ConfigPanel.vue'

const activeMenu = ref('home')

const componentMap = {
  home: Home,
  memory: MemoryManager,
  intent: IntentManager,
  trace: TraceManager,
  impact: ImpactAnalysis,
  agent: SubagentManager,
  channel: ChannelManager,
  test: TestRecommender,
  chat: ChatPanel,
  config: ConfigPanel
}

const currentComponent = computed(() => componentMap[activeMenu.value])

const pageTitle = computed(() => {
  const titles = {
    home: '首页',
    memory: '记忆管理',
    intent: '意图驱动',
    trace: '代码追溯',
    impact: '影响分析',
    agent: 'Agent 管理',
    channel: '通道管理',
    test: '测试推荐',
    chat: '💬 聊天交互',
    config: '系统配置'
  }
  return titles[activeMenu.value] || 'JClaw'
})

const handleMenuSelect = (index) => {
  activeMenu.value = index
}

// 提供给子组件使用
provide('setActiveMenu', (menu) => {
  activeMenu.value = menu
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.main-container {
  height: 100%;
}

.sidebar {
  background: linear-gradient(180deg, #409EFF 0%, #3a8ee6 100%);
  color: white;
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255,255,255,0.2);
}

.logo h2 {
  margin: 0;
  font-size: 24px;
  color: white;
}

.logo p {
  margin: 5px 0 0;
  font-size: 12px;
  color: rgba(255,255,255,0.8);
}

.sidebar-menu {
  border-right: none !important;
  background: transparent !important;
  flex: 1;
  overflow-y: auto;
}

.sidebar-menu .el-menu-item {
  color: rgba(255,255,255,0.9) !important;
  border-radius: 8px;
  margin: 4px 8px;
}

.sidebar-menu .el-menu-item:hover {
  background: rgba(255,255,255,0.15) !important;
  color: white !important;
}

.sidebar-menu .el-menu-item.is-active {
  background: rgba(255,255,255,0.25) !important;
  color: white !important;
  font-weight: 600;
}

.sidebar-menu .el-icon {
  margin-right: 10px;
}

.main-content {
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.top-header {
  background: white;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  flex-shrink: 0;
}

.header-left h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.version {
  font-size: 12px;
  color: #909399;
}

.content-area {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.footer {
  background: white;
  border-top: 1px solid #e4e7ed;
  text-align: center;
  padding: 10px;
  height: 40px;
  flex-shrink: 0;
}

.footer p {
  margin: 0;
  font-size: 12px;
  color: #909399;
}
</style>
