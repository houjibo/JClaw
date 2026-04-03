# Redis 缓存策略设计

> **任务**: 任务 3 - Redis 技术预研  
> **日期**: 2026-04-03  
> **作者**: 可乐 🥤  
> **用途**: 记忆 L1 层缓存 + 会话缓存

---

## 📊 缓存需求分析

### 1. 记忆 L1 层（工作记忆）

**需求**:
- 存储当前会话上下文
- 短期缓存，会话级生命周期
- 快速读写（<10ms）
- 自动过期（30 分钟无访问）

**数据结构**:
```
Key 格式：jclaw:memory:working:{sessionId}
类型：Hash
过期：30 分钟 TTL
内容：{ context, shortTermMemories, lastAccessed }
```

### 2. 会话缓存

**需求**:
- 存储用户会话信息
- 会话级生命周期
- 快速读写
- 自动过期（24 小时）

**数据结构**:
```
Key 格式：jclaw:session:{sessionId}
类型：Hash
过期：24 小时 TTL
内容：{ userId, userPreferences, settings }
```

### 3. API 响应缓存

**需求**:
- 缓存频繁查询的 API 响应
- 降低数据库压力
- 可配置过期时间

**数据结构**:
```
Key 格式：jclaw:cache:api:{endpoint}:{params}
类型：String (JSON)
过期：可配置（默认 10 分钟）
内容：{ data, timestamp, ttl }
```

### 4. 工具执行进度缓存

**需求**:
- 缓存工具执行进度
- WebSocket 实时推送
- 临时数据，执行完成即删除

**数据结构**:
```
Key 格式：jclaw:progress:{sessionId}:{toolName}
类型：String (JSON)
过期：5 分钟 TTL
内容：{ toolName, progress, status, message }
```

---

## 🏗️ Redis 架构设计

### 部署架构

```yaml
# 开发环境（单机）
Redis:6380
├── 数据库 0: 缓存（默认）
├── 数据库 1: 会话
├── 数据库 2: 记忆
└── 数据库 3: 工具进度

# 生产环境（主从 + 哨兵）
Redis-Master:6380
├── Redis-Slave1:6381
├── Redis-Slave2:6382
└── Sentinel:26379 x 3
```

### 连接配置

```yaml
spring:
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6380}
    password: ${REDIS_PASSWORD:}
    database: 0
    timeout: 5000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: 1000ms
```

---

## 📝 Redis 数据结构设计

### 1. 记忆 L1 层

**Hash 结构**:
```
Key: jclaw:memory:working:{sessionId}

Field:
  - context: JSON 字符串（当前会话上下文）
  - short_term: JSON 数组（短期记忆列表）
  - last_accessed: 时间戳
  - access_count: 访问次数

TTL: 1800 秒（30 分钟）
```

**操作示例**:
```java
// 写入记忆
redisTemplate.opsForHash().putAll(
    "jclaw:memory:working:" + sessionId,
    Map.of(
        "context", contextJson,
        "short_term", shortTermJson,
        "last_accessed", String.valueOf(System.currentTimeMillis()),
        "access_count", String.valueOf(count)
    )
);

// 读取记忆
Map<Object, Object> memory = redisTemplate.opsForHash().entries(
    "jclaw:memory:working:" + sessionId
);

// 设置过期时间
redisTemplate.expire(
    "jclaw:memory:working:" + sessionId,
    1800, TimeUnit.SECONDS
);
```

### 2. 会话缓存

**Hash 结构**:
```
Key: jclaw:session:{sessionId}

Field:
  - user_id: 用户 ID
  - preferences: JSON 字符串（用户偏好）
  - settings: JSON 字符串（会话设置）
  - created_at: 创建时间戳

TTL: 86400 秒（24 小时）
```

### 3. API 响应缓存

**String 结构**:
```
Key: jclaw:cache:api:{endpoint}:{md5(params)}

Value: {
  "data": {...},
  "timestamp": 1712131200000,
  "ttl": 600
}

TTL: 600 秒（10 分钟）
```

**操作示例**:
```java
// 写入缓存
String key = "jclaw:cache:api:" + endpoint + ":" + md5(params);
String value = objectMapper.writeValueAsString(cacheData);
redisTemplate.opsForValue().set(key, value, 600, TimeUnit.SECONDS);

// 读取缓存
String cached = redisTemplate.opsForValue().get(key);
if (cached != null) {
    return objectMapper.readValue(cached, CacheData.class);
}
```

### 4. 工具执行进度

**String 结构**:
```
Key: jclaw:progress:{sessionId}:{toolName}

Value: {
  "toolName": "file_read",
  "progress": 50,
  "status": "running",
  "message": "正在读取文件..."
}

TTL: 300 秒（5 分钟）
```

---

## 🛠️ Spring Data Redis 集成

### 依赖配置

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

### 配置类

