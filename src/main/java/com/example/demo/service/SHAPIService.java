package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.FireStatistics;
import com.example.demo.repository.FireStatisticsRepository;

@Service
public class SHAPIService {

    @Autowired
    FireStatisticsRepository fireStatisticsRepository;

    // DB 검색 로직을 여기에 작성
    public List<FireStatistics> getFiresData(String year) {
        System.out.println("API Service - getFiresData");
        return fireStatisticsRepository.findByYear(year);
    }

    public List<FireStatistics> getFiresDataByStatName(String statName) {
        System.out.println("API Service - getFiresDataByStatName");
        return fireStatisticsRepository.findByStatName(statName);
    }

    public List<FireStatistics> getFiresDataByYearAndStatNameLike(String year, String statName) {
        System.out.println("API Service - getFiresDataByYearAndStatNameLike");
        return fireStatisticsRepository.findByYearAndStatNameLike(year, statName);
    }

    public List<FireStatistics> getFiresDataByStatNameLike(String statName) {
        System.out.println("API Service - getFiresDataByStatNameLike");
        return fireStatisticsRepository.findByStatNameLike(statName);
    }

    public Optional<FireStatistics> getFiresDataByYearAndStatName(String year, String statName) {
        System.out.println("API Service - getFiresDataByYearAndStatName");
        return fireStatisticsRepository.findByYearAndStatName(year, statName);
    }
}
