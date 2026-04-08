<template>
  <div class="trace-manager">
    <el-row :gutter="20">
      <!-- 左侧：代码单元列表 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>代码单元</span>
              <el-input
                v-model="searchQuery"
                placeholder="搜索文件/方法..."
                prefix-icon="Search"
                style="width: 250px"
                @keyup.enter="fetchCodeUnits"
              />
            </div>
          </template>
          
          <el-table :data="traceStore.codeUnits" stripe v-loading="traceStore.loading" @row-click="handleRowClick">
            <el-table-column prop="file_path" label="文件路径" show-overflow-tooltip />
            <el-table-column prop="unit_name" label="名称" />
            <el-table-column prop="unit_type" label="类型" width="80">
              <template #default="{ row }">
                <el-tag size="small">{{ row.unit_type }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <!-- 右侧：调用链可视化 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>调用链分析</span>
              <el-button icon="Refresh" @click="refreshCallChain">刷新</el-button>
            </div>
          </template>
          
          <div v-if="selectedUnit" class="call-chain-container">
            <div class="unit-info">
              <h4>{{ selectedUnit.unit_name }}</h4>
              <p class="file-path">{{ selectedUnit.file_path }}</p>
            </div>
            
            <div class="chain-visualization" ref="chainContainer">
              <!-- 调用链树形展示 -->
              <el-tree
                :data="callChainTree"
                :props="{ label: 'name', children: 'children' }"
                default-expand-all
                node-key="id"
              >
                <template #default="{ node, data }">
                  <span class="tree-node">
                    <el-tag size="small" :type="getNodeTypeColor(data.type)">{{ data.type }}</el-tag>
                    <span style="margin-left: 8px">{{ node.label }}</span>
                  </span>
                </template>
              </el-tree>
            </div>
          </div>
          
          <el-empty v-else description="请选择一个代码单元" />
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 底部：调用关系统计 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>调用关系统计</span>
          </template>
          <el-descriptions :column="4" border>
            <el-descriptions-item label="总调用数">{{ callStats.total }}</el-descriptions-item>
            <el-descriptions-item label="入调用">{{ callStats.incoming }}</el-descriptions-item>
            <el-descriptions-item label="出调用">{{ callStats.outgoing }}</el-descriptions-item>
            <el-descriptions-item label="圈复杂度">{{ callStats.complexity }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useTraceStore } from '@/stores/trace'

const traceStore = useTraceStore()
const searchQuery = ref('')
const selectedUnit = ref(null)
const callStats = reactive({
  total: 0,
  incoming: 0,
  outgoing: 0,
  complexity: 0
})

const callChainTree = computed(() => {
  if (!traceStore.callChain || traceStore.callChain.length === 0) return []
  
  // 将调用链转换为树形结构
  return buildTree(traceStore.callChain)
})

const buildTree = (relationships) => {
  // 简化版：实际应该根据调用关系构建树
  return relationships.map((rel, index) => ({
    id: rel.id || index,
    name: rel.callee_name || rel.method,
    type: rel.call_type || 'CALL',
    children: []
  }))
}

const getNodeTypeColor = (type) => {
  const colors = {
    'CALL': '',
    'INHERIT': 'success',
    'IMPLEMENT': 'warning',
    'IMPORT': 'info'
  }
  return colors[type] || ''
}

const fetchCodeUnits = async () => {
  await traceStore.fetchCodeUnits({ query: searchQuery.value })
}

const handleRowClick = async (row) => {
  selectedUnit.value = row
  await traceStore.fetchCallChain(row.id)
  
  // 更新统计
  callStats.total = traceStore.callRelationships.length
  callStats.incoming = traceStore.callRelationships.filter(r => r.callee_id === row.id).length
  callStats.outgoing = traceStore.callRelationships.filter(r => r.caller_id === row.id).length
  callStats.complexity = row.complexity || 1
}

const refreshCallChain = async () => {
  if (selectedUnit.value) {
    await traceStore.fetchCallChain(selectedUnit.value.id)
  }
}

onMounted(() => {
  fetchCodeUnits()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.call-chain-container {
  height: 500px;
  overflow-y: auto;
}

.unit-info {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.unit-info h4 {
  margin: 0 0 8px 0;
  color: #303133;
}

.file-path {
  margin: 0;
  color: #909399;
  font-size: 13px;
  font-family: monospace;
}

.chain-visualization {
  padding: 10px;
}

.tree-node {
  display: flex;
  align-items: center;
}
</style>
