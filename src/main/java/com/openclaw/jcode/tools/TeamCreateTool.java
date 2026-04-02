package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 团队创建工具
 */
@Component
public class TeamCreateTool extends Tool {
    
    private static final List<Map<String, String>> TEAMS = new ArrayList<>();
    
    public TeamCreateTool() {
        this.name = "team_create";
        this.description = "创建团队";
        this.category = ToolCategory.TEAM;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String name = (String) params.get("name");
        String description = (String) params.get("description");
        
        if (name == null) return ToolResult.error("name 参数不能为空");
        
        Map<String, String> team = new HashMap<>();
        team.put("id", "team_" + (TEAMS.size() + 1));
        team.put("name", name);
        team.put("description", description != null ? description : "");
        team.put("createdAt", new Date().toString());
        
        TEAMS.add(team);
        
        return ToolResult.success("团队创建成功", 
            String.format("团队：%s\nID: %s\n描述：%s", name, team.get("id"), description));
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { return params.containsKey("name"); }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 name        - 团队名称 (必填)
                 description - 团队描述 (可选)
               示例:
                 team_create name="开发团队" description="负责核心开发"
               """.formatted(name, description);
    }
}
