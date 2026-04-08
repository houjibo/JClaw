<template>
  <div class="call-chain-3d-page">
    <el-row :gutter="20">
      <!-- 控制面板 -->
      <el-col :span="6">
        <el-card>
          <template #header>
            <span>控制面板</span>
          </template>
          
          <el-form label-width="100px">
            <el-form-item label="文件路径">
              <el-input 
                v-model="filePath" 
                placeholder="com/jclaw/service/UserService.java"
                @keyup.enter="loadFile"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadFile" :loading="loading">加载</el-button>
            </el-form-item>
          </el-form>
          
          <el-divider />
          
          <div class="stats">
            <h4>统计信息</h4>
            <el-descriptions :column="1" size="small">
              <el-descriptions-item label="节点数">{{ nodes.length }}</el-descriptions-item>
              <el-descriptions-item label="连接数">{{ links.length }}</el-descriptions-item>
            </el-descriptions>
          </div>
          
          <el-divider />
          
          <div class="legend">
            <h4>图例</h4>
            <div class="legend-item">
              <span class="legend-color" style="background: #667eea"></span>
              <span>类 (Class)</span>
            </div>
            <div class="legend-item">
              <span class="legend-color" style="background: #f093fb"></span>
              <span>方法 (Method)</span>
            </div>
            <div class="legend-item">
              <span class="legend-color" style="background: #4facfe"></span>
              <span>接口 (Interface)</span>
            </div>
            <div class="legend-item">
              <span class="legend-color" style="background: #43e97b"></span>
              <span>其他</span>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 3D 可视化区域 -->
      <el-col :span="18">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>代码调用链 3D 可视化</span>
              <el-button icon="Refresh" @click="refreshView">刷新视图</el-button>
            </div>
          </template>
          
          <CallChain3D :nodes="nodes" :links="links" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import CallChain3D from '@/components/CallChain3D.vue'
import { traceApi } from '@/api/trace'

const filePath = ref('')
const loading = ref(false)
const nodes = ref([])
const links = ref([])

const loadFile = async () => {
  if (!filePath.value) return
  
  loading.value = true
  try {
    // 获取代码单元
    const codeUnits = await traceApi.getCodeUnits({ file_path: filePath.value })
    
    // 获取调用关系
    const relationships = await traceApi.getCallRelationships({})
    
    // 转换为 3D 图数据
    nodes.value = codeUnits.map(unit => ({
      id: unit.id,
      label: unit.unit_name,
      type: unit.unit_type,
      x: Math.random() * 200 - 100,
      y: Math.random() * 200 - 100,
      z: Math.random() * 200 - 100
    }))
    
    links.value = relationships.map(rel => ({
      source: rel.caller_id,
      target: rel.callee_id,
      type: rel.call_type
    }))
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

const refreshView = () => {
  // 重新加载
  loadFile()
}
</script>

<style scoped>
.call-chain-3d-page {
  padding: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats h4,
.legend h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 14px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 8px 0;
  font-size: 13px;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 50%;
}
</style>
