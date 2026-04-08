<template>
  <div class="channel-manager">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>通道管理</span>
          <el-button type="primary" icon="Plus" @click="showCreateDialog = true">新建通道</el-button>
        </div>
      </template>
      
      <el-table :data="channels" stripe>
        <el-table-column prop="id" label="ID" width="180" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" icon="ChatLineRounded">发送消息</el-button>
            <el-button size="small" icon="Setting">配置</el-button>
            <el-button size="small" type="danger" icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { channelApi } from '@/api/channel'

const channels = ref([])
const showCreateDialog = ref(false)

const fetchChannels = async () => {
  try {
    channels.value = await channelApi.getList()
  } catch (error) {
    console.error('获取通道列表失败:', error)
  }
}

onMounted(() => {
  fetchChannels()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
