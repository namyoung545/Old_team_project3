package com.example.demo.controller;

import com.example.demo.service.dy_aiRiskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller  // RestController가 아닌 Controller 사용
@RequestMapping("/aiRisk")
public class dy_aiRiskController {

    private final dy_aiRiskService riskService;

    public dy_aiRiskController(dy_aiRiskService riskService) {
        this.riskService = riskService;
    }

    // JSON 데이터를 반환하는 API
    @ResponseBody  // JSON 응답을 반환하도록 설정
    @GetMapping("/data")
    public List<Map<String, Object>> getRiskData() {
        return riskService.getRiskData();
    }

    // Thymeleaf 템플릿을 렌더링하는 메서드
    @GetMapping("/chart")
    public String showRiskChart(Model model) {
        List<Map<String, Object>> riskData = riskService.getRiskData();
        model.addAttribute("riskData", riskData);
        return "dy_html/dy_aiRiskChart";  // dy_html/을 제거하고 기본 폴더에서 찾도록 변경
    }
}
