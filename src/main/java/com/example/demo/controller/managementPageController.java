package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class managementPageController {
    
    @GetMapping("/managementPage")
    public String getindex() {
        return "managementPage";
    }

}
