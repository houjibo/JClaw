package com.jclaw.intent.service;

import com.jclaw.intent.entity.Intent;
import com.jclaw.intent.mapper.IntentMapper;
import com.jclaw.intent.service.impl.IntentRecognitionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * IntentRecognitionService 单元测试
 */
class IntentRecognitionServiceTest {

    @Mock
    private IntentMapper intentMapper;

    @InjectMocks
    private IntentRecognitionServiceImpl intentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRecognize() {
        // 准备测试数据
        String userInput = "创建一个用户管理功能";
        
        when(intentMapper.insert(any(Intent.class))).thenReturn(1);
        
        // 执行测试
        Intent result = intentService.recognize(userInput);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("示例意图", result.getName());
        assertEquals("task", result.getType());
        verify(intentMapper, times(1)).insert(any(Intent.class));
    }

    @Test
    void testGenerateClarificationQuestions() {
        // 准备测试数据
        Intent intent = new Intent();
        intent.setId("intent-id");
        intent.setName("测试意图");
        intent.setDescription("测试描述");
        
        // 执行测试
        List<String> questions = intentService.generateClarificationQuestions(intent);
        
        // 验证结果
        assertNotNull(questions);
        assertEquals(3, questions.size());
        assertTrue(questions.get(0).contains("详细描述"));
    }

    @Test
    void testGetIntent() {
        // 准备测试数据
        Intent mockIntent = new Intent();
        mockIntent.setId("get-id");
        mockIntent.setName("获取的意图");
        
        when(intentMapper.selectById("get-id")).thenReturn(mockIntent);
        
        // 执行测试
        Intent result = intentService.getIntent("get-id");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("get-id", result.getId());
        verify(intentMapper, times(1)).selectById("get-id");
    }

    @Test
    void testCreateIntent() {
        // 准备测试数据
        Intent intent = new Intent();
        intent.setName("新意图");
        intent.setType("feature");
        
        when(intentMapper.insert(any(Intent.class))).thenReturn(1);
        
        // 执行测试
        Intent result = intentService.createIntent(intent);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("新意图", result.getName());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(intentMapper, times(1)).insert(any(Intent.class));
    }

    @Test
    void testUpdateIntent() {
        // 准备测试数据
        Intent intent = new Intent();
        intent.setId("update-id");
        intent.setName("更新后的意图");
        
        when(intentMapper.updateById(any(Intent.class))).thenReturn(1);
        
        // 执行测试
        intentService.updateIntent(intent);
        
        // 验证
        verify(intentMapper, times(1)).updateById(any(Intent.class));
    }

    @Test
    void testDeleteIntent() {
        // 执行测试
        intentService.deleteIntent("delete-id");
        
        // 验证
        verify(intentMapper, times(1)).deleteById("delete-id");
    }

    @Test
    void testRecognize_withDifferentInputTypes() {
        // 测试不同类型的用户输入
        String[] inputs = {
            "修复登录 bug",
            "查询用户列表",
            "开发新的 API 接口",
            "部署到生产环境"
        };
        
        when(intentMapper.insert(any(Intent.class))).thenReturn(1);
        
        for (String input : inputs) {
            Intent result = intentService.recognize(input);
            assertNotNull(result);
            assertEquals(input, result.getDescription());
        }
        
        verify(intentMapper, times(4)).insert(any(Intent.class));
    }
}
