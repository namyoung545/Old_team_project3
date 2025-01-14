package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.SHPythonService;

@Controller
public class SHAPythonController {
    @Autowired
    private SHPythonService pythonService;

    @GetMapping("/sh_api")
    public String getPrediction(Model model) {
        String result = pythonService.executePythonScript();
        model.addAttribute("result", result);
        return "/sh_html/sh_result"; // templates/result.html 반환
    }
}
