package com.jclaw.task.service;

import com.jclaw.task.dto.TaskDTO;
import com.jclaw.task.dto.TaskUpdateRequest;

import java.util.List;

/**
 * 任务管理服务接口
 * 
 * @author JClaw
 * @since 2026-04-13
 */
public interface TaskService {
    
    /**
     * 创建任务
     */
    TaskDTO createTask(TaskDTO task);
    
    /**
     * 获取任务详情
     * 
     * @param taskId 任务 ID
     * @return 任务详情
     */
    TaskDTO getTask(String taskId);
    
    /**
     * 获取任务列表
     * 
     * @param status 状态过滤（可选）
     * @param assignee 负责人（可选）
     * @return 任务列表
     */
    List<TaskDTO> listTasks(String status, String assignee);
    
    /**
     * 更新任务
     * 
     * @param taskId 任务 ID
     * @param request 更新请求
     * @return 更新后的任务
     */
    TaskDTO updateTask(String taskId, TaskUpdateRequest request);
    
    /**
     * 停止任务（优雅终止）
     * 
     * @param taskId 任务 ID
     */
    void stopTask(String taskId);
    
    /**
     * 删除任务
     * 
     * @param taskId 任务 ID
     */
    void deleteTask(String taskId);
}
