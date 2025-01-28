package com.example.demo.service;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class SHFireInfoService {
    private final String FIRE_INFO_API_KEY;
    private static final String FIRE_INFO_URL = "http://apis.data.go.kr/1661000/FireInformationService";

    public SHFireInfoService() {
        if (Files.exists(Paths.get(".env"))) {
            Dotenv dotenv;

            try {
                dotenv = Dotenv.configure().load();
            } catch(Exception e) {
                System.err.println("Can't load ENV.");
                dotenv = null;
            }

            this.FIRE_INFO_API_KEY = dotenv != null ? dotenv.get("FIRE_INFO_API_KEY") : null;

            if (this.FIRE_INFO_API_KEY == null || this.FIRE_INFO_API_KEY.isEmpty()) {
                System.err.println("FIRE_INFO_API_KEY is missing!");
            }
        } else {
            System.err.println("ENV file not found.");
            this.FIRE_INFO_API_KEY = null;
        }
    }

    
}
