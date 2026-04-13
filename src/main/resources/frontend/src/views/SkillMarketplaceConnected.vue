<template>
  <div class="skills-page">
    <h1>技能市场</h1>
    
    <!-- 搜索框 -->
    <div class="search-box">
      <input 
        v-model="searchQuery" 
        placeholder="搜索技能..."
        @input="filterSkills"
      />
    </div>
    
    <!-- 技能列表 -->
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="filteredSkills.length === 0" class="empty">
      暂无技能
    </div>
    <div v-else class="skills-grid">
      <div 
        v-for="skill in filteredSkills" 
        :key="skill.name"
        class="skill-card"
      >
        <h3>{{ skill.name }}</h3>
        <p>{{ skill.description }}</p>
        <button @click="testSkill(skill.name)">测试</button>
      </div>
    </div>
    
    <!-- 测试结果 -->
    <div v-if="testResult" class="test-result">
      <h3>测试结果</h3>
      <pre>{{ testResult }}</pre>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SkillMarketplace',
  data() {
    return {
      skills: [],
      filteredSkills: [],
      searchQuery: '',
      loading: true,
      testResult: null
    }
  },
  mounted() {
    this.loadSkills()
  },
  methods: {
    async loadSkills() {
      try {
        const res = await fetch('/api/skills')
        this.skills = await res.json()
        this.filteredSkills = this.skills
      } catch (e) {
        console.error('加载技能失败:', e)
      } finally {
        this.loading = false
      }
    },
    filterSkills() {
      if (!this.searchQuery) {
        this.filteredSkills = this.skills
      } else {
        this.filteredSkills = this.skills.filter(s => 
          s.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
          s.description.toLowerCase().includes(this.searchQuery.toLowerCase())
        )
      }
    },
    async testSkill(skillName) {
      this.testResult = '测试中...'
      try {
        const params = skillName === 'bash' 
          ? { command: 'whoami' }
          : skillName === 'git'
          ? { command: 'status', repoPath: '.' }
          : {}
        
        const res = await fetch('/api/skills/execute', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            skill: skillName,
            params
          })
        })
        const data = await res.json()
        this.testResult = JSON.stringify(data, null, 2)
      } catch (e) {
        this.testResult = '错误：' + e.message
      }
    }
  }
}
</script>

<style scoped>
.skills-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.search-box input {
  width: 100%;
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 20px;
}
.skills-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}
.skill-card {
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
}
.skill-card h3 {
  margin-top: 0;
  color: #333;
}
.skill-card p {
  color: #666;
  margin: 10px 0;
}
.skill-card button {
  background: #007bff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}
.skill-card button:hover {
  background: #0056b3;
}
.test-result {
  margin-top: 20px;
  background: #f5f5f5;
  padding: 15px;
  border-radius: 4px;
}
.test-result pre {
  background: #1e1e1e;
  color: #d4d4d4;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}
.loading, .empty {
  text-align: center;
  color: #666;
  padding: 40px;
}
</style>
