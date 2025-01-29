package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.service.dy_TestService;

@Component
public class DYApplication {
    @Autowired
    private dy_TestService testService;

    public void dy_start() {
        System.out.println("DYApplication dy_start() 호출");
        // testService.test();
    }
}
