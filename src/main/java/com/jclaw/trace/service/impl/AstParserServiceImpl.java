package com.jclaw.trace.service.impl;

import com.jclaw.trace.ast.JavaAstParser;
import com.jclaw.trace.entity.CodeUnit;
import com.jclaw.trace.mapper.CodeUnitMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * AST 解析服务实现
 */
@Slf4j
@Service
public class AstParserServiceImpl {

    @Autowired
    private CodeUnitMapper codeUnitMapper;

    /**
     * 解析 Java 文件
     */
    public CodeUnit parseJavaFile(String filePath) {
        log.info("解析 Java 文件：{}", filePath);
        
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            
            // TODO: 集成 JavaParser 库进行 AST 解析
            // 当前简化实现
            
            CodeUnit codeUnit = CodeUnit.builder()
                .filePath(filePath)
                .unitType("class")
                .unitName(extractClassName(filePath))
                .signature("public class " + extractClassName(filePath))
                .build();
            
            codeUnitMapper.insert(codeUnit);
            return codeUnit;
            
        } catch (Exception e) {
            log.error("解析 Java 文件失败：{}", filePath, e);
            return null;
        }
    }

    /**
     * 批量解析目录
     */
    public int parseDirectory(String dirPath) {
        log.info("批量解析目录：{}", dirPath);
        
        int count = 0;
        File dir = new File(dirPath);
        
        if (!dir.exists()) {
            log.error("目录不存在：{}", dirPath);
            return 0;
        }
        
        File[] files = dir.listFiles((d, name) -> name.endsWith(".java"));
        if (files != null) {
            for (File file : files) {
                parseJavaFile(file.getAbsolutePath());
                count++;
            }
        }
        
        log.info("解析完成：{} 个文件", count);
        return count;
    }

    private String extractClassName(String filePath) {
        String fileName = new File(filePath).getName();
        return fileName.replace(".java", "");
    }
}
