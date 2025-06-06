package com.yang.kingofbotsserver.service.impl.user.bot;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yang.kingofbotsserver.config.RabbitConifg;
import com.yang.kingofbotsserver.model.CompilationMessage;
import com.yang.kingofbotsserver.pojo.Bot;
import com.yang.kingofbotsserver.pojo.User;
import com.yang.kingofbotsserver.service.impl.UserDetailsServiceImpl;
import com.yang.kingofbotsserver.service.user.bot.AddService;
import com.yang.kingofbotsserver.utils.LanguageHelp;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddServiceImpl implements AddService {
    @Autowired
    BaseMapper<Bot> botMapper;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public Map<String, String> add(Map<String, String> data) {
        User user = UserDetailsServiceImpl.getUser();
        Map<String, String> map = new HashMap<>();

        String title = data.get("title");
        String desc = data.get("desc");
        String content = data.get("content");
        String language = data.get("language");

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
        String botStatus = LanguageHelp.isStatic(language);
        Bot bot = new Bot(null, user.getId(), title, desc, content, language, botStatus, "-", now, now);
        try {
            botMapper.insert(bot);
            
            // 发送MQ消息
            if (!botStatus.equals("noneed")) {
                // 只有需要编译的语言才发送消息
                CompilationMessage message = new CompilationMessage(
                        bot.getId(),
                        content,
                        language
                );
                
                // 根据语言类型确定路由键
                String routingKey = "compile." + language.toLowerCase();
                
                // 发送消息到RabbitMQ
                rabbitTemplate.convertAndSend(
                        RabbitConifg.TOPIC_EXCHANGE_NAME,
                        routingKey,
                        message
                );

                System.out.println("已发送编译消息到MQ: " + message.getBotId()+" " + message.getContent() + " " + message.getLanguage());
            }

        } catch (Exception e) {
            map.put("msg", "Bot创建异常（Bot参数合法）");
            e.printStackTrace();
            return map;
        }
        map.put("msg", "success");
        return map;
    }
}
