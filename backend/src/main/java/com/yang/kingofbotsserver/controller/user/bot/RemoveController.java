package com.yang.kingofbotsserver.controller.user.bot;

import com.yang.kingofbotsserver.service.user.bot.RemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RemoveController {
    @Autowired
    private RemoveService removeService;
    @PostMapping("/api/user/bot/remove/")
    public Map<String,String> remove(@RequestBody Map<String,String> data){
        return removeService.remove(data);
    }
}
