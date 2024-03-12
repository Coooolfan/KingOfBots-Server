package com.yang.kingofbotsserver.service.impl.user.account;

import com.yang.kingofbotsserver.pojo.User;
import com.yang.kingofbotsserver.service.impl.utils.UserDetailsImpl;
import com.yang.kingofbotsserver.service.user.account.InfoService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service

public class InfoServiceImpl implements InfoService {
    @Override
    public Map<String, String> getInfo() {
        Map<String, String> map = new HashMap<>();
        UserDetailsImpl loginUser;
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        } catch (RuntimeException e) {
            map.put("msg", "Invalid token");
            return map;
        }

        User user = loginUser.getUser();

        map.put("msg", "success");
        map.put("id", user.getId().toString());
        map.put("username", user.getUsername());
        map.put("photo", user.getPhoto());
        return map;
    }
}
