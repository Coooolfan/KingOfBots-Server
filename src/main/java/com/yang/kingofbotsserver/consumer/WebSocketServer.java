package com.yang.kingofbotsserver.consumer;

import com.yang.kingofbotsserver.mapper.UserMapper;
import com.yang.kingofbotsserver.pojo.User;
import com.yang.kingofbotsserver.utils.JwtUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {
    //    线程安全的hashmap
    private static final ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    private User user;
    private Session session = null;
    private static UserMapper userMapper;

    @Autowired
    public void setIserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
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
        System.out.println("Connected" + user);
        // 建立连接
    }

    @OnClose
    public void onClose() {
        System.out.println("Closed");
        // 关闭链接
        if (this.user != null) {
            users.remove(user.getId());
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("New Msg!!!");
        // 从Client接收消息
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
