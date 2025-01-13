package com.example.demo.PHG_Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 샘
@Controller
public class PHGController {
    
    @GetMapping("/index")
    public String getIndex() {
        return "/PHG/index";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "/PHG/login";
    }

    @GetMapping("/singup")
    public String getSingUp() {
        return "/PHG/singup";
    }
    
}
