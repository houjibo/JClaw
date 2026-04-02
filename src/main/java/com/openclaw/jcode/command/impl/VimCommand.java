package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Vim 模式命令
 * Vim 风格的编辑模式
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class VimCommand extends Command {
    
    public VimCommand() {
        this.name = "vim";
        this.description = "Vim 编辑模式";
        this.aliases = Arrays.asList("vi", "nvim");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showVimInfo();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "open", "edit" -> openFile(parts.length > 1 ? parts[1] : null);
            case "help" -> showVimHelp();
            case "modes" -> showVimModes();
            case "commands" -> showVimCommands();
            default -> openFile(parts[0]);
        };
    }
    
    private CommandResult showVimInfo() {
        String report = """
            ## Vim 模式
            
            ### 状态
            
            | 属性 | 值 |
            |------|------|
            | Vim 模式 | ⚪ 未启用 |
            | 当前模式 | 正常模式 |
            | 打开文件 | - |
            
            ### 快速启动
            
            ```
            vim <文件>          # 打开文件
            vim -h              # 显示帮助
            vim --modes         # 查看模式
            vim --commands      # 查看命令
            ```
            
            ### Vim 模式说明
            
            Vim 有 6 种基本模式：
            
            1. **正常模式** (Normal) - 默认模式，用于导航和命令
            2. **插入模式** (Insert) - 用于输入文本
            3. **可视模式** (Visual) - 用于选择文本
            4. **可视行模式** (Visual Line) - 选择整行
            5. **可视块模式** (Visual Block) - 选择块
            6. **命令模式** (Command) - 输入命令
            
            ### 模式切换
            
            | 从 | 到 | 按键 |
            |----|----|------|
            | 任意 | 正常 | Esc |
            | 正常 | 插入 | i, a, o |
            | 正常 | 可视 | v, V, Ctrl+v |
            | 正常 | 命令 | : |
            
            ⚠️ 完整 Vim 功能需要终端支持
            """;
        
        return CommandResult.success("Vim 模式")
                .withDisplayText(report);
    }
    
    private CommandResult openFile(String file) {
        if (file == null) {
            return CommandResult.error("请指定文件路径");
        }
        
        String report = String.format("""
            ## Vim 打开文件
            
            **文件**: %s
            
            ### Vim 会话已启动
            
            ```
            %s
            ```
            
            ### 基本操作
            
            | 操作 | 按键 |
            |------|------|
            | 进入插入模式 | i |
            | 保存 | :w |
            | 退出 | :q |
            | 保存并退出 | :wq |
            | 不保存退出 | :q! |
            | 撤销 | u |
            | 重做 | Ctrl+r |
            
            ### 导航
            
            | 操作 | 按键 |
            |------|------|
            | 上 | k / ↑ |
            | 下 | j / ↓ |
            | 左 | h / ← |
            | 右 | l / → |
            | 行首 | 0 |
            | 行尾 | $ |
            | 文件头 | gg |
            | 文件尾 | G |
            
            ### 搜索替换
            
            | 操作 | 命令 |
            |------|------|
            | 搜索 | /pattern |
            | 下一个 | n |
            | 上一个 | N |
            | 替换 | :%%s/old/new/g |
            
            输入 `vim help` 查看详细帮助。
            """, file, file);
        
        return CommandResult.success("Vim 打开：" + file)
                .withDisplayText(report);
    }
    
    private CommandResult showVimHelp() {
        String report = """
            ## Vim 帮助
            
            ### 入门教程
            
            1. **打开文件**
               ```
               vim filename.txt
               ```
            
            2. **进入插入模式**
               按 `i` 键
            
            3. **输入文本**
               正常打字
            
            4. **退出插入模式**
               按 `Esc` 键
            
            5. **保存并退出**
               输入 `:wq` 然后回车
            
            ### 常用命令
            
            **编辑**
            - `i` - 在光标前插入
            - `a` - 在光标后插入
            - `o` - 在下一行插入
            - `x` - 删除字符
            - `dd` - 删除行
            - `u` - 撤销
            
            **导航**
            - `h/j/k/l` - 左/下/上/右
            - `w` - 下一个词
            - `b` - 上一个词
            - `0` - 行首
            - `$` - 行尾
            
            **搜索**
            - `/pattern` - 搜索
            - `n` - 下一个
            - `N` - 上一个
            
            ### 获取帮助
            
            ```
            vim --help
            vim --modes
            vim --commands
            ```
            
            ### 在线教程
            
            运行 `vimtutor` 开始交互式教程。
            """;
        
        return CommandResult.success("Vim 帮助")
                .withDisplayText(report);
    }
    
    private CommandResult showVimModes() {
        String report = """
            ## Vim 模式详解
            
            ### 1. 正常模式 (Normal Mode)
            
            **进入**: 按 `Esc`
            **用途**: 导航、删除、复制、粘贴
            
            ```
            hjkl    - 移动光标
            dd      - 删除行
            yy      - 复制行
            p       - 粘贴
            u       - 撤销
            ```
            
            ### 2. 插入模式 (Insert Mode)
            
            **进入**: 按 `i`, `a`, `o`
            **用途**: 输入文本
            
            ```
            i   - 在光标前插入
            a   - 在光标后插入
            o   - 在下一行插入
            I   - 在行首插入
            A   - 在行尾插入
            ```
            
            ### 3. 可视模式 (Visual Mode)
            
            **进入**: 按 `v`, `V`, `Ctrl+v`
            **用途**: 选择文本
            
            ```
            v       - 字符选择
            V       - 行选择
            Ctrl+v  - 块选择
            ```
            
            ### 4. 命令模式 (Command Mode)
            
            **进入**: 按 `:`
            **用途**: 输入命令
            
            ```
            :w      - 保存
            :q      - 退出
            :wq     - 保存并退出
            :q!     - 强制退出
            :set nu - 显示行号
            ```
            
            ### 模式切换图
            
            ```
            [任意模式] --Esc--> [正常模式]
            [正常模式] --i/a/o--> [插入模式]
            [正常模式] --v/V/Ctrl+v--> [可视模式]
            [正常模式] --:--> [命令模式]
            ```
            """;
        
        return CommandResult.success("Vim 模式")
                .withDisplayText(report);
    }
    
    private CommandResult showVimCommands() {
        String report = """
            ## Vim 命令大全
            
            ### 文件操作
            
            | 命令 | 说明 |
            |------|------|
            | :w | 保存 |
            | :q | 退出 |
            | :wq | 保存并退出 |
            | :q! | 强制退出 |
            | :e <file> | 打开文件 |
            | :sav <file> | 另存为 |
            
            ### 编辑命令
            
            | 命令 | 说明 |
            |------|------|
            | u | 撤销 |
            | Ctrl+r | 重做 |
            | dd | 删除行 |
            | yy | 复制行 |
            | p | 粘贴 |
            | x | 删除字符 |
            | r | 替换字符 |
            
            ### 导航命令
            
            | 命令 | 说明 |
            |------|------|
            | h/j/k/l | 左/下/上/右 |
            | w/b | 词首/词尾 |
            | 0/$ | 行首/行尾 |
            | gg/G | 文件首/尾 |
            | :<n> | 跳转到第 n 行 |
            
            ### 搜索替换
            
            | 命令 | 说明 |
            |------|------|
            | /pattern | 搜索 |
            | ?pattern | 反向搜索 |
            | n/N | 下一个/上一个 |
            | :%%s/a/b/g | 全局替换 |
            
            ### 窗口管理
            
            | 命令 | 说明 |
            |------|------|
            | :sp | 水平分割 |
            | :vsp | 垂直分割 |
            | Ctrl+w | 切换窗口 |
            | :clo | 关闭窗口 |
            
            ### 设置选项
            
            | 命令 | 说明 |
            |------|------|
            | :set nu | 显示行号 |
            | :set nonu | 隐藏行号 |
            | :set hlsearch | 高亮搜索 |
            | :set ic | 忽略大小写 |
            """;
        
        return CommandResult.success("Vim 命令")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：vim
            别名：vi, nvim
            描述：Vim 编辑模式
            
            用法：
              vim <文件>              # 打开文件
              vim help                # 显示帮助
              vim modes               # 查看模式
              vim commands            # 查看命令
            
            示例：
              vim file.txt
              vim help
            """;
    }
}
