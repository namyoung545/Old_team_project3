package com.example.demo.service;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SHVMapService {

    private final String VWORLD_WFS_URL = "https://api.vworld.kr/req/wfs";

    public String fetchVWorldWFS(Map<String, String> params) {
        RestTemplate restTemplate = new RestTemplate();
        String queryString = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        String url = VWORLD_WFS_URL + "?" + queryString;
        
        return restTemplate.getForObject(url, String.class);
    }

}
