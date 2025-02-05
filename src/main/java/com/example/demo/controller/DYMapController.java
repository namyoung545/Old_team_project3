package com.example.demo.controller;

import com.example.demo.service.DYMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DYMapController {

    @Autowired
    private DYMapService mapService;

    @GetMapping("/dy_map") // GeoJSON 데이터를 반환
    public ResponseEntity<String> getGeoJson() {
        String data = mapService.getGeoJsonFile();
        return ResponseEntity.ok(data);
    }
}
