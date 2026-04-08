import { defineStore } from 'pinia'
import { ref } from 'vue'
import { traceApi } from '@/api'

export const useTraceStore = defineStore('trace', () => {
  // 状态
  const codeUnits = ref([])
  const callRelationships = ref([])
  const callChain = ref([])
  const loading = ref(false)
  
  // 行动
  async function fetchCodeUnits(params) {
    loading.value = true
    try {
      const data = await traceApi.getCodeUnits(params)
      codeUnits.value = data.records || []
    } finally {
      loading.value = false
    }
  }
  
  async function fetchCallRelationships(params) {
    loading.value = true
    try {
      const data = await traceApi.getCallRelationships(params)
      callRelationships.value = data.records || []
    } finally {
      loading.value = false
    }
  }
  
  async function fetchCallChain(id) {
    loading.value = true
    try {
      callChain.value = await traceApi.getCallChain(id)
    } finally {
      loading.value = false
    }
  }
  
  async function parseAst(filePath) {
    return await traceApi.parseAst(filePath)
  }
  
  return {
    codeUnits,
    callRelationships,
    callChain,
    loading,
    fetchCodeUnits,
    fetchCallRelationships,
    fetchCallChain,
    parseAst
  }
})
