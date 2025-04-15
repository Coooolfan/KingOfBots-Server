package com.yang.botrunner.botrunner.schedule;

import com.yang.botrunner.botrunner.Utils.CodeCompilerImpl.CodeCompilerCpp;
import com.yang.botrunner.botrunner.Utils.CodeCompilerImpl.CodeCompilerJava;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class Complier {
    private static RestTemplate restTemplate;
    @Value("${kob.backend.host}")
    private String HOST;
    private final static String getUncompiled = "/api/user/bot/uncompiled/";
    @Autowired
    private CodeCompilerJava codeCompilerJava;

    @Autowired
    private CodeCompilerCpp codeCompilerCpp;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        Complier.restTemplate = restTemplate;
    }
/*
    @Scheduled(fixedDelay = 5000)
    public void compile() {
        String string = restTemplate.getForEntity(HOST + getUncompiled, String.class).getBody();
        JSONArray resp = JSON.parseArray(string);
        for (int i = 0; i < resp.size(); i++) {
            String botString = resp.getString(i);
            JSONObject jsonObject = JSON.parseObject(botString);
            String language = jsonObject.getString("language").toLowerCase();

            CodeCompiler codeCompiler = switch (language) {
                case "java" -> codeCompilerJava;
                case "cpp" -> codeCompilerCpp;
                default -> throw new IllegalStateException("Unexpected value: " + language);
            };
            codeCompiler.compile(jsonObject.getString("content"), jsonObject.getInteger("id"));
        }
    }

 */
}
