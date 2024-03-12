package com.yang.kingofbotsserver.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yang.kingofbotsserver.mapper.UserMapper;
import com.yang.kingofbotsserver.service.user.account.RegisterService;
import com.yang.kingofbotsserver.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public Map<String, String> register(String username, String passowrd) {
        Map<String, String> map = new HashMap<>();
        if (username == null || username.trim().isEmpty()) {
            map.put("msg", "用户名不能为空");
            return map;
        }
        if (passowrd == null) {
            map.put("msg", "密码不能为空");
            return map;
        }
//删除字符串的头部和尾部的空白字符。这些空白字符包括：空格、制表符、换行符等
        username = username.trim();
        if (username.length() > 100) {
            map.put("msg", "用户名长度不能大于100");
            return map;
        }
        if (passowrd.length() > 100) {
            map.put("msg", "密码名长度不能大于100");
            return map;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        List<User> users = userMapper.selectList(queryWrapper);
        if(!users.isEmpty()){
            map.put("msg","此用户名已存在");
            return map;
        }

        String encodedPassword = passwordEncoder.encode(passowrd);
        String photo = "https://res.coooolfan.com/c-q.jpg";
        User user = new User(null,username,encodedPassword,photo);
        userMapper.insert(user);
        map.put("msg","success");
        return map;


    }
}
