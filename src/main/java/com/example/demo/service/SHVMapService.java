package com.example.demo.service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.APIVWorldWFSDTO;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class SHVMapService {

    private final String VWORLD_API_KEY;

    private final String VWORLD_WFS_URL = "https://api.vworld.kr/req/wfs";

    public SHVMapService() {
        Dotenv dotenv = Dotenv.configure().load();
        this.VWORLD_API_KEY = dotenv.get("VWORLD_API_KEY");
    }

    public String fetchVWorldWFS(Map<String, String> params) {
        RestTemplate restTemplate = new RestTemplate();
        params.put("key", VWORLD_API_KEY);
        params.put("SERVICE", "WFS");
        params.put("VERSION", "2.0.0");
        params.put("REQUEST", "GetFeature");
        params.put("OUTPUT", "application/json");
        params.put("SRSNAME", "EPSG:4326");
        params.put("DOMAIN", "localhost");
        String queryString = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        String url = VWORLD_WFS_URL + "?" + queryString;

        return restTemplate.getForObject(url, String.class);
    }

    public Object apiVWorldWFSData(APIVWorldWFSDTO requestDTO) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();

        params.put("key", VWORLD_API_KEY);
        params.put("SERVICE", requestDTO.getSERVICE());
        params.put("VERSION", requestDTO.getVERSION());
        params.put("REQUEST", requestDTO.getREQUEST());
        params.put("OUTPUT", requestDTO.getOUTPUT());
        params.put("SRSNAME", requestDTO.getSRSNAME());
        params.put("DOMAIN", requestDTO.getDOMAIN());
        params.put("TYPENAME", requestDTO.getTYPENAME());

        String queryString = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        String url = VWORLD_WFS_URL + "?" + queryString;
        System.out.println(queryString);
        System.out.println(url);
        try {
            String response = restTemplate.getForObject(url, String.class);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("[ERROR] API 요청 오류" + e.getMessage());
        }
    }

}
