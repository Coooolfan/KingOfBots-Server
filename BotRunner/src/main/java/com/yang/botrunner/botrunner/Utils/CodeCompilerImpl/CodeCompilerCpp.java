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
public class CodeCompilerCpp implements CodeCompiler {
    private static RestTemplate restTemplate;
    @Value("${kob.backend.host}")
    private String HOST;
    @Value("${kob.bot.result.cpp}")
    private String RESULT_Cpp;
    @Value("${kob.cpp.build.path}")
    private String BUILD_PATH;
    @Value("${kob.cpp.bin.path}")
    private String Gcc_BUILD_PATH;
    private final static String URL = "/api/revice/bot/update/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        CodeCompilerCpp.restTemplate = restTemplate;
    }

    @Override
    public void compile(String sourceCode, Integer botId) {
        System.out.println("sourceCode: " + sourceCode);
        System.out.println("HOST: " + HOST);
        System.out.println("Gcc_BUILD_PATH: " + Gcc_BUILD_PATH);
        /*
        1. 预生产一个UUID，用于命名这个Bot的产物
        2. 写入sourceCode到 BUILD_PATH/uuid.cpp
        4. 调用g++ -o uuid uuid.cpp命令
        5. 如果编译成功，把uuid文件移到专用的目录下（生成的uuid文件应该为BUILD_PATH/uuid）
        6. 调用restTemplate.postForEntity(URL, data, String.class)把编译好的文件路径和UUID传给后端
         */
        UUID uuid = UUID.randomUUID();
        Path sourceCodePath = Path.of(BUILD_PATH + "/" + uuid + ".cpp");
        Path binPath = Path.of(BUILD_PATH + "/" + uuid);
        Path storageBinPath = Path.of(RESULT_Cpp + "/" + uuid);
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            binPath = Path.of(BUILD_PATH + "/" + uuid + ".exe");
            storageBinPath = Path.of(RESULT_Cpp + "/" + uuid + ".exe");
        }

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("id", botId.toString());
        // 抹平平台差异，尽量使用java完成文件操作
        try {
            java.nio.file.Files.writeString(sourceCodePath, sourceCode);
            ProcessBuilder processBuilder =
                    new ProcessBuilder(Gcc_BUILD_PATH, "-o", uuid.toString(), sourceCodePath.toString());
            processBuilder.directory(Path.of(BUILD_PATH).toFile());
            processBuilder.inheritIO(); // 将子进程的输入输出流与当前进程的输入输出流连接，可以在控制台看到gradle的输出
            Process process = processBuilder.start();
            boolean completed = process.waitFor(30, TimeUnit.SECONDS);
            int exitCode = completed ? process.exitValue() : -1;
            if (exitCode == 0) {
                System.out.println("gcc build completed successfully.");
                System.out.println("Cpp Bin: " + binPath);
                // 移动bin包到指定目录
                java.nio.file.Files.move(binPath, storageBinPath);
                java.nio.file.Files.delete(sourceCodePath);
                map.add("status", "ready");
                map.add("target_file", storageBinPath.toString());
                restTemplate.postForEntity(HOST + URL, map, String.class);
            } else if (exitCode == -1) {
                System.out.println("gcc build timed out ");
                map.add("status", "timeout");
                map.add("target_file", "-");
                restTemplate.postForEntity(HOST + URL, map, String.class);
            } else {
                map.add("status", "failed");
                map.add("target_file", "-");
                restTemplate.postForEntity(HOST + URL, map, String.class);
                System.out.println("gcc build failed with exit code: " + exitCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
