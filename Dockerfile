# 1. 完美替换为目前国内能正常拉取的轻量级 JDK 21 镜像
FROM eclipse-temurin:21-jre-alpine

# 2. 设置容器内部的工作目录
WORKDIR /app

# 3. 将本地 Maven 打包好的 jar 包复制到容器中
COPY target/Restaurant-0.0.1-SNAPSHOT.jar app.jar

# 4. 暴露后端服务的端口
EXPOSE 8080

# 5. 容器启动时执行 Java 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]