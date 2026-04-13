package com.jclaw.skills;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

/**
 * 文件写入技能
 */
@Slf4j
@Service
public class FileWriteSkill implements Skill {
    
    @Override
    public String getName() {
        return "file_write";
    }
    
    @Override
    public String getDescription() {
        return "写入文件内容";
    }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        try {
            String pathStr = (String) params.get("path");
            String content = (String) params.get("content");
            String mode = (String) params.getOrDefault("mode", "write"); // write 或 append
            
            if (pathStr == null || pathStr.isEmpty()) {
                return SkillResult.error("缺少参数：path");
            }
            if (content == null) {
                return SkillResult.error("缺少参数：content");
            }
            
            // 安全检查
            Path path = Paths.get(pathStr).normalize();
            String pathAbs = path.toAbsolutePath().toString();
            if (pathAbs.startsWith("/etc/") || pathAbs.startsWith("/root/") || 
                pathAbs.startsWith("/proc/") || pathAbs.startsWith("/sys/")) {
                return SkillResult.error("禁止写入系统目录");
            }
            
            // 创建父目录（如果不存在）
            Files.createDirectories(path.getParent());
            
            // 写入文件
            if ("append".equals(mode)) {
                Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } else {
                Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
            
            log.info("写入文件：{} ({} 字节)", pathStr, content.length());
            
            return SkillResult.success("文件写入成功", Map.of(
                "path", pathStr,
                "size", content.length(),
                "mode", mode
            ));
            
        } catch (IOException e) {
            log.error("写入文件失败", e);
            return SkillResult.error("写入失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("执行技能失败", e);
            return SkillResult.error("执行失败：" + e.getMessage());
        }
    }
}
