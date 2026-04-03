import { defineStore } from 'pinia'
import axios from 'axios'

// 创建 axios 实例
const api = axios.create({
  baseURL: '/api',
  timeout: 30000
})

export const useModelStore = defineStore('model', {
  state: () => ({
    // 大模型配置
    models: {
      qwen: {
        enabled: true,
        name: 'Qwen3.5-Plus',
        provider: 'modelstudio',
        baseUrl: 'https://coding.dashscope.aliyuncs.com/v1',
        apiKey: '',
        model: 'qwen3.5-plus'
      },
      kimi: {
        enabled: false,
        name: 'Kimi K2.5',
        provider: 'moonshot',
        baseUrl: 'https://api.moonshot.cn/v1',
        apiKey: '',
        model: 'kimi-k2.5'
      },
      glm: {
        enabled: false,
        name: 'GLM-4.7',
        provider: 'zhipu',
        baseUrl: 'https://open.bigmodel.cn/api/paas/v4',
        apiKey: '',
        model: 'glm-4.7'
      }
    },
    // 当前使用的模型
    activeModel: 'qwen',
    // 加载状态
    loading: false
  }),
  
  getters: {
    activeModelConfig: (state) => state.models[state.activeModel]
  },
  
  actions: {
    // 更新 API Key
    updateApiKey(provider, apiKey) {
      if (this.models[provider]) {
        this.models[provider].apiKey = apiKey
        this.saveToLocalStorage()
      }
    },
    
    // 切换模型
    switchModel(provider) {
      if (this.models[provider]) {
        this.activeModel = provider
        this.saveToLocalStorage()
      }
    },
    
    // 启用/禁用模型
    toggleModel(provider, enabled) {
      if (this.models[provider]) {
        this.models[provider].enabled = enabled
        this.saveToLocalStorage()
      }
    },
    
    // 保存到本地存储
    saveToLocalStorage() {
      localStorage.setItem('jclaw-models', JSON.stringify(this.models))
      localStorage.setItem('jclaw-active-model', this.activeModel)
    },
    
    // 从本地存储加载
    loadFromLocalStorage() {
      const savedModels = localStorage.getItem('jclaw-models')
      const savedActive = localStorage.getItem('jclaw-active-model')
      
      if (savedModels) {
        this.models = JSON.parse(savedModels)
      }
      
      if (savedActive && this.models[savedActive]) {
        this.activeModel = savedActive
      }
    },
    
    // 测试连接
    async testConnection(provider) {
      this.loading = true
      try {
        const config = this.models[provider]
        const response = await fetch(`${config.baseUrl}/chat/completions`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${config.apiKey}`
          },
          body: JSON.stringify({
            model: config.model,
            messages: [{ role: 'user', content: 'Hello' }],
            max_tokens: 10
          })
        })
        
        if (response.ok) {
          return { success: true, message: '连接成功' }
        } else {
          const error = await response.json()
          return { success: false, message: error.error?.message || '连接失败' }
        }
      } catch (error) {
        return { success: false, message: error.message }
      } finally {
        this.loading = false
      }
    }
  }
})
