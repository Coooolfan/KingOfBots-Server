package com.yang.kingofbotsserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KingOfBotsServerApplicationTests {

    @Test
    void contextLoads() {
        String version = org.springframework.security.core.SpringSecurityCoreVersion.class.getPackage().getImplementationVersion();
        System.out.println("Spring Security version: " + version);
    }

}
