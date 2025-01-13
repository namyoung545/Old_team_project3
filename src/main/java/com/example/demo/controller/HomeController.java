package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // localhost:8080 요청을 처리하는 메서드
    @GetMapping("/")
    public String index() {
        return "index.html"; // resources/templates/index.html을 반환
    }
}