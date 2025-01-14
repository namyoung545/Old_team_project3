package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.FireStatistics;
import com.example.demo.repository.FireStatisticsRepository;
import com.example.demo.repository.FiresDataRepository;

@Service
public class SHFireStatisticsService {
    @Autowired
    private FiresDataRepository firesDataRepository;
    // @Autowired
    // private FireStatisticsRepository fireStatisticsRepository;

    public void runFireStatistics() {
        // saveYearlyCasualtyStatistics(2023);
    }

    // public void saveYearlyCasualtyStatistics(Integer year) {
    //     System.out.println("saveYearlyCasualtyStatistics " + year);
    //     List<Object[]> results;
    //     if (year != null) {
    //         results = firesDataRepository.findYearlyCasualtyStatistics(year);
    //     } else {
    //         results = firesDataRepository.findYearlyCasualtyStatistics();
    //     }

    //     // 결과 저장
    //     for (Object[] result : results) {

    //         System.out.println(result.toString());
    //         Integer yearValue = (Integer) result[0];
    //         Long totalDeaths = (Long) result[1];
    //         Long totalInjuries = (Long) result[2];
    //         System.out.println(yearValue);
    //         System.out.println(totalDeaths);
    //         System.out.println(totalInjuries);

    //         // // 통계 데이터 생성
    //         // FireStatistics deathStatistics = new FireStatistics();
    //         // deathStatistics.setYear(yearValue);
    //         // deathStatistics.setStatName("사망자 수");
    //         // deathStatistics.setStatValue(totalDeaths.doubleValue());
    //         // deathStatistics.setCreatedAt(LocalDateTime.now());
    //         // deathStatistics.setUpdatedAt(LocalDateTime.now());
    //         // fireStatisticsRepository.save(deathStatistics);

    //         // FireStatistics injuryStatistics = new FireStatistics();
    //         // injuryStatistics.setYear(yearValue);
    //         // injuryStatistics.setStatName("부상자 수");
    //         // injuryStatistics.setStatValue(totalInjuries.doubleValue());
    //         // injuryStatistics.setCreatedAt(LocalDateTime.now());
    //         // injuryStatistics.setUpdatedAt(LocalDateTime.now());
    //         // fireStatisticsRepository.save(injuryStatistics);
    //     }
    // }


    // // 통계 데이터 업데이트 또는 저장
    // private void updateOrSaveStatistics(Integer year, String statName, String statValue) {
    //     // 기존 데이터 조회
    //     // Optional<FireStatistics> existingStat = fireStatisticsRepository.findByYearAndStatName(year, statName);

    //     // FireStatistics fireStatistics = existingStat.orElse(new FireStatistics());

    //     // // 기존 데이터가 있으면 업데이트, 없으면 새로 생성
    //     // fireStatistics.setYear(year);
    //     // fireStatistics.setStatName(statName);
    //     // fireStatistics.setStatValue(statValue);
    //     // fireStatistics.setCreatedAt(fireStatistics.getCreatedAt() == null ? LocalDateTime.now() : fireStatistics.getCreatedAt());
    //     // fireStatistics.setUpdatedAt(LocalDateTime.now());

    //     // // 저장
    //     // fireStatisticsRepository.save(fireStatistics);
    // }

}
