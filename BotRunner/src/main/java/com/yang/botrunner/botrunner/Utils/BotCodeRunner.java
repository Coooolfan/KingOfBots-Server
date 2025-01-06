package com.yang.botrunner.botrunner.Utils;

import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class BotCodeRunner extends Thread {
    private Bot bot;
    private static RestTemplate restTemplate;
    private final static String URL = "http://localhost:8080/pk/receive/bot/move/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        BotCodeRunner.restTemplate = restTemplate;
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

    private String addUUID(String code, String uuid) {
        int index = code.indexOf(" implements com.yang.botrunner.botrunner.Utils.UserBotInterface");
        return code.substring(0, index) + uuid + code.substring(index);
    }

    @Override
    public void run() {
        System.out.println("BotRunner " + bot.getUserId() + " started");
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        UserBotInterface userBotInterface = Reflect.compile(
                "com.yang.botrunner.botrunner.Utils.UserBotImpl" + uuid,
                addUUID(bot.getBotCode(), uuid)
        ).create().get();

        Integer direction = userBotInterface.nextMove(bot.getInput());

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("direction", direction.toString());

        restTemplate.postForObject(URL, data, String.class);
        System.out.println("Bot " + bot.getUserId() + " direction: " + direction);
    }
}
