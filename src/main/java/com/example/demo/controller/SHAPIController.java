package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.APIFiresDTO;
import com.example.demo.entity.FireStatistics;
import com.example.demo.service.SHAPIService;

@RestController
@RequestMapping("/sh_api")
public class SHAPIController {
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


    // 사용안함 검색 부정확
    // @PostMapping("/firesByStatNameLike")
    // public List<FireStatistics> firesDataByStatNameLike(@RequestBody APIFiresDTO apiFiresDTO) {
    //     System.out.println("SHAPI - FiresByStatNameLike");
    //     String statName = apiFiresDTO.getStatName();
    //     return shAPIService.getFiresDataByStatNameLike(statName);
    // }

    // @PostMapping("/firesByYearAndStatName") 
    // public Optional<FireStatistics> firesDataByYearAndStatName(@RequestBody APIFiresDTO apiFiresDTO) {
    //     System.out.println("SHAPI - FiresByYearAndStatName");
    //     String year = apiFiresDTO.getYear();
    //     String statName = apiFiresDTO.getStatName();
    //     return shAPIService.getFiresDataByYearAndStatName(year, statName);
    // }
    
}
