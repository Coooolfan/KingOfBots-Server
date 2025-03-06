package com.yang.botrunner.botrunner.schedule;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.yang.botrunner.botrunner.Utils.CodeCompiler;
import com.yang.botrunner.botrunner.Utils.CodeCompilerImpl.CodeCompilerCpp;
import com.yang.botrunner.botrunner.Utils.CodeCompilerImpl.CodeCompilerJava;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class Complier {
    private static RestTemplate restTemplate;
    private final static String getUncompiled = "http://localhost:8080/api/user/bot/uncompiled/";
    private final static String updateBot = "http://localhost:8080/api/revice/bot/update/";
    @Autowired
    private CodeCompilerJava codeCompilerJava;

    @Autowired
    private CodeCompilerCpp codeCompilerCpp;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        Complier.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void compile() {
        String string = restTemplate.getForEntity(getUncompiled, String.class).getBody();
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
}
