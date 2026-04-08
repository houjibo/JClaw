<template>
  <div class="test-recommender">
    <el-row :gutter="20">
      <!-- 测试覆盖率统计 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>测试覆盖率</span>
          </template>
          <div class="coverage-stats">
            <el-progress type="dashboard" :percentage="coveragePercentage" :color="coverageColor" />
            <div class="coverage-details">
              <p>总测试数：<strong>{{ testStats.total }}</strong></p>
              <p>通过数：<strong style="color: #67c23a">{{ testStats.passed }}</strong></p>
              <p>失败数：<strong style="color: #f56c6c">{{ testStats.failed }}</strong></p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 推荐测试列表 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>推荐测试</span>
              <el-button icon="Refresh" @click="fetchRecommendations">刷新</el-button>
            </div>
          </template>
          
          <el-table :data="recommendations" stripe v-loading="loading">
            <el-table-column prop="test_name" label="测试名称" />
            <el-table-column prop="related_file" label="关联文件" show-overflow-tooltip />
            <el-table-column prop="priority" label="优先级" width="80">
              <template #default="{ row }">
                <el-tag :type="getPriorityType(row.priority)" size="small">{{ row.priority }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="reason" label="推荐原因" show-overflow-tooltip />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button size="small" type="primary" icon="VideoPlay" @click="handleRunTest(row)">运行</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { testApi } from '@/api'

const recommendations = ref([])
const loading = ref(false)
const testStats = ref({
  total: 733,
  passed: 730,
  failed: 3
})

const coveragePercentage = computed(() => {
  return Math.round((testStats.value.passed / testStats.value.total) * 100)
})

const coverageColor = computed(() => {
  if (coveragePercentage.value >= 80) return '#67c23a'
  if (coveragePercentage.value >= 60) return '#e6a23c'
  return '#f56c6c'
})

const getPriorityType = (priority) => {
  const types = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'success' }
  return types[priority] || 'info'
}

const fetchRecommendations = async () => {
  loading.value = true
  try {
    recommendations.value = await testApi.getRecommendations()
  } catch (error) {
    console.error('获取推荐测试失败:', error)
  } finally {
    loading.value = false
  }
}

const handleRunTest = async (row) => {
  try {
    await testApi.runTest(row.id)
    ElMessage.success(`开始运行测试：${row.test_name}`)
  } catch (error) {
    console.error('运行测试失败:', error)
  }
}

onMounted(() => {
  fetchRecommendations()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.coverage-stats {
  text-align: center;
  padding: 20px 0;
}

.coverage-details {
  margin-top: 20px;
  text-align: left;
}

.coverage-details p {
  margin: 8px 0;
  color: #606266;
}
</style>
