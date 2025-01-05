package com.yang.kingofbotsserver.service.user.account;

import java.util.Map;

public interface RegisterService {
    Map<String, String> register(String username, String passowrd);
}
