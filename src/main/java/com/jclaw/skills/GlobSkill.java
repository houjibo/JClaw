package com.jclaw.skills;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Glob 文件匹配技能
 */
@Slf4j
@Service
public class GlobSkill implements Skill {
    
    @Override
    public String getName() {
        return "glob";
    }
    
    @Override
    public String getDescription() {
        return "匹配文件路径";
    }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        try {
            String pattern = (String) params.get("pattern");
            String basePath = (String) params.getOrDefault("basePath", ".");
            
            if (pattern == null || pattern.isEmpty()) {
                return SkillResult.error("缺少参数：pattern");
            }
            
            Path base = Paths.get(basePath);
            if (!Files.exists(base)) {
                return SkillResult.error("基础路径不存在：" + basePath);
            }
            
            // 简单实现：支持 * 和 ** 通配符
            String regex = pattern
                .replace(".", "\\.")
                .replace("**", ".+")
                .replace("*", "[^/]*");
            
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("^" + regex + "$");
            
            List<String> matches;
            try (Stream<Path> walk = Files.walk(base)) {
                matches = walk
                    .map(Path::toString)
                    .filter(path -> p.matcher(path.replace(basePath + "/", "")).matches())
                    .limit(1000)
                    .collect(Collectors.toList());
            }
            
            log.info("Glob 匹配完成：{} 找到 {} 个文件", pattern, matches.size());
            
            return SkillResult.success(
                matches.isEmpty() ? "未找到匹配文件" : String.join("\n", matches),
                Map.of(
                    "pattern", pattern,
                    "count", matches.size()
                )
            );
            
        } catch (IOException e) {
            log.error("Glob 匹配失败", e);
            return SkillResult.error("匹配失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("执行技能失败", e);
            return SkillResult.error("执行失败：" + e.getMessage());
        }
    }
}
