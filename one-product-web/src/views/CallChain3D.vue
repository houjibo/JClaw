<template>
  <div class="call-chain-3d">
    <h1>调用链 3D 可视化</h1>

    <!-- 代码单元选择 -->
    <div class="selector">
      <select v-model="selectedUnitId" @change="loadCallChain">
        <option value="">选择代码单元</option>
        <option v-for="unit in codeUnits" :key="unit.id" :value="unit.id">
          {{ unit.unitName }} ({{ unit.filePath }})
        </option>
      </select>
    </div>

    <!-- 3D 图谱容器 -->
    <div ref="chartContainer" class="chart-container"></div>

    <!-- 图例 -->
    <div class="legend">
      <div class="legend-item">
        <span class="dot class"></span> 类
      </div>
      <div class="legend-item">
        <span class="dot method"></span> 方法
      </div>
      <div class="legend-item">
        <span class="dot function"></span> 函数
      </div>
    </div>

    <!-- 节点详情 -->
    <div v-if="selectedNode" class="node-detail">
      <h3>{{ selectedNode.name }}</h3>
      <p>类型：{{ selectedNode.type }}</p>
      <p>文件：{{ selectedNode.filePath }}</p>
      <p>调用次数：{{ selectedNode.callCount }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CallChain3D',
  data() {
    return {
      codeUnits: [],
      selectedUnitId: '',
      callChain: [],
      selectedNode: null,
      chart: null
    }
  },
  mounted() {
    this.loadCodeUnits()
    this.init3DChart()
  },
  methods: {
    async loadCodeUnits() {
      const res = await fetch('/api/trace/code-units')
      const data = await res.json()
      this.codeUnits = data.data || []
    },
    async loadCallChain() {
      if (!this.selectedUnitId) return
      
      const res = await fetch(`/api/trace/callchain/${this.selectedUnitId}`)
      const data = await res.json()
      this.callChain = data.data || []
      this.update3DChart()
    },
    init3DChart() {
      // TODO: 集成 ECharts GL 或 Three.js
      // 当前使用简化实现
      console.log('初始化 3D 图谱')
    },
    update3DChart() {
      // TODO: 更新 3D 图谱数据
      console.log('更新 3D 图谱', this.callChain)
    },
    onNodeClick(node) {
      this.selectedNode = node
    }
  }
}
</script>

<style scoped>
.call-chain-3d { padding: 20px; }
.selector { margin-bottom: 20px; }
.selector select { padding: 8px; width: 400px; }
.chart-container { width: 100%; height: 600px; border: 1px solid #ddd; border-radius: 8px; background: #fafafa; }
.legend { display: flex; gap: 20px; margin: 15px 0; }
.legend-item { display: flex; align-items: center; gap: 5px; }
.dot { width: 12px; height: 12px; border-radius: 50%; display: inline-block; }
.dot.class { background: #f44336; }
.dot.method { background: #2196F3; }
.dot.function { background: #4CAF50; }
.node-detail { margin-top: 20px; padding: 15px; background: #f5f5f5; border-radius: 8px; }
</style>
