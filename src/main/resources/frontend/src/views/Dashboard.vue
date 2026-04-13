<template>
  <div class="dashboard-container">
    <el-card class="dashboard-card">
      <template #header>
        <div class="card-header">
          <span>📊 系统概览</span>
          <el-button type="primary" size="small" @click="refreshData">刷新</el-button>
        </div>
      </template>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :span="6">
          <el-statistic title="工具数量" :value="stats.toolsCount">
            <template #suffix>个</template>
          </el-statistic>
        </el-col>
        <el-col :span="6">
          <el-statistic title="命令数量" :value="stats.commandsCount">
            <template #suffix>个</template>
          </el-statistic>
        </el-col>
        <el-col :span="6">
          <el-statistic title="Agent 数量" :value="stats.agentsCount">
            <template #suffix>个</template>
          </el-statistic>
        </el-col>
        <el-col :span="6">
          <el-statistic title="任务完成率" :value="stats.taskSuccessRate" :precision="1">
            <template #suffix>%</template>
          </el-statistic>
        </el-col>
      </el-row>

      <!-- 系统状态 -->
      <el-divider>系统状态</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="服务状态">
          <el-tag :type="health.status === 'UP' ? 'success' : 'danger'">
            {{ health.status === 'UP' ? '正常运行' : '异常' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="启动时间">
          {{ health.uptime }}
        </el-descriptions-item>
        <el-descriptions-item label="内存使用">
          {{ health.memoryUsage }}
        </el-descriptions-item>
        <el-descriptions-item label="缓存命中率">
          {{ health.cacheHitRate }}%
        </el-descriptions-item>
      </el-descriptions>

      <!-- 最近活动 -->
      <el-divider>最近活动</el-divider>
      <el-timeline>
        <el-timeline-item
          v-for="(activity, index) in activities"
          :key="index"
          :timestamp="activity.timestamp"
          placement="top"
          :type="activity.type"
        >
          {{ activity.content }}
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const stats = ref({
  toolsCount: 0,
  commandsCount: 0,
  agentsCount: 0,
  taskSuccessRate: 0
})

const health = ref({
  status: 'UNKNOWN',
  uptime: '-',
  memoryUsage: '-',
  cacheHitRate: 0
})

const activities = ref([
  { content: '系统启动', timestamp: '2026-04-12 10:00', type: 'primary' },
  { content: '加载 47 个工具', timestamp: '2026-04-12 10:01', type: 'success' },
  { content: '初始化完成', timestamp: '2026-04-12 10:01', type: 'success' }
])

const refreshData = async () => {
  try {
    // 获取工具统计
    const toolsRes = await fetch('/api/tools')
    const toolsData = await toolsRes.json()
    stats.value.toolsCount = toolsData.length || 0

    // 获取命令统计
    const commandsRes = await fetch('/api/commands')
    const commandsData = await commandsRes.json()
    stats.value.commandsCount = commandsData.length || 0

    // 获取 Agent 统计
    const agentsRes = await fetch('/api/agents/stats')
    const agentsData = await agentsRes.json()
    stats.value.agentsCount = agentsData.agentCount || 0
    stats.value.taskSuccessRate = agentsData.taskSuccessRate || 0

    // 获取健康状态
    const healthRes = await fetch('/api/health')
    const healthData = await healthRes.json()
    health.value.status = healthData.status
    health.value.uptime = healthData.uptime || '-'
    health.value.memoryUsage = healthData.memoryUsage || '-'
    health.value.cacheHitRate = healthData.cacheHitRate || 0

    ElMessage.success('数据已刷新')
  } catch (error) {
    ElMessage.error('获取数据失败：' + error.message)
  }
}

onMounted(() => {
  refreshData()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.dashboard-card {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-row {
  margin-bottom: 20px;
}

:deep(.el-statistic) {
  text-align: center;
}
</style>
