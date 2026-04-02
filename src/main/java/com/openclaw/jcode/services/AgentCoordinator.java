package com.openclaw.jcode.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

/**
 * 多 Agent 协调器服务
 * 
 * 功能：
 * - Agent 创建和管理
 * - 任务分配
 * - 结果聚合
 * - Agent 间通信
 */
@Service
public class AgentCoordinator {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentCoordinator.class);
    
    /**
     * Agent 实例
     */
    public static class AgentInstance {
        public String id;
        public String role;
        public String model;
        public String status; // idle, busy, offline
        public String currentTask;
        public long createdAt;
        public long lastActiveAt;
        public int completedTasks;
        
        public AgentInstance(String id, String role, String model) {
            this.id = id;
            this.role = role;
            this.model = model;
            this.status = "idle";
            this.createdAt = System.currentTimeMillis();
            this.lastActiveAt = System.currentTimeMillis();
            this.completedTasks = 0;
        }
    }
    
    /**
     * 任务实例
     */
    public static class TaskInstance {
        public String id;
        public String description;
        public String assignedTo;
        public String status; // pending, running, completed, failed
        public String result;
        public long createdAt;
        public long startedAt;
        public long completedAt;
        
        public TaskInstance(String id, String description) {
            this.id = id;
            this.description = description;
            this.status = "pending";
            this.createdAt = System.currentTimeMillis();
        }
    }
    
    /**
     * Agent 实例池
     */
    private final ConcurrentHashMap<String, AgentInstance> agents = new ConcurrentHashMap<>();
    
    /**
     * 任务队列
     */
    private final ConcurrentHashMap<String, TaskInstance> tasks = new ConcurrentHashMap<>();
    
    /**
     * 任务结果存储
     */
    private final ConcurrentHashMap<String, String> taskResults = new ConcurrentHashMap<>();
    
    /**
     * 消息队列（Agent 间通信）
     */
    private final BlockingQueue<Map<String, Object>> messageQueue = new LinkedBlockingQueue<>();
    
    /**
     * 创建 Agent
     */
    public AgentInstance createAgent(String role, String model) {
        String agentId = "agent-" + UUID.randomUUID().toString().substring(0, 8);
        logger.info("创建 Agent：{} (role={}, model={})", agentId, role, model);
        
        AgentInstance agent = new AgentInstance(agentId, role, model);
        agents.put(agentId, agent);
        
        return agent;
    }
    
    /**
     * 获取 Agent 列表
     */
    public List<Map<String, Object>> listAgents() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AgentInstance agent : agents.values()) {
            Map<String, Object> info = new HashMap<>();
            info.put("id", agent.id);
            info.put("role", agent.role);
            info.put("model", agent.model);
            info.put("status", agent.status);
            info.put("currentTask", agent.currentTask);
            info.put("completedTasks", agent.completedTasks);
            info.put("createdAt", agent.createdAt);
            info.put("lastActiveAt", agent.lastActiveAt);
            list.add(info);
        }
        return list;
    }
    
    /**
     * 删除 Agent
     */
    public boolean deleteAgent(String agentId) {
        AgentInstance agent = agents.remove(agentId);
        if (agent != null) {
            logger.info("删除 Agent：{}", agentId);
            return true;
        }
        return false;
    }
    
    /**
     * 创建任务
     */
    public TaskInstance createTask(String description) {
        String taskId = "task-" + UUID.randomUUID().toString().substring(0, 8);
        logger.info("创建任务：{} - {}", taskId, description);
        
        TaskInstance task = new TaskInstance(taskId, description);
        tasks.put(taskId, task);
        
        return task;
    }
    
    /**
     * 分配任务给指定 Agent
     */
    public boolean assignTask(String taskId, String agentId) {
        TaskInstance task = tasks.get(taskId);
        AgentInstance agent = agents.get(agentId);
        
        if (task == null) {
            logger.warn("任务不存在：{}", taskId);
            return false;
        }
        
        if (agent == null) {
            logger.warn("Agent 不存在：{}", agentId);
            return false;
        }
        
        if (!"idle".equals(agent.status)) {
            logger.warn("Agent 正忙：{} (status={})", agentId, agent.status);
            return false;
        }
        
        logger.info("分配任务：{} -> {}", taskId, agentId);
        
        task.assignedTo = agentId;
        task.status = "running";
        task.startedAt = System.currentTimeMillis();
        
        agent.status = "busy";
        agent.currentTask = taskId;
        agent.lastActiveAt = System.currentTimeMillis();
        
        // 异步执行任务（模拟）
        executeTaskAsync(taskId, agentId);
        
        return true;
    }
    
    /**
     * 自动分配任务（选择空闲 Agent）
     */
    public String autoAssignTask(String taskId, String requiredRole) {
        // 查找符合角色的空闲 Agent
        for (AgentInstance agent : agents.values()) {
            if ("idle".equals(agent.status) && 
                (requiredRole == null || requiredRole.equals(agent.role))) {
                
                if (assignTask(taskId, agent.id)) {
                    return agent.id;
                }
            }
        }
        
        // 如果没有符合的 Agent，创建一个新的
        if (requiredRole != null) {
            AgentInstance newAgent = createAgent(requiredRole, "default");
            if (assignTask(taskId, newAgent.id)) {
                return newAgent.id;
            }
        }
        
        return null;
    }
    
    /**
     * 完成任务
     */
    public void completeTask(String taskId, String result) {
        TaskInstance task = tasks.get(taskId);
        if (task != null) {
            task.status = "completed";
            task.completedAt = System.currentTimeMillis();
            task.result = result;
            taskResults.put(taskId, result);
            
            // 更新 Agent 状态
            if (task.assignedTo != null) {
                AgentInstance agent = agents.get(task.assignedTo);
                if (agent != null) {
                    agent.status = "idle";
                    agent.currentTask = null;
                    agent.completedTasks++;
                    agent.lastActiveAt = System.currentTimeMillis();
                }
            }
            
            logger.info("任务完成：{} - {}", taskId, result != null ? result.substring(0, Math.min(50, result.length())) : "null");
        }
    }
    
    /**
     * 失败任务
     */
    public void failTask(String taskId, String error) {
        TaskInstance task = tasks.get(taskId);
        if (task != null) {
            task.status = "failed";
            task.completedAt = System.currentTimeMillis();
            task.result = error;
            
            // 更新 Agent 状态
            if (task.assignedTo != null) {
                AgentInstance agent = agents.get(task.assignedTo);
                if (agent != null) {
                    agent.status = "idle";
                    agent.currentTask = null;
                    agent.lastActiveAt = System.currentTimeMillis();
                }
            }
            
            logger.error("任务失败：{} - {}", taskId, error);
        }
    }
    
    /**
     * 获取任务状态
     */
    public Map<String, Object> getTaskStatus(String taskId) {
        TaskInstance task = tasks.get(taskId);
        if (task == null) {
            return null;
        }
        
        Map<String, Object> status = new HashMap<>();
        status.put("id", task.id);
        status.put("description", task.description);
        status.put("assignedTo", task.assignedTo);
        status.put("status", task.status);
        status.put("result", task.result);
        status.put("createdAt", task.createdAt);
        status.put("startedAt", task.startedAt);
        status.put("completedAt", task.completedAt);
        
        return status;
    }
    
    /**
     * 获取任务结果
     */
    public String getTaskResult(String taskId) {
        return taskResults.get(taskId);
    }
    
    /**
     * 发送 Agent 间消息
     */
    public void sendMessage(String fromAgent, String toAgent, String message) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("from", fromAgent);
        msg.put("to", toAgent);
        msg.put("message", message);
        msg.put("timestamp", System.currentTimeMillis());
        
        try {
            messageQueue.offer(msg);
            logger.debug("发送 Agent 消息：{} -> {}", fromAgent, toAgent);
        } catch (Exception e) {
            logger.error("发送 Agent 消息失败", e);
        }
    }
    
    /**
     * 接收消息（非阻塞）
     */
    public Map<String, Object> receiveMessage() {
        return messageQueue.poll();
    }
    
    /**
     * 获取统计信息
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        int totalAgents = agents.size();
        int idleAgents = (int) agents.values().stream().filter(a -> "idle".equals(a.status)).count();
        int busyAgents = (int) agents.values().stream().filter(a -> "busy".equals(a.status)).count();
        
        int totalTasks = tasks.size();
        int pendingTasks = (int) tasks.values().stream().filter(t -> "pending".equals(t.status)).count();
        int runningTasks = (int) tasks.values().stream().filter(t -> "running".equals(t.status)).count();
        int completedTasks = (int) tasks.values().stream().filter(t -> "completed".equals(t.status)).count();
        int failedTasks = (int) tasks.values().stream().filter(t -> "failed".equals(t.status)).count();
        
        stats.put("totalAgents", totalAgents);
        stats.put("idleAgents", idleAgents);
        stats.put("busyAgents", busyAgents);
        stats.put("totalTasks", totalTasks);
        stats.put("pendingTasks", pendingTasks);
        stats.put("runningTasks", runningTasks);
        stats.put("completedTasks", completedTasks);
        stats.put("failedTasks", failedTasks);
        
        return stats;
    }
    
    /**
     * 异步执行任务（模拟）
     */
    private void executeTaskAsync(String taskId, String agentId) {
        CompletableFuture.runAsync(() -> {
            try {
                // 模拟任务执行（实际应该调用 Agent API）
                Thread.sleep(1000 + (int)(Math.random() * 2000));
                
                // 模拟完成
                String result = "Task " + taskId + " completed by " + agentId;
                completeTask(taskId, result);
                
            } catch (Exception e) {
                failTask(taskId, "执行失败：" + e.getMessage());
            }
        });
    }
}
