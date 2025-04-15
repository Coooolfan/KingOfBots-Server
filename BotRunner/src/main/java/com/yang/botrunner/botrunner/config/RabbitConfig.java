package com.yang.botrunner.botrunner.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String TOPIC_EXCHANGE_NAME = "bot_compilation_exchange";
    public static final String JAVA_QUEUE_NAME = "compile.java";
    public static final String CPP_QUEUE_NAME = "compile.cpp";
    public static final String JAVA_ROUTING_KEY = "compile.java";
    public static final String CPP_ROUTING_KEY = "compile.cpp";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    @Bean
    public Queue javaQueue() {
        return new Queue(JAVA_QUEUE_NAME);
    }

    @Bean
    public Queue cppQueue() {
        return new Queue(CPP_QUEUE_NAME);
    }

    @Bean
    public Binding javaBinding(Queue javaQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(javaQueue)
                .to(topicExchange)
                .with(JAVA_ROUTING_KEY);
    }

    @Bean
    public Binding cppBinding(Queue cppQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(cppQueue)
                .to(topicExchange)
                .with(CPP_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
