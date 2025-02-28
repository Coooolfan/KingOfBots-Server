package com.yang.botrunner.botrunner.Utils;

import org.springframework.web.client.RestTemplate;

public interface CodeRunner {

    /**
     * 启动代码执行并设置超时时间
     *
     * @param timeout 超时时间（毫秒）
     * @param bot 要执行代码的机器人对象
     */
    void startTimeout(long timeout, Bot bot);

    /**
     * 执行机器人代码的核心方法
     */
    void run();

    /**
     * 发送机器人的移动结果到服务器
     *
     * @param userId 用户ID
     * @param response 机器人的响应
     */
    void sendResponse(Integer userId, String response);
}
