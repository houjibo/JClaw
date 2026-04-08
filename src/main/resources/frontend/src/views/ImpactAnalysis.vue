<template>
  <div class="impact-analysis">
    <el-row :gutter="20">
      <!-- 左侧：变更输入 -->
      <el-col :span="10">
        <el-card>
          <template #header>
            <span>变更影响分析</span>
          </template>
          
          <el-form :model="changeForm" label-width="100px">
            <el-form-item label="变更类型" required>
              <el-select v-model="changeForm.type" placeholder="选择变更类型" style="width: 100%">
                <el-option label="代码修改" value="code_change" />
                <el-option label="接口变更" value="api_change" />
                <el-option label="数据库变更" value="db_change" />
                <el-option label="配置变更" value="config_change" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="变更文件" required>
              <el-input
                v-model="changeForm.filePath"
                placeholder="例如：com/jclaw/service/UserService.java"
              />
            </el-form-item>
            
            <el-form-item label="变更描述">
              <el-input
                v-model="changeForm.description"
                type="textarea"
                :rows="4"
                placeholder="描述变更内容"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleAnalyze" :loading="impactStore.loading">
                开始分析
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <!-- 历史分析记录 -->
        <el-card style="margin-top: 20px">
          <template #header>
            <span>历史分析</span>
          </template>
          <el-table :data="impactStore.analyses" size="small" @row-click="handleViewResult">
            <el-table-column prop="id" label="ID" width="120" />
            <el-table-column prop="created_at" label="时间" width="160" />
            <el-table-column prop="risk_level" label="风险等级" width="80">
              <template #default="{ row }">
                <el-tag :type="getRiskLevelType(row.risk_level)" size="small">{{ row.risk_level }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <!-- 右侧：分析结果 -->
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>影响范围</span>
              <el-tag v-if="currentRiskScore" :type="getRiskScoreType(currentRiskScore)">
                风险评分：{{ currentRiskScore }}
              </el-tag>
            </div>
          </template>
          
          <div v-if="impactStore.currentAnalysis" class="analysis-result">
            <!-- 影响模块 -->
            <div class="result-section">
              <h4>📦 影响模块</h4>
              <el-tag
                v-for="module in impactStore.currentAnalysis.affected_modules"
                :key="module"
                style="margin: 5px"
              >
                {{ module }}
              </el-tag>
            </div>
            
            <!-- 影响接口 -->
            <div class="result-section">
              <h4>🔌 影响接口</h4>
              <el-table :data="impactStore.currentAnalysis.affected_apis" size="small">
                <el-table-column prop="method" label="方法" width="80" />
                <el-table-column prop="path" label="路径" />
                <el-table-column prop="risk" label="风险" width="80">
                  <template #default="{ row }">
                    <el-tag :type="getRiskType(row.risk)" size="small">{{ row.risk }}</el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            
            <!-- 建议测试用例 -->
            <div class="result-section">
              <h4>✅ 建议测试</h4>
              <el-table :data="impactStore.currentAnalysis.suggested_tests" size="small">
                <el-table-column prop="test_name" label="测试名称" />
                <el-table-column prop="priority" label="优先级" width="80">
                  <template #default="{ row }">
                    <el-tag :type="getPriorityType(row.priority)" size="small">{{ row.priority }}</el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
          
          <el-empty v-else description="暂无分析结果，请在左侧输入变更信息并点击分析" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useImpactStore } from '@/stores/impact'

const impactStore = useImpactStore()
const currentRiskScore = ref(null)

const changeForm = reactive({
  type: 'code_change',
  filePath: '',
  description: ''
})

const getRiskLevelType = (level) => {
  const types = { LOW: 'success', MEDIUM: 'warning', HIGH: 'danger', CRITICAL: 'danger' }
  return types[level] || 'info'
}

const getRiskScoreType = (score) => {
  if (score >= 80) return 'danger'
  if (score >= 60) return 'warning'
  return 'success'
}

const getRiskType = (risk) => {
  const types = { LOW: 'success', MEDIUM: 'warning', HIGH: 'danger' }
  return types[risk] || 'info'
}

const getPriorityType = (priority) => {
  const types = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'success' }
  return types[priority] || 'info'
}

const handleAnalyze = async () => {
  if (!changeForm.filePath) {
    ElMessage.warning('请输入变更文件')
    return
  }
  
  try {
    const result = await impactStore.analyzeChange(changeForm)
    currentRiskScore.value = result.risk_score || 0
    ElMessage.success('分析完成')
  } catch (error) {
    console.error('分析失败:', error)
  }
}

const resetForm = () => {
  changeForm.type = 'code_change'
  changeForm.filePath = ''
  changeForm.description = ''
  currentRiskScore.value = null
}

const handleViewResult = async (row) => {
  try {
    impactStore.currentAnalysis = await impactStore.getResult(row.id)
  } catch (error) {
    console.error('获取结果失败:', error)
  }
}

onMounted(() => {
  impactStore.fetchAnalyses()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-section {
  margin-bottom: 25px;
}

.result-section h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 15px;
}
</style>
