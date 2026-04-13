<template>
  <div class="call-chain-3d-page">
    <el-card class="main-card">
      <template #header>
        <div class="page-header">
          <h2>🔗 3D 代码调用链可视化</h2>
          <el-button type="primary" @click="loadDemoData">加载示例数据</el-button>
        </div>
      </template>
      
      <!-- 3D 可视化组件 -->
      <CallChain3DEnhanced
        ref="visualizer"
        :nodes="nodes"
        :links="links"
      />
      
      <!-- 数据加载提示 -->
      <el-empty
        v-if="nodes.length === 0"
        description="点击"加载示例数据"查看 3D 可视化效果"
        :image-size="200"
      >
        <el-button type="primary" @click="loadDemoData">加载示例数据</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import CallChain3DEnhanced from '../components/CallChain3DEnhanced.vue'

const nodes = ref([])
const links = ref([])
const visualizer = ref(null)

const loadDemoData = () => {
  // 生成示例数据
  const demoNodes = []
  const demoLinks = []
  
  // 创建主类
  demoNodes.push({
    id: 'MainController',
    label: 'MainController',
    type: 'CLASS',
    file: 'com/jclaw/controller/MainController.java',
    complexity: 15,
    callCount: 25
  })
  
  // 创建服务类
  for (let i = 1; i <= 5; i++) {
    demoNodes.push({
      id: `Service${i}`,
      label: `Service${i}`,
      type: 'CLASS',
      file: `com/jclaw/service/Service${i}.java`,
      complexity: 10 + i,
      callCount: 15 + i * 2
    })
    
    // 连接到主控制器
    demoLinks.push({
      source: { id: 'MainController' },
      target: { id: `Service${i}` },
      type: 'CALLS'
    })
    
    // 创建方法
    for (let j = 1; j <= 3; j++) {
      demoNodes.push({
        id: `Service${i}_method${j}`,
        label: `method${j}()`,
        type: 'METHOD',
        file: `com/jclaw/service/Service${i}.java`,
        complexity: 5 + j,
        callCount: 5 + j
      })
      
      // 方法连接到类
      demoLinks.push({
        source: { id: `Service${i}_method${j}` },
        target: { id: `Service${i}` },
        type: 'CONTAINS'
      })
      
      // 方法间调用
      if (j > 1) {
        demoLinks.push({
          source: { id: `Service${i}_method${j}` },
          target: { id: `Service${i}_method${j-1}` },
          type: 'CALLS'
        })
      }
    }
  }
  
  // 创建接口
  for (let i = 1; i <= 2; i++) {
    demoNodes.push({
      id: `Interface${i}`,
      label: `Interface${i}`,
      type: 'INTERFACE',
      file: `com/jclaw/api/Interface${i}.java`,
      complexity: 3,
      callCount: 8
    })
    
    demoLinks.push({
      source: { id: `Service${i}` },
      target: { id: `Interface${i}` },
      type: 'IMPLEMENTS'
    })
  }
  
  nodes.value = demoNodes
  links.value = demoLinks
  
  console.log('加载示例数据:', demoNodes.length, '节点', demoLinks.length, '连接')
}
</script>

<style scoped>
.call-chain-3d-page {
  padding: 20px;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.main-card {
  max-width: 1600px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.95);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}
</style>
