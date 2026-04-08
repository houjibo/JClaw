<template>
  <div id="jclaw-app">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="220px">
        <div class="logo">
          <el-icon><Monitor /></el-icon>
          <span>JClaw</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#1a1a2e"
          text-color="#e0e0e0"
          active-text-color="#00d9ff"
        >
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/memory">
            <el-icon><Document /></el-icon>
            <span>记忆管理</span>
          </el-menu-item>
          <el-menu-item index="/intent">
            <el-icon><Aim /></el-icon>
            <span>意图管理</span>
          </el-menu-item>
          <el-menu-item index="/trace">
            <el-icon><Connection /></el-icon>
            <span>代码追溯</span>
          </el-menu-item>
          <el-menu-item index="/impact">
            <el-icon><TrendCharts /></el-icon>
            <span>影响分析</span>
          </el-menu-item>
          <el-menu-item index="/subagent">
            <el-icon><User /></el-icon>
            <span>Subagent</span>
          </el-menu-item>
          <el-menu-item index="/channel">
            <el-icon><ChatDotRound /></el-icon>
            <span>通道管理</span>
          </el-menu-item>
          <el-menu-item index="/test">
            <el-icon><Finished /></el-icon>
            <span>测试推荐</span>
          </el-menu-item>
          <el-menu-item index="/config">
            <el-icon><Setting /></el-icon>
            <span>配置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <el-header>
          <div class="header-content">
            <h2>{{ pageTitle }}</h2>
            <div class="user-info">
              <el-tag type="success" size="small">v1.0.0</el-tag>
              <el-dropdown @command="handleCommand">
                <span class="user-name">
                  <el-icon><User /></el-icon>
                  {{ userStore.userName || '用户' }}
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                    <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const pageTitle = computed(() => {
  const titles = {
    '/': 'JClaw 控制台',
    '/memory': '记忆管理',
    '/intent': '意图管理',
    '/trace': '代码追溯',
    '/impact': '影响分析',
    '/subagent': 'Subagent 管理',
    '/channel': '通道管理',
    '/test': '测试推荐',
    '/config': '系统配置'
  }
  return titles[route.path] || 'JClaw'
})

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/config')
  }
}
</script>

<style scoped>
#jclaw-app {
  height: 100vh;
  overflow: hidden;
}

.el-container {
  height: 100%;
}

.el-aside {
  background: #1a1a2e;
  color: #e0e0e0;
  overflow-x: hidden;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px;
  font-size: 24px;
  font-weight: bold;
  color: #00d9ff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.el-menu {
  border-right: none;
}

.el-header {
  background: #16213e;
  color: #e0e0e0;
  display: flex;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.header-content h2 {
  margin: 0;
  font-size: 20px;
  color: #00d9ff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  color: #e0e0e0;
}

.user-name:hover {
  color: #00d9ff;
}

.el-main {
  background: #0f0f1a;
  padding: 20px;
  overflow-y: auto;
}
</style>
