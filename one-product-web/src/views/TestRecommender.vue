<template>
  <div class="test-recommender">
    <h1>测试推荐</h1>

    <!-- 代码文件输入 -->
    <div class="input-section">
      <input v-model="filePath" placeholder="代码文件路径，如：/src/main/java/Test.java" />
      <button @click="recommendTests">推荐测试</button>
    </div>

    <!-- 推荐结果 -->
    <div v-if="recommendations.length > 0" class="recommendations">
      <h2>推荐测试用例</h2>
      <div v-for="(rec, index) in recommendations" :key="index" class="rec-card">
        <h3>{{ rec.testName }}</h3>
        <p>原因：{{ rec.reason }}</p>
        <span class="priority" :class="rec.priority.toLowerCase()">{{ rec.priority }}</span>
      </div>
    </div>

    <!-- 覆盖率分析 -->
    <div v-if="coverage" class="coverage">
      <h2>代码覆盖率</h2>
      <div class="coverage-stats">
        <div class="stat">
          <span class="label">行覆盖率:</span>
          <span class="value">{{ coverage.lineCoverage }}%</span>
        </div>
        <div class="stat">
          <span class="label">分支覆盖率:</span>
          <span class="value">{{ coverage.branchCoverage }}%</span>
        </div>
      </div>
      <div v-if="coverage.uncoveredLines?.length > 0">
        <h3>未覆盖行:</h3>
        <span class="uncovered">{{ coverage.uncoveredLines.join(', ') }}</span>
      </div>
    </div>

    <!-- 测试建议 -->
    <div v-if="suggestions.length > 0" class="suggestions">
      <h2>测试建议</h2>
      <ul>
        <li v-for="(sug, index) in suggestions" :key="index">
          <strong>{{ sug.suggestion }}</strong> - {{ sug.reason }}
          <span class="priority" :class="sug.priority.toLowerCase()">{{ sug.priority }}</span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TestRecommender',
  data() {
    return {
      filePath: '',
      recommendations: [],
      coverage: null,
      suggestions: []
    }
  },
  methods: {
    async recommendTests() {
      const res = await fetch('/api/test/recommend', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ filePath: this.filePath })
      })
      const data = await res.json()
      this.recommendations = data.data || []
      
      // 同时获取覆盖率和же
      this.loadCoverage()
      this.loadSuggestions()
    },
    async loadCoverage() {
      const res = await fetch(`/api/test/coverage?filePath=${this.filePath}`)
      const data = await res.json()
      this.coverage = data.data
    },
    async loadSuggestions() {
      const res = await fetch(`/api/test/suggestions?filePath=${this.filePath}`)
      const data = await res.json()
      this.suggestions = data.data || []
    }
  }
}
</script>

<style scoped>
.test-recommender { padding: 20px; max-width: 900px; margin: 0 auto; }
.input-section { margin-bottom: 20px; }
.input-section input { padding: 8px; width: 400px; margin-right: 10px; }
.input-section button { padding: 8px 20px; background: #4CAF50; color: white; border: none; border-radius: 4px; }
.rec-card { border: 1px solid #ddd; padding: 15px; margin: 10px 0; border-radius: 8px; }
.priority { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 12px; margin-left: 10px; }
.priority.high { background: #f44336; color: white; }
.priority.medium { background: #ff9800; color: white; }
.priority.low { background: #4CAF50; color: white; }
.coverage { margin-top: 20px; padding: 20px; background: #f5f5f5; border-radius: 8px; }
.coverage-stats { display: flex; gap: 20px; margin: 15px 0; }
.stat { font-size: 18px; }
.stat .value { font-weight: bold; color: #2196F3; }
.uncovered { background: #ffebee; padding: 4px 8px; border-radius: 4px; }
.suggestions ul { line-height: 2; }
</style>
