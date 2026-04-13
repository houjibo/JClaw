<template>
  <div class="call-chain-3d-enhanced">
    <!-- 3D 渲染容器 -->
    <div class="canvas-container" ref="container"></div>
    
    <!-- 控制面板 -->
    <div class="control-panel">
      <el-card class="control-card" shadow="hover">
        <template #header>
          <span>🎮 3D 控制面板</span>
        </template>
        
        <!-- 视图控制 -->
        <div class="control-section">
          <h4>视图控制</h4>
          <el-button-group>
            <el-button size="small" @click="resetView">重置</el-button>
            <el-button size="small" @click="toggleRotation">
              {{ isRotating ? '暂停旋转' : '自动旋转' }}
            </el-button>
          </el-button-group>
        </div>

        <!-- 节点过滤 -->
        <div class="control-section">
          <h4>节点过滤</h4>
          <el-checkbox-group v-model="visibleTypes" @change="updateVisibility">
            <el-checkbox label="CLASS">类</el-checkbox>
            <el-checkbox label="METHOD">方法</el-checkbox>
            <el-checkbox label="INTERFACE">接口</el-checkbox>
          </el-checkbox-group>
        </div>

        <!-- 统计信息 -->
        <div class="control-section">
          <h4>统计信息</h4>
          <el-descriptions :column="1" size="small">
            <el-descriptions-item label="节点数">{{ stats.nodeCount }}</el-descriptions-item>
            <el-descriptions-item label="连接数">{{ stats.linkCount }}</el-descriptions-item>
            <el-descriptions-item label="FPS">{{ stats.fps }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 性能控制 -->
        <div class="control-section">
          <h4>性能控制</h4>
          <el-slider
            v-model="lodLevel"
            :min="1"
            :max="3"
            :step="1"
            :marks="lodMarks"
            @change="updateLOD"
            show-input
          />
        </div>
      </el-card>
    </div>

    <!-- 节点详情面板 -->
    <div class="detail-panel" v-if="selectedNode">
      <el-card class="detail-card" shadow="hover">
        <template #header>
          <div class="detail-header">
            <span>📋 节点详情</span>
            <el-button size="small" @click="selectedNode = null">关闭</el-button>
          </div>
        </template>
        
        <el-descriptions :column="1" border>
          <el-descriptions-item label="名称">{{ selectedNode.label }}</el-descriptions-item>
          <el-descriptions-item label="类型">
            <el-tag :type="getTypeTag(selectedNode.type)">
              {{ getTypeLabel(selectedNode.type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="文件">{{ selectedNode.file || 'N/A' }}</el-descriptions-item>
          <el-descriptions-item label="圈复杂度">{{ selectedNode.complexity || 1 }}</el-descriptions-item>
          <el-descriptions-item label="调用次数">{{ selectedNode.callCount || 0 }}</el-descriptions-item>
        </el-descriptions>

        <el-divider>操作</el-divider>
        <el-button type="primary" size="small" @click="focusOnNode">聚焦</el-button>
        <el-button type="info" size="small" @click="showCallChain">查看调用链</el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import * as THREE from 'three'
import { forceManyBody, forceLink, forceCenter, forceCollide } from 'd3-force'

const props = defineProps({
  nodes: { type: Array, default: () => [] },
  links: { type: Array, default: () => [] }
})

const container = ref(null)
const selectedNode = ref(null)
const isRotating = ref(true)
const visibleTypes = ref(['CLASS', 'METHOD', 'INTERFACE'])
const lodLevel = ref(2)

const stats = reactive({
  nodeCount: 0,
  linkCount: 0,
  fps: 60
})

const lodMarks = {
  1: '低',
  2: '中',
  3: '高'
}

let scene, camera, renderer, nodes_group, links_group
let animationId = null
let frameCount = 0
let lastTime = Date.now()

// 类型颜色映射
const typeColors = {
  CLASS: 0x667eea,
  METHOD: 0xf093fb,
  INTERFACE: 0x4facfe,
  DEFAULT: 0x43e97b
}

const getTypeTag = (type) => {
  const tagMap = {
    CLASS: 'primary',
    METHOD: 'success',
    INTERFACE: 'warning'
  }
  return tagMap[type] || 'info'
}

const getTypeLabel = (type) => {
  const labelMap = {
    CLASS: '类',
    METHOD: '方法',
    INTERFACE: '接口'
  }
  return labelMap[type] || type
}

// 初始化场景
const initScene = () => {
  if (!container.value) return
  
  const width = container.value.clientWidth
  const height = container.value.clientHeight
  
  scene = new THREE.Scene()
  scene.background = new THREE.Color(0x0f0f1a)
  
  camera = new THREE.PerspectiveCamera(75, width / height, 0.1, 1000)
  camera.position.set(0, 0, 500)
  
  renderer = new THREE.WebGLRenderer({ antialias: true })
  renderer.setSize(width, height)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  container.value.appendChild(renderer.domElement)
  
  // 光源
  const ambientLight = new THREE.AmbientLight(0xffffff, 0.6)
  scene.add(ambientLight)
  
  const pointLight = new THREE.PointLight(0xffffff, 0.8)
  pointLight.position.set(200, 200, 200)
  scene.add(pointLight)
  
  nodes_group = new THREE.Group()
  links_group = new THREE.Group()
  scene.add(nodes_group)
  scene.add(links_group)
  
  // 坐标轴
  const axesHelper = new THREE.AxesHelper(100)
  scene.add(axesHelper)
  
  // 网格
  const gridHelper = new THREE.GridHelper(500, 50, 0x444444, 0x222222)
  gridHelper.rotation.x = Math.PI / 2
  scene.add(gridHelper)
  
  animate()
}

// 创建节点 - 根据 LOD 级别调整细节
const createNodeMesh = (node, type) => {
  let geometry, segments
  
  switch (lodLevel.value) {
    case 1: // 低细节
      segments = 8
      break
    case 2: // 中细节
      segments = 16
      break
    case 3: // 高细节
      segments = 32
      break
  }
  
  geometry = new THREE.SphereGeometry(8, segments, segments)
  const color = typeColors[type] || typeColors.DEFAULT
  
  const material = new THREE.MeshPhongMaterial({
    color,
    emissive: color,
    emissiveIntensity: 0.3,
    transparent: true,
    opacity: 0.9
  })
  
  const mesh = new THREE.Mesh(geometry, material)
  mesh.position.set(node.x || 0, node.y || 0, node.z || 0)
  mesh.userData = node
  
  // 点击事件
  mesh.onClick = () => {
    selectedNode.value = node
  }
  
  // 标签（仅在中高 LOD 显示）
  if (lodLevel.value >= 2) {
    const canvas = document.createElement('canvas')
    const context = canvas.getContext('2d')
    canvas.width = 256
    canvas.height = 64
    context.fillStyle = 'rgba(255, 255, 255, 0.9)'
    context.font = 'Bold 20px Arial'
    context.fillText(node.label, 10, 40)
    
    const texture = new THREE.CanvasTexture(canvas)
    const labelMaterial = new THREE.SpriteMaterial({ map: texture })
    const label = new THREE.Sprite(labelMaterial)
    label.position.set(0, 15, 0)
    label.scale.set(100, 25, 1)
    mesh.add(label)
  }
  
  return mesh
}

// 创建连线
const createLinkMesh = (link) => {
  const material = new THREE.LineBasicMaterial({
    color: 0x666666,
    transparent: true,
    opacity: 0.4
  })
  
  const points = []
  points.push(new THREE.Vector3(link.source.x, link.source.y, link.source.z))
  points.push(new THREE.Vector3(link.target.x, link.target.y, link.target.z))
  
  const geometry = new THREE.BufferGeometry().setFromPoints(points)
  return new THREE.Line(geometry, material)
}

// 力导向布局
const runForceLayout = () => {
  const simulation = forceManyBody().strength(-300).distanceMax(400)
  const linkForce = forceLink(props.links).id(d => d.id).distance(100).strength(0.7)
  const centerForce = forceCenter(0, 0)
  const collideForce = forceCollide(20)
  
  props.nodes.forEach(node => {
    node.x = node.x || (Math.random() - 0.5) * 200
    node.y = node.y || (Math.random() - 0.5) * 200
    node.z = node.z || (Math.random() - 0.5) * 200
    node.vx = 0
    node.vy = 0
    node.vz = 0
  })
  
  for (let i = 0; i < 150; i++) {
    simulation(props.nodes)
    linkForce(props.links)
    centerForce(props.nodes)
    collideForce(props.nodes)
  }
}

// 渲染图形
const renderGraph = () => {
  while (nodes_group.children.length > 0) {
    nodes_group.remove(nodes_group.children[0])
  }
  while (links_group.children.length > 0) {
    links_group.remove(links_group.children[0])
  }
  
  runForceLayout()
  
  // 过滤可见节点
  const visibleNodes = props.nodes.filter(n => visibleTypes.value.includes(n.type))
  const visibleNodeIds = new Set(visibleNodes.map(n => n.id))
  const visibleLinks = props.links.filter(l => 
    visibleNodeIds.has(l.source.id) && visibleNodeIds.has(l.target.id)
  )
  
  visibleNodes.forEach(node => {
    const mesh = createNodeMesh(node, node.type)
    nodes_group.add(mesh)
  })
  
  visibleLinks.forEach(link => {
    const mesh = createLinkMesh(link)
    links_group.add(mesh)
  })
  
  // 更新统计
  stats.nodeCount = visibleNodes.length
  stats.linkCount = visibleLinks.length
}

// 动画循环
const animate = () => {
  animationId = requestAnimationFrame(animate)
  
  // FPS 计算
  frameCount++
  const currentTime = Date.now()
  if (currentTime - lastTime >= 1000) {
    stats.fps = frameCount
    frameCount = 0
    lastTime = currentTime
  }
  
  if (nodes_group && isRotating.value) {
    nodes_group.rotation.y += 0.001
  }
  
  renderer.render(scene, camera)
}

// 控制功能
const resetView = () => {
  camera.position.set(0, 0, 500)
  camera.lookAt(0, 0, 0)
  if (nodes_group) {
    nodes_group.rotation.set(0, 0, 0)
  }
}

const toggleRotation = () => {
  isRotating.value = !isRotating.value
}

const updateVisibility = () => {
  renderGraph()
}

const updateLOD = () => {
  renderGraph()
}

const focusOnNode = () => {
  if (!selectedNode.value) return
  // 实现相机聚焦逻辑
  console.log('聚焦到节点:', selectedNode.value.label)
}

const showCallChain = () => {
  if (!selectedNode.value) return
  // 实现调用链显示逻辑
  console.log('显示调用链:', selectedNode.value.label)
}

// 鼠标交互
const setupInteraction = () => {
  if (!container.value) return
  
  const raycaster = new THREE.Raycaster()
  const mouse = new THREE.Vector2()
  
  const onMouseMove = (event) => {
    const rect = container.value.getBoundingClientRect()
    mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1
    mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1
    
    raycaster.setFromCamera(mouse, camera)
    const intersects = raycaster.intersectObjects(nodes_group.children)
    
    container.value.style.cursor = intersects.length > 0 ? 'pointer' : 'default'
    
    if (intersects.length > 0) {
      const node = intersects[0].object.userData
      // 显示节点提示
    }
  }
  
  const onClick = (event) => {
    const rect = container.value.getBoundingClientRect()
    mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1
    mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1
    
    raycaster.setFromCamera(mouse, camera)
    const intersects = raycaster.intersectObjects(nodes_group.children)
    
    if (intersects.length > 0) {
      selectedNode.value = intersects[0].object.userData
    }
  }
  
  container.value.addEventListener('mousemove', onMouseMove)
  container.value.addEventListener('click', onClick)
}

const handleResize = () => {
  if (!container.value || !camera || !renderer) return
  
  const width = container.value.clientWidth
  const height = container.value.clientHeight
  
  camera.aspect = width / height
  camera.updateProjectionMatrix()
  renderer.setSize(width, height)
}

onMounted(() => {
  initScene()
  setupInteraction()
  window.addEventListener('resize', handleResize)
})

watch(() => [props.nodes, props.links], () => {
  renderGraph()
}, { immediate: true, deep: true })

onUnmounted(() => {
  if (animationId) cancelAnimationFrame(animationId)
  if (renderer) renderer.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.call-chain-3d-enhanced {
  position: relative;
  width: 100%;
  height: 600px;
  background: #0f0f1a;
  border-radius: 12px;
  overflow: hidden;
}

.canvas-container {
  width: 100%;
  height: 100%;
}

.control-panel {
  position: absolute;
  top: 20px;
  left: 20px;
  width: 280px;
  z-index: 10;
}

.control-card {
  background: rgba(255, 255, 255, 0.95);
}

.control-section {
  margin-bottom: 20px;
}

.control-section h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #333;
}

.detail-panel {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 320px;
  z-index: 10;
}

.detail-card {
  background: rgba(255, 255, 255, 0.95);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-card__header) {
  padding: 12px 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

:deep(.el-descriptions-item__label) {
  font-size: 12px;
}
</style>
