<template>
  <div class="channel-manager">
    <h1>通道管理</h1>

    <!-- 通道列表 -->
    <div class="channels">
      <h2>已注册通道</h2>
      <div v-for="channel in channels" :key="channel" class="channel-card">
        <h3>{{ channel }}</h3>
        <span class="status" :class="channelStatus[channel] ? 'connected' : 'disconnected'">
          {{ channelStatus[channel] ? '已连接' : '未连接' }}
        </span>
        <div class="actions">
          <button @click="testChannel(channel)">测试</button>
          <button @click="showSendDialog(channel)">发送消息</button>
        </div>
      </div>
    </div>

    <!-- 发送消息对话框 -->
    <div v-if="showDialog" class="dialog">
      <h2>发送消息到 {{ selectedChannel }}</h2>
      <input v-model="target" placeholder="目标用户/群组" />
      <textarea v-model="content" placeholder="消息内容" rows="4"></textarea>
      <button @click="sendMessage">发送</button>
      <button @click="showDialog = false">取消</button>
    </div>

    <!-- 广播消息 -->
    <div class="broadcast">
      <h2>广播消息</h2>
      <textarea v-model="broadcastContent" placeholder="广播内容" rows="3"></textarea>
      <button @click="broadcast">广播到所有通道</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChannelManager',
  data() {
    return {
      channels: [],
      channelStatus: {},
      showDialog: false,
      selectedChannel: '',
      target: '',
      content: '',
      broadcastContent: ''
    }
  },
  mounted() {
    this.loadChannels()
  },
  methods: {
    async loadChannels() {
      const res = await fetch('/api/channels')
      const data = await res.json()
      this.channels = data.data || []
      this.channels.forEach(ch => {
        this.channelStatus[ch] = true
      })
    },
    async sendMessage() {
      await fetch(`/api/channels/${this.selectedChannel}/send`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          target: this.target,
          content: this.content
        })
      })
      alert('消息已发送')
      this.showDialog = false
    },
    async broadcast() {
      await fetch('/api/channels/broadcast', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ content: this.broadcastContent })
      })
      alert('广播已发送')
    },
    showSendDialog(channel) {
      this.selectedChannel = channel
      this.showDialog = true
    },
    testChannel(channel) {
      alert(`测试通道：${channel}`)
    }
  }
}
</script>

<style scoped>
.channel-manager { padding: 20px; }
.channel-card { border: 1px solid #ddd; padding: 15px; margin: 10px 0; border-radius: 8px; display: flex; align-items: center; gap: 15px; }
.status { padding: 4px 12px; border-radius: 4px; font-size: 12px; }
.status.connected { background: #4CAF50; color: white; }
.status.disconnected { background: #f44336; color: white; }
.actions { margin-left: auto; }
.actions button { margin-left: 5px; padding: 6px 12px; }
.dialog { position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); background: white; padding: 30px; box-shadow: 0 0 20px rgba(0,0,0,0.3); }
.dialog input, .dialog textarea { width: 100%; margin: 10px 0; padding: 8px; }
.broadcast { margin-top: 30px; }
.broadcast button { margin-top: 10px; padding: 10px 20px; background: #2196F3; color: white; border: none; border-radius: 4px; }
</style>
