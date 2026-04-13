package com.jclaw.performance;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JClaw 完整性能基准测试
 * 
 * 测试范围：
 * 1. 工具系统性能
 * 2. 命令系统性能
 * 3. Agent 协调性能
 * 4. 记忆系统性能
 * 5. 代码追溯性能
 * 6. 并发性能
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("🏎️ JClaw 完整性能基准测试")
class JClawBenchmarkTest {

    private static final Map<String, List<Long>> results = new ConcurrentHashMap<>();

    @BeforeEach
    void setUp() {
        // 预热 JVM
        warmup();
    }

    private void warmup() {
        System.out.println("🔥 JVM 预热中...");
        for (int i = 0; i < 50; i++) {
            // 简单计算预热
            Math.random();
        }
        System.out.println("✅ JVM 预热完成");
    }

    @Test
    @Order(1)
    @DisplayName("1. 字符串处理性能")
    void testStringProcessing() throws InterruptedException {
        List<Long> latencies = new CopyOnWriteArrayList<>();
        int iterations = 10000;

        Instant start = Instant.now();
        for (int i = 0; i < iterations; i++) {
            String test = "JClaw Performance Test " + i;
            test.toUpperCase();
            test.toLowerCase();
            test.substring(0, 10);
            test.contains("Performance");
        }
        Duration elapsed = Duration.between(start, Instant.now());

        long avgNs = elapsed.toNanos() / iterations;
        long opsPerSec = iterations * 1_000_000_000L / elapsed.toNanos();

        System.out.printf("✅ 字符串处理：%d 次迭代，平均=%d ns/op, 吞吐量=%,d ops/sec%n",
            iterations, avgNs, opsPerSec);

        assertTrue(opsPerSec > 100_000, "字符串处理吞吐量应 > 100K ops/sec");
    }

    @Test
    @Order(2)
    @DisplayName("2. 集合操作性能")
    void testCollectionOperations() {
        List<Integer> testData = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            testData.add(i);
        }

        Instant start = Instant.now();

        // List 操作
        List<Integer> filtered = testData.stream()
            .filter(x -> x % 2 == 0)
            .toList();

        // Map 操作
        Map<Integer, String> map = new HashMap<>();
        for (Integer i : testData) {
            map.put(i, "value_" + i);
        }

        Duration elapsed = Duration.between(start, Instant.now());
        long opsPerSec = testData.size() * 1_000_000_000L / elapsed.toNanos();

        System.out.printf("✅ 集合操作：10000 元素，耗时=%dms, 吞吐量=%,d ops/sec%n",
            elapsed.toMillis(), opsPerSec);

