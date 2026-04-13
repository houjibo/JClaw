package com.jclaw.controller;

import com.jclaw.security.ApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 审批 REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ApprovalController {
    
    private final ApprovalService approvalService;
    
    /**
     * 创建审批请求
     */
    @PostMapping
    public ResponseEntity<ApprovalService.ApprovalRequest> createRequest(@RequestBody CreateRequest request) {
        ApprovalService.ApprovalRequest approvalRequest = approvalService.createRequest(
            request.getUserId(),
            request.getAction(),
            request.getParams()
        );
        return ResponseEntity.ok(approvalRequest);
    }
    
    /**
     * 获取审批请求
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ApprovalService.ApprovalRequest> getRequest(@PathVariable String requestId) {
        ApprovalService.ApprovalRequest request = approvalService.getRequest(requestId);
        if (request == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(request);
    }
    
    /**
     * 审批通过
     */
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<Map<String, String>> approve(
            @PathVariable String requestId,
            @RequestBody ApproveRequest request) {
        boolean success = approvalService.approve(requestId, request.getApproverId());
        return success 
            ? ResponseEntity.ok(Map.of("status", "approved"))
            : ResponseEntity.badRequest().body(Map.of("error", "审批失败"));
    }
    
    /**
     * 审批拒绝
     */
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Map<String, String>> reject(
            @PathVariable String requestId,
            @RequestBody RejectRequest request) {
        boolean success = approvalService.reject(requestId, request.getApproverId(), request.getReason());
        return success
            ? ResponseEntity.ok(Map.of("status", "rejected"))
            : ResponseEntity.badRequest().body(Map.of("error", "审批失败"));
    }
    
    /**
     * 列出待审批
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ApprovalService.ApprovalRequest>> listPending() {
        return ResponseEntity.ok(approvalService.listPendingRequests());
    }
    
    /**
     * 列出用户历史
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ApprovalService.ApprovalRequest>> listHistory(@PathVariable String userId) {
        return ResponseEntity.ok(approvalService.listUserHistory(userId));
    }
    
    public static class CreateRequest {
        private String userId;
        private String action;
        private Map<String, Object> params;
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }
    
    public static class ApproveRequest {
        private String approverId;
        public String getApproverId() { return approverId; }
        public void setApproverId(String approverId) { this.approverId = approverId; }
    }
    
    public static class RejectRequest {
        private String approverId;
        private String reason;
        public String getApproverId() { return approverId; }
        public void setApproverId(String approverId) { this.approverId = approverId; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
