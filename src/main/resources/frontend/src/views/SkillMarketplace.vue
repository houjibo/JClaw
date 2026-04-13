<template>
  <div class="skill-marketplace-container">
    <el-card class="marketplace-card">
      <template #header>
        <div class="card-header">
          <span>🛒 技能市场</span>
          <el-input
            v-model="searchQuery"
            placeholder="搜索技能..."
            style="width: 200px"
            clearable
            @input="filterSkills"
          />
        </div>
      </template>

      <!-- 技能列表 -->
      <el-table :data="filteredSkills" style="width: 100%" stripe>
        <el-table-column prop="name" label="技能名称" width="200" />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            <el-tag :type="getCategoryType(row.category)">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="!row.installed"
              type="primary"
              size="small"
              @click="installSkill(row)"
            >
              安装
            </el-button>
            <el-button
              v-else
              type="success"
              size="small"
              disabled
            >
              已安装
            </el-button>
            <el-button
              type="info"
              size="small"
              @click="viewDetails(row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="totalSkills"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="dialogVisible" title="技能详情" width="60%">
      <el-descriptions :column="1" border v-if="selectedSkill">
        <el-descriptions-item label="名称">{{ selectedSkill.name }}</el-descriptions-item>
        <el-descriptions-item label="版本">{{ selectedSkill.version }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ selectedSkill.category }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ selectedSkill.author }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{ selectedSkill.description }}</el-descriptions-item>
        <el-descriptions-item label="依赖">
          {{ selectedSkill.dependencies?.join(', ') || '无' }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button
          v-if="selectedSkill && !selectedSkill.installed"
          type="primary"
          @click="installSkill(selectedSkill)"
        >
          安装
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const selectedSkill = ref(null)

// 示例技能数据
const allSkills = ref([
  {
    name: 'github-integration',
    version: '1.0.0',
    category: '集成',
    author: 'JClaw Team',
    description: 'GitHub 集成技能，支持 Issues、PR、Actions 等操作',
    dependencies: [],
    installed: false
  },
  {
    name: 'docker-deploy',
    version: '1.2.0',
    category: '部署',
    author: 'DevOps Team',
    description: 'Docker 容器化部署技能，支持一键部署到生产环境',
    dependencies: [],
    installed: true
  },
  {
    name: 'code-review',
    version: '2.0.1',
    category: '代码',
    author: 'QA Team',
    description: 'AI 代码审查技能，自动检测代码质量和潜在问题',
    dependencies: ['ai-integration'],
    installed: false
  },
  {
    name: 'performance-monitor',
    version: '1.1.0',
    category: '监控',
    author: 'SRE Team',
    description: '性能监控技能，实时追踪系统性能指标',
    dependencies: [],
    installed: false
  },
  {
    name: 'database-migration',
    version: '1.0.5',
    category: '数据库',
    author: 'DBA Team',
    description: '数据库迁移技能，支持 MySQL、PostgreSQL 等',
    dependencies: [],
    installed: false
  }
])

const filteredSkills = computed(() => {
  if (!searchQuery.value) {
    return allSkills.value
  }
  return allSkills.value.filter(skill =>
    skill.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    skill.description.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

const totalSkills = computed(() => allSkills.value.length)

const getCategoryType = (category) => {
  const typeMap = {
    '集成': 'primary',
    '部署': 'success',
    '代码': 'warning',
    '监控': 'danger',
    '数据库': 'info'
  }
  return typeMap[category] || ''
}

const filterSkills = () => {
  currentPage.value = 1
}

const handleSizeChange = () => {
  currentPage.value = 1
}

const handleCurrentChange = () => {
  // 加载对应页数据
}

const installSkill = async (skill) => {
  try {
    // 模拟安装
    await new Promise(resolve => setTimeout(resolve, 1000))
    skill.installed = true
    ElMessage.success(`技能 "${skill.name}" 安装成功`)
  } catch (error) {
    ElMessage.error(`安装失败：${error.message}`)
  }
}

const viewDetails = (skill) => {
  selectedSkill.value = skill
  dialogVisible.value = true
}

onMounted(() => {
  // 加载技能列表
})
</script>

<style scoped>
.skill-marketplace-container {
  padding: 20px;
}

.marketplace-card {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-pagination) {
  display: flex;
}
</style>
