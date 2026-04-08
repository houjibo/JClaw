<template>
  <div class="subagent-manager">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>Subagent 管理</span>
          <el-button type="primary" icon="Plus" @click="showCreateDialog = true">新建任务</el-button>
        </div>
      </template>
      
      <!-- 统计卡片 -->
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :span="6">
          <el-statistic title="总任务数" :value="subagentStore.stats.total" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="运行中" :value="subagentStore.stats.running">
            <template #suffix><span style="color: #e6a23c">●</span></template>
          </el-statistic>
        </el-col>
        <el-col :span="6">
          <el-statistic title="已完成" :value="subagentStore.stats.completed">
            <template #suffix><span style="color: #67c23a">●</span></template>
          </el-statistic>
        </el-col>
        <el-col :span="6">
          <el-statistic title="失败" :value="subagentStore.stats.failed">
            <template #suffix><span style="color: #f56c6c">●</span></template>
          </el-statistic>
        </el-col>
      </el-row>
      
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
        <el-table-column prop="message" label="任务描述" show-overflow-tooltip />
        <el-table-column prop="created_at" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" icon="View">查看</el-button>
            <el-button 
              v-if="row.status === 'running'" 
              size="small" 
              type="danger" 
              icon="CircleClose" 
              @click="handleCancelTask(row.id)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 创建任务对话框 -->
    <el-dialog v-model="showCreateDialog" title="创建 Subagent 任务" width="600px">
      <el-form :model="newTask" label-width="100px">
        <el-form-item label="角色" required>
          <el-select v-model="newTask.role" placeholder="选择角色" style="width: 100%">
            <el-option v-for="role in subagentStore.roles" :key="role.id" :label="role.name" :value="role.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务描述" required>
          <el-input v-model="newTask.message" type="textarea" :rows="4" placeholder="请输入任务描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreateTask">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useSubagentStore } from '@/stores/subagent'

const subagentStore = useSubagentStore()
const showCreateDialog = ref(false)

const newTask = reactive({
  role: '',
  message: ''
})

const getStatusType = (status) => {
  const types = {
    running: 'warning',
    completed: 'success',
    failed: 'danger',
    pending: 'info'
  }
  return types[status] || 'info'
}

const handleCreateTask = async () => {
  if (!newTask.role || !newTask.message) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  try {
    await subagentStore.createTask(newTask)
    ElMessage.success('任务创建成功')
    showCreateDialog.value = false
    await subagentStore.fetchTasks()
  } catch (error) {
    console.error('创建任务失败:', error)
  }
}

const handleCancelTask = async (taskId) => {
  try {
    await subagentStore.cancelTask(taskId)
    ElMessage.success('任务已取消')
    await subagentStore.fetchTasks()
  } catch (error) {
    console.error('取消任务失败:', error)
  }
}

onMounted(() => {
  subagentStore.fetchTasks()
  subagentStore.fetchRoles()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
