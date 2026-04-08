import { defineStore } from 'pinia'
import { ref } from 'vue'
import { intentApi } from '@/api/intent'

export const useIntentStore = defineStore('intent', () => {
  // 状态
  const intents = ref([])
  const loading = ref(false)
  const total = ref(0)
  const pagination = ref({
    page: 1,
    size: 20,
    status: ''
  })
  
  // 行动
  async function fetchIntents() {
    loading.value = true
    try {
      const data = await intentApi.getList(pagination.value)
      intents.value = data.records || []
      total.value = data.total || 0
    } finally {
      loading.value = false
    }
  }
  
  async function createIntent(data) {
    return await intentApi.create(data)
  }
  
  async function updateIntent(id, data) {
    return await intentApi.update(id, data)
  }
  
  async function deleteIntent(id) {
    await intentApi.delete(id)
    await fetchIntents()
  }
  
  async function decomposeTask(intentId) {
    return await intentApi.decompose(intentId)
  }
  
  async function getTasks(intentId) {
    return await intentApi.getTasks(intentId)
  }
  
  return {
    intents,
    loading,
    total,
    pagination,
    fetchIntents,
    createIntent,
    updateIntent,
    deleteIntent,
    decomposeTask,
    getTasks
  }
})
