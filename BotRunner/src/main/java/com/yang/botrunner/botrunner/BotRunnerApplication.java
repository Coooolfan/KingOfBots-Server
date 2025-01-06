package com.yang.botrunner.botrunner;

import com.yang.botrunner.botrunner.service.BotRunningService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotRunnerApplication {
    public static void main(String[] args) {
        BotRunningService.botPool.start();
        SpringApplication.run(BotRunnerApplication.class, args);
    }

}
