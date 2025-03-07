package com.yang.botrunner.botrunner.service;

import com.yang.botrunner.botrunner.Utils.BotPool;
import org.springframework.stereotype.Service;

@Service
public class BotRunningService {
    public final static BotPool botPool = new BotPool();

    public String addBot(Integer userId, String botCode, String input, String language,String targetFile) {
        botPool.addBot(userId,botCode,input,language,targetFile);
        return "Bot added";
    }
}
