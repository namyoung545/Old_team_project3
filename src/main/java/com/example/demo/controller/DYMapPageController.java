package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DYMapPageController {

    @GetMapping("/dy_3dmap") // HTML 페이지 반환
    public String showMapPage() {
        return "dy_html/dy_3dmap"; // templates/dy_3dmap.html 렌더링
    }
}
