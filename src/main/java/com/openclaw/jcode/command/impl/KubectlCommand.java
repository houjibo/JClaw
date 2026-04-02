package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Kubectl 命令 - K8s 集群管理
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class KubectlCommand extends Command {
    
    public KubectlCommand() {
        this.name = "kubectl";
        this.description = "K8s 集群管理";
        this.aliases = Arrays.asList("k8s", "kube");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showKubectlInfo();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "get" -> kubectlGet(parts.length > 1 ? parts[1] : "pods");
            case "describe" -> kubectlDescribe(parts.length > 1 ? parts[1] : null);
            case "apply" -> kubectlApply(parts.length > 1 ? parts[1] : null);
            case "delete" -> kubectlDelete(parts.length > 1 ? parts[1] : null);
            case "logs" -> kubectlLogs(parts.length > 1 ? parts[1] : null);
            case "exec" -> kubectlExec(parts.length > 1 ? parts[1] : null);
            case "config" -> kubectlConfig();
            case "info" -> showKubectlInfo();
            default -> showKubectlInfo();
        };
    }
    
    private CommandResult showKubectlInfo() {
        String report = """
            ## Kubectl 信息
            
            ### K8s 集群状态
            
            | 属性 | 值 |
            |------|------|
            | 状态 | ⚪ 未连接 |
            | 集群 | - |
            | 命名空间 | default |
            | 节点数 | - |
            
            ### 快速命令
            
            ```bash
            # 查看集群信息
            kubectl cluster-info
            
            # 查看节点
            kubectl get nodes
            
            # 查看 Pod
            kubectl get pods
            
            # 查看服务
            kubectl get services
            ```
            
            ### JClaw Kubectl 命令
            
            | 命令 | 说明 |
            |------|------|
            | kubectl get <资源> | 查看资源 |
            | kubectl describe <资源> | 查看详情 |
            | kubectl apply -f <文件> | 应用配置 |
            | kubectl delete <资源> | 删除资源 |
            | kubectl logs <Pod> | 查看日志 |
            | kubectl exec <Pod> | 执行命令 |
            
            ⚠️ 需要配置 K8s 集群连接
            """;
        
        return CommandResult.success("Kubectl 信息")
                .withDisplayText(report);
    }
    
    private CommandResult kubectlGet(String resource) {
        String report = String.format("""
            ## Kubectl Get: %s
            
            ### 执行命令
            
            ```bash
            kubectl get %s
            kubectl get %s -o wide      # 详细信息
            kubectl get %s -o yaml      # YAML 格式
            kubectl get %s -A           # 所有命名空间
            ```
            
            ### 常见资源
            
            | 资源 | 简写 | 说明 |
            |------|------|------|
            | pods | po | Pod 列表 |
            | services | svc | 服务列表 |
            | deployments | deploy | 部署列表 |
            | nodes | no | 节点列表 |
            | namespaces | ns | 命名空间 |
            | configmaps | cm | 配置映射 |
            | secrets | - | 密钥 |
            
            ### 示例
            
            ```bash
            kubectl get pods
            kubectl get svc -o wide
            kubectl get deployments -n kube-system
            ```
            
            ⚠️ 需要 K8s 集群连接
            """, resource, resource, resource, resource, resource);
        
        return CommandResult.success("Kubectl Get: " + resource)
                .withDisplayText(report);
    }
    
    private CommandResult kubectlDescribe(String resource) {
        if (resource == null) {
            return CommandResult.error("请指定资源名称");
        }
        
        String report = String.format("""
            ## Kubectl Describe: %s
            
            ### 执行命令
            
            ```bash
            kubectl describe %s
            ```
            
            ### 输出内容
            
            - 资源基本信息
            - 标签和注解
            - 事件历史
            - 状态详情
            
            ### 示例
            
            ```bash
            kubectl describe pod my-pod
            kubectl describe svc my-service
            kubectl describe deployment my-deploy
            ```
            
            ⚠️ 需要 K8s 集群连接
            """, resource, resource);
        
        return CommandResult.success("Kubectl Describe: " + resource)
                .withDisplayText(report);
    }
    
    private CommandResult kubectlApply(String file) {
        if (file == null) {
            return CommandResult.error("请指定配置文件");
        }
        
        String report = String.format("""
            ## Kubectl Apply
            
            ### 执行命令
            
            ```bash
            kubectl apply -f %s
            kubectl apply -f %s --dry-run=client  # 预演
            kubectl apply -f %s --validate=false  # 跳过验证
            ```
            
            ### 应用场景
            
            - 部署应用
            - 更新配置
            - 创建资源
            
            ### 示例
            
            ```bash
            kubectl apply -f deployment.yaml
            kubectl apply -f configmap.yaml
            kubectl apply -f .  # 应用目录下所有配置
            ```
            
            ⚠️ 需要 K8s 集群连接
            """, file, file, file);
        
        return CommandResult.success("Kubectl Apply: " + file)
                .withDisplayText(report);
    }
    
    private CommandResult kubectlDelete(String resource) {
        if (resource == null) {
            return CommandResult.error("请指定资源");
        }
        
        String report = String.format("""
            ## Kubectl Delete
            
            ### 执行命令
            
            ```bash
            kubectl delete %s
            kubectl delete -f %s.yaml
            kubectl delete %s --grace-period=0  # 立即删除
            ```
            
            ### 注意事项
            
            - 删除操作不可逆
            - 建议先备份配置
            - 注意依赖关系
            
            ### 示例
            
            ```bash
            kubectl delete pod my-pod
            kubectl delete -f deployment.yaml
            kubectl delete namespace test
            ```
            
            ⚠️ 需要 K8s 集群连接
            """, resource, resource, resource);
        
        return CommandResult.success("Kubectl Delete: " + resource)
                .withDisplayText(report);
    }
    
    private CommandResult kubectlLogs(String pod) {
        if (pod == null) {
            return CommandResult.error("请指定 Pod 名称");
        }
        
        String report = String.format("""
            ## Kubectl Logs: %s
            
            ### 执行命令
            
            ```bash
            kubectl logs %s
            kubectl logs %s -f          # 追踪日志
            kubectl logs %s --tail=100  # 最后 100 行
            kubectl logs %s -c <容器>    # 指定容器
            ```
            
            ### 常用选项
            
            | 选项 | 说明 |
            |------|------|
            | -f | 追踪日志 |
            | --tail | 显示行数 |
            | --since | 起始时间 |
            | -c | 指定容器 |
            
            ### 示例
            
            ```bash
            kubectl logs my-pod -f
            kubectl logs my-pod --tail=50
            kubectl logs my-pod -c app
            ```
            
            ⚠️ 需要 K8s 集群连接
            """, pod, pod, pod, pod, pod);
        
        return CommandResult.success("Kubectl Logs: " + pod)
                .withDisplayText(report);
    }
    
    private CommandResult kubectlExec(String pod) {
        if (pod == null) {
            return CommandResult.error("请指定 Pod 名称");
        }
        
        String report = String.format("""
            ## Kubectl Exec: %s
            
            ### 执行命令
            
            ```bash
            kubectl exec -it %s -- /bin/bash
            kubectl exec -it %s -- /bin/sh
            kubectl exec -it %s -c <容器> -- /bin/bash
            ```
            
            ### 常用操作
            
            | 操作 | 命令 |
            |------|------|
            | 进入 Shell | kubectl exec -it pod -- /bin/bash |
            | 执行命令 | kubectl exec pod -- ls -la |
            | 指定容器 | kubectl exec -it pod -c app -- /bin/bash |
            
            ### 示例
            
            ```bash
            kubectl exec -it my-pod -- /bin/bash
            kubectl exec -it my-pod -- ls -la
            kubectl exec -it my-pod -c app -- /bin/sh
            ```
            
            ⚠️ 需要 K8s 集群连接
            """, pod, pod, pod, pod);
        
        return CommandResult.success("Kubectl Exec: " + pod)
                .withDisplayText(report);
    }
    
    private CommandResult kubectlConfig() {
        String report = """
            ## Kubectl Config
            
            ### 查看配置
            
            ```bash
            kubectl config view              # 查看配置
            kubectl config current-context   # 当前上下文
            kubectl config get-contexts      # 所有上下文
            ```
            
            ### 切换上下文
            
            ```bash
            kubectl config use-context <name>
            ```
            
            ### 配置管理
            
            ```bash
            kubectl config set-context <name> --cluster=<cluster> --namespace=<ns>
            kubectl config delete-context <name>
            ```
            
            ### 配置文件位置
            
            ```
            ~/.kube/config
            ```
            
            ⚠️ 需要 K8s 配置
            """;
        
        return CommandResult.success("Kubectl Config")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：kubectl
            别名：k8s, kube
            描述：K8s 集群管理
            
            用法：
              kubectl                   # K8s 信息
              kubectl get <资源>        # 查看资源
              kubectl describe <资源>   # 查看详情
              kubectl apply -f <文件>   # 应用配置
              kubectl delete <资源>     # 删除资源
              kubectl logs <Pod>        # 查看日志
              kubectl exec <Pod>        # 执行命令
            
            示例：
              kubectl get pods
              kubectl describe svc my-service
              kubectl logs my-pod -f
            """;
    }
}
