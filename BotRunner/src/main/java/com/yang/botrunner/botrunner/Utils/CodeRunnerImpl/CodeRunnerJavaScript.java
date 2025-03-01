package com.yang.botrunner.botrunner.Utils.CodeRunnerImpl;

import com.yang.botrunner.botrunner.Utils.Bot;
import com.yang.botrunner.botrunner.Utils.CodeRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Component
public class CodeRunnerJavaScript extends Thread implements CodeRunner {
    private Bot bot;
    private static RestTemplate restTemplate;
    private final static String URL = "http://localhost:8080/pk/receive/bot/move/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        CodeRunnerJavaScript.restTemplate = restTemplate;
    }

    public void startTimeout(long timeout, Bot bot) {
        this.bot = bot;
        this.start();
        try {
            this.join(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.interrupt();
        }
    }

    @Override
    public void run() {
        System.out.println("BotRunner " + bot.getUserId() + " started");

        String direction = "-1";
        try {
            direction = runJavaScriptCode(bot.getBotCode(), bot.getInput());
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendResponse(bot.getUserId(), direction);
    }

    @Override
    public void sendResponse(Integer userId, String response) {
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", userId.toString());
        data.add("direction", response);
        System.out.println(data);
        restTemplate.postForObject(URL, data, String.class);
    }

    /**
     * 执行JavaScript代码并获取输出结果
     *
     * @param code     JavaScript代码字符串
     * @param argument 传递给JavaScript脚本的命令行参数
     * @return JavaScript代码的输出结果
     * @throws IOException          如果IO操作失败
     * @throws InterruptedException 如果进程执行被中断
     */
    public static String runJavaScriptCode(String code, String argument) throws IOException, InterruptedException {
        // 创建临时JavaScript文件
        Path tempFile = Files.createTempFile("js_script_", ".js");
        Files.write(tempFile, code.getBytes());

        try {
            // node [脚本路径] [参数]
            ProcessBuilder processBuilder = new ProcessBuilder("node", tempFile.toString(), argument);
            processBuilder.redirectErrorStream(true); // 合并标准错误和标准输出

            // 启动进程
            Process process = processBuilder.start();

            // 读取输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }

            // 等待进程完成，设置超时时间为10秒
            boolean completed = process.waitFor(10, TimeUnit.SECONDS);
            if (!completed) {
                process.destroyForcibly();
                throw new InterruptedException("JavaScript脚本执行超时");
            }

            // 检查进程退出值
            if (process.exitValue() != 0) {
                throw new IOException("JavaScript脚本执行失败，退出码: " + process.exitValue());
            }

            return output.toString().trim();
        } finally {
            // 清理临时文件
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException e) {
                // 记录但不抛出异常
                System.err.println("无法删除临时文件: " + e.getMessage());
            }
        }
    }
}
