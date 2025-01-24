package com.example.demo.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.entity.SHDisasterMessageEntity;
import com.example.demo.repository.SHDisasterMessageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.cdimascio.dotenv.Dotenv;

@Service
@EnableScheduling
public class SHDisasterService {
    // API 세팅
    private final String DISASTER_API_KEY;
    private static final String DISASTER_URL = "https://www.safetydata.go.kr/V2/api/DSSP-IF-00247";

    // DB Repository 세팅
    private final SHDisasterMessageRepository shDisasterMessageRepository;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss[.SSSSSSSSS]");

    public SHDisasterService(SHDisasterMessageRepository shDisasterMessageRepository) {
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
        this.shDisasterMessageRepository = shDisasterMessageRepository;

    }

    public List<SHDisasterMessageEntity> getDisasterMessage() {
        System.out.println("DisasterService - getDisastermessage");
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        List<SHDisasterMessageEntity> response = shDisasterMessageRepository.findCrtDtAfter(startOfToday);
        
        return response;
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

    @Scheduled(fixedRate = 1800000)
    public void updateDisasterMessageData() {
        String response = getDisasterMessageData();
        System.out.println("Response : " + response);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);

            JsonNode bodyArray = rootNode.path("body");
            if (bodyArray.isArray()) {
                for (JsonNode item : bodyArray) {
                    Map<String, Object> apiResponse = new HashMap<>();
                    apiResponse.put("SN", item.path("SN").asInt());
                    apiResponse.put("MSG_CN", item.path("MSG_CN").asText());
                    apiResponse.put("RCPTN_RGN_NM", item.path("RCPTN_RGN_NM").asText());
                    apiResponse.put("CRT_DT", item.path("CRT_DT").asText());
                    apiResponse.put("REG_YMD", item.path("REG_YMD").asText());
                    apiResponse.put("EMRG_STEP_NM", item.path("EMRG_STEP_NM").asText());
                    apiResponse.put("DST_SE_NM", item.path("DST_SE_NM").asText());
                    apiResponse.put("MDFCN_YMD", item.path("MDFCN_YMD").asText());

                    // System.out.println(apiResponse);
                    SHDisasterMessageEntity savedEntity = saveDisasterMessageDB(apiResponse);
                    System.out.println("Saved Data : " + savedEntity);
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to process disaster data : " + e.getMessage());
        }
    }

    private SHDisasterMessageEntity saveDisasterMessageDB(Map<String, Object> apiResponse) {
        SHDisasterMessageEntity entity = new SHDisasterMessageEntity();
        Optional<SHDisasterMessageEntity> existingEntity = shDisasterMessageRepository
                .findBySN((Integer) apiResponse.get("SN"));

        if (existingEntity.isPresent()) {
            entity = existingEntity.get();
        }

        // API 응답 데이터를 매핑
        entity.setSn((Integer) apiResponse.get("SN"));
        // String -> LocalDateTime 변환
        // entity.setCrt_dt(LocalDateTime.parse((String) apiResponse.get("CRT_DT")));
        entity.setCrt_dt(LocalDateTime.parse((String) apiResponse.get("CRT_DT"), formatter));
        entity.setMsg_cn((String) apiResponse.get("MSG_CN"));
        entity.setRcptn_rgn_nm((String) apiResponse.get("RCPTN_RGN_NM"));
        entity.setEmrg_step_nm((String) apiResponse.get("EMRG_STEP_NM"));
        entity.setDst_se_nm((String) apiResponse.get("DST_SE_NM"));
        entity.setReg_ymd(LocalDateTime.parse((String) apiResponse.get("REG_YMD"), formatter));
        entity.setMdfcn_ymd(LocalDateTime.parse((String) apiResponse.get("MDFCN_YMD"), formatter));

        // 데이터 저장
        return shDisasterMessageRepository.save(entity);
    }
}
