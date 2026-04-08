<template>
  <div class="home">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="6" v-for="stat in stats" :key="stat.title">
        <el-card class="stat-card">
          <div class="stat-icon" :style="{ background: stat.color }">
            <el-icon :size="30"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-title">{{ stat.title }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快速入口 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>🚀 快速入口</span>
            </div>
          </template>
          <el-space direction="vertical" style="width: 100%">
            <el-button type="primary" icon="Document" @click="$router.push('/memory')" style="width: 100%">
              记忆管理
            </el-button>
            <el-button type="success" icon="Aim" @click="$router.push('/intent')" style="width: 100%">
              意图管理
            </el-button>
            <el-button type="warning" icon="Connection" @click="$router.push('/trace')" style="width: 100%">
              代码追溯
            </el-button>
            <el-button type="danger" icon="TrendCharts" @click="$router.push('/impact')" style="width: 100%">
              影响分析
            </el-button>
          </el-space>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>📊 系统状态</span>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="后端状态">
              <el-tag type="success">运行中</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="API 端点">100+</el-descriptions-item>
            <el-descriptions-item label="测试用例">733</el-descriptions-item>
            <el-descriptions-item label="覆盖率">~65%</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- Subagent 任务统计 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>🤖 Subagent 任务</span>
              <el-button type="primary" size="small" icon="Plus" @click="showCreateTask = true">新建任务</el-button>
            </div>
          </template>
          <el-table :data="subagentStore.tasks" stripe v-loading="subagentStore.loading">
            <el-table-column prop="id" label="任务 ID" width="180" />
            <el-table-column prop="role" label="角色" width="120">
              <template #default="{ row }">
                <el-tag>{{ row.role }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="created_at" label="创建时间" width="180" />
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button size="small" icon="View">查看</el-button>
                <el-button v-if="row.status === 'running'" size="small" type="danger" icon="CircleClose" @click="handleCancelTask(row.id)">取消</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useSubagentStore } from '@/stores/subagent'

const subagentStore = useSubagentStore()
const showCreateTask = ref(false)

const stats = computed(() => [
  { title: '记忆条目', value: subagentStore.tasks.length, icon: 'Document', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { title: '意图任务', value: '56', icon: 'Aim', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { title: '代码单元', value: '2,890', icon: 'Connection', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { title: 'Subagent', value: subagentStore.stats.running, icon: 'User', color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
])

const getStatusType = (status) => {
  const types = {
    running: 'warning',
    completed: 'success',
    failed: 'danger',
    pending: 'info'
  }
  return types[status] || 'info'
}

const handleCancelTask = async (taskId) => {
  try {
    await subagentStore.cancelTask(taskId)
    await subagentStore.fetchTasks()
  } catch (error) {
    console.error('取消任务失败:', error)
  }
}

onMounted(() => {
  subagentStore.fetchTasks()
})
</script>

<style scoped>
.home {
  padding: 10px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 15px;
  cursor: pointer;
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
</style>
