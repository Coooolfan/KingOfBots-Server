package com.yang.botrunner.botrunner;

import com.yang.botrunner.botrunner.service.BotRunningService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling //开启定时任务

public class BotRunnerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotRunnerApplication.class, args);
    }

}
