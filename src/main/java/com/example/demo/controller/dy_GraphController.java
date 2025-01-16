package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Controller
public class dy_GraphController {

    @Value("${python.executable}") // 환경변수에서 Python 경로 주입
    private String pythonExecutable;

    @GetMapping("/electric-fires")
    public ResponseEntity<String> getElectricFires(@RequestParam("year") int year) {
        try {
            // Python 스크립트 실행
            ProcessBuilder pb = new ProcessBuilder(
                    pythonExecutable, "src/main/python/dyPython/dyElec.py", String.valueOf(year));
            pb.redirectErrorStream(true);

            Process process = pb.start();

            // Python 출력 읽기
            String output;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                output = reader.lines().collect(Collectors.joining());
            }

            // Python 프로세스 완료 대기
            process.waitFor();

            return ResponseEntity.ok(output); // JSON 응답 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    @GetMapping("/dy_graph")
    public String showGraphPage() {
    return "dy_html/dy_graph"; // dy_graph.html을 렌더링
}
}