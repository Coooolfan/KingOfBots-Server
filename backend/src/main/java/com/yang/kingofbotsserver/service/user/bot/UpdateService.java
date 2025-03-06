package com.yang.kingofbotsserver.service.user.bot;

import org.springframework.util.MultiValueMap;

import java.util.Map;

public interface UpdateService {
    Map<String, String> update(Map<String, String> data);

    void reviceComplied(MultiValueMap<String, String> data);
}
