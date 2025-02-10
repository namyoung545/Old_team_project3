package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class dy_ThreeJsController {

    @GetMapping("/threejs")
    public String show3DModel() {
        return "dy_html/dy_threeJs";
    }
}