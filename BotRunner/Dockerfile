FROM ubuntu:24.04

LABEL authors="Yang"

# 避免安装过程中的交互
ENV DEBIAN_FRONTEND=noninteractive

# 更新软件包列表并安装所需工具
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    python3 \
    python3-pip \
    g++ \
    curl \
    gnupg \
    unzip \
    wget \
    && curl -fsSL https://deb.nodesource.com/setup_18.x | bash - \
    && apt-get update && apt-get install -y nodejs \
    # 安装Gradle
    && wget https://services.gradle.org/distributions/gradle-8.12-bin.zip \
    && unzip -d /opt/gradle gradle-8.12-bin.zip \
    && rm gradle-8.12-bin.zip \
    # 设置Gradle环境变量
    && echo "export GRADLE_HOME=/opt/gradle/gradle-8.12" > /etc/profile.d/gradle.sh \
    && echo "export PATH=\${GRADLE_HOME}/bin:\${PATH}" >> /etc/profile.d/gradle.sh \
    && chmod +x /etc/profile.d/gradle.sh \
    # 使环境变量立即生效
    && ln -s /opt/gradle/gradle-8.12/bin/gradle /usr/bin/gradle \
    # 清理缓存减小镜像大小
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    # 创建各个目录
    && mkdir -p /app/compiler/java/src/main/java \
    && mkdir -p /app/compiler/cpp \
    && mkdir -p /app/compiled/java \
    && mkdir -p /app/compiled/cpp

# 验证Gradle安装
RUN gradle --version

WORKDIR /app

COPY target/BotRunner-0.0.1-SNAPSHOT.jar /app/application.jar

EXPOSE 8082

# 启动Spring Boot应用
CMD ["java", "-jar", "/app/application.jar"]