package com.yang.kingofbotsserver.controller.user.bot;

import com.yang.kingofbotsserver.service.user.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UpdateController {
    @Autowired
    UpdateService updateService;
    @PostMapping("/api/user/bot/update/")
    public Map<String,String> update(@RequestBody Map<String,String> data){
        return updateService.update(data);
    }

    @PostMapping("/api/revice/bot/update/")
    public Map<String,String> revice(@RequestBody Map<String,String> data){
        return updateService.reviceComplied(data);
    }
}
