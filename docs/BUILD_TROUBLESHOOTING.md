# 🔧 JClaw 构建问题排查指南

> **创建日期**: 2026-04-03  
> **来源**: 波哥指导  
> **状态**: 必须遵守

---

## 📋 构建问题排查流程

**遇到编译/构建问题时，按以下顺序分析：**

### 1️⃣ Java JDK 版本检查

```bash
# 检查 JDK 版本
java -version
javac -version

# 检查 Maven 使用的 JDK
mvn -version
```

**注意事项**:
- JDK 21 需要 Lombok 1.18.30+ (推荐 1.18.38+)
- Spring Boot 3.5.x 需要 JDK 17+
- 确保 `JAVA_HOME` 环境变量正确

---

### 2️⃣ Maven 构建插件版本检查

```bash
# 查看有效 POM
mvn help:effective-pom | grep -A5 "maven-compiler-plugin"

# 查看插件版本
mvn help:effective-pom | grep "lombok.version"
```

**关键配置**:
```xml
<!-- pom.xml -->
<properties>
    <java.version>21</java.version>
    <lombok.version>1.18.38</lombok.version>
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <source>21</source>
                <target>21</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>1.18.38</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

### 3️⃣ 依赖三方件版本兼容性检查

```bash
# 查看依赖树
mvn dependency:tree

# 查看特定依赖
mvn dependency:tree | grep lombok
mvn dependency:tree | grep spring-boot
```

**版本兼容性矩阵**:

| Spring Boot | Lombok | JDK | MyBatis-Plus |
|-------------|--------|-----|--------------|
| 3.5.4 | 1.18.38+ | 17-23 | 3.5.5+ |
| 3.2.4 | 1.18.30+ | 17-21 | 3.5.5+ |
| 3.1.x | 1.18.28+ | 17-20 | 3.5.3+ |

**常见兼容性问题**:

1. **Lombok 版本过低**
   - 症状：`程序包 lombok 不存在`、`找不到符号 @Data`
   - 解决：升级到 1.18.38+
   - 注意：Spring Boot parent 会覆盖 lombok.version，需要在 properties 中显式覆盖

2. **JDK 版本不匹配**
   - 症状：`不支持的语言级别`、`无法识别的选项`
   - 解决：确保 maven-compiler-plugin 的 source/target 与 JDK 一致

3. **注解处理器未配置**
   - 症状：Lombok 注解不生效
   - 解决：在 maven-compiler-plugin 中配置 annotationProcessorPaths

---

## 🔍 快速诊断命令

```bash
# 1. 检查 Java 环境
java -version && javac -version

# 2. 检查 Maven 环境
mvn -version

# 3. 清理并重新编译（强制更新）
mvn clean compile -U

# 4. 查看 Lombok 版本
mvn help:effective-pom | grep lombok.version

# 5. 查看编译错误详情
mvn compile 2>&1 | grep "ERROR" | head -20

# 6. 跳过测试打包
mvn package -DskipTests -Dmaven.test.skip=true
```

---

## 📝 本次构建问题记录

**问题**: Lombok 注解处理失败，200+ 编译错误

**根本原因**:
1. Spring Boot 3.2.4 默认 Lombok 版本为 1.18.30
2. JDK 21 需要 Lombok 1.18.38+
3. 未在 pom.xml 中显式覆盖 lombok.version
4. 未配置 maven-compiler-plugin 的 annotationProcessorPaths

**解决方案**:
```xml
<properties>
    <java.version>21</java.version>
    <lombok.version>1.18.38</lombok.version>  <!-- 关键！覆盖 parent 的版本 -->
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <source>21</source>
                <target>21</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>1.18.38</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

**最终技术栈**:
- Spring Boot: 3.5.4
- JDK: 21
- Lombok: 1.18.38
- MyBatis-Plus: 3.5.5
- PostgreSQL: runtime
- Redis: starter

---

## ✅ 检查清单

编译前必查:
- [ ] JDK 版本与项目要求一致
- [ ] Maven 版本 >= 3.8
- [ ] pom.xml 中显式声明 lombok.version
- [ ] maven-compiler-plugin 配置 annotationProcessorPaths
- [ ] 依赖树无版本冲突

---

*创建者：可乐 🥤*  
*最后更新：2026-04-03*
