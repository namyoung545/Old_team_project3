package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PHGController {
    
    @GetMapping("/index")
    public String getIndex() {
        return "/PHG/PHG_index";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "/PHG/PHG_login";
    }

    @GetMapping("/join")
    public String getSingUp() {
        return "/PHG/PHG_join";
    }
    
}
