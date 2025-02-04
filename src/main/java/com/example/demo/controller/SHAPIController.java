package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.APIFiresDTO;
import com.example.demo.dto.APIVWorldWFSDTO;
import com.example.demo.entity.FireInfoSidoCasualtyEntity;
import com.example.demo.entity.FireInfoSidoDamageEntity;
import com.example.demo.entity.FireInfoSidoEntity;
import com.example.demo.entity.FireStatistics;
import com.example.demo.entity.SHDisasterMessageEntity;
import com.example.demo.service.SHAPIService;
import com.example.demo.service.SHDisasterService;
import com.example.demo.service.SHFireInfoService;
import com.example.demo.service.SHFullCalendarService;
import com.example.demo.service.SHVMapService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/sh_api")
public class SHAPIController {

    // API Service
    @Autowired
    SHAPIService shAPIService;

    @Autowired
    SHVMapService shVMapService;

    @Autowired
    SHDisasterService shDisasterService;

    @Autowired
    SHFireInfoService shFireInfoService;

    @Autowired
    SHFullCalendarService shFullCalendarService;

    // 달력 이벤트 기능
    @PostMapping("/calendarEvents")
    public List<Map<String, Object>> postCalendarEvents() throws Exception {
        System.out.println("SHAPI - calendarEvents");
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);
        List<Map<String, Object>> eventsList = shFullCalendarService.getFullCalendarEvents(start, end);
        return eventsList;
    }

    // 화재정보 기능
    @PostMapping("/fireInfo")
    public ResponseEntity<?> postFireInformation() {
        System.out.println("SHAPI - fireInfo");
        List<FireInfoSidoEntity> response = shFireInfoService.getFireInfoSido();

        return ResponseEntity.ok(response);
    }

    // 화재정보 기능 - 인명피해
    @PostMapping("/fireInfoCasualty")
    public ResponseEntity<?> postFireInformationCasualty() {
        System.out.println("SHAPI - fireInfoCasualty");
        List<FireInfoSidoCasualtyEntity> response = shFireInfoService.getFireInfoSidoCasualty();

        return ResponseEntity.ok(response);
    }

    // 화재정보 기능 - 재산피해
    @PostMapping("/fireInfoDamage")
    public ResponseEntity<?> postFireInformationProperty() {
        System.out.println("SHAPI - fireInfoDamage");
        List<FireInfoSidoDamageEntity> response = shFireInfoService.getFireInfoSidoDamage();

        // String responseDamage =
        // shFireInfoService.fetchFireInfoSidoDamageData(LocalDate.now().minusDays(1));
        // System.out.println("responseDamage : " + responseDamage);

        return ResponseEntity.ok(response);
    }

    // 재난문자 기능 DB 호출
    @PostMapping("/disasterMessage")
    public ResponseEntity<?> postDisasterMessage() {
        System.out.println("SHAPI - getDisasterMessage");
        List<SHDisasterMessageEntity> response = shDisasterService.getDisasterMessage();
        return ResponseEntity.ok(response);
    }

    // 재난문자 기능 API 호출
    @PostMapping("/disaster")
    public String postDisaster() {
        System.out.println("SHAPI - getDisasterData");
        String response = shDisasterService.getDisasterMessageData();
        return response;
    }

    // 브이월드 기능
    @GetMapping("/vworldWFS")
    public ResponseEntity<String> getvworldWFS(@RequestParam Map<String, String> params) {
        System.out.println("SHAPI - getVWorldWFS");
        String response = shVMapService.fetchVWorldWFS(params);
        // RestTemplate restTemplate = new RestTemplate();
        // String queryString = params.entrySet().stream()
        // .map(entry -> entry.getKey() + "=" + entry.getValue())
        // .collect(Collectors.joining("&"));
        // String url = VWORLD_WFS_URL + "?" + queryString;

        // ResponseEntity<String> response = restTemplate.getForEntity(url,
        // String.class);
        return ResponseEntity.ok(response);
    }

    // 브이월드 기능
    @PostMapping("/vworldWFS")
    public ResponseEntity<?> postVWorldWFS(@Valid @RequestBody APIVWorldWFSDTO requestDTO,
            BindingResult bindingResult) {
        System.out.println("SHAPI - postVWorldWFS");

        // 유효성 검사
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        // 서비스 연결
        try {
            Object response = shVMapService.apiVWorldWFSData(requestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("[ERROR] Processing request!" + e.getMessage());
        }
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
