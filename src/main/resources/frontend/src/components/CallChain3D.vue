<template>
  <div class="call-chain-3d" ref="container"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as THREE from 'three'
import { forceManyBody, forceLink, forceCenter, forceCollide } from 'd3-force'

const props = defineProps({
  nodes: {
    type: Array,
    default: () => []
  },
  links: {
    type: Array,
    default: () => []
  }
})

const container = ref(null)
let scene, camera, renderer, nodes_group, links_group
let animationId = null

// 初始化 Three.js 场景
const initScene = () => {
  if (!container.value) return
  
  const width = container.value.clientWidth
  const height = container.value.clientHeight
  
  // 创建场景
  scene = new THREE.Scene()
  scene.background = new THREE.Color(0x0f0f1a)
  
  // 创建相机
  camera = new THREE.PerspectiveCamera(75, width / height, 0.1, 1000)
  camera.position.set(0, 0, 500)
  
  // 创建渲染器
  renderer = new THREE.WebGLRenderer({ antialias: true })
  renderer.setSize(width, height)
  renderer.setPixelRatio(window.devicePixelRatio)
  container.value.appendChild(renderer.domElement)
  
  // 添加光源
  const ambientLight = new THREE.AmbientLight(0xffffff, 0.6)
  scene.add(ambientLight)
  
  const pointLight = new THREE.PointLight(0xffffff, 0.8)
  pointLight.position.set(200, 200, 200)
  scene.add(pointLight)
  
  // 创建节点和连线组
  nodes_group = new THREE.Group()
  links_group = new THREE.Group()
  scene.add(nodes_group)
  scene.add(links_group)
  
  // 添加坐标轴辅助
  const axesHelper = new THREE.AxesHelper(100)
  scene.add(axesHelper)
  
  // 添加网格
  const gridHelper = new THREE.GridHelper(500, 50, 0x444444, 0x222222)
  gridHelper.rotation.x = Math.PI / 2
  scene.add(gridHelper)
  
  // 开始动画
  animate()
}

// 创建节点几何体 - 使用更简单的几何体提升性能
const createNodeMesh = (node, type) => {
  // 使用较低多边数的球体提升性能
  const geometry = new THREE.SphereGeometry(8, 16, 16)
  let color
  
  switch (type) {
    case 'CLASS':
      color = 0x667eea
      break
    case 'METHOD':
      color = 0xf093fb
      break
    case 'INTERFACE':
      color = 0x4facfe
      break
    default:
      color = 0x43e97b
  }
  
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
  
  // 添加标签
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
  const simulation = forceManyBody()
    .strength(-300)
    .distanceMax(400)
  
  const linkForce = forceLink(props.links)
    .id(d => d.id)
    .distance(100)
    .strength(0.7)
  
  const centerForce = forceCenter(0, 0)
  const collideForce = forceCollide(20)
  
  // 初始化节点位置
  props.nodes.forEach(node => {
    node.x = node.x || (Math.random() - 0.5) * 200
    node.y = node.y || (Math.random() - 0.5) * 200
    node.z = node.z || (Math.random() - 0.5) * 200
    node.vx = 0
    node.vy = 0
    node.vz = 0
  })
  
  // 运行模拟 - 减少迭代次数提升性能
  for (let i = 0; i < 150; i++) {
    simulation(props.nodes)
    linkForce(props.links)
    centerForce(props.nodes)
    collideForce(props.nodes)
  }
}

// 渲染图形
const renderGraph = () => {
  // 清空现有图形
  while(nodes_group.children.length > 0) {
    nodes_group.remove(nodes_group.children[0])
  }
  while(links_group.children.length > 0) {
    links_group.remove(links_group.children[0])
  }
  
  // 运行力导向布局
  runForceLayout()
  
  // 创建节点
  props.nodes.forEach(node => {
    const mesh = createNodeMesh(node, node.type)
    nodes_group.add(mesh)
  })
  
  // 创建连线
  props.links.forEach(link => {
    const mesh = createLinkMesh(link)
    links_group.add(mesh)
  })
}

// 动画循环 - 降低帧率提升性能
const animate = () => {
  animationId = requestAnimationFrame(animate)
  
  if (nodes_group) {
    nodes_group.rotation.y += 0.001
  }
  
  renderer.render(scene, camera)
}

// 处理窗口大小变化
const handleResize = () => {
  if (!container.value || !camera || !renderer) return
  
  const width = container.value.clientWidth
  const height = container.value.clientHeight
  
  camera.aspect = width / height
  camera.updateProjectionMatrix()
  renderer.setSize(width, height)
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
    
    if (intersects.length > 0) {
      container.value.style.cursor = 'pointer'
    } else {
      container.value.style.cursor = 'default'
    }
  }
  
  container.value.addEventListener('mousemove', onMouseMove)
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
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  if (renderer) {
    renderer.dispose()
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.call-chain-3d {
  width: 100%;
  height: 500px;
  border-radius: 8px;
  overflow: hidden;
}
</style>
