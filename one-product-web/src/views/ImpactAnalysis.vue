<template>
  <div class="impact-analysis">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>影响分析</h2>
          <el-button type="primary" @click="analyzeImpact">🔍 开始分析</el-button>
        </div>
      </template>

      <!-- 选择代码单元 -->
      <el-form :inline="true" :model="form">
        <el-form-item label="选择代码单元">
          <el-select v-model="form.codeUnitId" placeholder="请选择代码单元" style="width: 400px">
            <el-option
              v-for="unit in codeUnits"
              :key="unit.id"
              :label="`${unit.unitName} (${unit.filePath})`"
              :value="unit.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="analyzeImpact">分析</el-button>
        </el-form-item>
      </el-form>

      <!-- 影响分析结果 -->
      <div v-if="analysisResult" class="result">
        <h3>影响分析结果</h3>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-statistic title="影响范围" :value="analysisResult.affectedCount" suffix="个模块" />
          </el-col>
          <el-col :span="8">
            <el-statistic title="风险评分" :value="analysisResult.riskScore">
              <template #suffix>
                <el-tag :type="riskLevel(analysisResult.riskScore).type" size="small">
                  {{ riskLevel(analysisResult.riskScore).text }}
                </el-tag>
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="8">
            <el-statistic title="建议测试数" :value="analysisResult.suggestedTests" suffix="个" />
          </el-col>
        </el-row>

        <h4 style="margin-top: 30px">受影响的模块</h4>
        <el-table :data="analysisResult.affectedModules" style="width: 100%" border stripe>
          <el-table-column prop="moduleName" label="模块名称" />
          <el-table-column prop="changeType" label="变更类型" width="120">
            <template #default="scope">
              <el-tag :type="scope.row.changeType === 'MODIFY' ? 'warning' : 'info'" size="small">
                {{ scope.row.changeType }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="impactLevel" label="影响程度" width="100">
            <template #default="scope">
              <el-tag :type="getImpactType(scope.row.impactLevel)" size="small">
                {{ scope.row.impactLevel }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="说明" show-overflow-tooltip />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const form = reactive({
  codeUnitId: ''
})

const codeUnits = ref([
  { id: 1, unitName: 'UserService', filePath: '/src/main/java/com/jclaw/service/UserService.java' },
  { id: 2, unitName: 'MemoryManager', filePath: '/src/main/java/com/jclaw/memory/MemoryManager.java' },
  { id: 3, unitName: 'IntentRecognizer', filePath: '/src/main/java/com/jclaw/intent/IntentRecognizer.java' }
])

const analysisResult = ref(null)

const analyzeImpact = () => {
  if (!form.codeUnitId) {
    ElMessage.warning('请选择代码单元')
    return
  }
  
  // 模拟分析结果
  analysisResult.value = {
    affectedCount: 5,
    riskScore: 75,
    suggestedTests: 12,
    affectedModules: [
      { moduleName: 'Controller 层', changeType: 'MODIFY', impactLevel: '高', description: '需要更新接口定义' },
      { moduleName: 'Service 层', changeType: 'MODIFY', impactLevel: '高', description: '业务逻辑变更' },
      { moduleName: 'Repository 层', changeType: 'NONE', impactLevel: '低', description: '无影响' },
      { moduleName: '前端页面', changeType: 'MODIFY', impactLevel: '中', description: '需要更新表单字段' },
      { moduleName: '单元测试', changeType: 'ADD', impactLevel: '中', description: '需要补充测试用例' }
    ]
  }
  
  ElMessage.success('影响分析完成！')
}

const riskLevel = (score) => {
  if (score >= 80) return { text: '高风险', type: 'danger' }
  if (score >= 50) return { text: '中风险', type: 'warning' }
  return { text: '低风险', type: 'success' }
}

const getImpactType = (level) => {
  if (level === '高') return 'danger'
  if (level === '中') return 'warning'
  return 'info'
}
</script>

<style scoped>
.impact-analysis {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h2 {
  margin: 0;
  color: #303133;
  font-size: 20px;
}

h3 {
  margin: 20px 0 15px;
  color: #606266;
  font-size: 16px;
}

h4 {
  margin: 15px 0;
  color: #606266;
  font-size: 14px;
}

.result {
  margin-top: 20px;
}
</style>
