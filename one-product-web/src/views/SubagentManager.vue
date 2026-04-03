<template>
  <div class="subagent-manager">
    <h1>Subagent 管理</h1>

    <!-- 创建 Subagent -->
    <div class="create-section">
      <h2>创建 Subagent</h2>
      <select v-model="newSubagent.role">
        <option value="pm-qa">PM-QA (军政官)</option>
        <option value="architect">Architect (总架构师)</option>
        <option value="fullstack">FullStack (全栈工程师)</option>
        <option value="devops">DevOps (运维官)</option>
        <option value="analyst">Analyst (情报官)</option>
      </select>
      <textarea v-model="newSubagent.task" placeholder="任务描述" rows="3"></textarea>
      <button @click="createSubagent">创建</button>
    </div>

    <!-- Subagent 列表 -->
    <div class="subagent-list">
      <h2>Subagent 列表</h2>
      <div v-for="sub in subagents" :key="sub.id" class="sub-card">
        <div class="sub-header">
          <h3>{{ sub.role }}</h3>
          <span class="status" :class="sub.status">{{ statusText(sub.status) }}</span>
        </div>
        <p class="task">{{ sub.task }}</p>
        <div v-if="sub.output" class="output">
          <strong>输出:</strong>
          <pre>{{ sub.output }}</pre>
        </div>
        <div class="sub-meta">
          <span>创建：{{ formatTime(sub.createdAt) }}</span>
          <span v-if="sub.completedAt">完成：{{ formatTime(sub.completedAt) }}</span>
        </div>
      </div>
    </div>

    <!-- 联军创建 -->
    <div class="army-section">
      <h2>创建联军团队</h2>
      <textarea v-model="mission" placeholder="输入任务目标，自动分解给 5 大角色" rows="3"></textarea>
      <button @click="createArmy">创建联军</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SubagentManager',
  data() {
    return {
      subagents: [],
      newSubagent: {
        role: 'fullstack',
        task: ''
      },
      mission: ''
    }
  },
  mounted() {
    this.loadSubagents()
  },
  methods: {
    async loadSubagents() {
      const res = await fetch('/api/subagents')
      const data = await res.json()
      this.subagents = data.data || []
    },
    async createSubagent() {
      await fetch('/api/subagents', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          parentAgentId: 'main',
          role: this.newSubagent.role,
          task: this.newSubagent.task
        })
      })
      this.newSubagent.task = ''
      this.loadSubagents()
    },
    async createArmy() {
      await fetch('/api/subagents/army/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ mission: this.mission })
      })
      this.mission = ''
      this.loadSubagents()
    },
    statusText(status) {
      const map = {
        pending: '等待中',
        running: '运行中',
        completed: '已完成',
        failed: '失败'
      }
      return map[status] || status
    },
    formatTime(time) {
      return new Date(time).toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.subagent-manager { padding: 20px; max-width: 1200px; margin: 0 auto; }
.create-section, .army-section { margin-bottom: 30px; padding: 20px; background: #f5f5f5; border-radius: 8px; }
.create-section select, .create-section textarea, .army-section textarea { width: 100%; margin: 10px 0; padding: 8px; }
.create-section button, .army-section button { padding: 10px 30px; background: #4CAF50; color: white; border: none; border-radius: 4px; }
.sub-card { border: 1px solid #ddd; padding: 15px; margin: 10px 0; border-radius: 8px; }
.sub-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.status { padding: 4px 12px; border-radius: 4px; font-size: 12px; }
.status.pending { background: #ff9800; color: white; }
.status.running { background: #2196F3; color: white; }
.status.completed { background: #4CAF50; color: white; }
.status.failed { background: #f44336; color: white; }
.task { color: #666; margin: 10px 0; }
.output { background: #f9f9f9; padding: 10px; border-radius: 4px; margin: 10px 0; }
.output pre { white-space: pre-wrap; font-size: 12px; }
.sub-meta { display: flex; gap: 20px; font-size: 12px; color: #999; }
</style>
