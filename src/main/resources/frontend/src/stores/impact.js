import { defineStore } from 'pinia'
import { ref } from 'vue'
import { impactApi } from '@/api/impact'

export const useImpactStore = defineStore('impact', () => {
  // 状态
  const analyses = ref([])
  const currentAnalysis = ref(null)
  const loading = ref(false)
  
  // 行动
  async function fetchAnalyses() {
    loading.value = true
    try {
      const data = await impactApi.getList()
      analyses.value = data.records || []
    } finally {
      loading.value = false
    }
  }
  
  async function analyzeChange(data) {
    loading.value = true
    try {
      currentAnalysis.value = await impactApi.analyze(data)
      return currentAnalysis.value
    } finally {
      loading.value = false
    }
  }
  
  async function getResult(analysisId) {
    return await impactApi.getResult(analysisId)
  }
  
  async function getRiskScore(changeId) {
    return await impactApi.getRiskScore(changeId)
  }
  
  return {
    analyses,
    currentAnalysis,
    loading,
    fetchAnalyses,
    analyzeChange,
    getResult,
    getRiskScore
  }
})
