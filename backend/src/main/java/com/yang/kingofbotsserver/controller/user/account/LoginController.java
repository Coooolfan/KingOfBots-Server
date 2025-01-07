package com.yang.kingofbotsserver.controller.user.account;

import com.yang.kingofbotsserver.service.user.account.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/api/user/account/token/")
    public Map<String, String> getToken(@RequestBody Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");
        if (username == null || password == null)
            throw new IllegalArgumentException("Username or password cannot be null");
        return loginService.getToken(username, password);
    }
}
