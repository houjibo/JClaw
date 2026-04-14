package com.jclaw.examples;

import com.jclaw.client.JClawClient;
import java.util.Map;

/**
 * JClaw Hello World 示例
 * 
 * 展示如何快速开始使用 JClaw
 * 
 * @author JClaw
 * @since 2026-04-15
 */
public class HelloWorld {
    
    public static void main(String[] args) {
        System.out.println("🚀 JClaw Hello World!");
        System.out.println("====================\n");
        
        // 创建客户端（替换为你的 API Key）
        String apiKey = System.getenv("JCLAW_API_KEY");
        if (apiKey == null) {
            System.err.println("❌ 错误：请设置 JCLAW_API_KEY 环境变量");
            System.exit(1);
        }
        
        JClawClient client = JClawClient.create(apiKey);
        
        try {
            // 示例 1: 执行简单指令
            System.out.println("📝 示例 1: 执行简单指令");
            System.out.println("指令：读取当前目录的文件\n");
            
            String result = client.execute("读取当前目录的文件");
            System.out.println(result);
            System.out.println();
            
            // 示例 2: 使用工具
            System.out.println("🛠️  示例 2: 使用工具");
            System.out.println("工具：file_read\n");
            
            Map<String, Object> toolResult = client.tool("file_read", Map.of("path", "README.md"));
            System.out.println("文件内容预览：" + toolResult.get("content"));
            System.out.println();
            
            // 示例 3: 使用技能
            System.out.println("🎯 示例 3: 使用技能");
            System.out.println("技能：web_search\n");
            
            Map<String, Object> skillResult = client.skill("web_search", 
                Map.of("query", "JClaw AI 助手"));
            System.out.println("搜索结果：" + skillResult.get("results"));
            System.out.println();
            
            System.out.println("✅ 所有示例运行成功！");
            
        } catch (Exception e) {
            System.err.println("❌ 错误：" + e.getMessage());
            e.printStackTrace();
        } finally {
            client.close();
        }
    }
}
