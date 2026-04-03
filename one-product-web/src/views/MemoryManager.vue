<template>
  <div class="memory-manager">
    <h1>记忆管理</h1>
    
    <!-- 搜索栏 -->
    <div class="search-bar">
      <input 
        v-model="searchQuery" 
        placeholder="搜索记忆..." 
        @keyup.enter="searchMemories"
      />
      <button @click="searchMemories">搜索</button>
      <button @click="showCreateDialog = true">新建记忆</button>
    </div>

    <!-- 记忆列表 -->
    <div class="memory-list">
      <div v-for="memory in memories" :key="memory.id" class="memory-card">
        <h3>{{ memory.title }}</h3>
        <span class="tag" v-for="tag in memory.tags" :key="tag">{{ tag }}</span>
        <p>{{ memory.content.description }}</p>
        <div class="actions">
          <button @click="viewMemory(memory.id)">查看</button>
          <button @click="editMemory(memory.id)">编辑</button>
          <button @click="deleteMemory(memory.id)">删除</button>
        </div>
      </div>
    </div>

    <!-- 创建对话框 -->
    <div v-if="showCreateDialog" class="dialog">
      <h2>创建记忆</h2>
      <input v-model="newMemory.title" placeholder="标题" />
      <textarea v-model="newMemory.content" placeholder="内容"></textarea>
      <input v-model="newMemory.tags" placeholder="标签（逗号分隔）" />
      <button @click="createMemory">创建</button>
      <button @click="showCreateDialog = false">取消</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MemoryManager',
  data() {
    return {
      memories: [],
      searchQuery: '',
      showCreateDialog: false,
      newMemory: {
        title: '',
        content: {},
        tags: []
      }
    }
  },
  mounted() {
    this.loadMemories()
  },
  methods: {
    async loadMemories() {
      const res = await fetch('/api/memories')
      const data = await res.json()
      this.memories = data.data || []
    },
    async searchMemories() {
      const res = await fetch(`/api/memories/search?query=${this.searchQuery}`)
      const data = await res.json()
      this.memories = data.data || []
    },
    async createMemory() {
      await fetch('/api/memories', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          type: 'long_term',
          title: this.newMemory.title,
          content: { description: this.newMemory.content },
          tags: this.newMemory.tags.split(',').map(t => t.trim())
        })
      })
      this.showCreateDialog = false
      this.loadMemories()
    },
    async deleteMemory(id) {
      if (confirm('确定删除此记忆？')) {
        await fetch(`/api/memories/${id}`, { method: 'DELETE' })
        this.loadMemories()
      }
    },
    viewMemory(id) {
      this.$router.push(`/memories/${id}`)
    },
    editMemory(id) {
      this.$router.push(`/memories/${id}/edit`)
    }
  }
}
</script>

<style scoped>
.memory-manager { padding: 20px; }
.search-bar { margin-bottom: 20px; }
.search-bar input { padding: 8px; margin-right: 10px; width: 300px; }
.memory-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; }
.memory-card { border: 1px solid #ddd; padding: 15px; border-radius: 8px; }
.tag { display: inline-block; background: #e0e0e0; padding: 2px 8px; margin: 2px; border-radius: 4px; font-size: 12px; }
.actions { margin-top: 10px; }
.actions button { margin-right: 5px; }
.dialog { position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); background: white; padding: 30px; box-shadow: 0 0 20px rgba(0,0,0,0.3); }
.dialog input, .dialog textarea { width: 100%; margin: 10px 0; padding: 8px; }
</style>
