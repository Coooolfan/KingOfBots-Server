package com.yang.kingofbotsserver.utils;

import com.alibaba.fastjson2.JSONObject;
import com.yang.kingofbotsserver.consumer.WebSocketServer;
import com.yang.kingofbotsserver.pojo.Bot;
import com.yang.kingofbotsserver.pojo.Record;
import com.yang.kingofbotsserver.pojo.User;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class GameMapUtil extends Thread {
    final private Integer rows;
    final private Integer cols;
    final private Integer inner_walls_count;
    @Getter
    final private int[][] g;
    final private static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    @Getter
    private final Player playerA;
    @Getter
    private final Player playerB;
    private final ReentrantLock lock = new ReentrantLock();
    private Integer nextStepA = null;
    private Integer nextStepB = null;
    private String status = "playing"; // "playing" or "finished"
    private String loser = null; // all 平局 a/b a/b输
    private final static String addBotUrl = "http://localhost:8082/bot/add/";

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }


    public GameMapUtil(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Integer idB, Bot botA, Bot botB) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";
        String botLanguageA = "java", botLanguageB = "java";
        if (botA != null) {
            botIdA = botA.getId();
            botCodeA = botA.getContent();
            botLanguageA = botA.getLanguage();
        }
        playerA = new Player(idA, this.rows - 2, 1, new ArrayList<>(), botCodeA, botLanguageA, botIdA);

        if (botB != null) {
            botIdB = botB.getId();
            botCodeB = botB.getContent();
            botLanguageB = botB.getLanguage();
        }
        playerB = new Player(idB, 1, this.cols - 2, new ArrayList<>(), botCodeB, botLanguageB, botIdB);

    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;

        for (int i = 0; i < 4; i++) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                if (check_connectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }

        g[sx][sy] = 0;
        return false;
    }

    private boolean draw() {  // 画地图
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                g[i][j] = 0;
            }
        }

        for (int r = 0; r < this.rows; r++) {
            g[r][0] = g[r][this.cols - 1] = 1;
        }
        for (int c = 0; c < this.cols; c++) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        Random random = new Random();
        for (int i = 0; i < this.inner_walls_count / 2; i++) {
            for (int j = 0; j < 1000; j++) {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1) continue;
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;

                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }

        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {
        for (int i = 0; i < 1000; i++) {
            if (draw()) break;
        }
    }

    private String getInput(Player player) {
        Player me, you;
        if (playerA.getId().equals(player.getId())) {
            me = playerA;
            you = playerB;
        } else {
            me = playerB;
            you = playerA;
        }
        return getMapString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getStepsString() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepsString() + ")";

    }

    public void sendBotCode(Player player) {
        if (player.getBotId().equals(-1)) return;
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("user_id", player.getId().toString());
        map.add("bot_code", player.getBotCode());
        map.add("language", player.getBotLanguage());
        map.add("input", getInput(player));
        WebSocketServer.restTemplate.postForObject(addBotUrl, map, String.class);
    }

    // 检查是否获取到双方的下一步操作
    private boolean nextStep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
        sendBotCode(playerA);
        sendBotCode(playerB);
        for (int i = 0; i < 25; i++) {
            try {
                Thread.sleep(200);
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    private String getMapString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                stringBuilder.append(g[i][j]);
        return stringBuilder.toString();
    }

    private void updateUserRating(Player player, Integer reting) {
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(reting);
        WebSocketServer.userMapper.updateById(user);
    }

    private void saveToDatabase() {
        Integer ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();
        if ("a".equals(loser)) {
            ratingA -= 2;
            ratingB += 5;
        } else if ("b".equals(loser)) {
            ratingA += 5;
            ratingB -= 2;
        }
        updateUserRating(playerA, ratingA);
        updateUserRating(playerB, ratingB);

        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSx(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                this.getMapString(),
                this.loser,
                new Date()
        );
        WebSocketServer.getRecordMapper().insert(record);
    }

    private Boolean checkValid(List<SnakeCell> snakeA, List<SnakeCell> snakeB) {
        int n = snakeA.size();
        SnakeCell cell = snakeA.get(n - 1);
        if (g[cell.getX()][cell.getY()] == 1) return false;
        for (int i = 0; i < n - 1; i++) {
            if (snakeA.get(i).getX() == cell.getX() && snakeA.get(i).getY() == cell.getY()) {
                return false;
            }
        }
        for (int i = 0; i < n - 1; i++) {
            if (snakeB.get(i).getX() == cell.getX() && snakeB.get(i).getY() == cell.getY()) {
                return false;
            }
        }
        return true;
    }

    private void judge() {
        List<SnakeCell> snakeA = playerA.getCells();
        List<SnakeCell> snakeB = playerB.getCells();
        boolean validA = checkValid(snakeA, snakeB);
        boolean validB = checkValid(snakeB, snakeA);
        if (!validA || !validB) {
            status = "finished";
            if (!validA && !validB) loser = "all";
            else if (!validA) loser = "a";
            else loser = "b";
        }
    }

    //    向玩家传递对方的运动信息
    private void sendMove() {
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            nextStepA = nextStepB = null;
            broadcastMessage(resp.toJSONString());
        } finally {
            lock.unlock();
        }

    }

    //    向玩家传递游戏结果
    private void sendResult() {
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        broadcastMessage(resp.toJSONString());
    }

    //    向所有玩家广播信息
    private void broadcastMessage(String msg) {
        WebSocketServer.getUsers().get(playerA.getId()).sendMessage(msg);
        WebSocketServer.getUsers().get(playerB.getId()).sendMessage(msg);
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            if (nextStep()) {
                judge();
                if (status.equals("playing")) {
                    sendMove();
                } else {
                    saveToDatabase();
                    sendResult();
                    break;
                }
            } else {
                status = "finished";
                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null) {
                        loser = "all";
                    } else if (nextStepA == null) {
                        loser = "a";
                    } else {
                        loser = "b";
                    }
                } finally {
                    lock.unlock();
                }
                saveToDatabase();
                sendResult();
                break;
            }
        }
    }

}