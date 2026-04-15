# Prometheus + Grafana 部署指南

> JClaw 监控告警系统部署完整指南

---

## 前置要求

| 依赖 | 版本 | 说明 |
|------|------|------|
| Docker | 20.10+ | 容器运行时 |
| Docker Compose | 2.0+ | 容器编排 |
| 内存 | 2GB+ | Prometheus+Grafana |
| 磁盘 | 10GB+ | 监控数据存储 |

---

## 步骤 1: 创建 Docker Compose 配置

创建 `docker-compose.monitoring.yml`:

```yaml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus:v2.45.0
    container_name: jclaw-prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
    restart: unless-stopped

  grafana:
    image: grafana/grafana:10.0.0
    container_name: jclaw-grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=admin123
      - GF_USERS_ALLOW_SIGN_UP=false
    volumes:
      - grafana_data:/var/lib/grafana
      - ./monitoring/grafana/dashboard.json:/etc/grafana/provisioning/dashboards/dashboard.json
      - ./monitoring/grafana/alerts.json:/etc/grafana/provisioning/alerting/alerts.json
    restart: unless-stopped
    depends_on:
      - prometheus

volumes:
  prometheus_data: {}
  grafana_data: {}
```

---

## 步骤 2: 创建 Prometheus 配置

创建 `monitoring/prometheus/prometheus.yml`:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

alerting:
  alertmanagers:
    - static_configs:
        - targets: []

rule_files: []

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  - job_name: 'jclaw'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8080']
    relabel_configs:
      - source_labels: [__address__]
        target_label: instance
        replacement: 'jclaw-main'
```

---

## 步骤 3: 启用 Spring Boot Actuator

在 `pom.xml` 中添加依赖:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

在 `application.yml` 中配置:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,metrics
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: jclaw
```

---

## 步骤 4: 启动监控服务

```bash
# 启动 Prometheus + Grafana
docker-compose -f docker-compose.monitoring.yml up -d

# 查看状态
docker-compose -f docker-compose.monitoring.yml ps

# 查看日志
docker-compose -f docker-compose.monitoring.yml logs -f prometheus
docker-compose -f docker-compose.monitoring.yml logs -f grafana
```

---

## 步骤 5: 验证部署

### 访问 Prometheus

打开浏览器访问：http://localhost:9090

查询 JClaw 指标：
```
jclaw_tool_invocations_total
jclaw_ai_requests_total
jclaw_api_latency_seconds
```

### 访问 Grafana

打开浏览器访问：http://localhost:3000

登录：
- 用户名：`admin`
- 密码：`admin123`

导入仪表板：
1. 点击 "+" → Import
2. 上传 `monitoring/grafana/dashboard.json`
3. 选择 Prometheus 数据源
4. 点击 Import

---

## 步骤 6: 配置告警

### 方式 1: Grafana UI 配置

1. 进入 Alerting → Alert rules
2. 点击 New alert rule
3. 配置查询条件
4. 设置告警阈值
5. 配置通知渠道

### 方式 2: 导入告警规则

告警规则已在 `monitoring/grafana/alerts.json` 中定义：

```json
{
  "alert": "高错误率",
  "expr": "rate(jclaw_errors_total[5m]) > 0.1",
  "for": "5m",
  "severity": "warning"
}
```

---

## 步骤 7: 配置通知渠道

### Slack 通知

在 Grafana 中配置：
1. Alerting → Notification policies
2. Add contact point
3. 选择 Slack
4. 填写 Webhook URL
5. 测试通知

### 邮件通知

配置 SMTP:
```yaml
# Grafana 配置
GF_SMTP_ENABLED: "true"
GF_SMTP_HOST: "smtp.example.com:587"
GF_SMTP_USER: "alerts@example.com"
GF_SMTP_PASSWORD: "password"
```

---

## 监控指标说明

### 核心指标

| 指标名 | 说明 | 类型 |
|--------|------|------|
| `jclaw_tool_invocations_total` | 工具调用次数 | Counter |
| `jclaw_ai_requests_total` | AI 请求次数 | Counter |
| `jclaw_errors_total` | 错误次数 | Counter |
| `jclaw_api_latency_seconds` | API 延迟 | Histogram |
| `jclaw_ai_response_seconds` | AI 响应时间 | Histogram |

### JVM 指标

| 指标名 | 说明 |
|--------|------|
| `jvm_memory_used_bytes` | JVM 内存使用 |
| `jvm_threads_live_threads` | 活跃线程数 |
| `process_cpu_usage` | CPU 使用率 |

---

## 故障排除

### 问题 1: Prometheus 无法抓取指标

**检查**:
```bash
curl http://localhost:8080/actuator/prometheus
```

**解决**:
1. 确认 Actuator 已启用
2. 检查端口配置
3. 验证防火墙规则

### 问题 2: Grafana 无法连接 Prometheus

**检查**:
- Prometheus 是否运行
- 数据源配置是否正确

**解决**:
1. Configuration → Data sources
2. 编辑 Prometheus 数据源
3. URL: `http://jclaw-prometheus:9090`
4. 点击 Save & Test

### 问题 3: 告警不触发

**检查**:
- 告警规则是否正确
- 评估间隔是否合理

**解决**:
1. 检查告警表达式
2. 调整 `for` 持续时间
3. 验证通知渠道配置

---

## 维护指南

### 数据保留

Prometheus 默认保留 15 天数据，修改配置：

```yaml
command:
  - '--storage.tsdb.retention.time=30d'
```

### 备份恢复

**备份**:
```bash
docker cp jclaw-grafana:/var/lib/grafana grafana-backup
```

**恢复**:
```bash
docker cp grafana-backup jclaw-grafana:/var/lib/grafana
```

### 升级

```bash
# 拉取最新镜像
docker-compose pull

# 重启服务
docker-compose -f docker-compose.monitoring.yml up -d
```

---

## 资源链接

- [Prometheus 官方文档](https://prometheus.io/docs/)
- [Grafana 官方文档](https://grafana.com/docs/)
- [Micrometer 文档](https://micrometer.io/)
- [JClaw 监控指标](./MONITORING_METRICS.md)

---

*最后更新：2026-04-15*
