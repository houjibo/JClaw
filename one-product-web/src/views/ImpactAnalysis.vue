<template>
  <div class="impact-analysis">
    <h1>影响分析</h1>

    <!-- 选择代码单元 -->
    <div class="selector">
      <select v-model="selectedUnitId">
        <option value="">选择代码单元</option>
        <option v-for="unit in codeUnits" :key="unit.id" :value="unit.id">
          {{ unit.unitName }} ({{ unit.filePath }})
        </option>
      </select>
      <button @click="analyzeImpact" :disabled="!selectedUnitId">分析</button>
    </div>

    <!-- 分析结果 -->
    <div v-if="analysis" class="result">
      <h2>影响分析结果</h2>
      <div class="risk-score">
        风险评分：<span :class="riskLevel">{{ analysis.riskScore }}</span>
      </div>
      <div class="statistics">
        <div v-for="(value, key) in analysis.statistics" :key="key">
          <strong>{{ key }}:</strong> {{ value }}
        </div>
      </div>
      <div class="affected-nodes">
        <h3>受影响节点 ({{ analysis.affectedNodes?.length || 0 }})</h3>
        <ul>
          <li v-for="(node, index) in analysis.affectedNodes" :key="index">
            {{ node.name || node.id }}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ImpactAnalysis',
  data() {
    return {
      selectedUnitId: '',
      codeUnits: [],
      analysis: null
    }
  },
  computed: {
    riskLevel() {
      if (!this.analysis) return ''
      const score = this.analysis.riskScore
      if (score > 70) return 'high'
      if (score > 40) return 'medium'
      return 'low'
    }
  },
  mounted() {
    this.loadCodeUnits()
  },
  methods: {
    async loadCodeUnits() {
      const res = await fetch('/api/trace/code-units')
      const data = await res.json()
      this.codeUnits = data.data || []
    },
    async analyzeImpact() {
      if (!this.selectedUnitId) return
      const res = await fetch(`/api/trace/impact/${this.selectedUnitId}`, {
        method: 'POST'
      })
      const data = await res.json()
      this.analysis = data.data
    }
  }
}
</script>

<style scoped>
.impact-analysis { padding: 20px; max-width: 900px; margin: 0 auto; }
.selector { margin-bottom: 20px; }
.selector select { padding: 8px; margin-right: 10px; width: 300px; }
.selector button { padding: 8px 20px; background: #4CAF50; color: white; border: none; border-radius: 4px; }
.selector button:disabled { background: #ccc; }
.result { border: 1px solid #ddd; padding: 20px; border-radius: 8px; background: #f9f9f9; }
.risk-score { font-size: 24px; margin: 20px 0; }
.risk-score .high { color: #f44336; }
.risk-score .medium { color: #ff9800; }
.risk-score .low { color: #4CAF50; }
.statistics { margin: 20px 0; }
.affected-nodes ul { line-height: 2; }
</style>
