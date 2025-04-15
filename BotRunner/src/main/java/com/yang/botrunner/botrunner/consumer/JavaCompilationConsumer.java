package com.yang.botrunner.botrunner.consumer;

import com.yang.botrunner.botrunner.Utils.CodeCompilerImpl.CodeCompilerJava;
import com.yang.botrunner.botrunner.config.RabbitConfig;
import com.yang.botrunner.botrunner.model.CompilationMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JavaCompilationConsumer {

    private static final int MAX_RETRIES = 3;

    @Autowired
    private CodeCompilerJava codeCompilerJava;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitConfig.JAVA_QUEUE_NAME)
    public void receiveMessage(CompilationMessage message) {
        System.out.println("Received Java compilation request for bot ID: " + message.getBotId() + 
                           ", retry count: " + message.getRetryCount());
        try {
            codeCompilerJava.compile(message.getContent(), message.getBotId());
        } catch (Exception e) {
            System.err.println("Error processing Java compilation: " + e.getMessage());
            e.printStackTrace();
            
            // Check if we can retry
            if (message.canRetry(MAX_RETRIES)) {
                message.incrementRetryCount();
                System.out.println("Retrying Java compilation for bot ID: " + message.getBotId() + 
                                   ", retry attempt: " + message.getRetryCount());
                rabbitTemplate.convertAndSend(RabbitConfig.JAVA_QUEUE_NAME, message);
            } else {
                System.err.println("Max retries reached for Java compilation of bot ID: " + message.getBotId());
            }
        }
    }
} 