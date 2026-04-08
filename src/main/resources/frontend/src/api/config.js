import api from './index'

export const configApi = {
  // 获取配置
  get(key) {
    return api.get(`/config/${key}`)
  },
  
  // 获取所有配置
  getAll() {
    return api.get('/config')
  },
  
  // 更新配置
  update(key, value) {
    return api.put(`/config/${key}`, { value })
  },
  
  // 批量更新配置
  batchUpdate(data) {
    return api.put('/config/batch', data)
  },
  
  // 重置配置
  reset(key) {
    return api.post(`/config/${key}/reset`)
  },
  
  // 获取配置历史
  getHistory(key, params) {
    return api.get(`/config/${key}/history`, { params })
  }
}

export default configApi
