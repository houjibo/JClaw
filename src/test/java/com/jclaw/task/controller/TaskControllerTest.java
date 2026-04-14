package com.jclaw.task.controller;

import com.jclaw.task.dto.TaskDTO;
import com.jclaw.task.dto.TaskUpdateRequest;
import com.jclaw.task.service.TaskService;
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

class TaskControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private TaskService taskService;
    
    @InjectMocks
    private TaskController taskController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }
    
    @Test
    void testCreateTask() throws Exception {
        TaskDTO dto = new TaskDTO();
        dto.setId("1");
        dto.setTitle("test task");
        
        when(taskService.createTask(any(TaskDTO.class))).thenReturn(dto);
        
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"test task\"}"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testListTasks() throws Exception {
        when(taskService.listTasks(null, null)).thenReturn(new ArrayList<>());
        
        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testGetTask() throws Exception {
        TaskDTO dto = new TaskDTO();
        dto.setId("1");
        dto.setTitle("test");
        
        when(taskService.getTask("1")).thenReturn(dto);
        
        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testGetNonExistentTask() throws Exception {
        when(taskService.getTask("999")).thenThrow(new IllegalArgumentException("Not found"));
        
        mockMvc.perform(get("/api/tasks/999"))
            .andExpect(status().isNotFound());
    }
    
    @Test
    void testUpdateTask() throws Exception {
        TaskDTO dto = new TaskDTO();
        dto.setId("1");
        
        when(taskService.updateTask(eq("1"), any(TaskUpdateRequest.class))).thenReturn(dto);
        
        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"updated\"}"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testStopTask() throws Exception {
        doNothing().when(taskService).stopTask("1");
        
        mockMvc.perform(post("/api/tasks/1/stop"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTask("1");
        
        mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isOk());
    }
}