```java
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        
        // 使用 Jackson 序列化
        Jackson2JsonRedisSerializer<Object> serializer = 
            new Jackson2JsonRedisSerializer<>(Object.class);
        template.setValueSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new Jackson2JsonRedisSerializer<>(Object.class)));
        
        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .build();
    }
}
```

### 缓存工具类

```java
@Component
public class CacheUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 写入工作记忆
     */
    public void setWorkingMemory(String sessionId, WorkingMemory memory) {
        String key = "jclaw:memory:working:" + sessionId;
        redisTemplate.opsForHash().putAll(key, memory.toMap());
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
    }

    /**
     * 读取工作记忆
     */
    public WorkingMemory getWorkingMemory(String sessionId) {
        String key = "jclaw:memory:working:" + sessionId;
        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
        if (data.isEmpty()) {
            return null;
        }
        return WorkingMemory.fromMap(data);
    }

    /**
     * 写入 API 缓存
     */
    public void setApiCache(String endpoint, Object params, Object data) {
        String key = "jclaw:cache:api:" + endpoint + ":" + md5(params.toString());
        CacheData cacheData = new CacheData(data, System.currentTimeMillis(), 600);
        redisTemplate.opsForValue().set(key, cacheData, 10, TimeUnit.MINUTES);
    }

    /**
     * 读取 API 缓存
     */
    public CacheData getApiCache(String endpoint, Object params) {
        String key = "jclaw:cache:api:" + endpoint + ":" + md5(params.toString());
        return (CacheData) redisTemplate.opsForValue().get(key);
    }

    /**
     * 更新工具进度
     */
    public void updateProgress(String sessionId, String toolName, int progress, String message) {
        String key = "jclaw:progress:" + sessionId + ":" + toolName;
        ProgressData data = new ProgressData(toolName, progress, "running", message);
        redisTemplate.opsForValue().set(key, data, 5, TimeUnit.MINUTES);
    }

    private String md5(String input) {
        // MD5 工具方法
    }
}
```

---

## 🧪 测试用例

### 单元测试

```java
@SpringBootTest
class CacheUtilsTest {

    @Autowired
    private CacheUtils cacheUtils;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        // 清空测试数据库
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.flushDb();
            return null;
        });
    }

    @Test
    void testWorkingMemory() {
        String sessionId = "test-session-1";
        WorkingMemory memory = new WorkingMemory("context", List.of("mem1", "mem2"));
        
        // 写入
        cacheUtils.setWorkingMemory(sessionId, memory);
        
        // 读取
        WorkingMemory retrieved = cacheUtils.getWorkingMemory(sessionId);
        assertNotNull(retrieved);
        assertEquals("context", retrieved.getContext());
    }

    @Test
    void testApiCache() {
        String endpoint = "/api/test";
        Map<String, Object> params = Map.of("id", 1);
        Map<String, Object> data = Map.of("result", "success");
        
        // 写入
        cacheUtils.setApiCache(endpoint, params, data);
        
        // 读取
        CacheData cached = cacheUtils.getApiCache(endpoint, params);
        assertNotNull(cached);
        assertEquals("success", ((Map) cached.getData()).get("result"));
    }

    @Test
    void testProgressUpdate() {
        String sessionId = "test-session-1";
        String toolName = "file_read";
        
        // 更新进度
        cacheUtils.updateProgress(sessionId, toolName, 50, "读取中...");
        
        // 验证
        String key = "jclaw:progress:" + sessionId + ":" + toolName;
        ProgressData progress = (ProgressData) redisTemplate.opsForValue().get(key);
        assertNotNull(progress);
        assertEquals(50, progress.getProgress());
    }
}
```

---

## 📊 性能基准

### 预期性能

| 操作 | 目标延迟 | 预期结果 |
|------|----------|----------|
| Hash 写入 | <10ms | ~2ms |
| Hash 读取 | <10ms | ~2ms |
| String 写入 | <10ms | ~1ms |
| String 读取 | <10ms | ~1ms |
| 批量操作 | <50ms | ~20ms |

### 压测脚本

```bash
# Redis 基准测试
redis-benchmark -h localhost -p 6380 -q -n 10000 -c 100
redis-benchmark -h localhost -p 6380 -q -t set,get -n 10000
```

---

## ✅ 验收标准

- [ ] Docker Compose 可一键启动
- [ ] Spring Data Redis 可正常连接
- [ ] 缓存工具类支持 String/Hash/List
- [ ] 单元测试覆盖>80%
- [ ] 性能测试：读写<10ms
- [ ] 缓存策略文档完整

---

## 📚 参考资料

- [Spring Data Redis 官方文档](https://docs.spring.io/spring-data/redis/docs/current/reference/html/)
- [Redis 官方文档](https://redis.io/documentation)
- [Redis 最佳实践](https://redis.io/docs/manual/)

---

*报告完成时间：2026-04-03*  
*作者：可乐 🥤*  
*状态：✅ 完成*
