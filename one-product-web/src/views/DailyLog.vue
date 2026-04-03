<template>
  <div class="daily-log">
    <h1>每日日志 - {{ currentDate }}</h1>
    
    <div class="log-content">
      <h2>今日完成</h2>
      <ul>
        <li v-for="task in log.completed" :key="task">{{ task }}</li>
      </ul>

      <h2>亮点时刻</h2>
      <p>{{ log.highlights }}</p>

      <h2>明日计划</h2>
      <ul>
        <li v-for="plan in log.plans" :key="plan">{{ plan }}</li>
      </ul>
    </div>

    <button @click="saveLog">保存日志</button>
  </div>
</template>

<script>
export default {
  name: 'DailyLog',
  data() {
    return {
      currentDate: new Date().toISOString().split('T')[0],
      log: {
        completed: [],
        highlights: '',
        plans: []
      }
    }
  },
  mounted() {
    this.loadLog()
  },
  methods: {
    async loadLog() {
      const res = await fetch(`/api/daily-logs/${this.currentDate}`)
      if (res.ok) {
        const data = await res.json()
        this.log = data.data || this.log
      }
    },
    async saveLog() {
      await fetch('/api/daily-logs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          date: this.currentDate,
          content: this.log
        })
      })
      alert('日志已保存')
    }
  }
}
</script>

<style scoped>
.daily-log { padding: 20px; max-width: 800px; margin: 0 auto; }
.log-content { background: #f9f9f9; padding: 20px; border-radius: 8px; margin: 20px 0; }
ul { line-height: 1.8; }
button { background: #4CAF50; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; }
</style>
