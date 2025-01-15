package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.FireCauseData;
import com.example.demo.entity.FireIgnitionData;
import com.example.demo.entity.FireItemData;
import com.example.demo.entity.FireLocationData;
import com.example.demo.entity.FireRegionData;
import com.example.demo.entity.FireStatistics;
import com.example.demo.entity.FireTypeData;
import com.example.demo.repository.FireCauseDataRepository;
import com.example.demo.repository.FireIgnitionDataRepository;
import com.example.demo.repository.FireItemDataRepository;
import com.example.demo.repository.FireLocationDataRepository;
import com.example.demo.repository.FireRegionDataRepository;
import com.example.demo.repository.FireStatisticsRepository;
import com.example.demo.repository.FireTypeDataRepository;
import com.example.demo.repository.FiresDataRepository;

@Service
public class SHFireStatisticsService {
    @Autowired
    private FiresDataRepository firesDataRepository;
    @Autowired
    private FireCauseDataRepository fireCauseDataRepository;
    @Autowired
    private FireIgnitionDataRepository fireIgnitionDataRepository;
    @Autowired
    private FireItemDataRepository fireItemDataRepository;
    @Autowired
    private FireLocationDataRepository fireLocationDataRepository;
    @Autowired
    private FireRegionDataRepository fireRegionDataRepository;
    @Autowired
    private FireTypeDataRepository fireTypeDataRepository;
    @Autowired
    private FireStatisticsRepository fireStatisticsRepository;

    public void runFireStatistics() {

        // 연간 화재 발생 건수 분석
        analyzeYearlyFiresData();

        // 연간 원인별 화재 발생 건수 분석
        analyzeYearlyFiresByCauses();
        // analyzeYearlyFiresByCause("전기적 요인");

        // 연간 재산피해 분석
        analyzeYearlyDamageProperty();

        // 연간 원인별 재산피해 분석
        analyzeYearlyDamagePropertyByCauses();
        // analyzeYearlyDamagePropertyByCause("전기");

        // 연간 사망 부상자 분석
        analyzeYearlyCasualties(null);

        // 연간 원인별 사망 부상자 분석
        analyzeYearlyCasualtiesByCauses();
        // analyzeYearlyCasualtiesByCause("전기");

        // 기능 확인용 코드

        // 분류 목록
        // getFireCauseCategories();
        // getFireCauseSubcategories();

        // 데이터 검증 확인용
        // getFireCauseCategoryIds("전기");
        // getFireCauseSubcategoryIds("불씨");
        // getFireIgnitionCategoryIds("작동");
        // getFireIgnitionSubcategoryIds("전기");
        // getFireItemCategoryIds("전기");
        // getFireItemDetailIds("나무");
        // getFireLocationMainCategoryIds("주거");
        // getFireLocationSubcategoryIds("단독");
        // getFireLocationDetailIds("자동차");
        // getFireRegionProvinceIds("울산");
        // getFireRegionCityIds("남구");
        // getFireTypeIds("건축");

    }


    // 연간 화재 발생 건수 분석
    private void analyzeYearlyFiresData() {
        System.out.println("analyzeYearlFiresData");
        List<Object[]> results = firesDataRepository.countYearlyFires();

        for (Object[] result : results) {
            // System.out.println("Result :" + result);
            // System.out.println(Arrays.toString(result));

            String yearValue = result[0].toString();
            String totalFires = result[1].toString();

            saveFireStatisticsDB(yearValue, "화재발생건수", totalFires);
        }
    }

    // 연간 원인별 화재 발생 건수 분석(키워드)
    private void analyzeYearlyFiresByCause(String keyword) {
        System.out.println("analyzeYearlyFiresByCause");
        List<Integer> fireCauseIds = getFireCauseCategoryIds(keyword);

        List<Object[]> results = firesDataRepository.countYearlyFiresByCauseIds(fireCauseIds);
        for (Object[] result : results) {
            // System.out.println("Result : " + result);
            // System.out.println(Arrays.toString(result));

            String yearValue = result[0].toString();
            String countValue = result[1].toString();

            saveFireStatisticsDB(yearValue, "화재발생건수(원인) " + keyword, countValue);
        }
    }

