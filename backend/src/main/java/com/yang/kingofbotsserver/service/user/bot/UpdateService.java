package com.yang.kingofbotsserver.service.user.bot;

import java.util.Map;

public interface UpdateService {
    Map<String, String> update(Map<String, String> data);

    Map<String, String> reviceComplied(Map<String, String> data);
}
