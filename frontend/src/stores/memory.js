import { defineStore } from 'pinia'
import { ref } from 'vue'
import { memoryApi } from '@/api'

export const useMemoryStore = defineStore('memory', () => {
  // 状态
  const memories = ref([])
  const loading = ref(false)
  const total = ref(0)
  const pagination = ref({
    page: 1,
    size: 20,
    query: ''
  })
  
  // 行动
  async function fetchMemories() {
    loading.value = true
    try {
      const data = await memoryApi.getList(pagination.value)
      memories.value = data.records || []
      total.value = data.total || 0
    } finally {
      loading.value = false
    }
  }
  
  async function searchMemories(query) {
    loading.value = true
    try {
      pagination.value.query = query
      const data = query ? await memoryApi.fullTextSearch(query) : await memoryApi.getList(pagination.value)
      memories.value = data.records || []
      total.value = data.total || 0
    } finally {
      loading.value = false
    }
  }
  
  async function createMemory(data) {
    return await memoryApi.create(data)
  }
  
  async function updateMemory(id, data) {
    return await memoryApi.update(id, data)
  }
  
  async function deleteMemory(id) {
    await memoryApi.delete(id)
    await fetchMemories()
  }
  
  return {
    memories,
    loading,
    total,
    pagination,
    fetchMemories,
    searchMemories,
    createMemory,
    updateMemory,
    deleteMemory
  }
})