    // 연간 원인별 화재 발생 건수 분석(자동)
    private void analyzeYearlyFiresByCauses() {
        System.out.println("analyzeYearlyFiresByCauses");
        List<String> fireCauses = getFireCauseCategories();

        for (String cause : fireCauses) {
            List<FireCauseData> fireCauseDataList = fireCauseDataRepository.findByCauseCategory(cause);
            List<Integer> fireCauseIds = fireCauseDataList.stream().map(FireCauseData::getId)
                    .collect(Collectors.toList());

            List<Object[]> results = firesDataRepository.countYearlyFiresByCauseIds(fireCauseIds);
            for (Object[] result : results) {
                // System.out.println("Result : " + result);
                // System.out.println(Arrays.toString(result));

                String yearValue = result[0].toString();
                String countValue = result[1].toString();

                saveFireStatisticsDB(yearValue, "화재발생건수(원인) " + cause, countValue);
            }
        }
    }

    // 화재 열연별 분석 
    private void analyzeYearlyFiresByIgnitions() {
        System.out.println("analyzeFiresByIgnitions");
        // List<String> fireIgnitions = getFireIgnitionCategories();
        
    }
    // 연간 재산피해 분석
    private void analyzeYearlyDamageProperty() {
        System.out.println("analyzeYearlyDamageProperty");
        List<Object[]> results = firesDataRepository.sumYearlyDamageProperty();

        for (Object[] result : results) {
            // System.out.println("Result : " + result);
            // System.out.println(Arrays.toString(result));

            String yearValue = result[0].toString();
            String sumDamageProperty = result[1].toString();

            saveFireStatisticsDB(yearValue, "재산피해합계", sumDamageProperty);
        }
    }

    // 연간 원인별 재산피해 분석(키워드)
    private void analyzeYearlyDamagePropertyByCause(String keyword) {
        System.out.println("analyzeYearlyDamagePropertyByCause");
        List<Integer> fireCauseIds = getFireCauseCategoryIds(keyword);

        List<Object[]> results = firesDataRepository.sumYearlyDamagePropertyByCauseIds(fireCauseIds);
        for (Object[] result : results) {
            // System.out.println("Result : " + result);
            // System.out.println(Arrays.toString(result));

            String yearValue = result[0].toString();
            String sumDamageProperty = result[1].toString();

            saveFireStatisticsDB(yearValue, "재산피해합계(원인) " + keyword, sumDamageProperty);
        }
    }

    // 연간 원인별 재산피해 분석(자동)
    private void analyzeYearlyDamagePropertyByCauses() {
        System.out.println("analyzeYearlyDamagePropertyByCauses");
        List<String> fireCauses = getFireCauseCategories();

        for (String cause : fireCauses) {
            List<FireCauseData> fireCauseDataList = fireCauseDataRepository.findByCauseCategory(cause);
            List<Integer> fireCauseIds = fireCauseDataList.stream().map(FireCauseData::getId)
                    .collect(Collectors.toList());

            List<Object[]> results = firesDataRepository.sumYearlyDamagePropertyByCauseIds(fireCauseIds);
            for (Object[] result : results) {
                // System.out.println("Result : " + result);
                // System.out.println(Arrays.toString(result));

                String yearValue = result[0].toString();
                String sumDamageProperty = result[1].toString();

                saveFireStatisticsDB(yearValue, "재산피해합계(원인) " + cause, sumDamageProperty);
            }
        }
    }

