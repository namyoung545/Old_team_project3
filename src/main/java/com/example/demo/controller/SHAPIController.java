package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.APIFiresDTO;
import com.example.demo.entity.FireStatistics;
import com.example.demo.service.SHAPIService;
import com.example.demo.service.SHVMapService;

@RestController
@RequestMapping("/sh_api")
public class SHAPIController {

    // API Service
    @Autowired
    SHAPIService shAPIService;

    @Autowired
    SHVMapService shVMapService;

    // 브이월드 기능
    @GetMapping("/vworldWFS")
    public ResponseEntity<String> getvworldWFS(@RequestParam Map<String, String> params) {
        System.out.println("SHAPI - VWorldWFS");
        String response = shVMapService.fetchVWorldWFS(params);
        // RestTemplate restTemplate = new RestTemplate();
        // String queryString = params.entrySet().stream()
        //         .map(entry -> entry.getKey() + "=" + entry.getValue())
        //         .collect(Collectors.joining("&"));
        // String url = VWORLD_WFS_URL + "?" + queryString;

        // ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return ResponseEntity.ok(response);
    }


    // 브이월드 기능
    @PostMapping("/vworldWFS")
    public ResponseEntity<String> vworldWFS(@RequestParam Map<String, String> params) {
        System.out.println("SHAPI - VWorldWFS");
        String response = shVMapService.fetchVWorldWFS(params);
        // RestTemplate restTemplate = new RestTemplate();
        // String queryString = params.entrySet().stream()
        //         .map(entry -> entry.getKey() + "=" + entry.getValue())
        //         .collect(Collectors.joining("&"));
        // String url = VWORLD_WFS_URL + "?" + queryString;

        // ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return ResponseEntity.ok(response);
    }

    // 연도별 화재 데이터
    @PostMapping("/fires")
    public List<FireStatistics> firesData(@RequestBody APIFiresDTO apiFiresDTO) {
        System.out.println("SHAPI - Fires");
        String year = apiFiresDTO.getYear();
        return shAPIService.getFiresData(year);
    }

    // 통계명 화재 데이터
    @PostMapping("/firesByStatName")
    public List<FireStatistics> firesDataByStatName(@RequestBody APIFiresDTO apiFiresDTO) {
        System.out.println("SHAPI - FiresByStatName");
        String statName = apiFiresDTO.getStatName();
        return shAPIService.getFiresDataByStatName(statName);
    }

    // 년도 통계명 화재 데이터
    @PostMapping("/firesByYearAndStatName")
    public List<FireStatistics> firesDataByYearAndStatName(@RequestBody APIFiresDTO apiFiresDTO) {
        System.out.println("SHAPI - FiresDataByYearAndStatName");
        String year = apiFiresDTO.getYear();
        String statName = apiFiresDTO.getStatName();
        return shAPIService.getFiresDataByYearAndStatNameLike(year, statName);
    }

    // 통계명 유사 검색
    @PostMapping("/firesByStatNameLike")
    public List<FireStatistics> firesDataByStatNameLike(@RequestBody APIFiresDTO apiFiresDTO) {
        System.out.println("SHAPI - FiresByStatNameLike");
        String statName = apiFiresDTO.getStatName();
        return shAPIService.getFiresDataByStatNameLike(statName);
    }
}
