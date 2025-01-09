package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SHController {
    @GetMapping("/sh_index")
    public String getSHIndex(Model model) {
        model.addAttribute("message", "안녕하세요! 이것은 Thymeleaf 테스트 페이지입니다.");
        return "/sh_html/sh_index";
    }
    
}
