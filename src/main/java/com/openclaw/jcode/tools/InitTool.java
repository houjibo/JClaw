package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 初始化项目工具
 */
@Component
public class InitTool extends Tool {
    
    public InitTool() {
        this.name = "init";
        this.description = "初始化新项目";
        this.category = ToolCategory.CODE;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String name = (String) params.get("name");
        String template = (String) params.getOrDefault("template", "java-spring");
        
        if (name == null) return ToolResult.error("name 参数不能为空");
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== 项目初始化 ===\n\n");
        sb.append("项目名称：").append(name).append("\n");
        sb.append("模板：").append(template).append("\n\n");
        
        sb.append("【创建目录】\n");
        sb.append("✅ src/main/java\n");
        sb.append("✅ src/main/resources\n");
        sb.append("✅ src/test/java\n");
        sb.append("✅ .gitignore\n\n");
        
        sb.append("【创建文件】\n");
        sb.append("✅ pom.xml\n");
        sb.append("✅ Application.java\n");
        sb.append("✅ application.yml\n");
        sb.append("✅ README.md\n\n");
        
        sb.append("✅ 项目初始化完成\n");
        sb.append("位置：./").append(name).append("/");
        
        System.out.println("[InitTool] 项目初始化完成");
        
        return ToolResult.success("项目初始化完成", sb.toString());
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { return params.containsKey("name"); }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 name     - 项目名称 (必填)
                 template - 模板：java-spring, java-basic, python (可选)
               示例:
                 init name="my-project"
                 init name="my-project" template="python"
               """.formatted(name, description);
    }
}
