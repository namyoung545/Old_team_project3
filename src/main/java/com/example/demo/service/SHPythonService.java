package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class SHPythonService {

    public String executePythonScript() {
        try {
            // Python 스크립트를 실행합니다.
            Process process = Runtime.getRuntime().exec("python3 src/main/python/api/api_handler.py");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Python 스크립트 실행 중 오류 발생!";
        }
    }
}
