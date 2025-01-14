package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.FireCauseData;
import com.example.demo.entity.FireStatistics;
import com.example.demo.repository.FireCauseDataRepository;
import com.example.demo.repository.FireStatisticsRepository;
import com.example.demo.repository.FiresDataRepository;

@Service
public class SHFireStatisticsService {
    @Autowired
    private FiresDataRepository firesDataRepository;
    @Autowired
    private FireCauseDataRepository fireCauseDataRepository;
    @Autowired
    private FireStatisticsRepository fireStatisticsRepository;

    public void runFireStatistics() {
        // analyzeYearlyCasualty(null);
        getFireCauseCategory("전기");
    }



    // 화재 원인 분류 분석
    private List<Integer> getFireCauseCategory(String keyword) {
        System.out.println("getFireCauseCategory");
        List<FireCauseData> fireCauseDataList = fireCauseDataRepository.findLikeCauseCategory(keyword);

        // 검색된 결과 출력
        if (!fireCauseDataList.isEmpty()) {
            List<Integer> fireCauseIds = fireCauseDataList.stream().map(FireCauseData::getId).collect(Collectors.toList());
            System.out.println("검색된 화재 원인 카테고리 ID 리스트 : " + fireCauseIds);
            return fireCauseIds;
        } else {
            System.out.println("검색된 화재 원인 카테고리 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 연간 화재 원인 통계 분석
    private void analyzeYearlyCausesData(Integer year) {
        System.out.println("analyzeYearlyCausesData");
        List<Object[]> results;
        if (year != null) {
            // results = firesDataRepository.
            // Long fireCount = firesDataRepository.countByFireCauseIds(fireCauseIds);
        }
    }

    // 연간 사망 부상자 통계 분석
    private void analyzeYearlyCasualty(Integer year) {
        System.out.println("analyzeYearlyCasualty " + year);
        List<Object[]> results;
        if (year != null) {
            results = firesDataRepository.findYearlyCasualtyStatistics(year);
        } else {
            results = firesDataRepository.findYearlyCasualtyStatistics();
        }

        // 결과 저장
        for (Object[] result : results) {

            System.out.println(result.toString());
            String yearValue = result[0].toString();
            String totalDeaths = result[1].toString();
            String totalInjuries = result[2].toString();
            String totalCasualties = result[3].toString();

            // "사망자 수" 통계 저장 또는 업데이트
            saveFireStatisticsDB(yearValue, "사망자 수", totalDeaths);

            // "부상자 수" 통계 저장 또는 업데이트
            saveFireStatisticsDB(yearValue, "부상자 수", totalInjuries);

            // "인명피해 합계" 통계 저장 또는 업데이트
            saveFireStatisticsDB(yearValue, "인명피해 합계", totalCasualties);
        }
    }

    // 통계 데이터 업데이트 또는 저장
    private void saveFireStatisticsDB(String year, String statName, String statValue) {
        // 기존 데이터 조회
        Optional<FireStatistics> existingStat = fireStatisticsRepository.findByYearAndStatName(year, statName);
        FireStatistics fireStatistics = existingStat.orElse(new FireStatistics());

        // 기존 데이터가 있으면 업데이트, 없으면 새로 생성
        fireStatistics.setYear(year);
        fireStatistics.setStatName(statName);
        fireStatistics.setStatValue(statValue);
        // fireStatistics.setCreatedAt(fireStatistics.getCreatedAt() == null ?
        // LocalDateTime.now() : fireStatistics.getCreatedAt());
        // fireStatistics.setUpdatedAt(LocalDateTime.now());

        // 기존 데이터가 있으면 updatedAt을 갱신해야 함
        if (existingStat.isPresent()) {
            fireStatistics.setUpdatedAt(LocalDateTime.now()); // 수동으로 updatedAt 갱신
        }

        // 저장
        fireStatisticsRepository.save(fireStatistics);
    }

}
