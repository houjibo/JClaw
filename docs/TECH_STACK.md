# 🎯 JClaw 技术栈版本

> **最后更新**: 2026-04-03  
> **状态**: 生产就绪

---

## 📦 核心依赖

| 组件 | 版本 | 说明 |
|------|------|------|
| **Spring Boot** | 3.5.4 | 核心框架 |
| **JDK** | 21 | Java 运行环境 |
| **Lombok** | 1.18.38 | 代码生成 |
| **MyBatis-Plus** | 3.5.5 | ORM 框架 |

---

## 🗄️ 数据库

| 数据库 | 版本 | 用途 |
|--------|------|------|
| **PostgreSQL** | 16 | 主数据库（业务数据 + 记忆 + 追溯） |
| **FalkorDB** | edge | 图数据库（意图图谱 + 调用链） |
| **Redis** | 7 | 缓存（工作记忆 + 会话缓存） |

---

## 🌐 前端

| 框架 | 版本 | 用途 |
|------|------|------|
| **Vue 3** | 3.4.x | 前端框架 |
| **Vite** | 5.x | 构建工具 |

---

## 🛠️ 构建工具

| 工具 | 版本 | 说明 |
|------|------|------|
| **Maven** | 3.9.14+ | 构建工具 |
| **maven-compiler-plugin** | 3.11.0 | 编译插件 |
| **spring-boot-maven-plugin** | 3.5.4 | Spring Boot 插件 |

---

## ⚠️ 版本兼容性

### 已验证的兼容组合

| Spring Boot | Lombok | JDK | 状态 |
|-------------|--------|-----|------|
| 3.5.4 | 1.18.38 | 21 | ✅ 生产使用 |
| 3.2.4 | 1.18.30 | 21 | ⚠️ Lombok 需升级 |

### 已知兼容性问题

1. **Spring Boot 3.2.4 + Lombok 1.18.30 + JDK 21**
   - 问题：Lombok 注解处理失败
   - 解决：升级 Lombok 到 1.18.38+

2. **Spring Boot parent 覆盖 Lombok 版本**
   - 问题：pom.xml 中声明的 Lombok 版本被 parent 覆盖
   - 解决：在 `<properties>`中显式覆盖`<lombok.version>`

---

## 📋 pom.xml 关键配置

```xml
<properties>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <lombok.version>1.18.38</lombok.version>  <!-- 关键！覆盖 parent 版本 -->
</properties>

<dependencies>
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.38</version>  <!-- 显式声明版本 -->
        <scope>provided</scope>
    </dependency>
    
    <!-- MyBatis-Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.5.5</version>
    </dependency>
</dependencies>

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

## 🔍 验证命令

```bash
# 验证 Java 版本
java -version

# 验证 Maven 版本
mvn -version

# 验证 Lombok 版本
mvn help:effective-pom | grep lombok.version

# 验证依赖树
mvn dependency:tree | grep -E "lombok|spring-boot|mybatis"

# 清理并重新编译
mvn clean package -DskipTests -Dmaven.test.skip=true -U
```

---

*维护者：可乐 🥤*
