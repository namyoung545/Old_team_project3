package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/managementPage")
public class managementPageController {
    
    @GetMapping("")
    public String getindex() {
        return "managementPage";
    }

    // @GetMapping("/fullCalendar")
    // public String showNewsPage(Model model) {
    //     // 가짜 데이터: 실제로는 Service에서 데이터를 가져와야 함
    //     List<Map<String, Object>> events = new ArrayList<>();
    //     Map<String, Object> event = new HashMap<>();
    //     event.put("title", "Meeting");
    //     event.put("start", "2025-01-10");
    //     events.add(event);


    //     model.addAttribute("events", events); // 데이터를 모델에 추가
    //     return "fullCalendar"; // dy_html/dy_news.html 파일 반환
    // }

    @GetMapping("/fullCalendar")
    public String loadFullCalendarPage() {
        return "fullCalendar"; // templates/fullCalendar.html
    }

    // FullCalendar 이벤트 데이터 제공 (JSON)
    @ResponseBody
    @GetMapping("/full-calendar-events")
    public List<Map<String, Object>> getFullCalendarEvents() {
        List<Map<String, Object>> events = new ArrayList<>();

        // 예제 데이터
        Map<String, Object> event1 = new HashMap<>();
        event1.put("title", "회의");
        event1.put("start", "2025-01-10");

        Map<String, Object> event2 = new HashMap<>();
        event2.put("title", "프로젝트 마감");
        event2.put("start", "2025-01-15");

        events.add(event1);
        events.add(event2);

        return events; // JSON으로 반환
    }

    @GetMapping("/registAS")
    public String scheduleRegistAS() {
        return "registAS";
    }

}
