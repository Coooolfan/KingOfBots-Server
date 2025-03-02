package com.yang.kingofbotsserver.service.user.bot;

import com.yang.kingofbotsserver.pojo.Bot;

import java.util.List;

public interface GetListService {
//    请求用户可以用jwt解析得到，所以无需传入操作用户的ID
    List<Bot> getList();

    List<Bot> getUncompiled();
}
