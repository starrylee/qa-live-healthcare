# QA Service Question - Spring Boot Dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 先下载依赖（利用 Docker 缓存层）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用（跳过测试）
RUN mvn clean package -DskipTests

# 生产镜像
FROM eclipse-temurin:17-jre

# 安装必要的工具
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8081

# 健康检查 - 检查应用是否响应（根路径返回404也是正常）
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/ | grep -q "404" && exit 0 || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
