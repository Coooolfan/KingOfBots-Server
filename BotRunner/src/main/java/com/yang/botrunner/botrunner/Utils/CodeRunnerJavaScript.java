package com.yang.botrunner.botrunner.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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

//        Do something……

        sendResponse(bot.getUserId(), "2");
    }

    @Override
    public void sendResponse(Integer userId, String response) {
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", userId.toString());
        data.add("direction", response);

        restTemplate.postForObject(URL, data, String.class);
    }
}
