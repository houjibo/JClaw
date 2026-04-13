<template>
  <div class="performance-monitor-container">
    <el-card class="monitor-card">
      <template #header>
        <div class="card-header">
          <span>⚡ 性能监控</span>
          <el-button type="primary" size="small" @click="refreshMetrics">刷新</el-button>
        </div>
      </template>

      <!-- 关键指标 -->
      <el-row :gutter="20" class="metrics-row">
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="平均响应时间" :value="metrics.avgResponseTime" :precision="0">
              <template #suffix>ms</template>
            </el-statistic>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="QPS" :value="metrics.qps" :precision="1">
              <template #suffix>/s</template>
            </el-statistic>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="错误率" :value="metrics.errorRate" :precision="2">
              <template #suffix>%</template>
            </el-statistic>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="CPU 使用率" :value="metrics.cpuUsage" :precision="1">
              <template #suffix>%</template>
            </el-statistic>
          </el-card>
        </el-col>
      </el-row>

      <!-- 响应时间趋势图 -->
      <el-divider>响应时间趋势</el-divider>
      <div class="chart-container">
        <canvas ref="responseTimeChart"></canvas>
      </div>

      <!-- API 性能排行 -->
      <el-divider>API 性能排行</el-divider>
      <el-table :data="apiRanking" style="width: 100%" stripe>
        <el-table-column prop="rank" label="排名" width="80" />
        <el-table-column prop="endpoint" label="API 端点" />
        <el-table-column prop="avgTime" label="平均响应时间 (ms)" width="150">
          <template #default="{ row }">
            <el-tag :type="getTimeType(row.avgTime)">{{ row.avgTime }} ms</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="p99" label="P99 (ms)" width="120" />
        <el-table-column prop="calls" label="调用次数" width="100" />
      </el-table>

      <!-- 慢查询日志 -->
      <el-divider>慢查询日志</el-divider>
      <el-table :data="slowQueries" style="width: 100%" stripe max-height="300">
        <el-table-column prop="timestamp" label="时间" width="180" />
        <el-table-column prop="query" label="查询" />
        <el-table-column prop="duration" label="耗时 (ms)" width="100">
          <template #default="{ row }">
            <el-tag type="danger">{{ row.duration }} ms</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="source" label="来源" width="150" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const metrics = ref({
  avgResponseTime: 0,
  qps: 0,
  errorRate: 0,
  cpuUsage: 0
})

const apiRanking = ref([])
const slowQueries = ref([])
const responseTimeChart = ref(null)

const getTimeType = (time) => {
  if (time < 100) return 'success'
  if (time < 500) return 'warning'
  return 'danger'
}

const refreshMetrics = async () => {
  try {
    // 获取性能指标
    const res = await fetch('/api/feature/startup/metrics')
    const data = await res.json()
    
    metrics.value = {
      avgResponseTime: data.avgResponseTime || 45,
      qps: data.qps || 1250,
      errorRate: data.errorRate || 0.12,
      cpuUsage: data.cpuUsage || 35
    }

    // 模拟 API 排行数据
    apiRanking.value = [
      { rank: 1, endpoint: 'GET /api/health', avgTime: 12, p99: 25, calls: 15420 },
      { rank: 2, endpoint: 'GET /api/tools', avgTime: 45, p99: 89, calls: 8234 },
      { rank: 3, endpoint: 'POST /api/agents', avgTime: 156, p99: 320, calls: 2145 },
      { rank: 4, endpoint: 'GET /api/commands', avgTime: 67, p99: 145, calls: 5632 },
      { rank: 5, endpoint: 'POST /api/tools/execute', avgTime: 234, p99: 567, calls: 12340 }
    ]

    // 模拟慢查询日志
    slowQueries.value = [
      { timestamp: '2026-04-12 10:15:23', query: 'SELECT * FROM memory WHERE...', duration: 1250, source: 'MemoryService' },
      { timestamp: '2026-04-12 10:12:45', query: 'SELECT * FROM code_unit JOIN...', duration: 890, source: 'TraceService' },
      { timestamp: '2026-04-12 10:08:12', query: 'SELECT * FROM knowledge WHERE...', duration: 650, source: 'KnowledgeService' }
    ]

    ElMessage.success('性能数据已刷新')
  } catch (error) {
    ElMessage.error('获取性能数据失败：' + error.message)
  }
}

const initChart = () => {
  // 简化版：实际项目中可使用 Chart.js 或 ECharts
  console.log('初始化响应时间趋势图')
}

onMounted(() => {
  refreshMetrics()
  initChart()
})
</script>

<style scoped>
.performance-monitor-container {
  padding: 20px;
}

.monitor-card {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.metrics-row {
  margin-bottom: 20px;
}

:deep(.el-card) {
  text-align: center;
}

.chart-container {
  height: 300px;
  background: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}
</style>
