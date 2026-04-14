package com.jclaw.agent.controller;

import com.jclaw.agent.dto.AgentDTO;
import com.jclaw.agent.service.AgentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

class AgentControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private AgentService agentService;
    
    @InjectMocks
    private AgentController agentController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(agentController).build();
    }
    
    @Test
    void testCreateAgent() throws Exception {
        AgentDTO dto = new AgentDTO();
        dto.setId("agent-1");
        dto.setRole("developer");
        dto.setModel("qwen3.5-plus");
        
        when(agentService.createAgent(any(AgentDTO.class))).thenReturn(dto);
        
        mockMvc.perform(post("/api/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"role\":\"developer\",\"model\":\"qwen3.5-plus\"}"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testListAgents() throws Exception {
        when(agentService.listAgents()).thenReturn(new ArrayList<>());
        
        mockMvc.perform(get("/api/agents"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testSendMessage() throws Exception {
        doNothing().when(agentService).sendMessage("agent-001", "test");
        
        mockMvc.perform(post("/api/agents/agent-001/message")
                .param("content", "test"))
            .andExpect(status().isOk());
    }
}
