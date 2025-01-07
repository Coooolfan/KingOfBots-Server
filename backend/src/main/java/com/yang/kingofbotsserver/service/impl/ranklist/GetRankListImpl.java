package com.yang.kingofbotsserver.service.impl.ranklist;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yang.kingofbotsserver.mapper.UserMapper;
import com.yang.kingofbotsserver.pojo.User;
import com.yang.kingofbotsserver.service.ranklist.GetRankListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRankListImpl implements GetRankListService {
    @Autowired
    private UserMapper userMapper;
    private final static Integer pageSize = 5;

    @Override
    public JSONObject getList(Integer page) {
        IPage<User> userIPage = new Page<>(page, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(User::getRating);
        List<User> users = userMapper.selectPage(userIPage,queryWrapper).getRecords();
        users.forEach(user -> user.setPassword(null));
        JSONObject resp = new JSONObject();
        resp.put("users", users);
        resp.put("users_count", userMapper.selectCount(null));
        return resp;
    }
}
