<template>
  <div class="chat-panel">
    <el-card class="chat-card">
      <template #header>
        <div class="card-header">
          <h3>💬 智能助手</h3>
          <el-tag type="success" size="small">在线</el-tag>
        </div>
      </template>

      <!-- 聊天记录 -->
      <div class="chat-messages" ref="messagesContainer">
        <div
          v-for="(msg, index) in messages"
          :key="index"
          class="message"
          :class="msg.role"
        >
          <div class="avatar">
            <el-avatar
              :icon="msg.role === 'user' ? 'User' : 'Service'"
              :size="36"
              :style="{ background: msg.role === 'user' ? '#409EFF' : '#67C23A' }"
            />
          </div>
          <div class="content">
            <div class="bubble">
              {{ msg.content }}
            </div>
            <div class="time">{{ msg.time }}</div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="输入消息，按 Ctrl+Enter 发送..."
          @keydown.ctrl.enter="sendMessage"
        />
        <div class="actions">
          <el-button @click="clearChat">🗑️ 清空</el-button>
          <el-button type="primary" @click="sendMessage">📤 发送</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'

const messages = ref([
  {
    role: 'assistant',
    content: '你好！我是 JClaw 智能助手，有什么可以帮你的吗？',
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
])

const inputMessage = ref('')
const messagesContainer = ref(null)

const sendMessage = async () => {
  if (!inputMessage.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: inputMessage.value,
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  })

  const userMessage = inputMessage.value
  inputMessage.value = ''

  // 滚动到底部
  await nextTick()
  scrollToBottom()

  // 模拟 AI 回复
  setTimeout(() => {
    const reply = getAIReply(userMessage)
    messages.value.push({
      role: 'assistant',
      content: reply,
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    })
    nextTick(() => scrollToBottom())
  }, 1000)
}

const getAIReply = (message) => {
  const msg = message.toLowerCase()
  
  if (msg.includes('你好') || msg.includes('hello')) {
    return '你好！很高兴为你服务。我可以帮你查看代码、分析影响、管理 Agent 等。'
  }
  
  if (msg.includes('代码') || msg.includes('追溯')) {
    return '代码追溯功能可以帮你查看代码单元和调用链。点击左侧菜单的"追溯"即可查看。'
  }
  
  if (msg.includes('影响') || msg.includes('分析')) {
    return '影响分析功能可以分析代码变更的影响范围。请选择代码单元后点击"分析"按钮。'
  }
  
  if (msg.includes('agent') || msg.includes('智能体')) {
    return 'Agent 管理页面可以查看和管理所有智能体的状态。当前有 5 个 Agent 在线运行。'
  }
  
  if (msg.includes('通道') || msg.includes('消息')) {
    return '通道管理支持飞书、QQBot、微信等多个平台。你可以在这里发送消息或广播通知。'
  }
  
  if (msg.includes('谢谢') || msg.includes('感谢')) {
    return '不客气！有其他问题随时问我。'
  }
  
  return '我理解了你的问题。让我帮你处理...（演示模式：这是自动回复）'
}

const clearChat = () => {
  messages.value = [{
    role: 'assistant',
    content: '聊天记录已清空。有什么可以帮你的吗？',
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }]
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}
</script>

<style scoped>
.chat-panel {
  padding: 0;
}

.chat-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h3 {
  margin: 0;
  color: #303133;
  font-size: 16px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  min-height: 400px;
  max-height: 600px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 15px;
}

.message {
  display: flex;
  margin-bottom: 15px;
  gap: 10px;
}

.message.user {
  flex-direction: row-reverse;
}

.message.user .content {
  align-items: flex-end;
}

.message.user .bubble {
  background: #409EFF;
  color: white;
}

.message.assistant .bubble {
  background: white;
  color: #303133;
  border: 1px solid #e4e7ed;
}

.avatar {
  flex-shrink: 0;
}

.content {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}

.bubble {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  word-wrap: break-word;
}

.time {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  text-align: right;
}

.chat-input {
  border-top: 1px solid #e4e7ed;
  padding-top: 15px;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}
</style>
