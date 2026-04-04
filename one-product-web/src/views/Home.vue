<template>
  <div class="home">
    <el-card>
      <template #header>
        <h2>欢迎使用 JClaw</h2>
      </template>
      
      <div class="stats">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-statistic title="工具数量" :value="47" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="API 端点" :value="100" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="代码行数" :value="31157" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="前端页面" :value="10" />
          </el-col>
        </el-row>
      </div>
      
      <div class="quick-links">
        <h3>快速入口</h3>
        <el-button type="primary" @click="navigateTo('intent')">意图驱动</el-button>
        <el-button type="success" @click="navigateTo('trace')">代码追溯</el-button>
        <el-button type="warning" @click="navigateTo('impact')">影响分析</el-button>
        <el-button type="info" @click="navigateTo('agent')">Agent 管理</el-button>
        <el-button type="danger" @click="navigateTo('config')">系统配置</el-button>
      </div>
      
      <div class="model-status">
        <h3>当前模型</h3>
        <el-tag :type="modelStore.activeModel === 'qwen' ? 'primary' : 'info'">
          {{ modelStore.activeModelConfig.name }}
        </el-tag>
        <el-button size="small" @click="navigateTo('config')">切换模型</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { inject } from 'vue'

const setActiveMenu = inject('setActiveMenu', null)

const navigateTo = (menu) => {
  console.log('导航到:', menu)
  if (setActiveMenu) {
    setActiveMenu(menu)
  } else {
    console.warn('setActiveMenu 未提供')
  }
}
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
}

.stats {
  margin: 30px 0;
}

.quick-links {
  margin: 30px 0;
}

.quick-links h3 {
  margin-bottom: 15px;
}

.quick-links .el-button {
  margin-right: 10px;
  margin-bottom: 10px;
}

.model-status {
  margin: 30px 0;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.model-status h3 {
  margin-bottom: 15px;
}
</style>
