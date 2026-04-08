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
  </div>
</template>

<script setup>
const stats = [
  { title: '记忆条目', value: '1,234', icon: 'Document', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { title: '意图任务', value: '56', icon: 'Aim', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { title: '代码单元', value: '2,890', icon: 'Connection', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { title: 'Subagent', value: '12', icon: 'User', color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
]
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
