package com.example.demo.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.entity.FireInfoSidoEntity;
import com.example.demo.repository.FireInfoSidoRepository;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class SHFireInfoService {
    private final String FIRE_INFO_API_KEY;
    private static final String FIRE_INFO_URL = "http://apis.data.go.kr/1661000/FireInformationService";

    private final FireInfoSidoRepository fireInfoSidoRepository;

    public SHFireInfoService(FireInfoSidoRepository fireInfoSidoRepository) {
        if (Files.exists(Paths.get(".env"))) {
            Dotenv dotenv;

            try {
                dotenv = Dotenv.configure().load();
            } catch (Exception e) {
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

        this.fireInfoSidoRepository = fireInfoSidoRepository;
    }

    public List<FireInfoSidoEntity> getFireInfoSido() {
        System.out.println("FireInfoService - getFireInfo");
        LocalDate today = LocalDate.now();
        List<FireInfoSidoEntity> response = fireInfoSidoRepository.findByOcrnYmd(today);

        return response;
    }

    public String fetchFireInfoSidoData() {
        if (FIRE_INFO_API_KEY == null || FIRE_INFO_API_KEY.isEmpty()) {
            return "FireInfoService - API Key is missing! Check ENF!";
        }

        LocalDate localDate = LocalDate.now();
        String today = localDate.toString().replace("=", "");

        String url = UriComponentsBuilder.fromUriString(FIRE_INFO_URL)
                .queryParam("ServiceKey", FIRE_INFO_API_KEY)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 100)
                .queryParam("resultType", "json")
                .queryParam("ocrn_ymd", today)
                .build().toUriString();

        WebClient webClient = WebClient.create();
        try {
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return response;
        } catch (Exception e) {
            System.err.println("[ERROR] FireInfoSerbice - Webclient / Fail to fetch fire information data!");
            return "Failed to fetch fire information data!";
        }
    }

}
