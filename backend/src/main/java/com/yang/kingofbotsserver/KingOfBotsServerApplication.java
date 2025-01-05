package com.yang.kingofbotsserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yang.kingofbotsserver.mapper")
public class KingOfBotsServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KingOfBotsServerApplication.class, args);
    }

}
