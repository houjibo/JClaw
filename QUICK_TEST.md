# 快速测试 JClaw 核心功能

## 1. 测试 AI 服务

```bash
cd ~/.openclaw/workspace/projects/code/core/JClaw

# 配置 API Key
cat > application.yml << EOF
jclaw:
  ai:
    zhipu:
      api-key: YOUR_API_KEY_HERE
EOF

# 运行测试
mvn test -Dtest=AiServiceTest
```

## 2. 测试技能系统

```bash
# 测试文件读取
mvn test -Dtest=SkillEngineTest#testFileReadSkill

# 测试 Bash 命令
mvn test -Dtest=SkillEngineTest#testBashSkill
```

## 3. 手动测试

```java
@Autowired
private AiService aiService;

@Autowired
private SkillEngine skillEngine;

// 测试 AI
String response = aiService.chat("你好");
System.out.println(response);

// 测试技能
SkillResult result = skillEngine.execute("bash", Map.of("command", "ls -la"));
System.out.println(result.getContent());
```

## 4. 启动应用

```bash
./jclaw start
tail -f logs/jclaw.log
```
