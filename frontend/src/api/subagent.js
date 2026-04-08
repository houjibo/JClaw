import api from './index'

export const subagentApi = {
  // 获取 Subagent 列表
  getList(params) {
    return api.get('/subagent', { params })
  },
  
  // 创建 Subagent 任务
  createTask(data) {
    return api.post('/subagent/task', data)
  },
  
  // 获取任务详情
  getTask(taskId) {
    return api.get(`/subagent/task/${taskId}`)
  },
  
  // 获取任务状态
  getTaskStatus(taskId) {
    return api.get(`/subagent/task/${taskId}/status`)
  },
  
  // 取消任务
  cancelTask(taskId) {
    return api.post(`/subagent/task/${taskId}/cancel`)
  },
  
  // 获取任务结果
  getTaskResult(taskId) {
    return api.get(`/subagent/task/${taskId}/result`)
  },
  
  // 获取角色列表
  getRoles() {
    return api.get('/subagent/roles')
  }
}

export default subagentApi
