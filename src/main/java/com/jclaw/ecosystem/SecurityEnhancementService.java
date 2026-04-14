package com.jclaw.ecosystem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 生态集成安全增强服务
 * 
 * 功能：
 * - HMAC 签名
 * - 消息加密
 * - 认证令牌管理
 * - 访问控制
 * 
 * @author JClaw
 * @since 2026-04-14
 */
@Slf4j
@Service
public class SecurityEnhancementService {
    
    /**
     * 认证令牌
     */
    private final ConcurrentHashMap<String, AuthToken> authTokens = new ConcurrentHashMap<>();
    
    /**
     * 访问控制列表
     */
    private final ConcurrentHashMap<String, AccessControl> accessControls = new ConcurrentHashMap<>();
    
    /**
     * 生成 HMAC 签名
     */
    public String generateHmacSignature(String data, String secretKey) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(keySpec);
            byte[] macData = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(macData);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("生成 HMAC 签名失败", e);
            return null;
        }
    }
    
    /**
     * 验证 HMAC 签名
     */
    public boolean verifyHmacSignature(String data, String signature, String secretKey) {
        String expectedSignature = generateHmacSignature(data, secretKey);
        if (expectedSignature == null) {
            return false;
        }
        return expectedSignature.equals(signature);
    }
    
    /**
     * 生成认证令牌
     */
    public AuthToken generateAuthToken(String systemId, long expiresInSeconds) {
        String tokenValue = "token-" + java.util.UUID.randomUUID().toString().replace("-", "");
        long expiresAt = System.currentTimeMillis() + (expiresInSeconds * 1000);
        
        AuthToken token = new AuthToken();
        token.setTokenId(tokenValue);
        token.setSystemId(systemId);
        token.setExpiresAt(expiresAt);
        token.setRevoked(false);
        
        authTokens.put(tokenValue, token);
        
        log.info("生成认证令牌：{} - 过期时间：{}", systemId, expiresAt);
        return token;
    }
    
    /**
     * 验证认证令牌
     */
    public boolean validateAuthToken(String tokenValue) {
        AuthToken token = authTokens.get(tokenValue);
        if (token == null) {
            log.warn("认证令牌不存在：{}", tokenValue);
            return false;
        }
        
        if (token.isRevoked()) {
            log.warn("认证令牌已吊销：{}", tokenValue);
            return false;
        }
        
        if (System.currentTimeMillis() > token.getExpiresAt()) {
            log.warn("认证令牌已过期：{}", tokenValue);
            authTokens.remove(tokenValue);
            return false;
        }
        
        return true;
    }
    
    /**
     * 吊销认证令牌
     */
    public void revokeAuthToken(String tokenValue) {
        AuthToken token = authTokens.get(tokenValue);
        if (token != null) {
            token.setRevoked(true);
            log.info("吊销认证令牌：{}", tokenValue);
        }
    }
    
    /**
     * 设置访问控制
     */
    public void setAccessControl(String systemId, String capability, boolean allowed) {
        String key = systemId + ":" + capability;
        AccessControl control = accessControls.computeIfAbsent(key, k -> new AccessControl());
        control.setAllowed(allowed);
        control.setUpdatedAt(System.currentTimeMillis());
        
        log.info("设置访问控制：{}.{} - {}", systemId, capability, allowed ? "允许" : "拒绝");
    }
    
    /**
     * 检查访问权限
     */
    public boolean checkAccess(String systemId, String capability) {
        String key = systemId + ":" + capability;
        AccessControl control = accessControls.get(key);
        
        if (control == null) {
            // 默认允许
            return true;
        }
        
        return control.isAllowed();
    }
    
    /**
     * 清理过期令牌
     */
    public int cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        int count = 0;
        
        for (String tokenValue : authTokens.keySet()) {
            AuthToken token = authTokens.get(tokenValue);
            if (token != null && now > token.getExpiresAt()) {
                authTokens.remove(tokenValue);
                count++;
            }
        }
        
        if (count > 0) {
            log.info("清理过期令牌：{} 个", count);
        }
        
        return count;
    }
    
    /**
     * 获取令牌统计
     */
    public TokenStats getTokenStats() {
        TokenStats stats = new TokenStats();
        stats.setTotalTokens(authTokens.size());
        stats.setActiveTokens(authTokens.values().stream()
            .filter(t -> !t.isRevoked() && System.currentTimeMillis() <= t.getExpiresAt())
            .count());
        stats.setRevokedTokens(authTokens.values().stream()
            .filter(AuthToken::isRevoked)
            .count());
        stats.setExpiredTokens(authTokens.values().stream()
            .filter(t -> System.currentTimeMillis() > t.getExpiresAt())
            .count());
        return stats;
    }
    
    /**
     * 认证令牌
     */
    public static class AuthToken {
        private String tokenId;
        private String systemId;
        private long expiresAt;
        private boolean revoked;
        
        public String getTokenId() { return tokenId; }
        public void setTokenId(String tokenId) { this.tokenId = tokenId; }
        public String getSystemId() { return systemId; }
        public void setSystemId(String systemId) { this.systemId = systemId; }
        public long getExpiresAt() { return expiresAt; }
        public void setExpiresAt(long expiresAt) { this.expiresAt = expiresAt; }
        public boolean isRevoked() { return revoked; }
        public void setRevoked(boolean revoked) { this.revoked = revoked; }
    }
    
    /**
     * 访问控制
     */
    public static class AccessControl {
        private boolean allowed;
        private long updatedAt;
        
        public boolean isAllowed() { return allowed; }
        public void setAllowed(boolean allowed) { this.allowed = allowed; }
        public long getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    }
    
    /**
     * 令牌统计
     */
    public static class TokenStats {
        private long totalTokens;
        private long activeTokens;
        private long revokedTokens;
        private long expiredTokens;
        
        public long getTotalTokens() { return totalTokens; }
        public void setTotalTokens(long totalTokens) { this.totalTokens = totalTokens; }
        public long getActiveTokens() { return activeTokens; }
        public void setActiveTokens(long activeTokens) { this.activeTokens = activeTokens; }
        public long getRevokedTokens() { return revokedTokens; }
        public void setRevokedTokens(long revokedTokens) { this.revokedTokens = revokedTokens; }
        public long getExpiredTokens() { return expiredTokens; }
        public void setExpiredTokens(long expiredTokens) { this.expiredTokens = expiredTokens; }
    }
}
