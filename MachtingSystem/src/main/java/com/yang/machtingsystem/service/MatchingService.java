package com.yang.machtingsystem.service;

import com.yang.machtingsystem.utils.MatchingPool;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MatchingService {
    public final static MatchingPool matchingPool = new MatchingPool();

    public String addPlayer(Integer userId, Integer rating) {
        System.out.println("Player " + userId + " added! Rating: " + rating);
        matchingPool.addPlayer(userId, rating);
        return "Player " + userId + " added! Rating: " + rating;
    }

    public String removePlayer(Integer userId) {
        System.out.println("Player " + userId + " removed!");
        matchingPool.removePlayer(userId);
        return "Player " + userId + " removed!";
    }
}
