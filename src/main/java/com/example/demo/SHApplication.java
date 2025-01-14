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

    public void runSHApplication() {
        // Python 가상환경 설정
        System.out.println("Python Service");
        virtualEnvService.setupVirtualEnv();

        // Fire Statistics
        pythonService.checkFiresData();

        // ED Statistics DATA
        // Boolean result = pythonService.checkEDStatistics();
        // System.out.println(result);
    }
}