    // 연간 사망 부상자 통계 분석
    private void analyzeYearlyCasualties(Integer year) {
        System.out.println("analyzeYearlyCasualty " + year);
        List<Object[]> results;
        if (year != null) {
            results = firesDataRepository.countYearlyCasualties(year);
        } else {
            results = firesDataRepository.countYearlyCasualties();
        }

        // 결과 저장
        for (Object[] result : results) {
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

    // 연간 원인별 사망자 부상자 분석(키워드)
    private void analyzeYearlyCasualtiesByCause(String keyword) {
        System.out.println("analyzeYearlyCasualtiesByCause");
        List<Integer> fireCauseIds = getFireCauseCategoryIds(keyword);
        List<Object[]> results = firesDataRepository.countYearlyCasualtiesByCausesIds(fireCauseIds);
        for (Object[] result : results) {
            // System.out.println("Result : " + result);
            // System.out.println(Arrays.toString(result));

            String yearValue = result[0].toString();
            String countDeaths = result[1].toString();
            String countInjuries = result[2].toString();
            String countCasualties = result[3].toString();

            saveFireStatisticsDB(yearValue, "사망자 수(원인) " + keyword, countDeaths);
            saveFireStatisticsDB(yearValue, "부상자 수(원인) " + keyword, countInjuries);
            saveFireStatisticsDB(yearValue, "인명피해 합계(원인) " + keyword, countCasualties);
        }
    }

    // 연간 원인별 사망자 부상자 분석(자동)
    private void analyzeYearlyCasualtiesByCauses() {
        System.out.println("analyzeYearlyCasualtiesByCauses");
        List<String> fireCauses = getFireCauseCategories();

        for (String cause : fireCauses) {
            List<FireCauseData> fireCauseDataList = fireCauseDataRepository.findByCauseCategory(cause);
            List<Integer> fireCauseIds = fireCauseDataList.stream().map(FireCauseData::getId)
                    .collect(Collectors.toList());

            List<Object[]> results = firesDataRepository.countYearlyCasualtiesByCausesIds(fireCauseIds);
            for (Object[] result : results) {
                // System.out.println("Result : " + result);
                // System.out.println(Arrays.toString(result));

                String yearValue = result[0].toString();
                String countDeaths = result[1].toString();
                String countInjuries = result[2].toString();
                String countCasualties = result[3].toString();

                saveFireStatisticsDB(yearValue, "사망자 수(원인) " + cause, countDeaths);
                saveFireStatisticsDB(yearValue, "부상자 수(원인) " + cause, countInjuries);
                saveFireStatisticsDB(yearValue, "인명피해 합계(원인) " + cause, countCasualties);
            }
        }
    }

    // 통계 데이터 업데이트 저장
    private void saveFireStatisticsDB(String year, String statName, String statValue) {
        // 기존 데이터 조회
        Optional<FireStatistics> existingStat = fireStatisticsRepository.findByYearAndStatName(year, statName);
        FireStatistics fireStatistics = existingStat.orElse(new FireStatistics());

        // 기존 데이터가 있으면 업데이트, 없으면 새로 생성
        fireStatistics.setYear(year);
        fireStatistics.setStatName(statName);
        fireStatistics.setStatValue(statValue);

        // 시간 정보 수동처리 (현재 자동화 시킴)
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

    // 화재 원인 카테고리 목록
    private List<String> getFireCauseCategories() {
        System.out.println("getFireCauseCetegories");
        List<String> fireCauses = fireCauseDataRepository.findDistinctCauseCategory();
        System.out.println("fireCausesCategories : " + fireCauses);
        return fireCauses;
    }

    // 화재 원인 서브 카테고리 목록
    private List<String> getFireCauseSubcategories() {
        System.out.println("getFireCauseSubcategories");
        List<String> fireCauses = fireCauseDataRepository.findDistinctCauseSubcategory();
        System.out.println("fireCauseSubcategories : " + fireCauses);
        return fireCauses;
    }

    // 화재 원인 카테고리 ID 정보
    private List<Integer> getFireCauseCategoryIds(String keyword) {
        System.out.println("getFireCauseCategoryIds");
        List<FireCauseData> fireCauseDataList = fireCauseDataRepository.findLikeCauseCategory(keyword);

        // 검색된 결과 출력
        if (!fireCauseDataList.isEmpty()) {
            List<Integer> fireCauseIds = fireCauseDataList.stream().map(FireCauseData::getId)
                    .collect(Collectors.toList());
            System.out.println("검색된 화재 원인 카테고리 ID 리스트 : " + fireCauseIds);
            return fireCauseIds;
        } else {
            System.out.println("검색된 화재 원인 카테고리 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    private List<Integer> getFireCauseSubcategoryIds(String keyword) {
        System.out.println("getFireCauseSubcategoryIds");
        List<FireCauseData> fireCauseDataList = fireCauseDataRepository.findLikeCauseSubcategory(keyword);

        if (!fireCauseDataList.isEmpty()) {
            List<Integer> fireCauseIds = fireCauseDataList.stream().map(FireCauseData::getId)
                    .collect(Collectors.toList());
            System.out.println("검색된 화재 원인 서브 카테고리 ID 리스트 : " + fireCauseIds);
            return fireCauseIds;
        } else {
            System.out.println("검색된 하재 원인 서브 카테고리 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 열원 카테고리 ID 정보
    private List<Integer> getFireIgnitionCategoryIds(String keyword) {
        System.out.println("getFireIgnitionCategoryIds");
        List<FireIgnitionData> fireIgnitionDataList = fireIgnitionDataRepository.findLikeIgnitionCategory(keyword);

        if (!fireIgnitionDataList.isEmpty()) {
            List<Integer> fireIgnitionIds = fireIgnitionDataList.stream().map(FireIgnitionData::getId)
                    .collect(Collectors.toList());
            System.out.println("검색된 화재 열원 카테고리 ID 리스트 : " + fireIgnitionIds);
            return fireIgnitionIds;
        } else {
            System.out.println("검색된 화재 열원 카테고리 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 열원 서브 카테고리 ID 정보
    private List<Integer> getFireIgnitionSubcategoryIds(String keyword) {
        System.out.println("getFireIgnitionSubcategoryIds");
        List<FireIgnitionData> fireIgnitionDataList = fireIgnitionDataRepository.findLikeIgnitionSubcategory(keyword);

        if (!fireIgnitionDataList.isEmpty()) {
            List<Integer> fireIgnitionIds = fireIgnitionDataList.stream().map(FireIgnitionData::getId)
                    .collect(Collectors.toList());
            System.out.println("검색된 화재 열원 서브 카테고리 ID 리스트 : " + fireIgnitionIds);
            return fireIgnitionIds;
        } else {
            System.out.println("검색된 화재 열원 서브 카테고리 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 발화 착화물 카테고리 ID 정보
    private List<Integer> getFireItemCategoryIds(String keyword) {
        System.out.println("getFireItemCategoryIds");
        List<FireItemData> fireItemDataList = fireItemDataRepository.findLikeItemCategory(keyword);

        if (!fireItemDataList.isEmpty()) {
            List<Integer> fireItemIds = fireItemDataList.stream().map(FireItemData::getId).collect(Collectors.toList());
            System.out.println("검색된 화재 착화물 카테고리 ID 리스트 : " + fireItemIds);
            return fireItemIds;
        } else {
            System.out.println("검색된 화재 착화물 카테고리 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 발화 착화물 디테일 ID 정보
    private List<Integer> getFireItemDetailIds(String keyword) {
        System.out.println("getFireItemDetailIds");
        List<FireItemData> fireItemDataList = fireItemDataRepository.findLikeItemDetail(keyword);

        if (!fireItemDataList.isEmpty()) {
            List<Integer> fireItemIds = fireItemDataList.stream().map(FireItemData::getId).collect(Collectors.toList());
            System.out.println("검색된 화재 착화물 디테일 ID 리스트 : " + fireItemIds);
            return fireItemIds;
        } else {
            System.out.println("검색된 화재 착화물 디테일 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 발화장소 대분류 ID 정보
    private List<Integer> getFireLocationMainCategoryIds(String keyword) {
        System.out.println("getFireLocationMainCategoryIds");
        List<FireLocationData> fireLocationDataList = fireLocationDataRepository.findLikeLocationMainCategory(keyword);

        if (!fireLocationDataList.isEmpty()) {
            List<Integer> fireLocationIds = fireLocationDataList.stream().map(FireLocationData::getId)
                    .collect(Collectors.toList());
            System.out.println("검색된 발화장소 대분류 ID 리스트 : " + fireLocationIds);
            return fireLocationIds;
        } else {
            System.out.println("검색된 발화장소 대분류 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 발화장소 분류 ID 정보
    private List<Integer> getFireLocationSubcategoryIds(String keyword) {
        System.out.println("getFireLocationSubcategoryIds");
        List<FireLocationData> fireLocationDataList = fireLocationDataRepository.findLikeLocationSubcategory(keyword);

        if (!fireLocationDataList.isEmpty()) {
            List<Integer> fireLocationIds = fireLocationDataList.stream().map(FireLocationData::getId)
                    .collect(Collectors.toList());
            System.out.println("검색된 발화장소 분류 ID 리스트 : " + fireLocationIds);
            return fireLocationIds;
        } else {
            System.out.println("검색된 발화장소 분류 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 발화장소 세부분류 ID 정보
    private List<Integer> getFireLocationDetailIds(String keyword) {
        System.out.println("getFireLocationDetailIds");
        List<FireLocationData> fireLocationDataList = fireLocationDataRepository.findLikeLocationDetail(keyword);

        if (!fireLocationDataList.isEmpty()) {
            List<Integer> fireLocationIds = fireLocationDataList.stream().map(FireLocationData::getId)
                    .collect(Collectors.toList());
            System.out.println("검색된 발화장소 세부분류 ID 리스트 : " + fireLocationIds);
            return fireLocationIds;
        } else {
            System.out.println("검색된 발화장소 세부분류 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 발화 지역 시도 ID 정보
    private List<Integer> getFireRegionProvinceIds(String keyword) {
        System.out.println("getFireRegionProvinceIds");
        List<FireRegionData> fireRegionDataList = fireRegionDataRepository.findLikeRegionProvince(keyword);

        if (!fireRegionDataList.isEmpty()) {
            List<Integer> fireRegionIds = fireRegionDataList.stream().map(FireRegionData::getId)
                    .collect(Collectors.toList());
            System.out.println("검색된 발화 지역 시도 ID 리스트 : " + fireRegionIds);
            return fireRegionIds;
        } else {
            System.out.println("검색된 발화 지역 시도 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 발화 지역 시군구 ID 정보
    private List<Integer> getFireRegionCityIds(String keyword) {
        System.out.println("getFireRegionCityIds");
        List<FireRegionData> fireRegionDataList = fireRegionDataRepository.findLikeRegionCity(keyword);

        if (!fireRegionDataList.isEmpty()) {
            List<Integer> fireRegionIds = fireRegionDataList.stream().map(FireRegionData::getId)
                    .collect(Collectors.toList());
            System.out.println("검색된 발화 지역 시군구 ID 리스트 : " + fireRegionIds);
            return fireRegionIds;
        } else {
            System.out.println("검색된 발화 지역 시군구 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 발화 유형 ID 정보
    private List<Integer> getFireTypeIds(String keyword) {
        System.out.println("getFireTypeIds");
        List<FireTypeData> fireTypeDataList = fireTypeDataRepository.findLikeType(keyword);

        if (!fireTypeDataList.isEmpty()) {
            List<Integer> fireTypeIds = fireTypeDataList.stream().map(FireTypeData::getId).collect(Collectors.toList());
            System.out.println("검색된 화재 유형 ID 리스트 : " + fireTypeIds);
            return fireTypeIds;
        } else {
            System.out.println("검색된 화재 유형 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

}
