package com.yang.kingofbotsserver.controller.user.bot;

import com.yang.kingofbotsserver.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AddController {
    @Autowired
    private AddService addService;
    @PostMapping("/api/user/bot/add/")
    public Map<String,String> add(@RequestBody Map<String,String> data){
        return addService.add(data);
    }
}
