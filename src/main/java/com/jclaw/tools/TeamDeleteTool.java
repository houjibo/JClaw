package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 团队删除工具
 */
@Component
public class TeamDeleteTool extends Tool {
    
    private static final List<Map<String, String>> TEAMS = new ArrayList<>();
    
    public TeamDeleteTool() {
        this.name = "team_delete";
        this.description = "删除团队";
        this.category = ToolCategory.TEAM;
        this.requiresConfirmation = true;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String teamId = (String) params.get("team_id");
        
        if (teamId == null) return ToolResult.error("team_id 参数不能为空");
        
        boolean removed = TEAMS.removeIf(t -> t.get("id").equals(teamId));
        
        if (removed) {
            return ToolResult.success("团队已删除", "已删除团队：" + teamId);
        } else {
            return ToolResult.error("未找到团队：" + teamId);
        }
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { return params.containsKey("team_id"); }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 team_id - 团队 ID (必填)
               示例:
                 team_delete team_id="team_1"
               """.formatted(name, description);
    }
}
