<template>
  <div class="dashboard">
    <h1>JClaw Dashboard</h1>
    
    <!-- 系统状态 -->
    <div class="status-card">
      <h2>系统状态</h2>
      <div v-if="health" class="status-content">
        <span :class="health.status === 'UP' ? 'status-ok' : 'status-error'">
          {{ health.status === 'UP' ? '✅ 运行中' : '❌ 异常' }}
        </span>
        <p>运行时间：{{ formatUptime(health.uptime) }}</p>
      </div>
      <div v-else class="loading">加载中...</div>
    </div>
    
    <!-- 技能列表 -->
    <div class="skills-card">
      <h2>可用技能 ({{ skills.length }})</h2>
      <div v-if="skills.length > 0" class="skills-list">
        <div v-for="skill in skills" :key="skill.name" class="skill-item">
          <strong>{{ skill.name }}</strong>
          <span>{{ skill.description }}</span>
        </div>
      </div>
      <div v-else class="loading">加载中...</div>
    </div>
    
    <!-- 快速操作 -->
    <div class="actions-card">
      <h2>快速操作</h2>
      <div class="action-buttons">
        <button @click="testBash">测试 Bash</button>
        <button @click="testAI">测试 AI 对话</button>
        <button @click="testGit">测试 Git</button>
      </div>
    </div>
    
    <!-- 日志输出 -->
    <div class="log-card">
      <h2>测试结果</h2>
      <pre class="log-output">{{ logOutput }}</pre>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Dashboard',
  data() {
    return {
      health: null,
      skills: [],
      logOutput: ''
    }
  },
  mounted() {
    this.loadHealth()
    this.loadSkills()
  },
  methods: {
    async loadHealth() {
      try {
        const res = await fetch('/api/health')
        this.health = await res.json()
      } catch (e) {
        this.logOutput = '加载健康状态失败：' + e.message
      }
    },
    async loadSkills() {
      try {
        const res = await fetch('/api/skills')
        this.skills = await res.json()
      } catch (e) {
        this.logOutput = '加载技能列表失败：' + e.message
      }
    },
    async testBash() {
      this.logOutput = '执行：bash echo "Hello"\n'
      try {
        const res = await fetch('/api/skills/execute', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            skill: 'bash',
            params: { command: 'echo "Hello from JClaw"' }
          })
        })
        const data = await res.json()
        this.logOutput += '结果：' + JSON.stringify(data, null, 2)
      } catch (e) {
        this.logOutput += '错误：' + e.message
      }
    },
    async testAI() {
      this.logOutput = '执行：AI 对话\n'
      try {
        const res = await fetch('/api/ai/chat', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ prompt: '你好，请自我介绍' })
        })
        const data = await res.json()
        this.logOutput += '结果：' + data.response
      } catch (e) {
        this.logOutput += '错误：' + e.message
      }
    },
    async testGit() {
      this.logOutput = '执行：git status\n'
      try {
        const res = await fetch('/api/skills/execute', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            skill: 'git',
            params: { command: 'status', repoPath: '.' }
          })
        })
        const data = await res.json()
        this.logOutput += '结果：' + data.content
      } catch (e) {
        this.logOutput += '错误：' + e.message
      }
    },
    formatUptime(seconds) {
      const h = Math.floor(seconds / 3600)
      const m = Math.floor((seconds % 3600) / 60)
      const s = seconds % 60
      return `${h}小时 ${m}分 ${s}秒`
    }
  }
}
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.status-card, .skills-card, .actions-card, .log-card {
  background: #f5f5f5;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}
.status-ok { color: green; font-weight: bold; }
.status-error { color: red; font-weight: bold; }
.skills-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 10px;
}
.skill-item {
  background: white;
  padding: 10px;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
}
.skill-item strong { margin-bottom: 5px; }
.action-buttons {
  display: flex;
  gap: 10px;
}
.action-buttons button {
  padding: 10px 20px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.action-buttons button:hover {
  background: #0056b3;
}
.log-output {
  background: #1e1e1e;
  color: #d4d4d4;
  padding: 15px;
  border-radius: 4px;
  font-family: monospace;
  white-space: pre-wrap;
  max-height: 400px;
  overflow-y: auto;
}
.loading {
  color: #666;
  font-style: italic;
}
</style>
