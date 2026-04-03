import { createRouter, createWebHistory } from 'vue-router'
import MemoryManager from './views/MemoryManager.vue'
import IntentManager from './views/IntentManager.vue'
import TraceManager from './views/TraceManager.vue'
import ImpactAnalysis from './views/ImpactAnalysis.vue'
import SubagentManager from './views/SubagentManager.vue'
import ChannelManager from './views/ChannelManager.vue'
import TestRecommender from './views/TestRecommender.vue'
import ConfigPanel from './views/ConfigPanel.vue'
import Home from './views/Home.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/memory', name: 'Memory', component: MemoryManager },
  { path: '/intent', name: 'Intent', component: IntentManager },
  { path: '/trace', name: 'Trace', component: TraceManager },
  { path: '/impact', name: 'Impact', component: ImpactAnalysis },
  { path: '/agent', name: 'Agent', component: SubagentManager },
  { path: '/channel', name: 'Channel', component: ChannelManager },
  { path: '/test', name: 'Test', component: TestRecommender },
  { path: '/config', name: 'Config', component: ConfigPanel }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
