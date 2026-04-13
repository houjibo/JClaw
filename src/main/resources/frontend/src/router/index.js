import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import MemoryManager from '../views/MemoryManager.vue'
import IntentManager from '../views/IntentManager.vue'
import TraceManager from '../views/TraceManager.vue'
import ImpactAnalysis from '../views/ImpactAnalysis.vue'
import SubagentManager from '../views/SubagentManager.vue'
import ChannelManager from '../views/ChannelManager.vue'
import TestRecommender from '../views/TestRecommender.vue'
import ConfigPanel from '../views/ConfigPanel.vue'
import CallChain3D from '../views/CallChain3D.vue'
import CallChain3DPage from '../views/CallChain3DPage.vue'
import IntentGraphPage from '../views/IntentGraphPage.vue'
import Dashboard from '../views/Dashboard.vue'
import SkillMarketplace from '../views/SkillMarketplace.vue'
import PerformanceMonitor from '../views/PerformanceMonitor.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/memory',
    name: 'Memory',
    component: MemoryManager,
    meta: { requiresAuth: true }
  },
  {
    path: '/intent',
    name: 'Intent',
    component: IntentManager,
    meta: { requiresAuth: true }
  },
  {
    path: '/trace',
    name: 'Trace',
    component: TraceManager,
    meta: { requiresAuth: true }
  },
  {
    path: '/impact',
    name: 'Impact',
    component: ImpactAnalysis,
    meta: { requiresAuth: true }
  },
  {
    path: '/subagent',
    name: 'Subagent',
    component: SubagentManager,
    meta: { requiresAuth: true }
  },
  {
    path: '/channel',
    name: 'Channel',
    component: ChannelManager,
    meta: { requiresAuth: true }
  },
  {
    path: '/test',
    name: 'Test',
    component: TestRecommender,
    meta: { requiresAuth: true }
  },
  {
    path: '/config',
    name: 'Config',
    component: ConfigPanel,
    meta: { requiresAuth: true }
  },
  {
    path: '/trace-3d',
    name: 'Trace3D',
    component: CallChain3D,
    meta: { requiresAuth: true }
  },
  {
    path: '/call-chain-3d',
    name: 'CallChain3DPage',
    component: CallChain3DPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/intent-graph',
    name: 'IntentGraphPage',
    component: IntentGraphPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/skill-marketplace',
    name: 'SkillMarketplace',
    component: SkillMarketplace,
    meta: { requiresAuth: true }
  },
  {
    path: '/performance',
    name: 'Performance',
    component: PerformanceMonitor,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('jclaw_token')
  
  if (to.meta.requiresAuth === false) {
    // 登录页面，如果已登录则跳转到首页
    if (token) {
      next('/')
    } else {
      next()
    }
  } else if (to.meta.requiresAuth) {
    // 需要认证的页面
    if (token) {
      next()
    } else {
      next('/login')
    }
  } else {
    next()
  }
})

export default router
