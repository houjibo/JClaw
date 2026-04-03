package com.jclaw.memory.service.impl;

import com.jclaw.memory.entity.Knowledge;
import com.jclaw.memory.mapper.KnowledgeMapper;
import com.jclaw.memory.service.KnowledgeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 知识服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class KnowledgeServiceTest {

    @Mock
    private KnowledgeMapper knowledgeMapper;

    @InjectMocks
    private KnowledgeServiceImpl knowledgeService;

    @Test
    void testCreateKnowledge() {
        Knowledge mockKnowledge = Knowledge.builder()
            .title("测试知识")
            .category("test")
            .content("测试内容")
            .build();
        
        when(knowledgeMapper.insert(any(Knowledge.class))).thenReturn(1);

        Knowledge result = knowledgeService.createKnowledge(mockKnowledge);

        assertNotNull(result);
        assertEquals("测试知识", result.getTitle());
        verify(knowledgeMapper, times(1)).insert(any(Knowledge.class));
    }

    @Test
    void testListKnowledge() {
        when(knowledgeMapper.selectList(any())).thenReturn(List.of());

        List<Knowledge> result = knowledgeService.listKnowledge(1, 20);

        assertNotNull(result);
        verify(knowledgeMapper, times(1)).selectList(any());
    }

    @Test
    void testDeleteKnowledge() {
        when(knowledgeMapper.deleteById("know_001")).thenReturn(1);

        knowledgeService.deleteKnowledge("know_001");

        verify(knowledgeMapper, times(1)).deleteById("know_001");
    }
}
