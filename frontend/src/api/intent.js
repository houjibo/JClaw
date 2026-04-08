import api from './index'

export const intentApi = {
  // 获取意图列表
  getList(params) {
    return api.get('/intent', { params })
  },
  
  // 获取意图详情
  getById(id) {
    return api.get(`/intent/${id}`)
  },
  
  // 创建意图
  create(data) {
    return api.post('/intent', data)
  },
  
  // 更新意图
  update(id, data) {
    return api.put(`/intent/${id}`, data)
  },
  
  // 删除意图
  delete(id) {
    return api.delete(`/intent/${id}`)
  },
  
  // 任务分解
  decompose(id) {
    return api.post(`/intent/${id}/decompose`)
  },
  
  // 获取任务分解结果
  getTasks(intentId) {
    return api.get(`/intent/${intentId}/tasks`)
  }
}

export default intentApi
