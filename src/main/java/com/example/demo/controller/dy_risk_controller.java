package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller // @RestController 대신 @Controller 사용
public class dy_risk_controller {

    @GetMapping("/api/dy-risk")
    public List<Object> getRiskData() throws Exception {
        // JSON 파일 로드
        String json = new String(Files.readAllBytes(Paths.get("src/main/resources/static/json/dy_risk.json")));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, List.class);
    }

    @GetMapping("/dy_risk")
    public String showDyRiskPage(Model model) {
        // 데이터를 Thymeleaf 템플릿에 전달 (필요시 데이터 추가 가능)
        return "dy_html/dy_risk"; // templates/dy_risk.html 렌더링
    }
}
