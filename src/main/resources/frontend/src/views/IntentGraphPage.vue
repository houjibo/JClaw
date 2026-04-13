<template>
  <div class="intent-graph-page">
    <el-card class="main-card">
      <template #header>
        <div class="page-header">
          <h2>🎯 意图图谱可视化</h2>
          <div class="actions">
            <el-button type="primary" @click="loadData">加载数据</el-button>
            <el-button @click="resetView">重置视图</el-button>
          </div>
        </div>
      </template>
      
      <!-- 3D 图谱 -->
      <CallChain3DEnhanced
        v-if="graphData.nodes.length > 0"
        :nodes="graphData.nodes"
        :links="graphData.links"
      />
      
      <!-- 空状态 -->
      <el-empty
        v-else
        description="点击"加载数据"查看意图图谱"
      >
        <el-button type="primary" @click="loadData">加载数据</el-button>
      </el-empty>
      
      <!-- 统计面板 -->
      <el-row :gutter="20" class="stats-row" v-if="graphData.nodes.length > 0">
        <el-col :span="6">
          <el-statistic title="节点总数" :value="stats.nodeCount" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="连接总数" :value="stats.linkCount" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="代码单元" :value="stats.codeUnitCount" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="需求数" :value="stats.requirementCount" />
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import CallChain3DEnhanced from '../components/CallChain3DEnhanced.vue'

const graphData = ref({ nodes: [], links: [] })

const stats = computed(() => {
  const nodeCount = graphData.value.nodes.length
  const linkCount = graphData.value.links.length
  const codeUnitCount = graphData.value.nodes.filter(n => 
    ['CLASS', 'METHOD', 'INTERFACE'].includes(n.type)
  ).length
  const requirementCount = graphData.value.nodes.filter(n => 
    n.type === 'REQUIREMENT'
  ).length
  
  return { nodeCount, linkCount, codeUnitCount, requirementCount }
})

const loadData = async () => {
  try {
    // 尝试从本地文件加载（开发环境）
    const response = await fetch('/tmp/intent_graph_data.json')
    if (!response.ok) throw new Error('数据文件不存在')
    
    const data = await response.json()
    graphData.value = {
      nodes: data.nodes || [],
      links: data.links || []
    }
    
    ElMessage.success(`加载 ${data.nodes.length} 个节点，${data.links.length} 个连接`)
  } catch (error) {
    // 加载示例数据
    loadDemoData()
    ElMessage.warning('使用示例数据（数据文件不存在）')
  }
}

const loadDemoData = () => {
  // 生成示例数据
  const nodes = []
  const links = []
  
  // 创建需求
  nodes.push({
    id: 'req_001',
    label: '用户管理功能',
    type: 'REQUIREMENT',
    status: 'DONE',
    x: 0, y: 0, z: 0
  })
  
  // 创建意图
  nodes.push({
    id: 'intent_001',
    label: '实现用户认证系统',
    type: 'INTENT',
    x: -100, y: 0, z: 0
  })
  
  // 创建类
  for (let i = 1; i <= 5; i++) {
    nodes.push({
      id: `class_${i}`,
      label: `Service${i}`,
      type: 'CLASS',
      file: `com/jclaw/service/Service${i}.java`,
      complexity: 10 + i,
      x: (Math.random() - 0.5) * 400,
      y: (Math.random() - 0.5) * 400,
      z: (Math.random() - 0.5) * 400
    })
    
    // 需求实现关系
    links.push({
      source: 'req_001',
      target: `class_${i}`,
      type: 'IMPLEMENTED_BY'
    })
    
    // 创建方法
    for (let j = 1; j <= 3; j++) {
      nodes.push({
        id: `class_${i}_method${j}`,
        label: `method${j}()`,
        type: 'METHOD',
        complexity: 5 + j,
        x: (Math.random() - 0.5) * 400,
        y: (Math.random() - 0.5) * 400,
        z: (Math.random() - 0.5) * 400
      })
      
      // 包含关系
      links.push({
        source: `class_${i}`,
        target: `class_${i}_method${j}`,
        type: 'CONTAINS'
      })
      
      // 调用关系
      if (j > 1) {
        links.push({
          source: `class_${i}_method${j}`,
          target: `class_${i}_method${j-1}`,
          callCount: 5 + j
        })
      }
    }
  }
  
  // 意图分解关系
  links.push({
    source: 'intent_001',
    target: 'req_001',
    type: 'DECOMPOSED_INTO'
  })
  
  graphData.value = { nodes, links }
}

const resetView = () => {
  graphData.value = { nodes: [], links: [] }
}
</script>

<style scoped>
.intent-graph-page {
  padding: 20px;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.main-card {
  max-width: 1600px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.95);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.actions {
  display: flex;
  gap: 10px;
}

.stats-row {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

:deep(.el-statistic) {
  text-align: center;
}
</style>
