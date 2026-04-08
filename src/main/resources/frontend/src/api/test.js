import api from './index'

export const testApi = {
  // 获取推荐测试列表
  getRecommendations(params) {
    return api.get('/test/recommendations', { params })
  },
  
  // 获取测试覆盖率
  getCoverage() {
    return api.get('/test/coverage')
  },
  
  // 运行测试
  runTest(testId) {
    return api.post(`/test/${testId}/run`)
  },
  
  // 获取测试结果
  getTestResult(testId) {
    return api.get(`/test/${testId}/result`)
  },
  
  // 获取测试统计
  getStats() {
    return api.get('/test/stats')
  }
}

export default testApi
