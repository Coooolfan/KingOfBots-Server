package com.yang.kingofbotsserver.service.impl.user.bot;

import com.yang.kingofbotsserver.mapper.BotMapper;
import com.yang.kingofbotsserver.pojo.Bot;
import com.yang.kingofbotsserver.pojo.User;
import com.yang.kingofbotsserver.service.impl.UserDetailsServiceImpl;
import com.yang.kingofbotsserver.service.user.bot.RemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Service
public class RemoveServiceImpl implements RemoveService {
    @Autowired
    BotMapper botMapper;
    @Override
    public Map<String, String> remove(Map<String, String> data) {
        int bot_id = Integer.parseInt(data.get("bot_id"));
        User user = UserDetailsServiceImpl.getUser();
        Bot bot = botMapper.selectById(bot_id);
        Map<String,String> map = new HashMap<>();
        if(bot==null){
            map.put("msg","参数非法：bot_id无效");
            return map;
        }

        if(!bot.getUserId().equals(user.getId())){
            map.put("msg","操作非法：权限不足");
            return map;
        }

        botMapper.deleteById(bot_id);
        map.put("msg","success");
        return map;
    }
}
