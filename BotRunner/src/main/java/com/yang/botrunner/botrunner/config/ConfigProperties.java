package com.yang.botrunner.botrunner.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigProperties {
    private static String host;
    
    @Value("${kob.backend.host}")
    public void setHost(String value) {
        ConfigProperties.host = value;
    }
    
    public static String getHost() {
        return host;
    }
}