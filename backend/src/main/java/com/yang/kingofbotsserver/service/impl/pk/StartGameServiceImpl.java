package com.yang.kingofbotsserver.service.impl.pk;

import com.yang.kingofbotsserver.consumer.WebSocketServer;
import com.yang.kingofbotsserver.service.user.pk.StartGameService;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {
    @Override
    public String startGame(Integer a, Integer b) {
        WebSocketServer.startGame(a, b);
        return "Game started! " + a + " vs " + b;
    }
}
