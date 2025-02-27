package com.yang.botrunner.botrunner.controller;

import com.yang.botrunner.botrunner.service.BotRunningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class BotRunningController {
    @Autowired
    private BotRunningService botRunningService;

    @PostMapping("/bot/add/")
    public String addBot(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        String BotCode = data.getFirst("bot_code");
        String input = data.getFirst("input");
        String language = data.getFirst("language");
        return botRunningService.addBot(userId, BotCode, input,language);
    }
}
