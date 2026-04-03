<template>
  <div class="config-panel">
    <el-card>
      <template #header>
        <h2>大模型配置</h2>
      </template>
      
      <el-form :model="form" label-width="120px">
        <el-divider content-position="left">Qwen (通义千问)</el-divider>
        <el-form-item label="启用状态">
          <el-switch v-model="form.qwen.enabled" />
        </el-form-item>
        <el-form-item label="API Key">
          <el-input 
            v-model="form.qwen.apiKey" 
            type="password" 
            show-password
            placeholder="请输入 Qwen API Key"
          />
        </el-form-item>
        <el-form-item label="模型">
          <el-select v-model="form.qwen.model">
            <el-option label="qwen3.5-plus" value="qwen3.5-plus" />
            <el-option label="qwen3-max" value="qwen3-max-2026-01-23" />
            <el-option label="qwen3-coder-plus" value="qwen3-coder-plus" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveConfig('qwen')">保存</el-button>
          <el-button @click="testConnection('qwen')">测试连接</el-button>
        </el-form-item>
        
        <el-divider content-position="left">Kimi (月之暗面)</el-divider>
        <el-form-item label="启用状态">
          <el-switch v-model="form.kimi.enabled" />
        </el-form-item>
        <el-form-item label="API Key">
          <el-input 
            v-model="form.kimi.apiKey" 
            type="password" 
            show-password
            placeholder="请输入 Kimi API Key"
          />
        </el-form-item>
        <el-form-item label="模型">
          <el-select v-model="form.kimi.model">
            <el-option label="kimi-k2.5" value="kimi-k2.5" />
            <el-option label="kimi-k2-thinking" value="kimi-k2-thinking" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveConfig('kimi')">保存</el-button>
          <el-button @click="testConnection('kimi')">测试连接</el-button>
        </el-form-item>
        
        <el-divider content-position="left">GLM (智谱 AI)</el-divider>
        <el-form-item label="启用状态">
          <el-switch v-model="form.glm.enabled" />
        </el-form-item>
        <el-form-item label="API Key">
          <el-input 
            v-model="form.glm.apiKey" 
            type="password" 
            show-password
            placeholder="请输入 GLM API Key"
          />
        </el-form-item>
        <el-form-item label="模型">
          <el-select v-model="form.glm.model">
            <el-option label="glm-4.7" value="glm-4.7" />
            <el-option label="glm-4.7-flash" value="glm-4.7-flash" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveConfig('glm')">保存</el-button>
          <el-button @click="testConnection('glm')">测试连接</el-button>
        </el-form-item>
      </el-form>
      
      <el-divider />
      
      <el-form-item label="当前使用">
        <el-radio-group v-model="modelStore.activeModel">
          <el-radio label="qwen">Qwen</el-radio>
          <el-radio label="kimi">Kimi</el-radio>
          <el-radio label="glm">GLM</el-radio>
        </el-radio-group>
        <el-button type="primary" @click="switchModel">切换模型</el-button>
      </el-form-item>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useModelStore } from '@/stores/model'

const modelStore = useModelStore()

const form = reactive({
  qwen: { ...modelStore.models.qwen },
  kimi: { ...modelStore.models.kimi },
  glm: { ...modelStore.models.glm }
})

onMounted(() => {
  modelStore.loadFromLocalStorage()
  form.qwen = { ...modelStore.models.qwen }
  form.kimi = { ...modelStore.models.kimi }
  form.glm = { ...modelStore.models.glm }
})

const saveConfig = (provider) => {
  modelStore.updateApiKey(provider, form[provider].apiKey)
  modelStore.toggleModel(provider, form[provider].enabled)
  ElMessage.success(`${provider} 配置已保存`)
}

const testConnection = async (provider) => {
  const result = await modelStore.testConnection(provider)
  if (result.success) {
    ElMessage.success(result.message)
  } else {
    ElMessage.error(result.message)
  }
}

const switchModel = () => {
  modelStore.switchModel(modelStore.activeModel)
  ElMessage.success(`已切换到 ${modelStore.activeModelConfig.name}`)
}
</script>

<style scoped>
.config-panel {
  max-width: 800px;
  margin: 0 auto;
}

.el-divider {
  margin: 30px 0 20px;
}
</style>
