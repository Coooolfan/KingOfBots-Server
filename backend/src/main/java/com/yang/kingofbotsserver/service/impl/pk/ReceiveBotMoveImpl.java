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
        WebSocketServer socketServer = WebSocketServer.users.get(userId);
        if (direction == -1)
            return "fail";

        if (socketServer == null)
            return "fail";

        GameMapUtil game = socketServer.game;
        if (game == null)
            return "fail";

        if (game.getPlayerA().getId().equals(userId)) {
            game.setNextStepA(direction);
        } else if (game.getPlayerB().getId().equals(userId)) {
            game.setNextStepB(direction);
        }


        return "success";
    }
}