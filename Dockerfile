# JClaw 应用 Dockerfile

FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# 复制 Maven 配置
COPY pom.xml .
COPY src ./src

# 构建应用
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 复制构建产物
COPY --from=builder /app/target/jclaw-1.0.0-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
    CMD wget -q --spider http://localhost:8080/api/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
