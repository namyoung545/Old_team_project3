package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@RestController
public class dy_GraphController {

    @GetMapping("/python-data")
    public String getPythonData() {
        try {
            // Python 파일 실행
            Process process = Runtime.getRuntime().exec("dy_python/dy_elec.py");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            process.waitFor();
            return output.toString(); // Python 출력값 반환
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to execute Python script\"}";
        }
    }
}
