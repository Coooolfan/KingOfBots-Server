package com.yang.botrunner.botrunner.consumer;

import com.yang.botrunner.botrunner.Utils.CodeCompilerImpl.CodeCompilerCpp;
import com.yang.botrunner.botrunner.config.RabbitConfig;
import com.yang.botrunner.botrunner.model.CompilationMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CppCompilationConsumer {

    private static final int MAX_RETRIES = 3;

    @Autowired
    private CodeCompilerCpp codeCompilerCpp;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitConfig.CPP_QUEUE_NAME)
    public void receiveMessage(CompilationMessage message) {
        System.out.println("Received C++ compilation request for bot ID: " + message.getBotId() + 
                           ", retry count: " + message.getRetryCount());
        try {
            codeCompilerCpp.compile(message.getContent(), message.getBotId());
        } catch (Exception e) {
            System.err.println("Error processing C++ compilation: " + e.getMessage());
            e.printStackTrace();
            
            // Check if we can retry
            if (message.canRetry(MAX_RETRIES)) {
                message.incrementRetryCount();
                System.out.println("Retrying C++ compilation for bot ID: " + message.getBotId() + 
                                   ", retry attempt: " + message.getRetryCount());
                rabbitTemplate.convertAndSend(RabbitConfig.CPP_QUEUE_NAME, message);
            } else {
                System.err.println("Max retries reached for C++ compilation of bot ID: " + message.getBotId());
            }
        }
    }
} 