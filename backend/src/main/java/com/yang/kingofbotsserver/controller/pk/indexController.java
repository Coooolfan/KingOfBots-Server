package com.yang.kingofbotsserver.controller.pk;

import com.yang.kingofbotsserver.service.user.pk.StartGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class indexController {
    private final StartGameService startGameService;

    @Autowired
    public indexController(StartGameService startGameService) {
        this.startGameService = startGameService;
    }
    @PostMapping("/pk/start/game/")
    public String startGame(@RequestParam MultiValueMap<String, String> data) {
        Integer a = Integer.parseInt(Objects.requireNonNull(data.getFirst("a_id")));
        Integer b = Integer.parseInt(Objects.requireNonNull(data.getFirst("b_id")));
        return startGameService.startGame(a, b);
    }
}
