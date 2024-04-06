package com.yang.kingofbotsserver.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.yang.kingofbotsserver.mapper.RecordMapper;
import com.yang.kingofbotsserver.mapper.UserMapper;
import com.yang.kingofbotsserver.pojo.User;
import com.yang.kingofbotsserver.utils.GameMapUtil;
import com.yang.kingofbotsserver.utils.JwtUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {
    //    线程安全的hashmap
    @Getter
    private static final ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    final private static CopyOnWriteArraySet<User> matchpool = new CopyOnWriteArraySet<>();
    private User user;
    private Session session = null;
    private static UserMapper userMapper;
    @Getter
    private static RecordMapper recordMapper;
    private GameMapUtil game = null;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper){
        WebSocketServer.recordMapper = recordMapper;
    }


    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session = session;
        int userID = JwtUtil.getUserId(token);
        if (userID == -1) {
            this.session.close();
            return;
        }
        this.user = userMapper.selectById(userID);
        if (user == null) {
            this.session.close();
            return;
        }
        users.put(userID, this);
        System.out.println("Connected " + user);
    }

    @OnClose
    public void onClose() {
        System.out.println("Closed");
        // 关闭链接
        if (this.user != null) {
            users.remove(user.getId());
            matchpool.remove(this.user);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息
        System.out.println(System.currentTimeMillis() + "receive message!");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("start-matching".equals(event)) {
            startMatching();
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        } else if ("move".equals(event)) {
            move(data.getInteger("direction"));
        }

    }

    private void move(Integer d) {
        if (game.getPlayerA().getId().equals(user.getId())) {
            game.setNextStepA(d);
        } else if (game.getPlayerB().getId().equals(user.getId())) {
            game.setNextStepB(d);
        }
    }

    private void stopMatching() {
        matchpool.remove(this.user);
    }

    private void startMatching() {
        System.out.println("start matching!");
        matchpool.add(this.user);

        while (matchpool.size() >= 2) {
            Iterator<User> it = matchpool.iterator();
            User a = it.next(), b = it.next();
            matchpool.remove(a);
            matchpool.remove(b);

            GameMapUtil game = new GameMapUtil(13, 14, 20, a.getId(), b.getId());
            game.createMap();
            game.start();

            users.get(a.getId()).game = game;
            users.get(b.getId()).game = game;

            JSONObject respGame = new JSONObject();
            respGame.put("a_id", game.getPlayerA().getId());
            respGame.put("a_sx", game.getPlayerA().getSx());
            respGame.put("a_sy", game.getPlayerA().getSy());
            respGame.put("b_id", game.getPlayerB().getId());
            respGame.put("b_sx", game.getPlayerB().getSx());
            respGame.put("b_sy", game.getPlayerB().getSy());
            respGame.put("map", game.getG());

            JSONObject respA = new JSONObject();
            respA.put("event", "start-matching");
            respA.put("opponent_username", b.getUsername());
            respA.put("opponent_photo", b.getPhoto());
            respA.put("game", respGame);
            users.get(a.getId()).sendMessage(respA.toJSONString());

            JSONObject respB = new JSONObject();
            respB.put("event", "start-matching");
            respB.put("opponent_username", a.getUsername());
            respB.put("opponent_photo", a.getPhoto());
            respB.put("game", respGame);
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }

    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) {
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
