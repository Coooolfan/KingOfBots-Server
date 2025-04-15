package com.yang.botrunner.botrunner.model;

import lombok.Data;

@Data
public class CompilationMessage {
    private Integer botId;
    private String content;
    private String language;
    private Integer retryCount;

    public CompilationMessage() {
        this.retryCount = 0; // Initialize retry count to 0
    }

    public CompilationMessage(Integer botId, String content, String language) {
        this.botId = botId;
        this.content = content;
        this.language = language;
        this.retryCount = 0;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public boolean canRetry(int maxRetries) {
        return this.retryCount < maxRetries;
    }
} 