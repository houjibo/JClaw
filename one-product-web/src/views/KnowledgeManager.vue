<template>
  <div class="knowledge-manager">
    <h1>知识管理</h1>
    
    <div class="search-bar">
      <input v-model="query" placeholder="搜索知识..." @keyup.enter="search" />
      <button @click="search">搜索</button>
    </div>

    <div class="knowledge-list">
      <div v-for="item in knowledge" :key="item.id" class="knowledge-card">
        <h3>{{ item.title }}</h3>
        <span class="category">{{ item.category }}</span>
        <p>{{ item.content.substring(0, 100) }}...</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'KnowledgeManager',
  data() {
    return {
      knowledge: [],
      query: ''
    }
  },
  mounted() {
    this.loadKnowledge()
  },
  methods: {
    async loadKnowledge() {
      const res = await fetch('/api/knowledge')
      const data = await res.json()
      this.knowledge = data.data || []
    },
    async search() {
      const res = await fetch(`/api/knowledge/search?query=${this.query}`)
      const data = await res.json()
      this.knowledge = data.data || []
    }
  }
}
</script>

<style scoped>
.knowledge-manager { padding: 20px; }
.search-bar { margin-bottom: 20px; }
.search-bar input { padding: 8px; width: 300px; }
.knowledge-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; }
.knowledge-card { border: 1px solid #ddd; padding: 15px; border-radius: 8px; }
.category { background: #2196F3; color: white; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
</style>
