package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.example.demo.service.SHFireStatisticsService;
import com.example.demo.service.SHPythonService;
import com.example.demo.service.SHVirtualEnvService;

@Component
@EnableScheduling
public class SHApplication {

    @Autowired
    private SHVirtualEnvService virtualEnvService;
    @Autowired
    private SHPythonService pythonService;
    @Autowired
    private SHFireStatisticsService shFireStatisticsService;

    public void runSHApplication() {
        // Python 가상환경 설정
        System.out.println("Python Service");
        virtualEnvService.setupVirtualEnv();

        // Python Service
        pythonService.checkFiresData();
        
        // Fire Statistics
        shFireStatisticsService.runFireStatistics();
        
        // ED Statistics DATA
        // Boolean result = pythonService.checkEDStatistics();
        // System.out.println(result);
    }
}
