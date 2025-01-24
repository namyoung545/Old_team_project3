package com.example.demo.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class SHDisasterService {

    private final String DISASTER_API_KEY;
    private static final String DISASTER_URL = "https://www.safetydata.go.kr/V2/api/DSSP-IF-00247";

    public SHDisasterService() {
        // ENV 파일 확인
        if (Files.exists(Paths.get(".env"))) {
            Dotenv dotenv;

            // API 키 로드
            try {
                dotenv = Dotenv.configure().load();
            } catch (Exception e) {
                System.err.println("Can't load ENV.");
                dotenv = null;
            }

            // API 키 설정
            this.DISASTER_API_KEY = dotenv != null ? dotenv.get("SAFETYDATA_API_KEY") : null;

            // 에러 메시지 출력
            if (this.DISASTER_API_KEY == null || this.DISASTER_API_KEY.isEmpty()) {
                System.err.println("SAFETYDATA_API_KEY is missing!");
            }
        } else {
            System.err.println("Env file not found.");
            this.DISASTER_API_KEY = null;
        }
    }

    public void updateDisasterMessageData() {
        LocalDate localDate = LocalDate.now();
        String today = localDate.toString().replace("-", "");
        

    }

    public String getDisasterMessageData() {
        // API 키 확인
        if (DISASTER_API_KEY == null || DISASTER_API_KEY.isEmpty()) {
            return "API Key is missing! Check ENV";
        }

        // 오늘 날짜 설정
        LocalDate localDate = LocalDate.now();
        String today = localDate.toString().replace("-", "");

        // API URL
        String url = UriComponentsBuilder.fromUriString(DISASTER_URL)
                .queryParam("serviceKey", DISASTER_API_KEY)
                .queryParam("numOfRows", 100)
                .queryParam("pageNo", 1)
                .queryParam("returnType", "json")
                .queryParam("crtDt", today)
                .build().toUriString();

        // API 요청
        WebClient webClient = WebClient.create();
        try {
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return response;
        } catch (Exception e) {
            System.err.println("[ERROR] WebClient - Fail to fetch disaster data!");
            return "Failed to fetch disaster data!";
        }
    }
}
