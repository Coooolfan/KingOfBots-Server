FROM ubuntu:22.04

LABEL authors="Yang"

# 避免安装过程中的交互
ENV DEBIAN_FRONTEND=noninteractive

# 更新软件包列表并安装所需工具
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    gradle \
    python3 \
    python3-pip \
    g++ \
    curl \
    gnupg \
    && curl -fsSL https://deb.nodesource.com/setup_18.x | bash - \
    && apt-get update && apt-get install -y nodejs \
    # 清理缓存减小镜像大小
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/BotRunner-0.0.1-SNAPSHOT.jar /app/application.jar

EXPOSE 8082

# 启动Spring Boot应用
CMD ["java", "-jar", "/app/application.jar"]