<template>
  <div id="app">
    <el-container>
      <el-header>
        <h1>JClaw - AI 原生研发平台</h1>
        <el-menu mode="horizontal" :router="true" :default-active="$route.path">
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/memory">记忆</el-menu-item>
          <el-menu-item index="/intent">意图</el-menu-item>
          <el-menu-item index="/trace">追溯</el-menu-item>
          <el-menu-item index="/impact">影响</el-menu-item>
          <el-menu-item index="/agent">Agent</el-menu-item>
          <el-menu-item index="/channel">通道</el-menu-item>
          <el-menu-item index="/test">测试</el-menu-item>
          <el-menu-item index="/config">配置</el-menu-item>
        </el-menu>
      </el-header>
      
      <el-main>
        <router-view />
      </el-main>
      
      <el-footer>
        <p>JClaw v1.0.0 - Made with ❤️ by Cola</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

#app .el-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.el-header {
  background: #409EFF;
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
  flex-shrink: 0;
}

.el-header h1 {
  margin: 10px 0;
  font-size: 24px;
}

.el-menu {
  background: transparent !important;
  border: none !important;
  width: 100%;
}

.el-menu-item {
  color: white !important;
}

.el-menu-item.is-active {
  background: rgba(255,255,255,0.2) !important;
}

.el-main {
  padding: 10px 20px;
  background: #f5f7fa;
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.el-footer {
  background: #409EFF;
  color: white;
  text-align: center;
  line-height: 40px;
  flex-shrink: 0;
  height: 40px;
}
</style>
