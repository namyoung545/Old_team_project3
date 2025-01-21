package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
public class dy_risk_controller {

    @GetMapping("/dy_risk")
    public String getRiskData(Model model) {
        try {
            // JSON 파일 경로
            String filePath = "src/main/resources/static/json/dy_risk.json";

            // JSON 파일 읽기 (UTF-8 인코딩)
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");

            // JSON 데이터를 모델에 추가
            model.addAttribute("jsonData", jsonContent);

               // 기본 연도 설정
             model.addAttribute("defaultYear", 2023);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Failed to load risk data: " + e.getMessage());
            return "error"; // 오류 템플릿 반환
        }

        return "dy_html/dy_risk"; // Thymeleaf 템플릿 경로 반환
    }
}
