import api from './index'

export const traceApi = {
  // 获取代码单元列表
  getCodeUnits(params) {
    return api.get('/trace/code-units', { params })
  },
  
  // 获取代码单元详情
  getCodeUnit(id) {
    return api.get(`/trace/code-units/${id}`)
  },
  
  // 获取调用关系
  getCallRelationships(params) {
    return api.get('/trace/call-relationships', { params })
  },
  
  // 获取调用链
  getCallChain(id) {
    return api.get(`/trace/code-units/${id}/call-chain`)
  },
  
  // AST 解析
  parseAst(filePath) {
    return api.post('/trace/ast/parse', { filePath })
  },
  
  // 需求追溯
  getRequirementTrace(requirementId) {
    return api.get(`/trace/requirement/${requirementId}`)
  }
}

export default traceApi
