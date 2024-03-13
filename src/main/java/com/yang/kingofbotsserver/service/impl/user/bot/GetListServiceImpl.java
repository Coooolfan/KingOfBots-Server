package com.yang.kingofbotsserver.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yang.kingofbotsserver.mapper.BotMapper;
import com.yang.kingofbotsserver.pojo.Bot;
import com.yang.kingofbotsserver.pojo.User;
import com.yang.kingofbotsserver.service.impl.UserDetailsServiceImpl;
import com.yang.kingofbotsserver.service.user.bot.GetListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetListServiceImpl implements GetListService {
    @Autowired
    BotMapper botMapper;

    @Override
    public List<Bot> getList() {
        User user = UserDetailsServiceImpl.getUser();
        QueryWrapper<Bot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        return botMapper.selectList(queryWrapper);
    }
}
