package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    // localhost:8080 요청을 처리하는 메서드
    @GetMapping("/")
    public String getindex() {
        return "index"; // resources/templates/index.html을 반환
    }

    // @GetMapping("/login")
    // public String getLogin() {
    //     return "/PHG/login";
    // }

    // @GetMapping("/singup")
    // public String getSingUp() {
    //     return "/PHG/singup";
    // }
    
}