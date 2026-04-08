import api from './index'

export const impactApi = {
  // 分析变更影响
  analyze(data) {
    return api.post('/impact/analyze', data)
  },
  
  // 获取影响分析结果
  getResult(analysisId) {
    return api.get(`/impact/analysis/${analysisId}`)
  },
  
  // 获取影响列表
  getList(params) {
    return api.get('/impact', { params })
  },
  
  // 获取影响详情
  getById(id) {
    return api.get(`/impact/${id}`)
  },
  
  // 风险评分
  getRiskScore(changeId) {
    return api.get(`/impact/${changeId}/risk-score`)
  }
}

export default impactApi
