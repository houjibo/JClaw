<template>
  <div class="trace-manager">
    <h1>代码追溯</h1>

    <!-- 代码单元列表 -->
    <div class="code-units">
      <h2>代码单元</h2>
      <div v-for="unit in codeUnits" :key="unit.id" class="unit-card">
        <h3>{{ unit.unitName }}</h3>
        <p>路径：{{ unit.filePath }}</p>
        <p>类型：{{ unit.unitType }}</p>
        <button @click="viewCallChain(unit.id)">查看调用链</button>
        <button @click="analyzeImpact(unit.id)">影响分析</button>
      </div>
    </div>

    <!-- 调用链展示 -->
    <div v-if="callChain.length > 0" class="call-chain">
      <h2>调用链</h2>
      <div v-for="(call, index) in callChain" :key="call.id" class="call-item">
        <span>{{ index + 1 }}.</span>
        <span>{{ call.callerId }}</span>
        <span>→</span>
        <span>{{ call.calleeId }}</span>
        <span class="type">{{ call.callType }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TraceManager',
  data() {
    return {
      codeUnits: [],
      callChain: []
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
    async viewCallChain(id) {
      const res = await fetch(`/api/trace/callchain/${id}`)
      const data = await res.json()
      this.callChain = data.data || []
    },
    async analyzeImpact(id) {
      this.$router.push(`/impact/${id}`)
    }
  }
}
</script>

<style scoped>
.trace-manager { padding: 20px; }
.unit-card { border: 1px solid #ddd; padding: 15px; margin: 10px 0; border-radius: 8px; }
.call-chain { margin-top: 20px; }
.call-item { display: flex; gap: 10px; padding: 8px; background: #f5f5f5; margin: 5px 0; border-radius: 4px; }
.type { background: #2196F3; color: white; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
</style>
