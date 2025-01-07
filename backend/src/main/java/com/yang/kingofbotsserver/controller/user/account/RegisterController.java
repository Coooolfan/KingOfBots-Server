package com.yang.kingofbotsserver.controller.user.account;

import com.yang.kingofbotsserver.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RegisterController {
    @Autowired
    RegisterService registerService;
    @PostMapping("/api/user/account/register/")
    public Map<String,String> regUser(@RequestBody Map<String,String> map){
        String username = map.get("username");
        String password = map.get("password");
        return registerService.register(username,password);
    }
}
