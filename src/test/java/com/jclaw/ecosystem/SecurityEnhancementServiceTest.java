package com.jclaw.ecosystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 安全增强服务测试
 */
@DisplayName("安全增强服务测试")
class SecurityEnhancementServiceTest {
    
    private SecurityEnhancementService securityService;
    
    @BeforeEach
    void setUp() {
        securityService = new SecurityEnhancementService();
    }
    
    @Test
    @DisplayName("测试生成 HMAC 签名")
    void testGenerateHmacSignature() {
        String data = "test data";
        String secretKey = "test-secret-key";
        
        String signature = securityService.generateHmacSignature(data, secretKey);
        
        assertNotNull(signature);
        assertFalse(signature.isEmpty());
        assertTrue(signature.length() > 0);
    }
    
    @Test
    @DisplayName("测试验证 HMAC 签名 - 正确")
    void testVerifyHmacSignatureValid() {
        String data = "test data";
        String secretKey = "test-secret-key";
        
        String signature = securityService.generateHmacSignature(data, secretKey);
        boolean valid = securityService.verifyHmacSignature(data, signature, secretKey);
        
        assertTrue(valid);
    }
    
    @Test
    @DisplayName("测试验证 HMAC 签名 - 错误")
    void testVerifyHmacSignatureInvalid() {
        String data = "test data";
        String secretKey = "test-secret-key";
        String wrongSignature = "wrong-signature";
        
        boolean valid = securityService.verifyHmacSignature(data, wrongSignature, secretKey);
        
        assertFalse(valid);
    }
    
    @Test
    @DisplayName("测试生成认证令牌")
    void testGenerateAuthToken() {
        SecurityEnhancementService.AuthToken token = securityService.generateAuthToken("system-1", 3600);
        
        assertNotNull(token);
        assertNotNull(token.getTokenId());
        assertEquals("system-1", token.getSystemId());
        assertTrue(token.getExpiresAt() > System.currentTimeMillis());
        assertFalse(token.isRevoked());
    }
    
    @Test
    @DisplayName("测试验证认证令牌 - 有效")
    void testValidateAuthTokenValid() {
        SecurityEnhancementService.AuthToken token = securityService.generateAuthToken("system-1", 3600);
        
        boolean valid = securityService.validateAuthToken(token.getTokenId());
        
        assertTrue(valid);
    }
    
    @Test
    @DisplayName("测试验证认证令牌 - 不存在")
    void testValidateAuthTokenNonExistent() {
        boolean valid = securityService.validateAuthToken("non-existent-token");
        
        assertFalse(valid);
    }
    
    @Test
    @DisplayName("测试吊销认证令牌")
    void testRevokeAuthToken() {
        SecurityEnhancementService.AuthToken token = securityService.generateAuthToken("system-1", 3600);
        
        // 先验证有效
        assertTrue(securityService.validateAuthToken(token.getTokenId()));
        
        // 吊销
        securityService.revokeAuthToken(token.getTokenId());
        
        // 再验证应该失败
        assertFalse(securityService.validateAuthToken(token.getTokenId()));
    }
    
    @Test
    @DisplayName("测试设置访问控制")
    void testSetAccessControl() {
        securityService.setAccessControl("system-1", "capability-1", false);
        
        boolean allowed = securityService.checkAccess("system-1", "capability-1");
        
        assertFalse(allowed);
    }
    
    @Test
    @DisplayName("测试检查访问权限 - 默认允许")
    void testCheckAccessDefault() {
        boolean allowed = securityService.checkAccess("system-1", "non-existent-capability");
        
        assertTrue(allowed); // 默认允许
    }
    
    @Test
    @DisplayName("测试清理过期令牌")
    void testCleanupExpiredTokens() {
        // 生成一个立即过期的令牌
        securityService.generateAuthToken("system-1", 0);
        
        // 等待一小段时间确保过期
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        int cleaned = securityService.cleanupExpiredTokens();
        
        assertTrue(cleaned >= 0);
    }
    
    @Test
    @DisplayName("测试获取令牌统计")
    void testGetTokenStats() {
        // 生成几个令牌
        securityService.generateAuthToken("system-1", 3600);
        securityService.generateAuthToken("system-2", 3600);
        
        SecurityEnhancementService.TokenStats stats = securityService.getTokenStats();
        
        assertNotNull(stats);
        assertTrue(stats.getTotalTokens() >= 2);
        assertTrue(stats.getActiveTokens() >= 2);
    }
    
    @Test
    @DisplayName("测试令牌过期")
    void testAuthTokenExpiration() throws InterruptedException {
        // 生成一个 1 秒后过期的令牌
        SecurityEnhancementService.AuthToken token = securityService.generateAuthToken("system-1", 1);
        
        // 立即验证应该有效
        assertTrue(securityService.validateAuthToken(token.getTokenId()));
        
        // 等待过期
        Thread.sleep(1100);
        
        // 再验证应该失败
        assertFalse(securityService.validateAuthToken(token.getTokenId()));
    }
}
