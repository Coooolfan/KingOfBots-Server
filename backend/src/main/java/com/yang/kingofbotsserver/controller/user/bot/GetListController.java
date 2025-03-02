package com.yang.kingofbotsserver.controller.user.bot;

import com.yang.kingofbotsserver.pojo.Bot;
import com.yang.kingofbotsserver.service.user.bot.GetListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetListController {
    @Autowired
    GetListService getListService;

    @GetMapping("/api/user/bot/getlist/")
    public List<Bot> getList() {
        return getListService.getList();
    }

    @GetMapping("/api/user/bot/uncompiled/")
    public List<Bot> getUncompiled() {
        return getListService.getUncompiled();
    }

}
