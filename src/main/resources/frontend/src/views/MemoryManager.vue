<template>
  <div class="memory-manager">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>记忆管理</span>
          <el-input
            v-model="searchQuery"
            placeholder="搜索记忆..."
            prefix-icon="Search"
            style="width: 300px"
            @keyup.enter="handleSearch"
          />
        </div>
      </template>
      
      <div class="toolbar" style="margin-bottom: 15px">
        <el-button type="primary" icon="Plus" @click="showCreateDialog = true">新建记忆</el-button>
        <el-button icon="Refresh" @click="fetchMemories">刷新</el-button>
      </div>
      
      <el-table :data="memoryStore.memories" stripe v-loading="memoryStore.loading">
        <el-table-column prop="id" label="ID" width="180" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="创建时间" width="180" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" icon="View" @click="handleView(row)">查看</el-button>
            <el-button size="small" type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination" style="margin-top: 20px; text-align: right">
        <el-pagination
          v-model:current-page="memoryStore.pagination.page"
          v-model:page-size="memoryStore.pagination.size"
          :total="memoryStore.total"
          @current-change="fetchMemories"
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50, 100]"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useMemoryStore } from '@/stores/memory'

const memoryStore = useMemoryStore()
const searchQuery = ref('')
const showCreateDialog = ref(false)

const fetchMemories = async () => {
  await memoryStore.fetchMemories()
}

const handleSearch = () => {
  memoryStore.searchMemories(searchQuery.value)
}

const handleView = (row) => {
  ElMessage.info(`查看记忆：${row.title}`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除记忆 "${row.title}" 吗？`, '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await memoryStore.deleteMemory(row.id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  fetchMemories()
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
}
</style>
