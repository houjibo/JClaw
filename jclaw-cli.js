#!/usr/bin/env node

/**
 * JClaw CLI 入口
 * 用法：
 *   jclaw chat        - 交互式对话
 *   jclaw skill <name> - 执行技能
 *   jclaw status      - 查看状态
 */

const http = require('http');

const API_BASE = 'http://localhost:18790/api';

// 命令处理
const commands = {
    async chat() {
        console.log('🥤 JClaw 交互式对话');
        console.log('输入 "exit" 退出\n');
        
        const readline = require('readline').createInterface({
            input: process.stdin,
            output: process.stdout
        });
        
        const ask = () => {
            readline.question('👤 你：', async (prompt) => {
                if (prompt.toLowerCase() === 'exit') {
                    readline.close();
                    return;
                }
                
                try {
                    const response = await postJson(`${API_BASE}/ai/chat`, { prompt });
                    console.log(`🤖 JClaw: ${response.response}\n`);
                } catch (e) {
                    console.error(`❌ 错误：${e.message}\n`);
                }
                
                ask();
            });
        };
        
        ask();
    },
    
    async skill(name, ...args) {
        if (!name) {
            console.log('用法：jclaw skill <技能名> [参数...]');
            console.log('\n可用技能:');
            const skills = await getJson(`${API_BASE}/skills`);
            skills.forEach(s => console.log(`  - ${s.name}: ${s.description}`));
            return;
        }
        
        console.log(`执行技能：${name}`);
        
        const params = {};
        args.forEach(arg => {
            const [key, value] = arg.split('=');
            if (key && value) {
                params[key] = value;
            }
        });
        
        try {
            const result = await postJson(`${API_BASE}/skills/execute`, {
                skill: name,
                params
            });
            
            if (result.success) {
                console.log('✅ 成功:', result.content);
            } else {
                console.log('❌ 失败:', result.error);
            }
        } catch (e) {
            console.error('❌ 错误:', e.message);
        }
    },
    
    async status() {
        console.log('📊 JClaw 状态\n');
        
        try {
            const health = await getJson(`${API_BASE}/health`);
            console.log('健康状态:', health.status === 'UP' ? '✅ 运行中' : '❌ 异常');
            console.log('运行时间:', formatUptime(health.uptime));
            
            const skills = await getJson(`${API_BASE}/skills`);
            console.log(`可用技能：${skills.length} 个`);
            
            const config = await getJson(`${API_BASE}/config/status`);
            console.log('AI 配置:', config.checks.aiConfigured ? '✅ 已配置' : '❌ 未配置');
            
        } catch (e) {
            console.error('❌ 无法连接 JClaw 服务，请先启动：java -jar jclaw.jar');
        }
    },
    
    help() {
        console.log(`
🥤 JClaw CLI 用法:

  jclaw chat              交互式对话
  jclaw skill <name>      执行技能
  jclaw status            查看状态
  jclaw help              显示帮助

示例:
  jclaw skill bash command="ls -la"
  jclaw skill git command=status repoPath=.
  jclaw skill todo_write action=create content="买可乐"
`);
    }
};

// HTTP 工具函数
function getJson(url) {
    return new Promise((resolve, reject) => {
        http.get(url, (res) => {
            let data = '';
            res.on('data', chunk => data += chunk);
            res.on('end', () => {
                try {
                    resolve(JSON.parse(data));
                } catch (e) {
                    reject(new Error('解析响应失败'));
                }
            });
        }).on('error', reject);
    });
}

function postJson(url, body) {
    return new Promise((resolve, reject) => {
        const data = JSON.stringify(body);
        const options = {
            hostname: new URL(url).hostname,
            port: new URL(url).port || 80,
            path: new URL(url).pathname,
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Content-Length': data.length
            }
        };
        
        const req = http.request(options, (res) => {
            let responseData = '';
            res.on('data', chunk => responseData += chunk);
            res.on('end', () => {
                try {
                    resolve(JSON.parse(responseData));
                } catch (e) {
                    reject(new Error('解析响应失败'));
                }
            });
        });
        
        req.on('error', reject);
        req.write(data);
        req.end();
    });
}

function formatUptime(seconds) {
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    const s = seconds % 60;
    return `${h}小时 ${m}分 ${s}秒`;
}

// 主函数
async function main() {
    const args = process.argv.slice(2);
    const command = args[0] || 'help';
    const commandArgs = args.slice(1);
    
    if (commands[command]) {
        await commands[command](...commandArgs);
    } else {
        console.log(`未知命令：${command}`);
        console.log('运行 "jclaw help" 查看帮助');
    }
}

main().catch(console.error);
