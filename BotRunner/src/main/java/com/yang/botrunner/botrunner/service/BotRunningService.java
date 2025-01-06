package com.yang.botrunner.botrunner.service;

import com.yang.botrunner.botrunner.Utils.BotPool;
import org.springframework.stereotype.Service;

@Service
public class BotRunningService {
    public final static BotPool botPool = new BotPool();

    public String addBot(Integer userId,String botCode,String input){
        botPool.addBot(userId,botCode,input);
        return "Bot added";
    }
}
