package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List; 
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class dy_NewsController {
    @GetMapping("/dy_news")
    public String showNewsPage(Model model) {
        // 가짜 데이터: 실제로는 Service에서 데이터를 가져와야 함
        List<Map<String, Object>> events = new ArrayList<>();
        Map<String, Object> event = new HashMap<>();
        event.put("title", "Meeting");
        event.put("start", "2025-01-10");
        events.add(event);


        model.addAttribute("events", events); // 데이터를 모델에 추가
        return "dy_html/dy_newsCalendar"; // dy_html/dy_news.html 파일 반환
    }
}
