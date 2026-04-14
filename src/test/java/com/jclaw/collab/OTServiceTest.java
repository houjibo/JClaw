package com.jclaw.collab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OT 服务测试
 */
@DisplayName("OT 服务测试")
class OTServiceTest {
    
    private OTService otService;
    
    @BeforeEach
    void setUp() {
        otService = new OTService();
    }
    
    @Test
    @DisplayName("测试初始化文档")
    void testInitDocument() {
        OTService.DocumentState state = otService.initDocument("doc-1", "Hello World");
        
        assertNotNull(state);
        assertEquals("doc-1", state.getDocumentId());
        assertEquals("Hello World", state.getContent());
        assertEquals(0, state.getVersion());
    }
    
    @Test
    @DisplayName("测试插入操作")
    void testInsertOperation() {
        otService.initDocument("doc-1", "Hello");
        
        OTService.Operation operation = new OTService.Operation();
        operation.setId("op-1");
        operation.setType(OTService.OperationType.INSERT);
        operation.setPosition(5);
        operation.setContent(" World");
        operation.setVersion(0);
        operation.setUserId("user-1");
        operation.setTimestamp(System.currentTimeMillis());
        
        OTService.DocumentState state = otService.applyOperation("doc-1", operation);
        
        assertEquals("Hello World", state.getContent());
        assertEquals(1, state.getVersion());
    }
    
    @Test
    @DisplayName("测试删除操作")
    void testDeleteOperation() {
        otService.initDocument("doc-1", "Hello World");
        
        OTService.Operation operation = new OTService.Operation();
        operation.setId("op-1");
        operation.setType(OTService.OperationType.DELETE);
        operation.setPosition(5);
        operation.setLength(6);
        operation.setVersion(0);
        operation.setUserId("user-1");
        operation.setTimestamp(System.currentTimeMillis());
        
        OTService.DocumentState state = otService.applyOperation("doc-1", operation);
        
        assertEquals("Hello", state.getContent());
        assertEquals(1, state.getVersion());
    }
    
    @Test
    @DisplayName("测试更新操作")
    void testUpdateOperation() {
        otService.initDocument("doc-1", "Hello World");
        
        OTService.Operation operation = new OTService.Operation();
        operation.setId("op-1");
        operation.setType(OTService.OperationType.UPDATE);
        operation.setPosition(6);
        operation.setLength(5);
        operation.setContent("Java");
        operation.setVersion(0);
        operation.setUserId("user-1");
        operation.setTimestamp(System.currentTimeMillis());
        
        OTService.DocumentState state = otService.applyOperation("doc-1", operation);
        
        assertEquals("Hello Java", state.getContent());
        assertEquals(1, state.getVersion());
    }
    
    @Test
    @DisplayName("测试并发操作转换")
    void testConcurrentOperations() {
        otService.initDocument("doc-1", "Hello");
        
        // 用户 1 插入
        OTService.Operation op1 = new OTService.Operation();
        op1.setId("op-1");
        op1.setType(OTService.OperationType.INSERT);
        op1.setPosition(5);
        op1.setContent(" World");
        op1.setVersion(0);
        op1.setUserId("user-1");
        op1.setTimestamp(System.currentTimeMillis());
        
        otService.applyOperation("doc-1", op1);
        
        // 用户 2 插入（基于旧版本）
        OTService.Operation op2 = new OTService.Operation();
        op2.setId("op-2");
        op2.setType(OTService.OperationType.INSERT);
        op2.setPosition(5);
        op2.setContent("!");
        op2.setVersion(0); // 基于旧版本
        op2.setUserId("user-2");
        op2.setTimestamp(System.currentTimeMillis());
        
        OTService.DocumentState state = otService.applyOperation("doc-1", op2);
        
        // 应该正确转换
        assertNotNull(state);
        assertTrue(state.getContent().contains("Hello"));
    }
    
    @Test
    @DisplayName("测试获取文档状态")
    void testGetDocumentState() {
        otService.initDocument("doc-1", "Test");
        
        OTService.DocumentState state = otService.getDocumentState("doc-1");
        
        assertNotNull(state);
        assertEquals("Test", state.getContent());
    }
    
    @Test
    @DisplayName("测试获取不存在的文档")
    void testGetNonExistentDocument() {
        OTService.DocumentState state = otService.getDocumentState("non-existent");
        
        assertNull(state);
    }
    
    @Test
    @DisplayName("测试获取操作历史")
    void testGetOperationHistory() {
        otService.initDocument("doc-1", "Hello");
        
        // 应用几个操作
        OTService.Operation op1 = new OTService.Operation();
        op1.setId("op-1");
        op1.setType(OTService.OperationType.INSERT);
        op1.setPosition(5);
        op1.setContent(" World");
        op1.setVersion(0);
        op1.setUserId("user-1");
        op1.setTimestamp(System.currentTimeMillis());
        
        otService.applyOperation("doc-1", op1);
        
        OTService.Operation op2 = new OTService.Operation();
        op2.setId("op-2");
        op2.setType(OTService.OperationType.INSERT);
        op2.setPosition(11);
        op2.setContent("!");
        op2.setVersion(1);
        op2.setUserId("user-2");
        op2.setTimestamp(System.currentTimeMillis());
        
        otService.applyOperation("doc-1", op2);
        
        // 获取历史
        List<OTService.Operation> history = otService.getOperationHistory("doc-1", 1);
        
        assertNotNull(history);
        assertEquals(1, history.size()); // 只返回 version >= 1 的操作
    }
    
    @Test
    @DisplayName("测试应用操作到不存在的文档")
    void testApplyOperationToNonExistentDocument() {
        OTService.Operation operation = new OTService.Operation();
        operation.setId("op-1");
        operation.setType(OTService.OperationType.INSERT);
        
        assertThrows(IllegalArgumentException.class, () -> {
            otService.applyOperation("non-existent", operation);
        });
    }
}
