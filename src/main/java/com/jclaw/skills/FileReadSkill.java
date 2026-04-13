package com.jclaw.skills;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * 文件读取技能
 */
@Slf4j
@Service
public class FileReadSkill implements Skill {
    
    @Override
    public String getName() {
        return "file_read";
    }
    
    @Override
    public String getDescription() {
        return "读取文件内容";
    }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        long start = System.currentTimeMillis();
        
        try {
            String pathStr = (String) params.get("path");
            if (pathStr == null || pathStr.isEmpty()) {
                return SkillResult.error("缺少参数：path");
            }
            
            // 安全检查：防止路径遍历攻击
            Path path = Paths.get(pathStr).normalize();
            String pathAbs = path.toAbsolutePath().toString();
            if (pathAbs.startsWith("/etc/") || pathAbs.startsWith("/root/") || 
                pathAbs.startsWith("/proc/") || pathAbs.startsWith("/sys/")) {
                return SkillResult.error("禁止访问系统目录");
            }
            
            if (!Files.exists(path)) {
                return SkillResult.error("文件不存在：" + pathStr);
            }
            
            if (!Files.isReadable(path)) {
                return SkillResult.error("文件不可读：" + pathStr);
            }
            
            String content = Files.readString(path);
            
            log.info("读取文件：{} ({} 字节)", pathStr, content.length());
            
            return SkillResult.success(content, Map.of(
                "path", pathStr,
                "size", content.length(),
                "lines", content.split("\n").length
            ));
            
        } catch (IOException e) {
            log.error("读取文件失败", e);
            return SkillResult.error("读取失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("执行技能失败", e);
            return SkillResult.error("执行失败：" + e.getMessage());
        } finally {
            long duration = System.currentTimeMillis() - start;
            // 可以在这里记录耗时
        }
    }
}
