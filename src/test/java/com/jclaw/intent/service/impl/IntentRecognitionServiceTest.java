package com.jclaw.intent.service.impl;

import com.jclaw.intent.entity.Intent;
import com.jclaw.intent.mapper.IntentMapper;
import com.jclaw.intent.service.IntentRecognitionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 意图识别服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class IntentRecognitionServiceTest {

    @Mock
    private IntentMapper intentMapper;

    @InjectMocks
    private IntentRecognitionServiceImpl intentService;

    @Test
    void testRecognizeIntent() {
        // Arrange
        String userInput = "创建一个用户管理系统";
        when(intentMapper.insert(any(Intent.class))).thenReturn(1);

        // Act
        Intent result = intentService.recognize(userInput);

        // Assert
        assertNotNull(result);
        assertEquals("示例意图", result.getName());
        verify(intentMapper, times(1)).insert(any(Intent.class));
    }

    @Test
    void testGenerateClarificationQuestions() {
        // Arrange
        Intent intent = Intent.builder()
            .id("intent_001")
            .name("测试意图")
            .build();

        // Act
        List<String> questions = intentService.generateClarificationQuestions(intent);

        // Assert
        assertNotNull(questions);
        assertEquals(3, questions.size());
        assertTrue(questions.get(0).contains("详细描述"));
    }

    @Test
    void testCreateIntent() {
        // Arrange
        Intent mockIntent = Intent.builder()
            .id("intent_001")
            .name("新意图")
            .type("task")
            .build();
        when(intentMapper.insert(any(Intent.class))).thenReturn(1);

        // Act
        Intent result = intentService.createIntent(mockIntent);

        // Assert
        assertNotNull(result);
        assertEquals("新意图", result.getName());
        verify(intentMapper, times(1)).insert(any(Intent.class));
    }
}
