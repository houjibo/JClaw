package com.jclaw.agent.controller;

import com.jclaw.agent.service.AgentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgentController.class)
class AgentControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AgentService agentService;
    
    @Test
    void testCreateAgent() throws Exception {
        mockMvc.perform(post("/api/agents"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testListAgents() throws Exception {
        mockMvc.perform(get("/api/agents"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testSendMessage() throws Exception {
        mockMvc.perform(post("/api/agents/agent-001/message")
                .param("content", "test"))
            .andExpect(status().isOk());
    }
}
