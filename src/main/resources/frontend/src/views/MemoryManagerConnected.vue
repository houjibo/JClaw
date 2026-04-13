<template>
  <div class="memory-page">
    <h1>记忆管理</h1>
    
    <!-- 保存记忆 -->
    <div class="save-section">
      <h2>保存记忆</h2>
      <textarea 
        v-model="memoryContent" 
        placeholder="输入要保存的记忆..."
        rows="4"
      ></textarea>
      <button @click="saveMemory" :disabled="saving">
        {{ saving ? '保存中...' : '保存' }}
      </button>
    </div>
    
    <!-- 搜索记忆 -->
    <div class="search-section">
      <h2>搜索记忆</h2>
      <input 
        v-model="searchQuery" 
        placeholder="搜索关键词..."
        @keyup.enter="searchMemory"
      />
      <button @click="searchMemory" :disabled="searching">
        {{ searching ? '搜索中...' : '搜索' }}
      </button>
    </div>
    
    <!-- 搜索结果 -->
    <div v-if="searchResult" class="result-section">
      <h3>搜索结果</h3>
      <pre>{{ searchResult }}</pre>
    </div>
    
    <!-- 今日日志 -->
    <div class="today-log-section">
      <h2>今日日志</h2>
      <button @click="loadTodayLog">加载今日日志</button>
      <div v-if="todayLog" class="log-content">
        <pre>{{ todayLog }}</pre>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MemoryManager',
  data() {
    return {
      memoryContent: '',
      searchQuery: '',
      searchResult: null,
      todayLog: null,
      saving: false,
      searching: false
    }
  },
  methods: {
    async saveMemory() {
      if (!this.memoryContent.trim()) {
        alert('请输入记忆内容')
        return
      }
      
      this.saving = true
      try {
        const res = await fetch('/api/memory', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ content: this.memoryContent })
        })
        if (res.ok) {
          alert('记忆保存成功')
          this.memoryContent = ''
        } else {
          alert('保存失败')
        }
      } catch (e) {
        alert('保存错误：' + e.message)
      } finally {
        this.saving = false
      }
    },
    async searchMemory() {
      if (!this.searchQuery.trim()) {
        alert('请输入搜索关键词')
        return
      }
      
      this.searching = true
      try {
        const res = await fetch(`/api/memory/search?query=${encodeURIComponent(this.searchQuery)}`)
        const data = await res.json()
        this.searchResult = data.result
      } catch (e) {
        this.searchResult = '搜索错误：' + e.message
      } finally {
        this.searching = false
      }
    },
    async loadTodayLog() {
      try {
        const res = await fetch('/api/memory/today')
        const data = await res.json()
        this.todayLog = data.log || '暂无今日日志'
      } catch (e) {
        this.todayLog = '加载失败：' + e.message
      }
    }
  }
}
</script>

<style scoped>
.memory-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.save-section, .search-section, .today-log-section {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}
textarea, input {
  width: 100%;
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 10px;
  box-sizing: border-box;
}
button {
  background: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}
button:disabled {
  background: #ccc;
  cursor: not-allowed;
}
button:hover:not(:disabled) {
  background: #0056b3;
}
.result-section, .log-content {
  background: white;
  padding: 15px;
  border-radius: 4px;
  margin-top: 10px;
}
pre {
  white-space: pre-wrap;
  font-family: monospace;
}
</style>
