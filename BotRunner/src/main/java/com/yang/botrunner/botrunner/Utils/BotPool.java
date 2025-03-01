package com.yang.botrunner.botrunner.Utils;

import com.yang.botrunner.botrunner.Utils.CodeRunnerImpl.CodeRunnerCpp;
import com.yang.botrunner.botrunner.Utils.CodeRunnerImpl.CodeRunnerJava;
import com.yang.botrunner.botrunner.Utils.CodeRunnerImpl.CodeRunnerJavaScript;
import com.yang.botrunner.botrunner.Utils.CodeRunnerImpl.CodeRunnerPython;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Queue<Bot> bots = new LinkedList<>();

    public void addBot(Integer userId, String botCode, String input, String language) {
        lock.lock();
        try {
            System.out.println("added bot to pool " + language);
            Bot newBot = new Bot(userId, botCode, input, language);
            bots.add(newBot);
            condition.signalAll();
        } finally {
            lock.unlock();
        }

    }

    private void consume(Bot bot) {
        CodeRunner botCodeRunner = switch (bot.getLanguage().toLowerCase()) {
            case "java" -> new CodeRunnerJava();
            case "javascript" -> new CodeRunnerJavaScript();
            case "cpp" -> new CodeRunnerCpp();
            case "python" -> new CodeRunnerPython();
            default -> throw new IllegalArgumentException("Unsupported language: " + bot.getLanguage());
        };
        botCodeRunner.startTimeout(2000, bot);
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            if (bots.isEmpty()) {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            } else {
                Bot bot = bots.remove();
                lock.unlock();
                consume(bot);
            }

        }
    }
}
