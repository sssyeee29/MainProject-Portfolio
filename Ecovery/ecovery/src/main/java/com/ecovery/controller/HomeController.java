package com.ecovery.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 로그인 성공 후 이동할 메인 페이지 컨트롤러
 * 작성자 : 방희경
 */

@Controller
@Slf4j
public class HomeController {

    @GetMapping("/main")
    public String mainPage(Model model) {

        log.info("mainPage");
        return "main"; // templates/main.html 렌더링
    }

    @GetMapping(value = "/")
    public String rootPage(Model model) {

        log.info("rootPage");
        return "main";
    }
}
