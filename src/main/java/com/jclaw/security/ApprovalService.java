package com.jclaw.security;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 审批服务
 */
@Slf4j
@Service
public class ApprovalService {
    
    // 内存存储审批请求
    private final Map<String, ApprovalRequest> requests = new ConcurrentHashMap<>();
    
    /**
     * 创建审批请求
     */
    public ApprovalRequest createRequest(String userId, String action, Map<String, Object> params) {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        
        ApprovalRequest request = new ApprovalRequest();
        request.setId(requestId);
        request.setUserId(userId);
        request.setAction(action);
        request.setParams(params);
        request.setStatus("pending");
        request.setCreatedAt(LocalDateTime.now());
        
        requests.put(requestId, request);
        
        log.info("创建审批请求：{} 用户：{} 操作：{}", requestId, userId, action);
        return request;
    }
    
    /**
     * 审批通过
     */
    public boolean approve(String requestId, String approverId) {
        ApprovalRequest request = requests.get(requestId);
        if (request == null) {
            log.warn("审批请求不存在：{}", requestId);
            return false;
        }
        
        request.setStatus("approved");
        request.setApprovedBy(approverId);
        request.setApprovedAt(LocalDateTime.now());
        
        log.info("审批通过：{} 审批人：{}", requestId, approverId);
        return true;
    }
    
    /**
     * 审批拒绝
     */
    public boolean reject(String requestId, String approverId, String reason) {
        ApprovalRequest request = requests.get(requestId);
        if (request == null) {
            log.warn("审批请求不存在：{}", requestId);
            return false;
        }
        
        request.setStatus("rejected");
        request.setApprovedBy(approverId);
        request.setRejectedReason(reason);
        request.setApprovedAt(LocalDateTime.now());
        
        log.info("审批拒绝：{} 审批人：{} 原因：{}", requestId, approverId, reason);
        return true;
    }
    
    /**
     * 获取审批请求
     */
    public ApprovalRequest getRequest(String requestId) {
        return requests.get(requestId);
    }
    
    /**
     * 列出待审批请求
     */
    public List<ApprovalRequest> listPendingRequests() {
        return requests.values().stream()
            .filter(r -> "pending".equals(r.getStatus()))
            .toList();
    }
    
    /**
     * 列出用户的审批历史
     */
    public List<ApprovalRequest> listUserHistory(String userId) {
        return requests.values().stream()
            .filter(r -> userId.equals(r.getUserId()))
            .toList();
    }
    
    /**
     * 审批请求
     */
    @Data
    @NoArgsConstructor
    public static class ApprovalRequest {
        private String id;
        private String userId;
        private String action;
        private Map<String, Object> params;
        private String status; // pending, approved, rejected
        private String approvedBy;
        private LocalDateTime createdAt;
        private LocalDateTime approvedAt;
        private String rejectedReason;
    }
}
