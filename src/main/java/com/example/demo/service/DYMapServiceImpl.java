package com.example.demo.service;

import com.example.demo.service.DYMapService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class DYMapServiceImpl implements DYMapService {

    private static final String GEOJSON_FILE_PATH = "src/main/resources/static/data/dy_MapData.json";

    @Override
    public String getGeoJsonFile() {
        try {
            return Files.readString(Paths.get(GEOJSON_FILE_PATH)); // 파일 내용 반환
        } catch (IOException e) {
            e.printStackTrace();
            return "{}"; // 빈 JSON 반환
        }
    }
}
