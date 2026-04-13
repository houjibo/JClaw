package com.jclaw.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jclaw.task.dto.TaskDTO;
import com.jclaw.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 任务管理控制器测试
 * 
 * @author JClaw
 * @since 2026-04-13
 */
@WebMvcTest(TaskController.class)
@DisplayName("任务管理 API 测试")
class TaskControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private TaskService taskService;
    
    private TaskDTO testTask;
    
    @BeforeEach
    void setUp() {
        testTask = TaskDTO.builder()
            .id("task-001")
            .title("测试任务")
            .description("这是一个测试任务")
            .status("pending")
            .assignee("developer")
            .priority("medium")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    @Test
    @DisplayName("创建任务")
    void testCreateTask() throws Exception {
        when(taskService.createTask(any(TaskDTO.class))).thenReturn(testTask);
        
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("task-001"))
            .andExpect(jsonPath("$.title").value("测试任务"));
    }
    
    @Test
    @DisplayName("获取任务详情")
    void testGetTask() throws Exception {
        when(taskService.getTask("task-001")).thenReturn(testTask);
        
        mockMvc.perform(get("/api/tasks/task-001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("task-001"))
            .andExpect(jsonPath("$.title").value("测试任务"));
    }
    
    @Test
    @DisplayName("获取任务列表")
    void testListTasks() throws Exception {
        List<TaskDTO> tasks = Arrays.asList(testTask);
        when(taskService.listTasks(null, null)).thenReturn(tasks);
        
        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("task-001"));
    }
    
    @Test
    @DisplayName("更新任务")
    void testUpdateTask() throws Exception {
        TaskDTO updatedTask = TaskDTO.builder()
            .id("task-001")
            .title("更新后的任务")
            .description("描述已更新")
            .status("running")
            .build();
        
        when(taskService.updateTask(eq("task-001"), any())).thenReturn(updatedTask);
        
        mockMvc.perform(put("/api/tasks/task-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTask)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("更新后的任务"));
    }
    
    @Test
    @DisplayName("停止任务")
    void testStopTask() throws Exception {
        doNothing().when(taskService).stopTask("task-001");
        
        mockMvc.perform(post("/api/tasks/task-001/stop"))
            .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("删除任务")
    void testDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTask("task-001");
        
        mockMvc.perform(delete("/api/tasks/task-001"))
            .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("获取不存在的任务")
    void testGetNonExistentTask() throws Exception {
        when(taskService.getTask("non-existent")).thenThrow(new IllegalArgumentException("任务不存在"));
        
        mockMvc.perform(get("/api/tasks/non-existent"))
            .andExpect(status().isNotFound());
    }
}
