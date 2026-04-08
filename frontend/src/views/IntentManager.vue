<template>
  <div class="intent-manager">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>意图管理</span>
          <el-button type="primary" icon="Plus" @click="showCreateDialog = true">新建意图</el-button>
        </div>
      </template>
      
      <div class="toolbar">
        <el-select v-model="statusFilter" placeholder="状态筛选" clearable @change="fetchIntents">
          <el-option label="全部" value="" />
          <el-option label="待处理" value="pending" />
          <el-option label="进行中" value="processing" />
          <el-option label="已完成" value="completed" />
          <el-option label="已取消" value="cancelled" />
        </el-select>
        <el-button icon="Refresh" @click="fetchIntents">刷新</el-button>
      </div>
      
      <el-table :data="intentStore.intents" stripe v-loading="intentStore.loading">
        <el-table-column prop="id" label="ID" width="180" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button size="small" icon="Operation" @click="handleDecompose(row)">任务分解</el-button>
            <el-button size="small" icon="View">查看</el-button>
            <el-button size="small" type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useIntentStore } from '@/stores/intent'

const intentStore = useIntentStore()
const statusFilter = ref('')
const showCreateDialog = ref(false)

const fetchIntents = async () => {
  intentStore.pagination.status = statusFilter.value
  await intentStore.fetchIntents()
}

const getStatusType = (status) => {
  const types = {
    pending: 'info',
    processing: 'warning',
    completed: 'success',
    cancelled: 'danger'
  }
  return types[status] || 'info'
}

const handleDecompose = async (row) => {
  try {
    await ElMessageBox.confirm(`确定对意图 "${row.name}" 进行任务分解吗？`, '任务分解', {
      confirmButtonText: '分解',
      cancelButtonText: '取消',
      type: 'info'
    })
    await intentStore.decomposeTask(row.id)
    ElMessage.success('任务分解成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('任务分解失败:', error)
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除意图 "${row.name}" 吗？`, '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await intentStore.deleteIntent(row.id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  fetchIntents()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}
</style>
