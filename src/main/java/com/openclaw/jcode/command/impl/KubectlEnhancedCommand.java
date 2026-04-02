package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Kubectl 增强命令 - 补充功能
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class KubectlEnhancedCommand extends Command {
    
    public KubectlEnhancedCommand() {
        this.name = "kubectl-enhanced";
        this.description = "Kubectl 增强功能";
        this.aliases = Arrays.asList("k8s-enhanced");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showKubectlEnhancedInfo();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "contexts" -> kubectlContexts();
            case "namespaces" -> kubectlNamespaces();
            case "port-forward" -> kubectlPortForward(parts.length > 1 ? parts[1] : null);
            case "debug" -> kubectlDebug(parts.length > 1 ? parts[1] : null);
            default -> showKubectlEnhancedInfo();
        };
    }
    
    private CommandResult showKubectlEnhancedInfo() {
        String report = """
            ## Kubectl 增强功能
            
            ### 可用命令
            
            | 命令 | 说明 |
            |------|------|
            | kubectl-enhanced contexts | 管理上下文 |
            | kubectl-enhanced namespaces | 命名空间管理 |
            | kubectl-enhanced port-forward | 端口转发 |
            | kubectl-enhanced debug | 调试 Pod |
            
            ### 上下文管理
            
            ```bash
            kubectl config get-contexts
            kubectl config use-context <name>
            ```
            
            ### 命名空间
            
            ```bash
            kubectl get namespaces
            kubectl create namespace <name>
            ```
            
            ### 端口转发
            
            ```bash
            kubectl port-forward pod/myapp 8080:80
            ```
            
            ### 调试 Pod
            
            ```bash
            kubectl debug -it pod/myapp --image=busybox
            ```
            
            ⚠️ 需要 K8s 集群连接
            """;
        
        return CommandResult.success("Kubectl 增强功能")
                .withDisplayText(report);
    }
    
    private CommandResult kubectlContexts() {
        String report = """
            ## Kubectl 上下文管理
            
            ### 查看上下文
            
            ```bash
            kubectl config get-contexts
            ```
            
            ### 示例输出
            
            ```
            CURRENT   NAME             CLUSTER         AUTHINFO        NAMESPACE
            *         dev-cluster      dev-cluster     dev-user        default
                      prod-cluster     prod-cluster    prod-user       default
                      test-cluster     test-cluster    test-user       default
            ```
            
            ### 切换上下文
            
            ```bash
            kubectl config use-context prod-cluster
            ```
            
            ### 查看当前上下文
            
            ```bash
            kubectl config current-context
            ```
            
            ### 设置命名空间
            
            ```bash
            kubectl config set-context --current --namespace=myns
            ```
            
            ### 删除上下文
            
            ```bash
            kubectl config delete-context old-cluster
            ```
            
            ⚠️ 需要 K8s 配置
            """;
        
        return CommandResult.success("Kubectl 上下文")
                .withDisplayText(report);
    }
    
    private CommandResult kubectlNamespaces() {
        String report = """
            ## Kubectl 命名空间
            
            ### 查看命名空间
            
            ```bash
            kubectl get namespaces
            kubectl get ns
            ```
            
            ### 示例输出
            
            ```
            NAME              STATUS   AGE
            default           Active   30d
            kube-system       Active   30d
            kube-public       Active   30d
            development       Active   10d
            production        Active   10d
            ```
            
            ### 创建命名空间
            
            ```bash
            kubectl create namespace myns
            ```
            
            ### 删除命名空间
            
            ```bash
            kubectl delete namespace myns
            ```
            
            ### 在命名空间中操作
            
            ```bash
            kubectl get pods -n myns
            kubectl config set-context --current --namespace=myns
            ```
            
            ### 命名空间资源配额
            
            ```yaml
            apiVersion: v1
            kind: ResourceQuota
            metadata:
              name: compute-quota
              namespace: myns
            spec:
              hard:
                requests.cpu: "4"
                requests.memory: 8Gi
                limits.cpu: "8"
                limits.memory: 16Gi
            ```
            
            ⚠️ 需要 K8s 集群连接
            """;
        
        return CommandResult.success("Kubectl 命名空间")
                .withDisplayText(report);
    }
    
    private CommandResult kubectlPortForward(String pod) {
        if (pod == null) {
            return CommandResult.error("请指定 Pod 名称");
        }
        
        String report = String.format("""
            ## Kubectl 端口转发
            
            ### Pod: %s
            
            ### 执行命令
            
            ```bash
            kubectl port-forward %s 8080:80
            kubectl port-forward %s 8443:443
            kubectl port-forward %s 3000:3000 --address=0.0.0.0
            ```
            
            ### 参数说明
            
            | 参数 | 说明 |
            |------|------|
            | 8080:80 | 本地 8080 -> Pod 80 |
            | --address | 监听地址 |
            | 0.0.0.0 | 允许外部访问 |
            
            ### 使用场景
            
            1. **访问 Pod 服务**
               ```bash
               kubectl port-forward pod/myapp 8080:80
               curl http://localhost:8080
               ```
            
            2. **访问数据库**
               ```bash
               kubectl port-forward pod/mysql 3306:3306
               mysql -h 127.0.0.1 -u root
               ```
            
            3. **调试服务**
               ```bash
               kubectl port-forward svc/myapp 8080:80
               ```
            
            ### 停止转发
            
            按 `Ctrl+C` 停止端口转发。
            
            ⚠️ 需要 K8s 集群连接
            """, pod, pod, pod, pod);
        
        return CommandResult.success("Kubectl 端口转发：" + pod)
                .withDisplayText(report);
    }
    
    private CommandResult kubectlDebug(String pod) {
        if (pod == null) {
            return CommandResult.error("请指定 Pod 名称");
        }
        
        String report = String.format("""
            ## Kubectl 调试 Pod
            
            ### Pod: %s
            
            ### 执行命令
            
            ```bash
            kubectl debug -it %s --image=busybox
            kubectl debug -it %s --image=ubuntu --target=<container>
            ```
            
            ### 调试场景
            
            1. **添加临时容器**
               ```bash
               kubectl debug -it myapp --image=busybox
               ```
            
            2. **调试特定容器**
               ```bash
               kubectl debug -it myapp --image=ubuntu --target=app
               ```
            
            3. **复制容器配置**
               ```bash
               kubectl debug -it myapp --image=debug --copy-to=myapp-debug
               ```
            
            ### 调试工具镜像
            
            | 镜像 | 用途 |
            |------|------|
            | busybox | 基础工具 |
            | ubuntu | 完整系统 |
            | nicolaka/netshoot | 网络调试 |
            | container-debug | 容器调试 |
            
            ### 常用调试命令
            
            ```bash
            # 进入调试容器
            kubectl debug -it myapp --image=busybox
            
            # 查看进程
            ps aux
            
            # 网络连接
            netstat -tlnp
            
            # 文件系统
            ls -la /proc/<pid>/root
            ```
            
            ⚠️ 需要 K8s 集群连接
            """, pod, pod, pod);
        
        return CommandResult.success("Kubectl 调试：" + pod)
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：kubectl-enhanced
            别名：k8s-enhanced
            描述：Kubectl 增强功能
            
            用法：
              kubectl-enhanced                 # 显示信息
              kubectl-enhanced contexts        # 管理上下文
              kubectl-enhanced namespaces      # 命名空间
              kubectl-enhanced port-forward    # 端口转发
              kubectl-enhanced debug           # 调试 Pod
            
            示例：
              kubectl-enhanced contexts
              kubectl-enhanced port-forward myapp
            """;
    }
}
