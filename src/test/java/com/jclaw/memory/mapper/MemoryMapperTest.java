package com.jclaw.memory.mapper;

import com.jclaw.BaseMapperTest;
import com.jclaw.memory.entity.Memory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

/**
 * MemoryMapper 集成测试
 * 测试记忆数据访问层的 CRUD 操作
 */
@DisplayName("MemoryMapper 集成测试")
class MemoryMapperTest extends BaseMapperTest {

    @Autowired
    private MemoryMapper memoryMapper;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        // 清理 memory 表
        memoryMapper.delete(null);
    }

    @Test
    @DisplayName("测试插入记忆")
    void testInsert() {
        // Arrange
        Memory memory = createMemory("mem_001", "long_term", "测试记忆", "{\"key\":\"value\"}");

        // Act
        int result = memoryMapper.insert(memory);

        // Assert
        assertEquals(1, result);
        assertNotNull(memory.getId());
    }

    @Test
    @DisplayName("测试根据 ID 查询记忆")
    void testSelectById() {
        // Arrange
        Memory memory = createMemory("mem_001", "long_term", "测试记忆", "{\"key\":\"value\"}");
        memoryMapper.insert(memory);

        // Act
        Memory result = memoryMapper.selectById("mem_001");

        // Assert
        assertNotNull(result);
        assertEquals("测试记忆", result.getTitle());
        assertEquals("long_term", result.getType());
    }

    @Test
    @DisplayName("测试根据类型查询记忆")
    void testSelectByType() {
        // Arrange
        memoryMapper.insert(createMemory("mem_001", "long_term", "长期记忆 1", "{}"));
        memoryMapper.insert(createMemory("mem_002", "long_term", "长期记忆 2", "{}"));
        memoryMapper.insert(createMemory("mem_003", "daily_log", "日志 1", "{}"));

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Memory> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("type", "long_term");

        // Act
        List<Memory> memories = memoryMapper.selectList(wrapper);

        // Assert
        assertEquals(2, memories.size());
        assertTrue(memories.stream().allMatch(m -> "long_term".equals(m.getType())));
    }

    @Test
    @DisplayName("测试更新记忆")
    void testUpdate() {
        // Arrange
        Memory memory = createMemory("mem_001", "long_term", "测试记忆", "{\"key\":\"value\"}");
        memoryMapper.insert(memory);

        // Act
        memory.setTitle("更新后的标题");
        memory.setContent("{\"updated_key\":\"updated_value\"}");
        int result = memoryMapper.updateById(memory);

        // Assert
        assertEquals(1, result);
        
        Memory updated = memoryMapper.selectById("mem_001");
        assertNotNull(updated);
        assertEquals("更新后的标题", updated.getTitle());
    }

    @Test
    @DisplayName("测试删除记忆")
    void testDelete() {
        // Arrange
        Memory memory = createMemory("mem_001", "long_term", "测试记忆", "{}");
        memoryMapper.insert(memory);

        // Act
        int result = memoryMapper.deleteById("mem_001");

        // Assert
        assertEquals(1, result);
        
        Memory deleted = memoryMapper.selectById("mem_001");
        assertNull(deleted);
    }

    @Test
    @DisplayName("测试查询记忆列表")
    void testSelectList() {
        // Arrange
        memoryMapper.insert(createMemory("mem_001", "long_term", "记忆 1", "{}"));
        memoryMapper.insert(createMemory("mem_002", "daily_log", "记忆 2", "{}"));
        memoryMapper.insert(createMemory("mem_003", "knowledge", "记忆 3", "{}"));

        // Act
        List<Memory> memories = memoryMapper.selectList(null);

        // Assert
        assertEquals(3, memories.size());
    }

    @Test
    @DisplayName("测试按标题模糊查询")
    void testSelectByTitleLike() {
        // Arrange
        memoryMapper.insert(createMemory("mem_001", "long_term", "用户管理记忆", "{}"));
        memoryMapper.insert(createMemory("mem_002", "long_term", "订单管理记忆", "{}"));
        memoryMapper.insert(createMemory("mem_003", "daily_log", "用户登录记忆", "{}"));

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Memory> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.like("title", "用户");

        // Act
        List<Memory> memories = memoryMapper.selectList(wrapper);

        // Assert
        assertEquals(2, memories.size());
        assertTrue(memories.stream().allMatch(m -> m.getTitle().contains("用户")));
    }

    @Test
    @DisplayName("测试按创建时间排序")
    void testSelectOrderByCreatedAt() {
        // Arrange
        memoryMapper.insert(createMemoryWithTime("mem_001", "记忆 1", Instant.now().minusSeconds(300)));
        memoryMapper.insert(createMemoryWithTime("mem_002", "记忆 2", Instant.now().minusSeconds(200)));
        memoryMapper.insert(createMemoryWithTime("mem_003", "记忆 3", Instant.now().minusSeconds(100)));

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Memory> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.orderByDesc("created_at");

        // Act
        List<Memory> memories = memoryMapper.selectList(wrapper);

        // Assert
        assertEquals(3, memories.size());
        assertEquals("记忆 3", memories.get(0).getTitle());  // 最新的在前面
        assertEquals("记忆 1", memories.get(2).getTitle());  // 最旧的在后面
    }

    @Test
    @DisplayName("测试查询不存在的记忆")
    void testSelectNonExistent() {
        // Act
        Memory result = memoryMapper.selectById("non_existent");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("测试限制结果数量")
    void testSelectWithLimit() {
        // Arrange
        for (int i = 1; i <= 25; i++) {
            memoryMapper.insert(createMemory("mem_" + String.format("%03d", i), 
                "long_term", "记忆" + i, "{}"));
        }

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Memory> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.last("LIMIT 10");

        // Act
        List<Memory> memories = memoryMapper.selectList(wrapper);

        // Assert
        assertEquals(10, memories.size());
    }

    @Test
    @DisplayName("测试批量插入记忆")
    void testBatchInsert() {
        // Arrange
        List<Memory> memories = List.of(
            createMemory("mem_001", "long_term", "批量记忆 1", "{}"),
            createMemory("mem_002", "long_term", "批量记忆 2", "{}"),
            createMemory("mem_003", "long_term", "批量记忆 3", "{}")
        );

        // Act
        for (Memory memory : memories) {
            memoryMapper.insert(memory);
        }

        // Assert
        List<Memory> result = memoryMapper.selectList(null);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("测试查询特定类型的记忆数量")
    void testCountByType() {
        // Arrange
        memoryMapper.insert(createMemory("mem_001", "long_term", "记忆 1", "{}"));
        memoryMapper.insert(createMemory("mem_002", "long_term", "记忆 2", "{}"));
        memoryMapper.insert(createMemory("mem_003", "daily_log", "记忆 3", "{}"));
        memoryMapper.insert(createMemory("mem_004", "daily_log", "记忆 4", "{}"));
        memoryMapper.insert(createMemory("mem_005", "daily_log", "记忆 5", "{}"));

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Memory> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("type", "daily_log");

        // Act
        Long count = memoryMapper.selectCount(wrapper);

        // Assert
        assertEquals(3, count);
    }

    /**
     * 辅助方法：创建测试记忆
     */
    private Memory createMemory(String id, String type, String title, String content) {
        return Memory.builder()
            .id(id)
            .type(type)
            .title(title)
            .content(content)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    private Memory createMemoryWithTime(String id, String title, Instant createdAt) {
        return Memory.builder()
            .id(id)
            .type("long_term")
            .title(title)
            .content("{}")
            .createdAt(createdAt)
            .updatedAt(createdAt)
            .build();
    }
}
