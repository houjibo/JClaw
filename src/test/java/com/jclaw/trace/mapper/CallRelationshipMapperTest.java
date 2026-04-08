package com.jclaw.trace.mapper;

import com.jclaw.BaseMapperTest;
import com.jclaw.trace.entity.CallRelationship;
import com.jclaw.trace.entity.CodeUnit;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CallRelationshipMapper 集成测试
 * 测试调用关系数据访问层的 CRUD 操作
 */
@DisplayName("CallRelationshipMapper 集成测试")
class CallRelationshipMapperTest extends BaseMapperTest {

    @Autowired
    private CallRelationshipMapper callRelationshipMapper;

    @Autowired
    private CodeUnitMapper codeUnitMapper;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        // 清理表
        callRelationshipMapper.delete(null);
        codeUnitMapper.delete(null);
    }

    @Test
    @DisplayName("测试插入调用关系")
    void testInsert() {
        // Arrange
        CodeUnit caller = createCodeUnit("caller_001", "CallerClass");
        CodeUnit callee = createCodeUnit("callee_001", "CalleeClass");
        codeUnitMapper.insert(caller);
        codeUnitMapper.insert(callee);

        CallRelationship relationship = CallRelationship.builder()
            .id("rel_001")
            .callerId("caller_001")
            .calleeId("callee_001")
            .callType("direct")
            .createdAt(Instant.now())
            .build();

        // Act
        int result = callRelationshipMapper.insert(relationship);

        // Assert
        assertEquals(1, result);
        assertNotNull(relationship.getId());
    }

    @Test
    @DisplayName("测试根据 ID 查询调用关系")
    void testSelectById() {
        // Arrange
        CodeUnit caller = createCodeUnit("caller_001", "CallerClass");
        CodeUnit callee = createCodeUnit("callee_001", "CalleeClass");
        codeUnitMapper.insert(caller);
        codeUnitMapper.insert(callee);

        CallRelationship relationship = createRelationship("rel_001", "caller_001", "callee_001");
        callRelationshipMapper.insert(relationship);

        // Act
        CallRelationship result = callRelationshipMapper.selectById("rel_001");

        // Assert
        assertNotNull(result);
        assertEquals("caller_001", result.getCallerId());
        assertEquals("callee_001", result.getCalleeId());
        assertEquals("direct", result.getCallType());
    }

    @Test
    @DisplayName("测试查询调用者的所有调用关系")
    void testSelectByCallerId() {
        // Arrange
        CodeUnit caller = createCodeUnit("caller_001", "CallerClass");
        CodeUnit caller2 = createCodeUnit("caller_002", "CallerClass2");
        CodeUnit callee1 = createCodeUnit("callee_001", "CalleeClass1");
        CodeUnit callee2 = createCodeUnit("callee_002", "CalleeClass2");
        codeUnitMapper.insert(caller);
        codeUnitMapper.insert(caller2);
        codeUnitMapper.insert(callee1);
        codeUnitMapper.insert(callee2);

        callRelationshipMapper.insert(createRelationship("rel_001", "caller_001", "callee_001"));
        callRelationshipMapper.insert(createRelationship("rel_002", "caller_001", "callee_002"));
        callRelationshipMapper.insert(createRelationship("rel_003", "caller_002", "callee_001"));

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CallRelationship> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("caller_id", "caller_001");

        // Act
        List<CallRelationship> relationships = callRelationshipMapper.selectList(wrapper);

        // Assert
        assertEquals(2, relationships.size());
        assertTrue(relationships.stream().allMatch(r -> "caller_001".equals(r.getCallerId())));
    }

    @Test
    @DisplayName("测试查询被调用者的所有调用关系")
    void testSelectByCalleeId() {
        // Arrange
        CodeUnit caller1 = createCodeUnit("caller_001", "CallerClass1");
        CodeUnit caller2 = createCodeUnit("caller_002", "CallerClass2");
        CodeUnit callee1 = createCodeUnit("callee_001", "CalleeClass1");
        CodeUnit callee2 = createCodeUnit("callee_002", "CalleeClass2");
        codeUnitMapper.insert(caller1);
        codeUnitMapper.insert(caller2);
        codeUnitMapper.insert(callee1);
        codeUnitMapper.insert(callee2);

        callRelationshipMapper.insert(createRelationship("rel_001", "caller_001", "callee_001"));
        callRelationshipMapper.insert(createRelationship("rel_002", "caller_002", "callee_001"));
        callRelationshipMapper.insert(createRelationship("rel_003", "caller_001", "callee_002"));

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CallRelationship> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("callee_id", "callee_001");

        // Act
        List<CallRelationship> relationships = callRelationshipMapper.selectList(wrapper);

        // Assert
        assertEquals(2, relationships.size());
        assertTrue(relationships.stream().allMatch(r -> "callee_001".equals(r.getCalleeId())));
    }

    @Test
    @DisplayName("测试更新调用关系")
    void testUpdate() {
        // Arrange
        CodeUnit caller = createCodeUnit("caller_001", "CallerClass");
        CodeUnit callee = createCodeUnit("callee_001", "CalleeClass");
        codeUnitMapper.insert(caller);
        codeUnitMapper.insert(callee);

        CallRelationship relationship = createRelationship("rel_001", "caller_001", "callee_001");
        callRelationshipMapper.insert(relationship);

        // Act
        relationship.setCallType("indirect");
        int result = callRelationshipMapper.updateById(relationship);

        // Assert
        assertEquals(1, result);
        
        CallRelationship updated = callRelationshipMapper.selectById("rel_001");
        assertNotNull(updated);
        assertEquals("indirect", updated.getCallType());
    }

    @Test
    @DisplayName("测试删除调用关系")
    void testDelete() {
        // Arrange
        CodeUnit caller = createCodeUnit("caller_001", "CallerClass");
        CodeUnit callee = createCodeUnit("callee_001", "CalleeClass");
        codeUnitMapper.insert(caller);
        codeUnitMapper.insert(callee);

        CallRelationship relationship = createRelationship("rel_001", "caller_001", "callee_001");
        callRelationshipMapper.insert(relationship);

        // Act
        int result = callRelationshipMapper.deleteById("rel_001");

        // Assert
        assertEquals(1, result);
        
        CallRelationship deleted = callRelationshipMapper.selectById("rel_001");
        assertNull(deleted);
    }

    @Test
    @DisplayName("测试查询调用关系列表")
    void testSelectList() {
        // Arrange
        CodeUnit caller = createCodeUnit("caller_001", "CallerClass");
        CodeUnit callee1 = createCodeUnit("callee_001", "CalleeClass1");
        CodeUnit callee2 = createCodeUnit("callee_002", "CalleeClass2");
        CodeUnit caller2 = createCodeUnit("caller_002", "CallerClass2");
        codeUnitMapper.insert(caller);
        codeUnitMapper.insert(caller2);
        codeUnitMapper.insert(callee1);
        codeUnitMapper.insert(callee2);

        callRelationshipMapper.insert(createRelationship("rel_001", "caller_001", "callee_001"));
        callRelationshipMapper.insert(createRelationship("rel_002", "caller_001", "callee_002"));
        callRelationshipMapper.insert(createRelationship("rel_003", "caller_002", "callee_001"));

        // Act
        List<CallRelationship> relationships = callRelationshipMapper.selectList(null);

        // Assert
        assertEquals(3, relationships.size());
    }

    @Test
    @DisplayName("测试按调用类型查询")
    void testSelectByCallType() {
        // Arrange
        CodeUnit caller = createCodeUnit("caller_001", "CallerClass");
        CodeUnit caller2 = createCodeUnit("caller_002", "CallerClass2");
        CodeUnit callee1 = createCodeUnit("callee_001", "CalleeClass1");
        CodeUnit callee2 = createCodeUnit("callee_002", "CalleeClass2");
        codeUnitMapper.insert(caller);
        codeUnitMapper.insert(caller2);
        codeUnitMapper.insert(callee1);
        codeUnitMapper.insert(callee2);

        callRelationshipMapper.insert(createRelationship("rel_001", "caller_001", "callee_001", "direct"));
        callRelationshipMapper.insert(createRelationship("rel_002", "caller_001", "callee_002", "indirect"));
        callRelationshipMapper.insert(createRelationship("rel_003", "caller_002", "callee_001", "direct"));

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CallRelationship> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("call_type", "direct");

        // Act
        List<CallRelationship> relationships = callRelationshipMapper.selectList(wrapper);

        // Assert
        assertEquals(2, relationships.size());
        assertTrue(relationships.stream().allMatch(r -> "direct".equals(r.getCallType())));
    }

    @Test
    @DisplayName("测试查询不存在的调用关系")
    void testSelectNonExistent() {
        // Act
        CallRelationship result = callRelationshipMapper.selectById("non_existent");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("测试限制结果数量")
    void testSelectWithLimit() {
        // Arrange
        CodeUnit caller = createCodeUnit("caller_001", "CallerClass");
        codeUnitMapper.insert(caller);
        for (int i = 1; i <= 25; i++) {
            CodeUnit callee = createCodeUnit("callee_" + String.format("%03d", i), "CalleeClass" + i);
            codeUnitMapper.insert(callee);
            callRelationshipMapper.insert(createRelationship("rel_" + String.format("%03d", i), 
                "caller_001", "callee_" + String.format("%03d", i)));
        }

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CallRelationship> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.last("LIMIT 10");

        // Act
        List<CallRelationship> relationships = callRelationshipMapper.selectList(wrapper);

        // Assert
        assertEquals(10, relationships.size());
    }

    @Test
    @DisplayName("测试查询调用链")
    void testCallChain() {
        // Arrange: A -> B -> C -> D
        CodeUnit a = createCodeUnit("class_a", "ClassA");
        CodeUnit b = createCodeUnit("class_b", "ClassB");
        CodeUnit c = createCodeUnit("class_c", "ClassC");
        CodeUnit d = createCodeUnit("class_d", "ClassD");
        codeUnitMapper.insert(a);
        codeUnitMapper.insert(b);
        codeUnitMapper.insert(c);
        codeUnitMapper.insert(d);

        callRelationshipMapper.insert(createRelationship("rel_1", "class_a", "class_b"));
        callRelationshipMapper.insert(createRelationship("rel_2", "class_b", "class_c"));
        callRelationshipMapper.insert(createRelationship("rel_3", "class_c", "class_d"));

        // Act: 查询从 A 开始的所有调用
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CallRelationship> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("caller_id", "class_a");

        List<CallRelationship> firstLevel = callRelationshipMapper.selectList(wrapper);

        // Assert
        assertEquals(1, firstLevel.size());
        assertEquals("class_b", firstLevel.get(0).getCalleeId());
    }

    /**
     * 辅助方法：创建测试代码单元
     */
    private CodeUnit createCodeUnit(String id, String unitName) {
        return CodeUnit.builder()
            .id(id)
            .unitName(unitName)
            .filePath("/src/" + unitName + ".java")
            .unitType("CLASS")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    /**
     * 辅助方法：创建测试调用关系
     */
    private CallRelationship createRelationship(String id, String callerId, String calleeId) {
        return createRelationship(id, callerId, calleeId, "direct");
    }

    private CallRelationship createRelationship(String id, String callerId, String calleeId, String callType) {
        return CallRelationship.builder()
            .id(id)
            .callerId(callerId)
            .calleeId(calleeId)
            .callType(callType)
            .createdAt(Instant.now())
            .build();
    }
}
