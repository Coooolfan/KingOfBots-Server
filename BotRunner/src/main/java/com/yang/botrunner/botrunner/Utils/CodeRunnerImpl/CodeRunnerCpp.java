package com.yang.botrunner.botrunner.Utils.CodeRunnerImpl;

import com.yang.botrunner.botrunner.Utils.Bot;
import com.yang.botrunner.botrunner.Utils.CodeRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Component
public class CodeRunnerCpp extends Thread implements CodeRunner {
    private Bot bot;
    private static RestTemplate restTemplate;
    @Value("${kob.backend.host}")
    private String HOST;
    private final static String URL = "/pk/receive/bot/move/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        CodeRunnerCpp.restTemplate = restTemplate;
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
            direction = runCpp(bot.getTargetFile(), bot.getInput());
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
        restTemplate.postForObject(HOST + URL, data, String.class);
    }

    /**
     * 运行Cpp文件并获取输出结果
     *
     * @param CppPath  Cpp exe的路径
     * @param argument 传递给Cpp exe命令行参数
     * @return Cpp exe的输出结果
     * @throws IOException          如果IO操作失败
     * @throws InterruptedException 如果进程执行被中断
     */
    public String runCpp(String CppPath, String argument) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(CppPath, argument);
        System.out.println(processBuilder.command());
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
            throw new InterruptedException("Cpp exe执行超时");
        }

        // 检查进程退出值
        if (process.exitValue() != 0) {
            throw new IOException("Cpp exe程序执行失败，退出码: " + process.exitValue() + ", 输出: " + output);
        }

        return output.toString().trim();
    }
}
