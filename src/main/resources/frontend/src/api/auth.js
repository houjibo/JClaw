import api from './index'

export const authApi = {
  // 登录
  login(username, password) {
    return api.post('/auth/login', { username, password })
  },
  
  // 登出
  logout() {
    return api.post('/auth/logout')
  },
  
  // 注册
  register(data) {
    return api.post('/auth/register', data)
  },
  
  // 刷新 Token
  refreshToken() {
    return api.post('/auth/refresh')
  },
  
  // 获取当前用户信息
  getCurrentUser() {
    return api.get('/auth/me')
  },
  
  // 修改密码
  changePassword(data) {
    return api.put('/auth/password', data)
  }
}

export default authApi
