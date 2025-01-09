package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.service.SHPythonService;
import com.example.demo.service.SHVirtualEnvService;

@Component
public class SHApplication {

    @Autowired
    private SHVirtualEnvService virtualEnvService;
    @Autowired
    private SHPythonService pythonService;

    public void setupPython() {
        // Python 가상환경 설정
        virtualEnvService.setupVirtualEnv();
        System.out.println("Python Service");
        Boolean result = pythonService.checkEDStatistics();
        System.out.println(result);
        // String result = pythonService.callEDStatistics("analyze_statistics", "2023");
        // System.out.println(result);
    }
}
