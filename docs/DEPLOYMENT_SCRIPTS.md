# JClaw 部署脚本

> 一键部署 JClaw 到生产环境

---

## 快速部署

### 方式 1: Docker 部署 (推荐)

```bash
#!/bin/bash
# deploy-docker.sh

set -e

echo "🚀 开始部署 JClaw..."

# 1. 拉取镜像 (待创建)
docker pull houjibo/jclaw:1.0.0

# 2. 创建配置文件
mkdir -p ~/.jclaw
cat > ~/.jclaw/config.yml << EOF
api:
  key: \${JCLAW_API_KEY}
  endpoint: https://api.bigmodel.cn

model:
  provider: zhipu
  name: glm-4.7
EOF

# 3. 启动容器
docker run -d \
  --name jclaw \
  -p 8080:8080 \
  -v ~/.jclaw:/root/.jclaw \
  -e JCLAW_API_KEY=$JCLAW_API_KEY \
  houjibo/jclaw:1.0.0

# 4. 健康检查
echo "等待服务启动..."
sleep 10

curl -f http://localhost:8080/actuator/health || {
  echo "❌ 服务启动失败"
  docker logs jclaw
  exit 1
}

echo "✅ JClaw 部署成功！"
echo "访问：http://localhost:8080"
```

### 方式 2: Jar 包部署

```bash
#!/bin/bash
# deploy-jar.sh

set -e

echo "🚀 开始部署 JClaw..."

# 1. 创建目录
mkdir -p /opt/jclaw
cd /opt/jclaw

# 2. 下载 Jar 包
wget https://github.com/houjibo/JClaw/releases/download/v1.0.0/jclaw-1.0.0.jar

# 3. 创建配置文件
mkdir -p ~/.jclaw
cat > ~/.jclaw/config.yml << EOF
api:
  key: \${JCLAW_API_KEY}
  endpoint: https://api.bigmodel.cn

server:
  port: 8080
EOF

# 4. 创建 systemd 服务
cat > /etc/systemd/system/jclaw.service << EOF
[Unit]
Description=JClaw AI Assistant
After=network.target

[Service]
Type=simple
User=jclaw
WorkingDirectory=/opt/jclaw
Environment=JCLAW_API_KEY=$JCLAW_API_KEY
ExecStart=/usr/bin/java -Xmx2g -jar jclaw-1.0.0.jar
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

# 5. 启动服务
systemctl daemon-reload
systemctl enable jclaw
systemctl start jclaw

# 6. 健康检查
sleep 10
systemctl status jclaw

echo "✅ JClaw 部署成功！"
echo "日志：journalctl -u jclaw -f"
```

### 方式 3: Kubernetes 部署

```yaml
# k8s/jclaw-deployment.yaml

apiVersion: apps/v1
kind: Deployment
metadata:
  name: jclaw
  labels:
    app: jclaw
spec:
  replicas: 2
  selector:
    matchLabels:
      app: jclaw
  template:
    metadata:
      labels:
        app: jclaw
    spec:
      containers:
      - name: jclaw
        image: houjibo/jclaw:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: JCLAW_API_KEY
          valueFrom:
            secretKeyRef:
              name: jclaw-secrets
              key: api-key
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: jclaw
spec:
  selector:
    app: jclaw
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

部署命令:
```bash
kubectl apply -f k8s/jclaw-deployment.yaml
kubectl create secret generic jclaw-secrets --from-literal=api-key=$JCLAW_API_KEY
```

---

## 监控部署

```bash
#!/bin/bash
# deploy-monitoring.sh

set -e

echo "📊 开始部署监控系统..."

# 1. 启动 Prometheus + Grafana
docker-compose -f docker-compose.monitoring.yml up -d

# 2. 等待服务启动
sleep 15

# 3. 验证
echo "验证 Prometheus..."
curl -f http://localhost:9090/-/healthy || exit 1

echo "验证 Grafana..."
curl -f http://localhost:3000/api/health || exit 1

echo "✅ 监控系统部署成功！"
echo "Prometheus: http://localhost:9090"
echo "Grafana: http://localhost:3000 (admin/admin123)"
```

---

## 验证脚本

```bash
#!/bin/bash
# verify-deployment.sh

set -e

echo "🔍 验证 JClaw 部署..."

# 1. 检查服务状态
echo "检查服务状态..."
curl -f http://localhost:8080/actuator/health || {
  echo "❌ 服务未响应"
  exit 1
}

# 2. 检查 API
echo "检查 API..."
curl -f http://localhost:8080/api/status || {
  echo "❌ API 不可用"
  exit 1
}

# 3. 检查监控
echo "检查监控指标..."
curl -f http://localhost:8080/actuator/prometheus || {
  echo "❌ 监控指标不可用"
  exit 1
}

# 4. 测试 AI 功能
echo "测试 AI 功能..."
response=$(curl -s -X POST http://localhost:8080/api/agent/execute \
  -H "Content-Type: application/json" \
  -d '{"message":"你好"}')

if echo "$response" | grep -q "response"; then
  echo "✅ AI 功能正常"
else
  echo "⚠️ AI 功能异常"
fi

# 5. 检查工具
echo "检查工具系统..."
tools=$(curl -s http://localhost:8080/api/tools/list)
tool_count=$(echo "$tools" | grep -c "name")

if [ "$tool_count" -ge 40 ]; then
  echo "✅ 工具系统正常 ($tool_count 个工具)"
else
  echo "⚠️ 工具数量异常 ($tool_count 个)"
fi

echo ""
echo "✅ 验证完成！"
echo ""
echo "服务信息:"
echo "  API: http://localhost:8080"
echo "  Prometheus: http://localhost:9090"
echo "  Grafana: http://localhost:3000"
```

---

## 回滚脚本

```bash
#!/bin/bash
# rollback.sh

set -e

VERSION=$1

if [ -z "$VERSION" ]; then
  echo "用法：rollback.sh <version>"
  echo "示例：rollback.sh v0.9.0"
  exit 1
fi

echo "⚠️ 开始回滚到 $VERSION..."

# Docker 回滚
docker stop jclaw
docker rm jclaw

docker run -d \
  --name jclaw \
  -p 8080:8080 \
  -v ~/.jclaw:/root/.jclaw \
  houjibo/jclaw:$VERSION

echo "✅ 回滚完成！"
echo "当前版本：$VERSION"
```

---

## 清理脚本

```bash
#!/bin/bash
# cleanup.sh

set -e

echo "🧹 清理 JClaw 部署..."

# 停止服务
docker stop jclaw || true
docker rm jclaw || true

# 清理镜像
docker rmi houjibo/jclaw:1.0.0 || true

# 清理数据 (谨慎使用)
# rm -rf ~/.jclaw

# 清理监控
docker-compose -f docker-compose.monitoring.yml down || true

echo "✅ 清理完成！"
```

---

## 环境变量

```bash
# .env.example

# JClaw API Key (必需)
JCLAW_API_KEY="your_api_key_here"

# 服务端口
SERVER_PORT=8080

# JVM 配置
JVM_OPTS="-Xmx2g -Xms1g"

# 日志级别
LOG_LEVEL=INFO

# 监控配置
PROMETHEUS_ENABLED=true
GRAFANA_ADMIN_PASSWORD=admin123
```

---

*最后更新：2026-04-15*
