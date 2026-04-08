import api from './index'

export const memoryApi = {
  // 获取记忆列表
  getList(params) {
    return api.get('/memory', { params })
  },
  
  // 获取记忆详情
  getById(id) {
    return api.get(`/memory/${id}`)
  },
  
  // 创建记忆
  create(data) {
    return api.post('/memory', data)
  },
  
  // 更新记忆
  update(id, data) {
    return api.put(`/memory/${id}`, data)
  },
  
  // 删除记忆
  delete(id) {
    return api.delete(`/memory/${id}`)
  },
  
  // 搜索记忆
  search(query) {
    return api.get('/memory/search', { params: { query } })
  },
  
  // 全文搜索
  fullTextSearch(query) {
    return api.get('/memory/search/fulltext', { params: { query } })
  }
}

export default memoryApi
