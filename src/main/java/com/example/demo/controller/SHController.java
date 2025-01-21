package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class SHController {
    @GetMapping("/sh_index")
    public String getSHIndex(Model model) {
        model.addAttribute("message", "안녕하세요! 이것은 Thymeleaf 테스트 페이지입니다.");
        return "/sh_html/sh_index";
    }
    
    @GetMapping("/sh_map")
    public String getSHMap(Model model) {
        return "/sh_html/sh_map";
    }
    
    @GetMapping("/sh_chart")
    public String getSHChart(Model model) {
        return "/sh_html/sh_chart";
    }
    
    @GetMapping("/sh_wms")
    public String getSHWMS(Model model) {
        return "/sh_html/sh_wms";
    }
    
}
