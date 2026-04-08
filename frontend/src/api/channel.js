import api from './index'

export const channelApi = {
  // 获取通道列表
  getList() {
    return api.get('/channel')
  },
  
  // 获取通道详情
  getById(id) {
    return api.get(`/channel/${id}`)
  },
  
  // 创建通道
  create(data) {
    return api.post('/channel', data)
  },
  
  // 更新通道
  update(id, data) {
    return api.put(`/channel/${id}`, data)
  },
  
  // 删除通道
  delete(id) {
    return api.delete(`/channel/${id}`)
  },
  
  // 发送消息
  sendMessage(channelId, data) {
    return api.post(`/channel/${channelId}/message`, data)
  },
  
  // 获取消息历史
  getMessages(channelId, params) {
    return api.get(`/channel/${channelId}/messages`, { params })
  }
}

export default channelApi
