# JClaw 技能开发示例

展示如何开发和发布自定义 JClaw 技能。

## 技能结构

```
my-skill/
├── skill.json          # 技能元数据（必需）
├── src/
│   └── MySkill.java    # 技能实现
├── README.md           # 使用说明
└── test/
    └── MySkillTest.java # 测试用例
```

## skill.json 格式

```json
{
  "name": "weather-skill",
  "version": "1.0.0",
  "description": "天气查询技能，支持全球城市",
  "author": "Your Name",
  "license": "MIT",
  "tags": ["weather", "api", "utility"],
  "dependencies": {
    "jclaw-core": ">=1.0.0"
  },
  "repository": "https://github.com/yourname/weather-skill",
  "documentation": "https://github.com/yourname/weather-skill#readme"
}
```

## 技能开发步骤

### 1. 创建技能目录

```bash
mkdir weather-skill
cd weather-skill
```

### 2. 创建 skill.json

```bash
cat > skill.json << 'EOF'
{
  "name": "weather-skill",
  "version": "1.0.0",
  "description": "天气查询技能",
  "author": "Your Name",
  "tags": ["weather", "api"]
}
EOF
```

### 3. 实现技能

```java
package com.jclaw.skills;

import com.jclaw.skill.Skill;
import com.jclaw.skill.SkillContext;
import java.util.Map;

public class WeatherSkill implements Skill {
    
    @Override
    public String getName() {
        return "weather";
    }
    
    @Override
    public String getDescription() {
        return "查询指定城市的天气";
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> params, SkillContext context) {
        String city = (String) params.get("city");
        
        // 调用天气 API
        String weather = fetchWeather(city);
        
        return Map.of(
            "city", city,
            "weather", weather,
            "temperature", "25°C"
        );
    }
    
    private String fetchWeather(String city) {
        // 实现天气查询逻辑
        return "晴朗";
    }
}
```

### 4. 测试技能

```bash
jclaw skill test ./weather-skill
```

### 5. 发布技能

```bash
jclaw skill publish ./weather-skill
```

### 6. 安装技能

```bash
jclaw skill install ./weather-skill-1.0.0.zip
```

### 7. 使用技能

```java
client.skill("weather", Map.of("city", "北京"));
```

## 技能 API

Skill 接口定义：

```java
public interface Skill {
    String getName();
    String getDescription();
    Map<String, Object> execute(Map<String, Object> params, SkillContext context);
    default void init() {}
    default void destroy() {}
}
```

## 最佳实践

1. **单一职责**：一个技能只做一件事
2. **错误处理**：优雅处理异常情况
3. **文档完善**：提供清晰的使用说明
4. **测试覆盖**：编写单元测试和集成测试
5. **版本管理**：遵循语义化版本规范

## 示例技能

- [File Reader](./file-reader/) - 文件读取技能
- [Web Search](./web-search/) - 网络搜索技能
- [Code Analyzer](./code-analyzer/) - 代码分析技能
- [Weather](./weather/) - 天气查询技能

## 资源

- [技能开发指南](https://docs.jclaw.dev/skills/developing)
- [技能发布流程](https://docs.jclaw.dev/skills/publishing)
- [技能市场](https://skills.jclaw.dev)
