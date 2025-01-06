package com.yang.machtingsystem.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Component
public class MatchingPool extends Thread {
    private static List<Player> players = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    private static RestTemplate restTemplate;
    private final static String startUrl = "http://localhost:8080/pk/start/game/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, Integer rating, Integer botId) {
        lock.lock();
        try {
            players = players.stream()
                    .filter(player -> !player.getUserId().equals(userId))
                    .collect(Collectors.toList());
            players.add(new Player(userId, rating, 0, botId));
        } finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId) {
        lock.lock();
        try {
            players = players.stream()
                    .filter(player -> !player.getUserId().equals(userId))
                    .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    private void increaseWaitTime() {
        lock.lock();
        try {
            players.forEach(player -> player.setWaitTime(player.getWaitTime() + 1));
        } finally {
            lock.unlock();
        }
    }

    private boolean isMatchable(Player player1, Player player2) {
        int ratingDelate = Math.abs(player1.getRating() - player2.getRating());
        int waitTimeDelate = Math.min(player1.getWaitTime(), player2.getWaitTime());
        return ratingDelate <= waitTimeDelate * 10;
    }

    private void sendResult(Player player1, Player player2) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("a_id", player1.getUserId().toString());
        map.add("a_bot_id", player1.getBotId().toString());
        map.add("b_id", player2.getUserId().toString());
        map.add("b_bot_id", player2.getBotId().toString());
        restTemplate.postForObject(startUrl, map, String.class);
        System.out.println("Matched players: " + player1.getUserId() + " and " + player2.getUserId());
    }

    private void matchPlayers() {
        boolean[] used = new boolean[players.size()];
        for (int i = 0; i < players.size(); i++) {
            if (used[i]) {
                continue;
            }
            for (int j = i + 1; j < players.size(); j++) {
                if (used[j]) {
                    continue;
                }
                if (isMatchable(players.get(i), players.get(j))) {
                    used[i] = true;
                    used[j] = true;
                    sendResult(players.get(i), players.get(j));
                    break;
                }
            }
        }

        lock.lock();
        try {
            for (int i = players.size() - 1; i >= 0; i--)
                if (used[i]) players.remove(i);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                increaseWaitTime();
                matchPlayers();
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
