<template>
  <div class="intent-manager">
    <h1>意图驱动</h1>

    <!-- 意图输入 -->
    <div class="intent-input">
      <h2>告诉我你想做什么</h2>
      <textarea 
        v-model="userInput" 
        placeholder="例如：创建一个用户管理系统，包括登录、注册、权限管理..."
        rows="4"
      ></textarea>
      <button @click="recognizeIntent" :disabled="loading">
        {{ loading ? '识别中...' : '开始' }}
      </button>
    </div>

    <!-- 意图识别结果 -->
    <div v-if="intent" class="intent-result">
      <h2>识别结果</h2>
      <div class="intent-card">
        <h3>{{ intent.name }}</h3>
        <p>{{ intent.description }}</p>
        <span class="priority">优先级：{{ intent.priority }}</span>
      </div>
    </div>

    <!-- 澄清问题 -->
    <div v-if="questions.length > 0" class="clarification">
      <h2>澄清问题</h2>
      <ul>
        <li v-for="q in questions" :key="q">{{ q }}</li>
      </ul>
    </div>

    <!-- 任务列表 -->
    <div v-if="tasks.length > 0" class="task-list">
      <h2>分解任务</h2>
      <div v-for="(task, index) in tasks" :key="index" class="task-card">
        <h3>{{ index + 1 }}. {{ task.title }}</h3>
        <p>{{ task.description }}</p>
        <div class="task-meta">
          <span class="agent">🤖 {{ task.assignedAgent }}</span>
          <span class="priority">🔥 优先级 {{ task.priority }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'IntentManager',
  data() {
    return {
      userInput: '',
      loading: false,
      intent: null,
      questions: [],
      tasks: []
    }
  },
  methods: {
    async recognizeIntent() {
      if (!this.userInput.trim()) return
      
      this.loading = true
      try {
        // 1. 识别意图
        const res = await fetch('/api/intents/recognize', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ input: this.userInput })
        })
        const data = await res.json()
        this.intent = data.data

        // 2. 获取澄清问题
        const clarifyRes = await fetch(`/api/intents/${this.intent.id}/clarify`, {
          method: 'POST'
        })
        const clarifyData = await clarifyRes.json()
        this.questions = clarifyData.data || []

        // 3. 分解任务（调用工具）
        const toolRes = await fetch('/api/tools/intent_recognition/execute', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ input: this.userInput })
        })
        const toolData = await toolRes.json()
        this.tasks = toolData.data?.tasks || []

      } catch (error) {
        console.error('识别失败:', error)
        alert('识别失败：' + error.message)
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.intent-manager { padding: 20px; max-width: 900px; margin: 0 auto; }
.intent-input { margin-bottom: 30px; }
.intent-input textarea { width: 100%; padding: 12px; font-size: 14px; border: 1px solid #ddd; border-radius: 8px; }
.intent-input button { margin-top: 10px; padding: 10px 30px; background: #4CAF50; color: white; border: none; border-radius: 6px; cursor: pointer; font-size: 16px; }
.intent-input button:disabled { background: #ccc; }
.intent-card, .task-card { border: 1px solid #ddd; padding: 20px; border-radius: 8px; margin: 15px 0; background: #f9f9f9; }
.task-meta { display: flex; gap: 15px; margin-top: 10px; }
.agent, .priority { background: #e0e0e0; padding: 4px 10px; border-radius: 4px; font-size: 12px; }
.clarification ul { line-height: 2; }
</style>
