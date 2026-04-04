<template>
  <div class="channel-manager">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>通道管理</h2>
          <el-tag type="success">已注册 {{ channels.length }} 个通道</el-tag>
        </div>
      </template>

      <!-- 通道列表 -->
      <div class="channels">
        <h3>已注册通道</h3>
        <el-table :data="channels" style="width: 100%" border>
          <el-table-column prop="name" label="通道名称" width="150" />
          <el-table-column prop="type" label="类型" width="100" />
          <el-table-column prop="account" label="账号" width="200" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 'connected' ? 'success' : 'danger'">
                {{ scope.row.status === 'connected' ? '已连接' : '未连接' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="lastActive" label="最后活跃" width="180" />
          <el-table-column label="操作" width="300">
            <template #default="scope">
              <el-button size="small" @click="testChannel(scope.row)">测试连接</el-button>
              <el-button size="small" type="primary" @click="showSendDialog(scope.row)">发送消息</el-button>
              <el-button size="small" type="warning" @click="viewLogs(scope.row)">查看日志</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 消息发送记录 -->
      <div class="message-logs">
        <h3>最近消息发送记录</h3>
        <el-table :data="messageLogs" style="width: 100%" border stripe>
          <el-table-column prop="timestamp" label="时间" width="180" />
          <el-table-column prop="channel" label="通道" width="120" />
          <el-table-column prop="target" label="目标" width="150" />
          <el-table-column prop="content" label="内容" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 'success' ? 'success' : 'danger'" size="small">
                {{ scope.row.status === 'success' ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 广播消息 -->
      <div class="broadcast">
        <h3>广播消息</h3>
        <el-input
          v-model="broadcastContent"
          type="textarea"
          :rows="4"
          placeholder="输入广播内容，将发送到所有已连接的通道..."
        />
        <div class="broadcast-actions">
          <el-checkbox v-model="broadcastToAll">发送到所有通道</el-checkbox>
          <el-select v-model="selectedChannels" multiple placeholder="选择通道" style="width: 300px" :disabled="broadcastToAll">
            <el-option v-for="ch in channels" :key="ch.name" :label="ch.name" :value="ch.name" />
          </el-select>
          <el-button type="primary" @click="broadcast">📢 发送广播</el-button>
        </div>
      </div>
    </el-card>

    <!-- 发送消息对话框 -->
    <el-dialog v-model="showDialog" title="发送消息" width="600px">
      <el-form :model="messageForm" label-width="80px">
        <el-form-item label="通道">
          <el-input v-model="messageForm.channel" disabled />
        </el-form-item>
        <el-form-item label="目标">
          <el-input v-model="messageForm.target" placeholder="用户 ID 或群组 ID" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="messageForm.content" type="textarea" :rows="4" placeholder="消息内容" />
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="messageForm.type">
            <el-radio label="text">文本</el-radio>
            <el-radio label="image">图片</el-radio>
            <el-radio label="file">文件</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="sendMessage">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'

// 通道数据（演示用）
const channels = ref([
  { name: '飞书 - 可乐', type: 'Feishu', account: 'ou_973bdfb2488ee91ec66bf9cc7a49715a', status: 'connected', lastActive: '2026-04-04 15:41' },
  { name: '飞书 - 毛毛', type: 'Feishu', account: 'ou_184103718c09a670ee9de1aab4f07e08', status: 'connected', lastActive: '2026-04-04 15:30' },
  { name: 'QQBot - 主账号', type: 'QQBot', account: '95C7217D27C291953008607B2D5F5563', status: 'connected', lastActive: '2026-04-04 15:35' },
  { name: '微信 - 工作号', type: 'WeChat', account: 'wx_work_001', status: 'disconnected', lastActive: '2026-04-03 18:20' },
  { name: 'Telegram Bot', type: 'Telegram', account: '@jclaw_bot', status: 'connected', lastActive: '2026-04-04 14:50' }
])

// 消息日志（演示用）
const messageLogs = ref([
  { timestamp: '2026-04-04 15:41:23', channel: '飞书 - 可乐', target: 'ou_xxx', content: 'JClaw 前端 UI 验证完成！', status: 'success' },
  { timestamp: '2026-04-04 15:40:15', channel: 'QQBot', target: 'group_001', content: '📊 测试报告已生成', status: 'success' },
  { timestamp: '2026-04-04 15:38:42', channel: '飞书 - 毛毛', target: 'ou_xxx', content: '通道管理页面更新完成', status: 'success' },
  { timestamp: '2026-04-04 15:35:10', channel: '飞书 - 可乐', target: 'oc_xxx', content: '✅ 所有功能验证通过', status: 'success' },
  { timestamp: '2026-04-04 15:30:05', channel: 'Telegram', target: '@user', content: '部署完成通知', status: 'failed' }
])

const showDialog = ref(false)
const broadcastContent = ref('')
const broadcastToAll = ref(true)
const selectedChannels = ref([])

const messageForm = reactive({
  channel: '',
  target: '',
  content: '',
  type: 'text'
})

const testChannel = (row) => {
  ElMessage.success(`✅ ${row.name} 连接测试成功！响应时间：${Math.floor(Math.random() * 100) + 20}ms`)
}

const showSendDialog = (row) => {
  messageForm.channel = row.name
  messageForm.target = ''
  messageForm.content = ''
  messageForm.type = 'text'
  showDialog.value = true
}

const sendMessage = async () => {
  // 模拟发送
  setTimeout(() => {
    ElNotification({
      title: '发送成功',
      message: `消息已发送到 ${messageForm.channel} → ${messageForm.target}`,
      type: 'success'
    })
    messageLogs.value.unshift({
      timestamp: new Date().toLocaleString('zh-CN'),
      channel: messageForm.channel,
      target: messageForm.target,
      content: messageForm.content,
      status: 'success'
    })
    showDialog.value = false
  }, 500)
}

const broadcast = async () => {
  if (!broadcastContent.value) {
    ElMessage.warning('请输入广播内容')
    return
  }
  const targets = broadcastToAll.value 
    ? channels.value.filter(c => c.status === 'connected').map(c => c.name)
    : selectedChannels.value
  
  ElNotification({
    title: '广播已发送',
    message: `已发送到 ${targets.length} 个通道：${targets.join(', ')}`,
    type: 'success',
    duration: 5000
  })
  
  // 添加日志
  targets.forEach(ch => {
    messageLogs.value.unshift({
      timestamp: new Date().toLocaleString('zh-CN'),
      channel: ch,
      target: 'ALL',
      content: broadcastContent.value,
      status: 'success'
    })
  })
  
  broadcastContent.value = ''
}

const viewLogs = (row) => {
  const logs = messageLogs.value.filter(log => log.channel === row.name)
  ElMessage.info(`${row.name} 共有 ${logs.length} 条消息记录`)
}
</script>

<style scoped>
.channel-manager {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.channels {
  margin-bottom: 30px;
}

.message-logs {
  margin: 30px 0;
}

.broadcast {
  margin-top: 30px;
}

.broadcast-actions {
  margin-top: 15px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.broadcast-actions .el-button {
  margin-left: auto;
}
</style>
