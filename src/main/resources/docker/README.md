# JClaw 开发环境 Docker Compose

## 启动全部服务
```bash
docker-compose -f docker-compose-dev.yml up -d
```

## 停止全部服务
```bash
docker-compose -f docker-compose-dev.yml down
```

## 查看服务状态
```bash
docker-compose -f docker-compose-dev.yml ps
```

## 查看日志
```bash
docker-compose -f docker-compose-dev.yml logs -f falkordb
docker-compose -f docker-compose-dev.yml logs -f mysql
docker-compose -f docker-compose-dev.yml logs -f redis
```

---

## 服务列表

| 服务 | 端口 | 说明 |
|------|------|------|
| falkordb | 6379 | 图数据库（意图图谱/代码追溯） |
| mysql | 3306 | 关系数据库（业务数据） |
| redis | 6380 | 缓存（记忆 L1 层/会话缓存） |

---

## 连接信息

### FalkorDB
- Host: localhost
- Port: 6379
- Password: (无)

### MySQL
- Host: localhost
- Port: 3306
- Database: jclaw
- Username: root
- Password: jclaw_password

### Redis
- Host: localhost
- Port: 6380
- Password: (无)
