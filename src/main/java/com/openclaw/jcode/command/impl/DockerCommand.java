package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Docker 命令 - Docker 容器管理
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class DockerCommand extends Command {
    
    public DockerCommand() {
        this.name = "docker";
        this.description = "Docker 容器管理";
        this.aliases = Arrays.asList();
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showDockerInfo();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "ps", "list" -> listContainers(parts.length > 1 && "-a".equals(parts[1]));
            case "images" -> listImages();
            case "run" -> runContainer(parts.length > 1 ? parts[1] : null);
            case "stop" -> stopContainer(parts.length > 1 ? parts[1] : null);
            case "start" -> startContainer(parts.length > 1 ? parts[1] : null);
            case "rm" -> removeContainer(parts.length > 1 ? parts[1] : null);
            case "logs" -> showContainerLogs(parts.length > 1 ? parts[1] : null);
            case "info" -> showDockerInfo();
            default -> showDockerInfo();
        };
    }
    
    private CommandResult showDockerInfo() {
        String report = """
            ## Docker 信息
            
            ### Docker 状态
            
            | 属性 | 值 |
            |------|------|
            | 状态 | ⚪ 未检测 |
            | 版本 | - |
            | 容器数 | - |
            | 镜像数 | - |
            
            ### 快速命令
            
            ```bash
            # 查看 Docker 版本
            docker --version
            
            # 查看运行容器
            docker ps
            
            # 查看所有容器
            docker ps -a
            
            # 查看镜像
            docker images
            
            # 查看系统信息
            docker info
            ```
            
            ### JClaw Docker 命令
            
            | 命令 | 说明 |
            |------|------|
            | docker ps | 查看运行容器 |
            | docker images | 查看镜像 |
            | docker run <镜像> | 运行容器 |
            | docker stop <容器> | 停止容器 |
            | docker logs <容器> | 查看日志 |
            
            ⚠️ 需要安装 Docker 才能使用完整功能
            """;
        
        return CommandResult.success("Docker 信息")
                .withDisplayText(report);
    }
    
    private CommandResult listContainers(boolean all) {
        String report = String.format("""
            ## Docker 容器列表
            
            **模式**: %s
            
            | 容器 ID | 名称 | 镜像 | 状态 | 端口 |
            |--------|------|------|------|------|
            | - | - | - | - | - |
            
            ### 查看命令
            
            ```bash
            docker ps %s
            ```
            
            ⚠️ 实际数据需要 Docker 运行
            """, all ? "所有容器" : "运行中", all ? "-a" : "");
        
        return CommandResult.success("容器列表")
                .withDisplayText(report);
    }
    
    private CommandResult listImages() {
        String report = """
            ## Docker 镜像列表
            
            | 仓库 | 标签 | ID | 大小 | 创建时间 |
            |------|------|-----|------|---------|
            | - | - | - | - | - |
            
            ### 查看命令
            
            ```bash
            docker images
            ```
            
            ⚠️ 实际数据需要 Docker 运行
            """;
        
        return CommandResult.success("镜像列表")
                .withDisplayText(report);
    }
    
    private CommandResult runContainer(String image) {
        if (image == null) {
            return CommandResult.error("请指定镜像名称");
        }
        
        String report = String.format("""
            ## 运行 Docker 容器
            
            **镜像**: %s
            
            ### 执行命令
            
            ```bash
            docker run -d %s
            ```
            
            ### 常用选项
            
            | 选项 | 说明 |
            |------|------|
            | -d | 后台运行 |
            | --name | 指定容器名 |
            | -p | 端口映射 |
            | -v | 挂载卷 |
            | -e | 环境变量 |
            
            ### 示例
            
            ```bash
            docker run -d --name myapp -p 8080:80 nginx
            docker run -d -v /data:/app/data myimage
            ```
            """, image, image);
        
        return CommandResult.success("运行容器：" + image)
                .withDisplayText(report);
    }
    
    private CommandResult stopContainer(String container) {
        if (container == null) {
            return CommandResult.error("请指定容器 ID 或名称");
        }
        
        String report = String.format("""
            ## 停止 Docker 容器
            
            **容器**: %s
            
            ### 执行命令
            
            ```bash
            docker stop %s
            ```
            
            ⚠️ 需要 Docker 运行
            """, container, container);
        
        return CommandResult.success("停止容器：" + container)
                .withDisplayText(report);
    }
    
    private CommandResult startContainer(String container) {
        if (container == null) {
            return CommandResult.error("请指定容器 ID 或名称");
        }
        
        String report = String.format("""
            ## 启动 Docker 容器
            
            **容器**: %s
            
            ### 执行命令
            
            ```bash
            docker start %s
            ```
            
            ⚠️ 需要 Docker 运行
            """, container, container);
        
        return CommandResult.success("启动容器：" + container)
                .withDisplayText(report);
    }
    
    private CommandResult removeContainer(String container) {
        if (container == null) {
            return CommandResult.error("请指定容器 ID 或名称");
        }
        
        String report = String.format("""
            ## 删除 Docker 容器
            
            **容器**: %s
            
            ### 执行命令
            
            ```bash
            docker rm %s
            ```
            
            ⚠️ 需要 Docker 运行
            ⚠️ 容器必须已停止
            """, container, container);
        
        return CommandResult.success("删除容器：" + container)
                .withDisplayText(report);
    }
    
    private CommandResult showContainerLogs(String container) {
        if (container == null) {
            return CommandResult.error("请指定容器 ID 或名称");
        }
        
        String report = String.format("""
            ## Docker 容器日志
            
            **容器**: %s
            
            ### 查看命令
            
            ```bash
            docker logs %s
            docker logs -f %s    # 追踪日志
            docker logs --tail 100 %s
            ```
            
            ⚠️ 实际日志需要 Docker 运行
            """, container, container, container, container);
        
        return CommandResult.success("容器日志：" + container)
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：docker
            描述：Docker 容器管理
            
            用法：
              docker                  # Docker 信息
              docker ps               # 查看容器
              docker images           # 查看镜像
              docker run <镜像>       # 运行容器
              docker stop <容器>      # 停止容器
              docker logs <容器>      # 查看日志
            
            示例：
              docker ps
              docker run nginx
              docker logs myapp
            """;
    }
}
