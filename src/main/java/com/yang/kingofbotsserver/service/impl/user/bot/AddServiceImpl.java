package com.yang.kingofbotsserver.service.impl.user.bot;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yang.kingofbotsserver.pojo.Bot;
import com.yang.kingofbotsserver.pojo.User;
import com.yang.kingofbotsserver.service.impl.UserDetailsServiceImpl;
import com.yang.kingofbotsserver.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddServiceImpl implements AddService {
    @Autowired
    BaseMapper<Bot> botMapper;

    @Override
    public Map<String, String> add(Map<String, String> data) {
        User user = UserDetailsServiceImpl.getUser();
        Map<String, String> map = new HashMap<>();

        String title = data.get("title");
        String desc = data.get("description");
        String content = data.get("content");

        if (title == null || title.isEmpty()) {
            map.put("msg", "Bot标题不能为空");
            return map;
        }
        if (title.length() >= 100) {
            map.put("msg", "Bot标题不能长于100");
        }

        if (desc == null || desc.isEmpty()) {
            desc = "这个用户很懒~";
        }
        if (desc.length() >= 300) {
            map.put("msg", "Bot的描述不能长于300");
            return map;
        }

        if (content == null || content.isEmpty()) {
            map.put("msg", "Bot的代码不能为空");
            return map;
        }
        if (content.length() > 100000) {
            map.put("msg", "Bot的代码不能长于100000");
            return map;
        }
        Date now = new Date();
        Bot bot = new Bot(null, user.getId(), title, desc, content, 1500, now, now);
        try {
            botMapper.insert(bot);
        } catch (Exception e) {
            map.put("msg", "Bot创建异常（Bot参数合法）");
            e.printStackTrace();
            return map;
        }
        map.put("msg", "success");
        return map;
    }
}
