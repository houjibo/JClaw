import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('jclaw_token') || '')
  const user = ref(null)
  const loading = ref(false)
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const userName = computed(() => user.value?.username || '')
  
  // 行动
  async function login(username, password) {
    loading.value = true
    try {
      const data = await authApi.login(username, password)
      token.value = data.token
      user.value = data.user
      localStorage.setItem('jclaw_token', token.value)
      return data
    } finally {
      loading.value = false
    }
  }
  
  async function logout() {
    try {
      await authApi.logout()
    } finally {
      token.value = ''
      user.value = null
      localStorage.removeItem('jclaw_token')
    }
  }
  
  async function getCurrentUser() {
    if (!token.value) return null
    try {
      user.value = await authApi.getCurrentUser()
      return user.value
    } catch (error) {
      console.error('获取用户信息失败:', error)
      return null
    }
  }
  
  return {
    token,
    user,
    loading,
    isLoggedIn,
    userName,
    login,
    logout,
    getCurrentUser
  }
})
