package com.yang.botrunner.botrunner.Utils.CodeCompilerImpl;

import com.yang.botrunner.botrunner.Utils.CodeCompiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class CodeCompilerJava implements CodeCompiler {
    private static RestTemplate restTemplate;
    @Value("${kob.backend.host}")
    private String HOST;
    @Value("${kob.gradle.bin.path}")
    private String GRADLE_BIN_PATH;
    @Value("${kob.gradle.build.path}")
    private String GRADLE_BUILD_PATH;
    @Value("${kob.bot.result.java}")
    private String RESULT_JAVA;
    private final static String URL = "/api/revice/bot/update/";

    private final static String BUILD_GRADLE_COTENT = """
            plugins {
                id 'java'
            }
            
            repositories {
                mavenCentral()
            }
            
            jar {
                archiveFileName = 'UUID.jar'
                manifest {
                    attributes 'Main-Class': 'Bot'
                }
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
            
            build.dependsOn jar""";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        CodeCompilerJava.restTemplate = restTemplate;
    }

    @Override
    public void compile(String sourceCode, Integer botId) {
        System.out.println("sourceCode: " + sourceCode);
        System.out.println("HOST: " + HOST);
        System.out.println("GRADLE_BUILD_PATH: " + GRADLE_BUILD_PATH);
        /*
        1. 预生产一个UUID，用于命名这个Bot的产物
        2. 生成对应的build.gradle文件（主要编辑其中的产物名字），写入到GRADLE_BUILD_PATH/build.gradle
        3. 写入sourceCode到 GRADLE_BUILD_PATH/src/main/java/Bot.java
        4. 调用gradle build命令
        5. 如果编译成功，把jar包移到专用的目录下（生成的jar包应该在GRADLE_BUILD_PATH/build/libs下）
        6. 调用restTemplate.postForEntity(URL, data, String.class)把编译好的jar包的路径和UUID传给后端
         */
        UUID uuid = UUID.randomUUID();
        String buildGradleCotent = BUILD_GRADLE_COTENT.replace("UUID", uuid.toString());
        Path gradleBuildPath = Path.of(GRADLE_BUILD_PATH);
        Path gradleBuildConfigPath = gradleBuildPath.resolve("build.gradle");
        Path sourceCodePath = gradleBuildPath.resolve("src/main/java/Bot.java");
        Path jarPath = gradleBuildPath.resolve("build/libs/" + uuid + ".jar");
        Path storageJarPath = gradleBuildPath.resolve(RESULT_JAVA + "/" + uuid + ".jar");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("id", botId.toString());
        // 抹平平台差异，尽量使用java完成文件操作
        try {
            java.nio.file.Files.writeString(gradleBuildConfigPath, buildGradleCotent);
            java.nio.file.Files.writeString(sourceCodePath, sourceCode);
            ProcessBuilder processBuilder =
                    new ProcessBuilder(GRADLE_BIN_PATH, "-b", gradleBuildConfigPath.toString(), "build");
            processBuilder.directory(gradleBuildPath.toFile());
            processBuilder.inheritIO(); // 将子进程的输入输出流与当前进程的输入输出流连接，可以在控制台看到gradle的输出
            Process process = processBuilder.start();
            boolean completed = process.waitFor(30, TimeUnit.SECONDS);
            int exitCode = completed ? process.exitValue() : -1;
            if (exitCode == 0) {
                System.out.println("Gradle build completed successfully.");
                System.out.println("jarPath: " + jarPath);
                // 移动jar包到指定目录
                java.nio.file.Files.move(jarPath, storageJarPath);
                map.add("status", "ready");
                map.add("target_file", storageJarPath.toString());
                restTemplate.postForEntity(HOST + URL, map, String.class);
            } else if (exitCode == -1) {
                System.out.println("Gradle build timed out ");
                map.add("status", "timeout");
                map.add("target_file", "-");
                restTemplate.postForEntity(HOST + URL, map, String.class);
            } else {
                map.add("status", "failed");
                map.add("target_file", "-");
                restTemplate.postForEntity(HOST + URL, map, String.class);
                System.out.println("Gradle build failed with exit code: " + exitCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
