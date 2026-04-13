package com.jclaw.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * JClaw Gateway 服务
 * OpenClaw 核心引擎
 */
@Slf4j
@SpringBootApplication
@EnableScheduling
public class JClawGateway {
    
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        
        ConfigurableApplicationContext context = SpringApplication.run(JClawGateway.class, args);
        
        long startupTime = System.currentTimeMillis() - startTime;
        
        // 打印启动信息
        printBanner();
        log.info("✅ JClaw Gateway 启动完成");
        log.info("⏱️  启动耗时：{}ms", startupTime);
        
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            log.info("🌐 服务地址：http://{}:18790", host);
        } catch (UnknownHostException e) {
            log.info("🌐 服务地址：http://localhost:18790");
        }
        
        log.info("📊 可用技能：{} 个", context.getBean(com.jclaw.skills.SkillEngine.class).listSkills().size());
        log.info("📡 消息通道：飞书{}", 
            context.getBean(com.jclaw.channels.impl.FeishuChannel.class).isEnabled() ? "✅" : "❌");
        
        log.info("\n💡 使用方式:");
        log.info("  - REST API: http://localhost:18790/api");
        log.info("  - CLI: node jclaw-cli.js chat");
        log.info("  - 前端：http://localhost:18790/frontend");
    }
    
    private static void printBanner() {
        System.out.println("""
            
              _   _                   _ 
             | | | | __ _ _ __   __ _| |
             | |_| |/ _` | '_ \\ / _` | |
             |  _  | (_| | | | | (_| | |
             |_| |_|\\__,_|_| |_|\\__,_|_|
                                        
              JClaw Gateway v4.9
              AI 智能助手
            """);
    }
}
