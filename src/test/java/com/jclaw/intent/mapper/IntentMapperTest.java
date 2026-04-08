package com.jclaw.intent.mapper;

import com.jclaw.BaseMapperTest;
import com.jclaw.intent.entity.Intent;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IntentMapper 集成测试
 * 测试意图数据访问层的 CRUD 操作
 */
@DisplayName("IntentMapper 集成测试")
class IntentMapperTest extends BaseMapperTest {

    @Autowired
    private IntentMapper intentMapper;

    @BeforeEach
    public void setUp() {
        // 清理 intent 表
        intentMapper.delete(null);
    }

    @Test
    @DisplayName("测试插入意图")
    void testInsert() {
        // Arrange
        Intent intent = createIntent("intent_001", "测试意图");

        // Act
        int result = intentMapper.insert(intent);

        // Assert
        assertEquals(1, result);
        assertNotNull(intent.getId());
    }

    @Test
    @DisplayName("测试根据 ID 查询意图")
    void testSelectById() {
        // Arrange
        Intent intent = createIntent("intent_001", "测试意图");
        intentMapper.insert(intent);

        // Act
        Intent result = intentMapper.selectById("intent_001");

        // Assert
        assertNotNull(result);
        assertEquals("测试意图", result.getName());
        assertEquals("这是一个测试意图", result.getDescription());
    }

    @Test
    @DisplayName("测试更新意图")
    void testUpdate() {
        // Arrange
        Intent intent = createIntent("intent_001", "测试意图");
        intentMapper.insert(intent);

        // Act
        intent.setName("更新后的意图");
        intent.setStatus("completed");
        intent.setPriority(5);
        int result = intentMapper.updateById(intent);

        // Assert
        assertEquals(1, result);
        
        Intent updated = intentMapper.selectById("intent_001");
        assertNotNull(updated);
        assertEquals("更新后的意图", updated.getName());
        assertEquals("completed", updated.getStatus());
        assertEquals(5, updated.getPriority());
    }

    @Test
    @DisplayName("测试删除意图")
    void testDelete() {
        // Arrange
        Intent intent = createIntent("intent_001", "测试意图");
        intentMapper.insert(intent);

        // Act
        int result = intentMapper.deleteById("intent_001");

        // Assert
        assertEquals(1, result);
        
        Intent deleted = intentMapper.selectById("intent_001");
        assertNull(deleted);
    }

    @Test
    @DisplayName("测试查询意图列表")
    void testSelectList() {
        // Arrange
        intentMapper.insert(createIntent("intent_001", "意图 1"));
        intentMapper.insert(createIntent("intent_002", "意图 2"));
        intentMapper.insert(createIntent("intent_003", "意图 3"));

        // Act
        List<Intent> intents = intentMapper.selectList(null);

        // Assert
        assertEquals(3, intents.size());
    }

    @Test
    @DisplayName("测试条件查询 - 按状态")
    void testSelectByStatus() {
        // Arrange
        intentMapper.insert(createIntent("intent_001", "意图 1", "pending"));
        intentMapper.insert(createIntent("intent_002", "意图 2", "pending"));
        intentMapper.insert(createIntent("intent_003", "意图 3", "completed"));

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Intent> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("status", "pending");

        // Act
        List<Intent> intents = intentMapper.selectList(wrapper);

        // Assert
        assertEquals(2, intents.size());
        assertTrue(intents.stream().allMatch(i -> "pending".equals(i.getStatus())));
    }

    @Test
    @DisplayName("测试条件查询 - 按优先级排序")
    void testSelectOrderByPriority() {
        // Arrange
        intentMapper.insert(createIntentWithPriority("intent_001", "低优先级", 1));
        intentMapper.insert(createIntentWithPriority("intent_002", "中优先级", 3));
        intentMapper.insert(createIntentWithPriority("intent_003", "高优先级", 5));

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Intent> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.orderByDesc("priority");

        // Act
        List<Intent> intents = intentMapper.selectList(wrapper);

        // Assert
        assertEquals(3, intents.size());
        assertEquals(5, intents.get(0).getPriority());
        assertEquals(3, intents.get(1).getPriority());
        assertEquals(1, intents.get(2).getPriority());
    }

    @Test
    @DisplayName("测试条件查询 + 限制结果数")
    void testSelectWithLimit() {
        // Arrange
        for (int i = 1; i <= 25; i++) {
            intentMapper.insert(createIntent("intent_" + String.format("%03d", i), "意图" + i));
        }

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Intent> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.last("LIMIT 10");

        // Act
        List<Intent> intents = intentMapper.selectList(wrapper);

        // Assert
        assertEquals(10, intents.size());
    }

    @Test
    @DisplayName("测试模糊查询")
    void testSelectLike() {
        // Arrange
        intentMapper.insert(createIntent("intent_001", "用户管理意图"));
        intentMapper.insert(createIntent("intent_002", "订单管理意图"));
        intentMapper.insert(createIntent("intent_003", "用户登录意图"));

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Intent> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.like("name", "用户");

        // Act
        List<Intent> intents = intentMapper.selectList(wrapper);

        // Assert
        assertEquals(2, intents.size());
        assertTrue(intents.stream().allMatch(i -> i.getName().contains("用户")));
    }

    @Test
    @DisplayName("测试查询不存在的意图")
    void testSelectNonExistent() {
        // Act
        Intent result = intentMapper.selectById("non_existent");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("测试批量插入")
    void testBatchInsert() {
        // Arrange
        List<Intent> intents = List.of(
            createIntent("intent_001", "批量意图 1"),
            createIntent("intent_002", "批量意图 2"),
            createIntent("intent_003", "批量意图 3")
        );

        // Act
        for (Intent intent : intents) {
            intentMapper.insert(intent);
        }

        // Assert
        List<Intent> result = intentMapper.selectList(null);
        assertEquals(3, result.size());
    }

    /**
     * 辅助方法：创建测试意图
     */
    private Intent createIntent(String id, String name) {
        return Intent.builder()
            .id(id)
            .name(name)
            .description("这是一个测试意图")
            .type("task")
            .status("pending")
            .priority(1)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    private Intent createIntent(String id, String name, String status) {
        Intent intent = createIntent(id, name);
        intent.setStatus(status);
        return intent;
    }

    private Intent createIntentWithPriority(String id, String name, int priority) {
        Intent intent = createIntent(id, name);
        intent.setPriority(priority);
        return intent;
    }
}
