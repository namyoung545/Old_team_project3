package com.example.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.APIFiresDTO;
import com.example.demo.entity.FireStatistics;
import com.example.demo.service.SHAPIService;

@RestController
@RequestMapping("/sh_api")
public class SHAPIController {
    private final String VWORLD_WFS_URL = "https://api.vworld.kr/req/wfs";

    // API Service
    @Autowired
    SHAPIService shAPIService;

    @PostMapping("/fires")
    public List<FireStatistics> firesData(@RequestBody APIFiresDTO apiFiresDTO) {
        System.out.println("SHAPI - Fires");
        String year = apiFiresDTO.getYear();
        return shAPIService.getFiresData(year);
    }

    @PostMapping("/firesByStatName")
    public List<FireStatistics> firesDataByStatName(@RequestBody APIFiresDTO apiFiresDTO) {
        System.out.println("SHAPI - FiresByStatName");
        String statName = apiFiresDTO.getStatName();
        return shAPIService.getFiresDataByStatName(statName);
    }

    @PostMapping("/firesByYearAndStatName")
    public List<FireStatistics> firesDataByYearAndStatName(@RequestBody APIFiresDTO apiFiresDTO) {
        System.out.println("SHAPI - FiresDataByYearAndStatName");
        String year = apiFiresDTO.getYear();
        String statName = apiFiresDTO.getStatName();
        return shAPIService.getFiresDataByYearAndStatNameLike(year, statName);
    }
    
    // 사용안함 검색 부정확
    @PostMapping("/firesByStatNameLike")
    public List<FireStatistics> firesDataByStatNameLike(@RequestBody APIFiresDTO
    apiFiresDTO) {
    System.out.println("SHAPI - FiresByStatNameLike");
    String statName = apiFiresDTO.getStatName();
    return shAPIService.getFiresDataByStatNameLike(statName);
    }

    // 브이월드 기능
    @PostMapping("vworldWFS")
    public ResponseEntity<String> vworldWFS(@RequestParam Map<String, String> params) {
        RestTemplate restTemplate = new RestTemplate();
        String queryString = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        String url = VWORLD_WFS_URL + "?" + queryString;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        return ResponseEntity.ok(response.getBody());
    }



}
