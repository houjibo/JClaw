package com.jclaw.trace.service.impl;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.jclaw.trace.entity.CodeUnit;
import com.jclaw.trace.mapper.CodeUnitMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * AST 解析服务实现（基于 JavaParser）
 */
@Service
@Slf4j
public class AstParserServiceImpl {

    @Autowired
    private CodeUnitMapper codeUnitMapper;

    private final JavaParser javaParser = new JavaParser();

    /**
     * 解析 Java 文件
     */
    public CodeUnit parseJavaFile(String filePath) {
        log.info("解析 Java 文件：{}", filePath);
        
        try {
            // 使用 JavaParser 解析文件
            ParseResult<CompilationUnit> parseResult = javaParser.parse(new FileInputStream(filePath));
            
            if (!parseResult.isSuccessful() || parseResult.getResult().isEmpty()) {
                log.error("解析失败：{}", parseResult.getProblems());
                return null;
            }
            
            CompilationUnit cu = parseResult.getResult().get();
            String className = extractClassName(filePath);
            
            // 解析类信息
            CodeUnit classUnit = parseClass(cu, className, filePath);
            codeUnitMapper.insert(classUnit);
            
            // 解析方法
            List<CodeUnit> methodUnits = parseMethods(cu, className, filePath);
            for (CodeUnit method : methodUnits) {
                codeUnitMapper.insert(method);
            }
            
            log.info("解析完成：{} 个类，{} 个方法", 1, methodUnits.size());
            return classUnit;
            
        } catch (Exception e) {
            log.error("解析 Java 文件失败：{}", filePath, e);
            return null;
        }
    }

    /**
     * 解析类
     */
    private CodeUnit parseClass(CompilationUnit cu, String className, String filePath) {
        Optional<ClassOrInterfaceDeclaration> classDecl = cu.getClassByName(className);
        
        CodeUnit.CodeUnitBuilder builder = CodeUnit.builder()
            .filePath(filePath)
            .unitName(className)
            .unitType("CLASS")
            .createdAt(java.time.Instant.now());
        
        if (classDecl.isPresent()) {
            ClassOrInterfaceDeclaration decl = classDecl.get();
            builder.signature(decl.getNameAsString());
            
            // 计算圈复杂度（简化版）
            int complexity = calculateComplexity(decl);
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("complexity", complexity);
            metrics.put("methodCount", decl.getMethods().size());
            metrics.put("fieldCount", decl.getFields().size());
            builder.metrics(metrics);
        }
        
        return builder.build();
    }

    /**
     * 解析方法
     */
    private List<CodeUnit> parseMethods(CompilationUnit cu, String className, String filePath) {
        List<CodeUnit> methods = new ArrayList<>();
        
        Optional<ClassOrInterfaceDeclaration> classDecl = cu.getClassByName(className);
        if (classDecl.isPresent()) {
            for (MethodDeclaration method : classDecl.get().getMethods()) {
                CodeUnit methodUnit = CodeUnit.builder()
                    .filePath(filePath)
                    .unitName(className + "#" + method.getNameAsString())
                    .unitType("METHOD")
                    .signature(method.getDeclarationAsString())
                    .createdAt(java.time.Instant.now())
                    .metrics(Map.of(
                        "complexity", calculateMethodComplexity(method),
                        "paramCount", method.getParameters().size(),
                        "lineCount", method.getEnd().isPresent() ? 
                            method.getEnd().get().line - method.getBegin().get().line : 0
                    ))
                    .build();
                methods.add(methodUnit);
            }
        }
        
        return methods;
    }

    /**
     * 计算类的圈复杂度（简化版）
     */
    private int calculateComplexity(ClassOrInterfaceDeclaration decl) {
        int complexity = 1;
        
        for (MethodDeclaration method : decl.getMethods()) {
            complexity += calculateMethodComplexity(method);
        }
        
        return complexity;
    }

    /**
     * 计算方法的圈复杂度（简化版）
     */
    private int calculateMethodComplexity(MethodDeclaration method) {
        int complexity = 1;
        
        // 统计控制流语句
        complexity += method.findAll(com.github.javaparser.ast.stmt.IfStmt.class).size();
        complexity += method.findAll(com.github.javaparser.ast.stmt.ForStmt.class).size();
        complexity += method.findAll(com.github.javaparser.ast.stmt.ForEachStmt.class).size();
        complexity += method.findAll(com.github.javaparser.ast.stmt.WhileStmt.class).size();
        complexity += method.findAll(com.github.javaparser.ast.stmt.DoStmt.class).size();
        complexity += method.findAll(com.github.javaparser.ast.stmt.SwitchStmt.class).size();
        complexity += method.findAll(com.github.javaparser.ast.stmt.CatchClause.class).size();
        
        return complexity;
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

    /**
     * 递归解析目录（包括子目录）
     */
    public int parseDirectoryRecursive(String dirPath) {
        log.info("递归解析目录：{}", dirPath);
        
        int count = 0;
        File dir = new File(dirPath);
        
        if (!dir.exists()) {
            log.error("目录不存在：{}", dirPath);
            return 0;
        }
        
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    count += parseDirectoryRecursive(file.getAbsolutePath());
                } else if (file.getName().endsWith(".java")) {
                    parseJavaFile(file.getAbsolutePath());
                    count++;
                }
            }
        }
        
        log.info("递归解析完成：{} 个文件", count);
        return count;
    }

    private String extractClassName(String filePath) {
        String fileName = new File(filePath).getName();
        return fileName.replace(".java", "");
    }
}