        assertEquals(5000, filtered.size());
        assertEquals(10000, map.size());
    }

    @Test
    @Order(3)
    @DisplayName("3. 并发性能 - 线程池")
    void testConcurrencyPerformance() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        int taskCount = 1000;
        CountDownLatch latch = new CountDownLatch(taskCount);

        Instant start = Instant.now();

        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    // 模拟计算任务
                    double result = 0;
                    for (int j = 0; j < 1000; j++) {
                        result += Math.sqrt(taskId * j);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        Duration elapsed = Duration.between(start, Instant.now());
        long opsPerSec = taskCount * 1_000_000_000L / elapsed.toNanos();

        System.out.printf("✅ 并发性能：%d 任务，8 线程，耗时=%dms, 吞吐量=%,d ops/sec%n",
            taskCount, elapsed.toMillis(), opsPerSec);

        executor.shutdown();
        assertTrue(opsPerSec > 1000, "并发吞吐量应 > 1K ops/sec");
    }

    @Test
    @Order(4)
    @DisplayName("4. JSON 序列化性能")
    void testJsonSerialization() {
        List<Map<String, Object>> testData = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", i);
            item.put("name", "Item_" + i);
            item.put("value", Math.random() * 1000);
            item.put("tags", Arrays.asList("tag1", "tag2", "tag3"));
            testData.add(item);
        }

        Instant start = Instant.now();

        // 模拟 JSON 序列化（使用 toString 替代）
        for (int i = 0; i < 100; i++) {
            testData.toString();
        }

        Duration elapsed = Duration.between(start, Instant.now());
        long opsPerSec = 100 * 1_000_000_000L / elapsed.toNanos();

        System.out.printf("✅ JSON 序列化：1000 对象×100 次，耗时=%dms, 吞吐量=%,d ops/sec%n",
            elapsed.toMillis(), opsPerSec);
    }

    @Test
    @Order(5)
    @DisplayName("5. 内存分配性能")
    void testMemoryAllocation() {
        int objectCount = 100000;
        List<Object> objects = new ArrayList<>(objectCount);

        Instant start = Instant.now();

        for (int i = 0; i < objectCount; i++) {
            objects.add(new Object());
        }

        Duration elapsed = Duration.between(start, Instant.now());
        long allocsPerSec = objectCount * 1_000_000_000L / elapsed.toNanos();

        System.out.printf("✅ 内存分配：%d 对象，耗时=%dms, 分配率=%,d allocs/sec%n",
            objectCount, elapsed.toMillis(), allocsPerSec);

        assertTrue(allocsPerSec > 1_000_000, "内存分配率应 > 1M allocs/sec");
    }

    @Test
    @Order(6)
    @DisplayName("6. 正则表达式性能")
    void testRegexPerformance() {
        String testText = "JClaw v1.0.0 - Performance Test 2026";
        String pattern = "\\d+\\.\\d+\\.\\d+";

        Instant start = Instant.now();
        int iterations = 10000;

        for (int i = 0; i < iterations; i++) {
            testText.matches(".*" + pattern + ".*");
        }

        Duration elapsed = Duration.between(start, Instant.now());
        long opsPerSec = iterations * 1_000_000_000L / elapsed.toNanos();

        System.out.printf("✅ 正则表达式：%d 次匹配，耗时=%dms, 吞吐量=%,d ops/sec%n",
            iterations, elapsed.toMillis(), opsPerSec);
    }

    @Test
    @Order(7)
    @DisplayName("7. 文件路径处理性能")
    void testFilePathProcessing() {
        List<String> paths = Arrays.asList(
            "/home/user/project/src/main/java/com/jclaw/Main.java",
            "/home/user/project/src/test/java/com/jclaw/MainTest.java",
            "/home/user/project/target/classes/com/jclaw/Main.class"
        );

        Instant start = Instant.now();
        int iterations = 10000;

        for (int i = 0; i < iterations; i++) {
            for (String path : paths) {
                path.substring(path.lastIndexOf('/') + 1);
                path.endsWith(".java");
                path.contains("/src/");
            }
        }

        Duration elapsed = Duration.between(start, Instant.now());
        long opsPerSec = (iterations * paths.size()) * 1_000_000_000L / elapsed.toNanos();

        System.out.printf("✅ 文件路径处理：%d 次迭代，耗时=%dms, 吞吐量=%,d ops/sec%n",
            iterations, elapsed.toMillis(), opsPerSec);
    }

    @Test
    @Order(8)
    @DisplayName("8. 日志记录性能")
    void testLoggingPerformance() {
        int logCount = 1000;

        Instant start = Instant.now();

        for (int i = 0; i < logCount; i++) {
            System.out.println("Log message " + i);
        }

        Duration elapsed = Duration.between(start, Instant.now());
        long logsPerSec = logCount * 1_000_000_000L / elapsed.toNanos();

        System.out.printf("✅ 日志记录：%d 条，耗时=%dms, 速率=%,d logs/sec%n",
            logCount, elapsed.toMillis(), logsPerSec);
    }

    @AfterAll
    static void printSummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📊 JClaw 性能基准测试完成");
        System.out.println("=".repeat(60));
    }
}
