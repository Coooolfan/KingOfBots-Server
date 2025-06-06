package com.yang.kingofbotsserver.service.impl.user.bot;

import com.yang.kingofbotsserver.config.RabbitConifg;
import com.yang.kingofbotsserver.mapper.BotMapper;
import com.yang.kingofbotsserver.model.CompilationMessage;
import com.yang.kingofbotsserver.pojo.Bot;
import com.yang.kingofbotsserver.pojo.User;
import com.yang.kingofbotsserver.service.impl.UserDetailsServiceImpl;
import com.yang.kingofbotsserver.service.user.bot.UpdateService;
import com.yang.kingofbotsserver.utils.LanguageHelp;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UpdateServiceImpl implements UpdateService {
    @Autowired
    BotMapper botMapper;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public Map<String, String> update(Map<String, String> data) {
        User user = UserDetailsServiceImpl.getUser();
        Integer bot_id = Integer.parseInt(data.get("bot_id"));
        String title = data.get("title");
        String desc = data.get("description");
        String content = data.get("content");
        String language = data.get("language");
        Bot bot = botMapper.selectById(bot_id);
        Map<String, String> map = new HashMap<>();
//        参数合法性校验
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

//        操作合法性校验
        if (bot == null) {
            map.put("msg", "参数非法：bot_id无效");
            return map;
        }
        if (!user.getId().equals(bot.getUserId())) {
            map.put("msg", "操作非法：权限不足");
            return map;
        }
        String botStatus = LanguageHelp.isStatic(language);
        Bot new_bot = new Bot(
                bot_id,
                user.getId(),
                title,
                desc,
                content,
                language,
                botStatus,
                "-",
                bot.getCreatetime(),
                new Date()
        );
        botMapper.updateById(new_bot);
        
        // 发送MQ消息
        if (!botStatus.equals("noneed")) {
            // 只有需要编译的语言才发送消息
            CompilationMessage message = new CompilationMessage(
                    bot_id,
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
        
        map.put("msg", "success");
        return map;
    }

    @Override
    public void reviceComplied(MultiValueMap<String, String> data) {
        Integer botId = Integer.parseInt(data.get("id").get(0));
        String botStatus = data.get("status").get(0);
        String BotTargetFile = data.get("target_file").get(0);
        Bot bot = botMapper.selectById(botId);
        bot.setStatus(botStatus);
        bot.setTargetFile(BotTargetFile);
        botMapper.updateById(bot);
    }
}
