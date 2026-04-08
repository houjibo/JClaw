package com.jclaw.intent.controller;

import com.jclaw.common.entity.Result;
import com.jclaw.intent.entity.Intent;
import com.jclaw.intent.mapper.IntentMapper;
import com.jclaw.intent.service.IntentRecognitionService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 意图控制器集成测试
 * TODO: 需要配置完整的 Spring 上下文和数据库环境
 */
@Disabled("需要集成测试环境")
@WebMvcTest(IntentController.class)
class IntentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IntentRecognitionService intentService;

    @MockBean
    private IntentMapper intentMapper;

    @Test
    void testRecognizeIntent() throws Exception {
        Intent mockIntent = Intent.builder()
            .id("intent_001")
            .name("测试意图")
            .build();
        
        when(intentService.recognize(any())).thenReturn(mockIntent);

        mockMvc.perform(post("/api/intents/recognize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"input\": \"test\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value("intent_001"));
    }

    @Test
    void testListIntents() throws Exception {
        when(intentService.listIntents(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/intents")
                .param("page", "1")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray());
    }
}
