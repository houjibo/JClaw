<template>
  <div class="trace-manager">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>代码追溯</h2>
          <el-tag type="primary">代码单元：{{ codeUnits.length }}</el-tag>
        </div>
      </template>

      <!-- 代码单元列表 -->
      <div class="code-units">
        <h3>代码单元</h3>
        <el-table :data="codeUnits" style="width: 100%" border stripe>
          <el-table-column prop="unitName" label="单元名称" width="200" />
          <el-table-column prop="filePath" label="文件路径" show-overflow-tooltip />
          <el-table-column prop="unitType" label="类型" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.unitType === 'class' ? 'primary' : 'info'" size="small">
                {{ scope.row.unitType }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="complexity" label="复杂度" width="80" />
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button size="small" @click="viewCallChain(scope.row.id)">查看调用链</el-button>
              <el-button size="small" type="warning" @click="analyzeImpact(scope.row.id)">影响分析</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 调用链展示 -->
      <div v-if="callChain.length > 0" class="call-chain">
        <h3>调用链</h3>
        <div v-for="(call, index) in callChain" :key="call.id" class="call-item">
          <span class="index">{{ index + 1 }}.</span>
          <el-tag size="small">{{ call.callerId }}</el-tag>
          <span class="arrow">→</span>
          <el-tag size="small" type="success">{{ call.calleeId }}</el-tag>
          <span class="type">{{ call.callType }}</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const codeUnits = ref([
  { id: 1, unitName: 'UserService', filePath: '/src/main/java/com/jclaw/service/UserService.java', unitType: 'class', complexity: 15 },
  { id: 2, unitName: 'MemoryManager', filePath: '/src/main/java/com/jclaw/memory/MemoryManager.java', unitType: 'class', complexity: 22 },
  { id: 3, unitName: 'IntentRecognizer', filePath: '/src/main/java/com/jclaw/intent/IntentRecognizer.java', unitType: 'class', complexity: 18 },
  { id: 4, unitName: 'TraceService', filePath: '/src/main/java/com/jclaw/trace/TraceService.java', unitType: 'class', complexity: 25 },
  { id: 5, unitName: 'ImpactAnalyzer', filePath: '/src/main/java/com/jclaw/impact/ImpactAnalyzer.java', unitType: 'class', complexity: 30 }
])

const callChain = ref([])

const viewCallChain = (id) => {
  callChain.value = [
    { id: 1, callerId: 'Controller', calleeId: 'Service', callType: 'METHOD_CALL' },
    { id: 2, callerId: 'Service', calleeId: 'Repository', callType: 'METHOD_CALL' },
    { id: 3, callerId: 'Repository', calleeId: 'Database', callType: 'QUERY' }
  ]
}

const analyzeImpact = (id) => {
  // 跳转到影响分析页面
}
</script>

<style scoped>
.trace-manager {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h2 {
  margin: 0;
  color: #303133;
  font-size: 20px;
}

h3 {
  margin: 20px 0 15px;
  color: #606266;
  font-size: 16px;
}

.code-units {
  margin-bottom: 30px;
}

.call-chain {
  margin-top: 20px;
}

.call-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 12px;
  margin: 8px 0;
  background: #f5f7fa;
  border-radius: 6px;
}

.index {
  font-weight: bold;
  color: #909399;
}

.arrow {
  color: #909399;
  font-size: 18px;
}

.type {
  margin-left: auto;
  padding: 4px 12px;
  background: #e6f7ff;
  color: #1890ff;
  border-radius: 4px;
  font-size: 12px;
}
</style>
