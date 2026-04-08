<template>
  <div class="memory-manager">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>记忆管理</span>
          <el-button type="primary" icon="Plus">新建记忆</el-button>
        </div>
      </template>
      <el-table :data="memories" stripe>
        <el-table-column prop="id" label="ID" width="180" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" icon="View">查看</el-button>
            <el-button size="small" type="danger" icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const memories = ref([
  { id: 'mem_001', title: 'JClaw 架构设计', type: 'doc', created_at: '2026-04-07 10:00' },
  { id: 'mem_002', title: '服务层优化笔记', type: 'note', created_at: '2026-04-06 15:30' },
  { id: 'mem_003', title: 'API 设计文档', type: 'doc', created_at: '2026-04-05 09:15' }
])
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
