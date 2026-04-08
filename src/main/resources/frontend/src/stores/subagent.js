import { defineStore } from 'pinia'
import { ref } from 'vue'
import { subagentApi } from '@/api/subagent'

export const useSubagentStore = defineStore('subagent', () => {
  // 状态
  const tasks = ref([])
  const roles = ref([])
  const loading = ref(false)
  const stats = ref({
    total: 0,
    running: 0,
    completed: 0,
    failed: 0
  })
  
  // 行动
  async function fetchTasks() {
    loading.value = true
    try {
      const data = await subagentApi.getList()
      tasks.value = data.records || []
      stats.value.total = data.total || 0
      stats.value.running = tasks.value.filter(t => t.status === 'running').length
      stats.value.completed = tasks.value.filter(t => t.status === 'completed').length
      stats.value.failed = tasks.value.filter(t => t.status === 'failed').length
    } finally {
      loading.value = false
    }
  }
  
  async function fetchRoles() {
    roles.value = await subagentApi.getRoles()
  }
  
  async function createTask(data) {
    return await subagentApi.createTask(data)
  }
  
  async function getTaskStatus(taskId) {
    return await subagentApi.getTaskStatus(taskId)
  }
  
  async function cancelTask(taskId) {
    return await subagentApi.cancelTask(taskId)
  }
  
  return {
    tasks,
    roles,
    loading,
    stats,
    fetchTasks,
    fetchRoles,
    createTask,
    getTaskStatus,
    cancelTask
  }
})
