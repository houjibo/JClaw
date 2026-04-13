package com.jclaw.skills;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Grep 搜索技能
 */
@Slf4j
@Service
public class GrepSkill implements Skill {
    
    @Override
    public String getName() {
        return "grep";
    }
    
    @Override
    public String getDescription() {
        return "在文件中搜索文本";
    }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        try {
            String pattern = (String) params.get("pattern");
            String pathStr = (String) params.get("path");
            Boolean ignoreCase = (Boolean) params.getOrDefault("ignoreCase", false);
            Integer maxResults = (Integer) params.getOrDefault("maxResults", 100);
            
            if (pattern == null || pattern.isEmpty()) {
                return SkillResult.error("缺少参数：pattern");
            }
            if (pathStr == null || pathStr.isEmpty()) {
                return SkillResult.error("缺少参数：path");
            }
            
            Path path = Paths.get(pathStr);
            if (!Files.exists(path)) {
                return SkillResult.error("路径不存在：" + pathStr);
            }
            
            java.util.regex.Pattern regex = ignoreCase 
                ? java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.CASE_INSENSITIVE)
                : java.util.regex.Pattern.compile(pattern);
            
            StringBuilder result = new StringBuilder();
            int matchCount = 0;
            
            try (Stream<String> lines = Files.lines(path)) {
                int lineNum = 0;
                for (String line : lines.limit(10000).toList()) {
                    lineNum++;
                    if (regex.matcher(line).find()) {
                        result.append(lineNum).append(": ").append(line).append("\n");
                        matchCount++;
                        if (matchCount >= maxResults) {
                            break;
                        }
                    }
                }
            }
            
            log.info("Grep 搜索完成：{} 匹配 {} 行", pathStr, matchCount);
            
            return SkillResult.success(
                result.toString().isEmpty() ? "未找到匹配项" : result.toString(),
                Map.of(
                    "pattern", pattern,
                    "path", pathStr,
                    "matches", matchCount
                )
            );
            
        } catch (IOException e) {
            log.error("Grep 搜索失败", e);
            return SkillResult.error("搜索失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("执行技能失败", e);
            return SkillResult.error("执行失败：" + e.getMessage());
        }
    }
}
