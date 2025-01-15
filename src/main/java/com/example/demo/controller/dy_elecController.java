package com.example.demo.controller;

import com.example.demo.service.dy_elecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/electrical-fires")
public class dy_elecController {

    // @Autowired
    // private dy_elecService elecService;

    // @GetMapping
    // public List<Map<String, Object>> getFireData() {
    //     return elecService.getFireDataAsJson();
    // }
}
