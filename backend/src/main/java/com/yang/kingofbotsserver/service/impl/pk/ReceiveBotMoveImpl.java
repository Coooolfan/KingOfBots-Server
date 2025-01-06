package com.yang.kingofbotsserver.service.impl.pk;

import com.yang.kingofbotsserver.consumer.WebSocketServer;
import com.yang.kingofbotsserver.service.user.pk.ReceiveBotMoveService;
import com.yang.kingofbotsserver.utils.GameMapUtil;
import org.springframework.stereotype.Service;

@Service
public class ReceiveBotMoveImpl implements ReceiveBotMoveService {
    @Override
    public String receiveBotMove(Integer userId, Integer direction) {
        System.out.println("userId: " + userId + " direction: " + direction);
        if (WebSocketServer.users.get(userId) != null) {
            GameMapUtil game = WebSocketServer.users.get(userId).game;
            if (game != null) {
                if (game.getPlayerA().getId().equals(userId)) {
                    game.setNextStepA(direction);
                } else if (game.getPlayerB().getId().equals(userId)) {
                    game.setNextStepB(direction);
                }
            }
        }
        return "success";
    }
}