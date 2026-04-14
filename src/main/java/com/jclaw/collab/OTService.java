package com.jclaw.collab;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OT (Operational Transformation) 协作服务
 * 
 * 功能：
 * - 操作转换
 * - 冲突解决
 * - 版本控制
 * - 实时协作
 * 
 * 状态：基础框架实现
 * 
 * @author JClaw
 * @since 2026-04-14
 */
@Slf4j
@Service
public class OTService {
    
    /**
     * 操作类型
     */
    public enum OperationType {
        INSERT,
        DELETE,
        UPDATE
    }
    
    /**
     * 操作
     */
    @Data
    public static class Operation {
        private String id;
        private OperationType type;
        private int position;
        private String content;
        private int length;
        private long version;
        private String userId;
        private long timestamp;
    }
    
    /**
     * 文档状态
     */
    @Data
    public static class DocumentState {
        private String documentId;
        private String content;
        private long version;
        private Map<String, Long> userVersions;
        private List<Operation> history;
    }
    
    /**
     * 文档存储
     */
    private final Map<String, DocumentState> documents = new ConcurrentHashMap<>();
    
    /**
     * 初始化文档
     */
    public DocumentState initDocument(String documentId, String initialContent) {
        log.info("初始化文档：{}", documentId);
        
        DocumentState state = new DocumentState();
        state.setDocumentId(documentId);
        state.setContent(initialContent);
        state.setVersion(0);
        state.setUserVersions(new ConcurrentHashMap<>());
        state.setHistory(new ArrayList<>());
        
        documents.put(documentId, state);
        return state;
    }
    
    /**
     * 应用操作
     */
    public synchronized DocumentState applyOperation(String documentId, Operation operation) {
        DocumentState state = documents.get(documentId);
        if (state == null) {
            throw new IllegalArgumentException("文档不存在：" + documentId);
        }
        
        log.debug("应用操作：{} - v{}", operation.getId(), operation.getVersion());
        
        // 转换操作
        Operation transformed = transformOperation(state, operation);
        
        // 应用转换后的操作
        String newContent = applyTransformation(state.getContent(), transformed);
        state.setContent(newContent);
        state.setVersion(state.getVersion() + 1);
        state.getUserVersions().put(operation.getUserId(), state.getVersion());
        state.getHistory().add(transformed);
        
        log.info("操作应用成功：{} - 新版本 v{}", operation.getId(), state.getVersion());
        
        return state;
    }
    
    /**
     * 转换操作
     */
    private Operation transformOperation(DocumentState state, Operation operation) {
        Operation transformed = cloneOperation(operation);
        transformed.setVersion(state.getVersion());
        
        // 简单的转换逻辑（实际应该更复杂）
        for (Operation historyOp : state.getHistory()) {
            if (historyOp.getVersion() >= operation.getVersion()) {
                transformed = transformPair(transformed, historyOp);
            }
        }
        
        return transformed;
    }
    
    /**
     * 转换操作对
     */
    private Operation transformPair(Operation op1, Operation op2) {
        if (op1.getUserId().equals(op2.getUserId())) {
            return op1;
        }
        
        Operation result = cloneOperation(op1);
        
        if (op2.getType() == OperationType.INSERT) {
            if (op1.getPosition() >= op2.getPosition()) {
                result.setPosition(op1.getPosition() + op2.getContent().length());
            }
        } else if (op2.getType() == OperationType.DELETE) {
            if (op1.getPosition() >= op2.getPosition()) {
                result.setPosition(Math.max(0, op1.getPosition() - op2.getLength()));
            }
        }
        
        return result;
    }
    
    /**
     * 应用转换到内容
     */
    private String applyTransformation(String content, Operation operation) {
        StringBuilder sb = new StringBuilder(content);
        
        switch (operation.getType()) {
            case INSERT:
                if (operation.getPosition() <= sb.length()) {
                    sb.insert(operation.getPosition(), operation.getContent());
                }
                break;
            case DELETE:
                if (operation.getPosition() < sb.length()) {
                    int end = Math.min(sb.length(), operation.getPosition() + operation.getLength());
                    sb.delete(operation.getPosition(), end);
                }
                break;
            case UPDATE:
                if (operation.getPosition() < sb.length()) {
                    int end = Math.min(sb.length(), operation.getPosition() + operation.getLength());
                    sb.replace(operation.getPosition(), end, operation.getContent());
                }
                break;
        }
        
        return sb.toString();
    }
    
    /**
     * 克隆操作
     */
    private Operation cloneOperation(Operation op) {
        Operation clone = new Operation();
        clone.setId(op.getId());
        clone.setType(op.getType());
        clone.setPosition(op.getPosition());
        clone.setContent(op.getContent());
        clone.setLength(op.getLength());
        clone.setVersion(op.getVersion());
        clone.setUserId(op.getUserId());
        clone.setTimestamp(op.getTimestamp());
        return clone;
    }
    
    /**
     * 获取文档状态
     */
    public DocumentState getDocumentState(String documentId) {
        return documents.get(documentId);
    }
    
    /**
     * 获取操作历史
     */
    public List<Operation> getOperationHistory(String documentId, long fromVersion) {
        DocumentState state = documents.get(documentId);
        if (state == null) {
            return new ArrayList<>();
        }
        
        return state.getHistory().stream()
            .filter(op -> op.getVersion() >= fromVersion)
            .toList();
    }
}
