package com.yang.kingofbotsserver.service.impl.user.account;

import com.yang.kingofbotsserver.pojo.User;
import com.yang.kingofbotsserver.service.impl.utils.UserDetailsImpl;
import com.yang.kingofbotsserver.service.user.account.LoginService;
import com.yang.kingofbotsserver.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Map<String, String> getToken(String name, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(name, password);
        Authentication authenticate;
        Map<String, String> map = new HashMap<>();
        try {
            authenticate = authenticationManager.authenticate(authenticationToken);
            // 用户登录成功后
            UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
            User user = loginUser.getUser();
            String jwt = JwtUtil.createJWT(user.getId().toString());
            map.put("msg", "success");
            map.put("token", jwt);
            return map;
        } catch (Exception e) {
            System.out.println("username: " +name +" password: "+password+"登录失败");
            map.put("msg","账号或密码错误");
            return map;
        }

    }
}
