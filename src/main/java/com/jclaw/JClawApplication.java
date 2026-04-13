package com.jclaw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * JClaw 启动类
 * 
 * @author JClaw Team
 */
@SpringBootApplication
@EnableScheduling
public class JClawApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(JClawApplication.class, args);
        System.out.println("\n" +
            "  _   _                   _ \n" +
            " | | | | __ _ _ __   __ _| |\n" +
            " | |_| |/ _` | '_ \\ / _` | |\n" +
            " |  _  | (_| | | | | (_| | |\n" +
            " |_| |_|\\__,_|_| |_|\\__,_|_|\n" +
            "                            \n" +
            " JClaw v4.0 - AI 智能助手\n" +
            " 启动成功!\n"
        );
    }
}
