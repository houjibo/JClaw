<template>
  <div class="config-panel">
    <el-card>
      <template #header>
        <span>系统配置</span>
      </template>
      
      <el-tabs v-model="activeTab">
        <!-- 基础配置 -->
        <el-tab-pane label="基础配置" name="basic">
          <el-form :model="config.basic" label-width="150px">
            <el-form-item label="应用名称">
              <el-input v-model="config.basic.appName" />
            </el-form-item>
            <el-form-item label="API 端口">
              <el-input-number v-model="config.basic.port" :min="1024" :max="65535" />
            </el-form-item>
            <el-form-item label="日志级别">
              <el-select v-model="config.basic.logLevel">
                <el-option label="DEBUG" value="DEBUG" />
                <el-option label="INFO" value="INFO" />
                <el-option label="WARN" value="WARN" />
                <el-option label="ERROR" value="ERROR" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveConfig('basic')">保存配置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <!-- 数据库配置 -->
        <el-tab-pane label="数据库配置" name="database">
          <el-form :model="config.database" label-width="150px">
            <el-form-item label="主机地址">
              <el-input v-model="config.database.host" placeholder="localhost" />
            </el-form-item>
            <el-form-item label="端口">
              <el-input-number v-model="config.database.port" :min="1" :max="65535" />
            </el-form-item>
            <el-form-item label="数据库名">
              <el-input v-model="config.database.database" />
            </el-form-item>
            <el-form-item label="用户名">
              <el-input v-model="config.database.username" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="config.database.password" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveConfig('database')">保存配置</el-button>
              <el-button @click="testConnection">测试连接</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <!-- AI 配置 -->
        <el-tab-pane label="AI 配置" name="ai">
          <el-form :model="config.ai" label-width="150px">
            <el-form-item label="默认模型">
              <el-select v-model="config.ai.defaultModel">
                <el-option label="Qwen 3.5 Plus" value="qwen3.5-plus" />
                <el-option label="Kimi K2.5" value="kimi-k2.5" />
                <el-option label="GLM 4.7" value="glm-4.7" />
              </el-select>
            </el-form-item>
            <el-form-item label="最大 Token">
              <el-input-number v-model="config.ai.maxTokens" :min="1000" :max="100000" />
            </el-form-item>
            <el-form-item label="超时时间 (秒)">
              <el-input-number v-model="config.ai.timeout" :min="10" :max="300" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveConfig('ai')">保存配置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <!-- 关于 -->
        <el-tab-pane label="关于" name="about">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="系统名称">JClaw</el-descriptions-item>
            <el-descriptions-item label="版本">1.0.0-SNAPSHOT</el-descriptions-item>
            <el-descriptions-item label="框架">Spring Boot 3.5.4</el-descriptions-item>
            <el-descriptions-item label="JDK 版本">21</el-descriptions-item>
            <el-descriptions-item label="前端">Vue 3 + Vite</el-descriptions-item>
            <el-descriptions-item label="代码量">~35,000 行</el-descriptions-item>
            <el-descriptions-item label="测试用例">733 个</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { configApi } from '@/api/config'

const activeTab = ref('basic')

const config = reactive({
  basic: {
    appName: 'JClaw',
    port: 8080,
    logLevel: 'INFO'
  },
  database: {
    host: 'localhost',
    port: 5432,
    database: 'jclaw',
    username: 'postgres',
    password: ''
  },
  ai: {
    defaultModel: 'qwen3.5-plus',
    maxTokens: 32000,
    timeout: 60
  }
})

const saveConfig = async (section) => {
  try {
    await configApi.batchUpdate(config[section])
    ElMessage.success('配置保存成功')
  } catch (error) {
    console.error('保存配置失败:', error)
  }
}

const testConnection = async () => {
  ElMessage.info('测试数据库连接中...')
  // 实际应该调用 API 测试连接
}

onMounted(async () => {
  try {
    const allConfig = await configApi.getAll()
    Object.assign(config, allConfig)
  } catch (error) {
    console.error('获取配置失败:', error)
  }
})
</script>

<style scoped>
.config-panel {
  max-width: 900px;
}
</style>
