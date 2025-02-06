package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        // ------------------------------
        // 통합 통계 기능 (완성)
        // analyzeYearlyFires();
        // analyzeYearlyFiresByCauseIds();
        // analyzeYearlyFiresByIgnitionIds();
        // analyzeYearlyFiresByItemIds();
        // analyzeYearlyFiresByLocationIds();
        // analyzeYearlyFiresByRegionIds();
        // analyzeYearlyFiresByTypeIds();


        // ------------------------------
        // 분할된 통계 기능(사용하지 않음)
        // // 연간 화재 발생 건수 분석
        // countYearlyFiresData();

        // ------------------------------
        // 기능 확인용 코드

        // 분류 목록
        // getFireCauseCategories();
        // getFireCauseSubcategories();

        // ------------------------------
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

    // ------------------------------
    // 통합 통계 기능
    private void analyzeYearlyFires() {
        // System.out.println("analyzeYearlyFires");
        List<Map<String, Object>> results = firesDataRepository.analyzeYearlyFires();

        for (Map<String, Object> row : results) {
            // NULL 체크만 수행하고 String으로 변환
            String year = row.get("year") != null ? row.get("year").toString() : "N/A";
            String count = row.get("count") != null ? row.get("count").toString() : "0";
            String sumProperty = row.get("sumProperty") != null ? row.get("sumProperty").toString() : "0";
            String sumDeaths = row.get("sumDeaths") != null ? row.get("sumDeaths").toString() : "0";
            String sumInjuries = row.get("sumInjuries") != null ? row.get("sumInjuries").toString() : "0";
            String sumCasualties = row.get("sumCasualties") != null ? row.get("sumCasualties").toString() : "0";

            // System.out.println("Year: " + year + ", Count: " + count + ", Total Property Damage: " + sumProperty
            //         + ", Total Deaths: " + sumDeaths + ", Total Injuries: " + sumInjuries + ", Total Casualties: "
            //         + sumCasualties);

            saveFireStatisticsDB(year, "화재발생건수", count);
            saveFireStatisticsDB(year, "재산피해합계", sumProperty);
            saveFireStatisticsDB(year, "사망자 수", sumDeaths);
            saveFireStatisticsDB(year, "부상자 수", sumInjuries);
            saveFireStatisticsDB(year, "인명피해 합계", sumCasualties);
        }
    }

    // 연간 화재 원인별 통합 통계
    private void analyzeYearlyFiresByCauseIds() {
        // System.out.println("analyzeYearlyFiresByCauseIds");
        List<String> fireCauses = getFireCauseCategories();

        for (String cause : fireCauses) {
            List<FireCauseData> fireCauseDataList = fireCauseDataRepository.findByCauseCategory(cause);
            List<Integer> fireCauseIds = fireCauseDataList.stream().map(FireCauseData::getId)
                    .collect(Collectors.toList());

            List<Map<String, Object>> results = firesDataRepository.analyzeYearlyFiresByCauseIds(fireCauseIds);
            for (Map<String, Object> row : results) {
                // NULL 체크만 수행하고 String으로 변환
                String year = row.get("year") != null ? row.get("year").toString() : "N/A";
                String count = row.get("count") != null ? row.get("count").toString() : "0";
                String sumProperty = row.get("sumProperty") != null ? row.get("sumProperty").toString() : "0";
                String sumDeaths = row.get("sumDeaths") != null ? row.get("sumDeaths").toString() : "0";
                String sumInjuries = row.get("sumInjuries") != null ? row.get("sumInjuries").toString() : "0";
                String sumCasualties = row.get("sumCasualties") != null ? row.get("sumCasualties").toString() : "0";

                // System.out.println("Cause: " + cause + ", Year: " + year + ", Count: " + count
                //         + ", Total Property Damage: " + sumProperty
                //         + ", Total Deaths: " + sumDeaths + ", Total Injuries: " + sumInjuries + ", Total Casualties: "
                //         + sumCasualties);

                saveFireStatisticsDB(year, "화재발생건수(원인) " + cause, count);
                saveFireStatisticsDB(year, "재산피해합계(원인) " + cause, sumProperty);
                saveFireStatisticsDB(year, "사망자 수(원인) " + cause, sumDeaths);
                saveFireStatisticsDB(year, "부상자 수(원인) " + cause, sumInjuries);
                saveFireStatisticsDB(year, "인명피해 합계(원인) " + cause, sumCasualties);
            }
        }
    }

    // 연간 열원별 통합 통계
    private void analyzeYearlyFiresByIgnitionIds() {
        // System.out.println("analyzeYearlyFiresByIgnitionIds");
        List<String> fireIgnitions = getFireIgnitionCategories();

        for (String ignition : fireIgnitions) {
            List<FireIgnitionData> fireIgnitionDataList = fireIgnitionDataRepository.findByIgnitionCategory(ignition);
            List<Integer> fireIgnitionIds = fireIgnitionDataList.stream().map(FireIgnitionData::getId)
                    .collect(Collectors.toList());

            List<Map<String, Object>> results = firesDataRepository.analyzeYearlyFiresByIgnitionIds(fireIgnitionIds);
            for (Map<String, Object> row : results) {
                // NULL 체크만 수행하고 String으로 변환
                String year = row.get("year") != null ? row.get("year").toString() : "N/A";
                String count = row.get("count") != null ? row.get("count").toString() : "0";
                String sumProperty = row.get("sumProperty") != null ? row.get("sumProperty").toString() : "0";
                String sumDeaths = row.get("sumDeaths") != null ? row.get("sumDeaths").toString() : "0";
                String sumInjuries = row.get("sumInjuries") != null ? row.get("sumInjuries").toString() : "0";
                String sumCasualties = row.get("sumCasualties") != null ? row.get("sumCasualties").toString() : "0";

                // System.out.println("Ignition: " + ignition + ", Year: " + year + ", Count: " + count
                //         + ", Total Property Damage: " + sumProperty
                //         + ", Total Deaths: " + sumDeaths + ", Total Injuries: " + sumInjuries + ", Total Casualties: "
                //         + sumCasualties);

                saveFireStatisticsDB(year, "화재발생건수(열원) " + ignition, count);
                saveFireStatisticsDB(year, "재산피해합계(열원) " + ignition, sumProperty);
                saveFireStatisticsDB(year, "사망자 수(열원) " + ignition, sumDeaths);
                saveFireStatisticsDB(year, "부상자 수(열원) " + ignition, sumInjuries);
                saveFireStatisticsDB(year, "인명피해 합계(열원) " + ignition, sumCasualties);
            }
        }
    }

    // 연간 착화물별 통합 통계
    private void analyzeYearlyFiresByItemIds() {
        // System.out.println("analyzeYearlyFiresByItemIds");
        List<String> fireItems = getFireItemCategories();

        for (String item : fireItems) {
            List<FireItemData> fireItemDataList = fireItemDataRepository.findByItemCategory(item);
            List<Integer> fireItemIds = fireItemDataList.stream().map(FireItemData::getId)
                    .collect(Collectors.toList());

            List<Map<String, Object>> results = firesDataRepository.analyzeYearlyFiresByItemIds(fireItemIds);
            for (Map<String, Object> row : results) {
                // NULL 체크만 수행하고 String으로 변환
                String year = row.get("year") != null ? row.get("year").toString() : "N/A";
                String count = row.get("count") != null ? row.get("count").toString() : "0";
                String sumProperty = row.get("sumProperty") != null ? row.get("sumProperty").toString() : "0";
                String sumDeaths = row.get("sumDeaths") != null ? row.get("sumDeaths").toString() : "0";
                String sumInjuries = row.get("sumInjuries") != null ? row.get("sumInjuries").toString() : "0";
                String sumCasualties = row.get("sumCasualties") != null ? row.get("sumCasualties").toString() : "0";

                // System.out.println("Item: " + item + ", Year: " + year + ", Count: " + count
                //         + ", Total Property Damage: " + sumProperty
                //         + ", Total Deaths: " + sumDeaths + ", Total Injuries: " + sumInjuries + ", Total Casualties: "
                //         + sumCasualties);

                saveFireStatisticsDB(year, "화재발생건수(착화물) " + item, count);
                saveFireStatisticsDB(year, "재산피해합계(착화물) " + item, sumProperty);
                saveFireStatisticsDB(year, "사망자 수(착화물) " + item, sumDeaths);
                saveFireStatisticsDB(year, "부상자 수(착화물) " + item, sumInjuries);
                saveFireStatisticsDB(year, "인명피해 합계(착화물) " + item, sumCasualties);
            }
        }
    }

    // 연간 발화 장소별 통합 통계
    private void analyzeYearlyFiresByLocationIds() {
        // System.out.println("analyzeYearlyFiresByLocationIds");
        List<String> fireLocations = getFireLocationMainCategories();

        for (String location : fireLocations) {
            List<FireLocationData> fireLocationDataList = fireLocationDataRepository.findByLocationMainCategory(location);
            List<Integer> fireLocationIds = fireLocationDataList.stream().map(FireLocationData::getId)
                    .collect(Collectors.toList());

            List<Map<String, Object>> results = firesDataRepository.analyzeYearlyFiresByLocationIds(fireLocationIds);
            for (Map<String, Object> row : results) {
                // NULL 체크만 수행하고 String으로 변환
                String year = row.get("year") != null ? row.get("year").toString() : "N/A";
                String count = row.get("count") != null ? row.get("count").toString() : "0";
                String sumProperty = row.get("sumProperty") != null ? row.get("sumProperty").toString() : "0";
                String sumDeaths = row.get("sumDeaths") != null ? row.get("sumDeaths").toString() : "0";
                String sumInjuries = row.get("sumInjuries") != null ? row.get("sumInjuries").toString() : "0";
                String sumCasualties = row.get("sumCasualties") != null ? row.get("sumCasualties").toString() : "0";

                // System.out.println("Location: " + location + ", Year: " + year + ", Count: " + count
                //         + ", Total Property Damage: " + sumProperty
                //         + ", Total Deaths: " + sumDeaths + ", Total Injuries: " + sumInjuries + ", Total Casualties: "
                //         + sumCasualties);

                saveFireStatisticsDB(year, "화재발생건수(장소) " + location, count);
                saveFireStatisticsDB(year, "재산피해합계(장소) " + location, sumProperty);
                saveFireStatisticsDB(year, "사망자 수(장소) " + location, sumDeaths);
                saveFireStatisticsDB(year, "부상자 수(장소) " + location, sumInjuries);
                saveFireStatisticsDB(year, "인명피해 합계(장소) " + location, sumCasualties);
            }
        }
    }

    // 연간 지역별 통합 통계
    private void analyzeYearlyFiresByRegionIds() {
        // System.out.println("analyzeYearlyFiresByRegionIds");
        List<String> fireRegions = getFireRegionProvinces();

        for (String region : fireRegions) {
            List<FireRegionData> fireRegionDataList = fireRegionDataRepository.findByRegionProvince(region);
            List<Integer> fireRegionIds = fireRegionDataList.stream().map(FireRegionData::getId)
                    .collect(Collectors.toList());

            List<Map<String, Object>> results = firesDataRepository.analyzeYearlyFiresByRegionIds(fireRegionIds);
            for (Map<String, Object> row : results) {
                // NULL 체크만 수행하고 String으로 변환
                String year = row.get("year") != null ? row.get("year").toString() : "N/A";
                String count = row.get("count") != null ? row.get("count").toString() : "0";
                String sumProperty = row.get("sumProperty") != null ? row.get("sumProperty").toString() : "0";
                String sumDeaths = row.get("sumDeaths") != null ? row.get("sumDeaths").toString() : "0";
                String sumInjuries = row.get("sumInjuries") != null ? row.get("sumInjuries").toString() : "0";
                String sumCasualties = row.get("sumCasualties") != null ? row.get("sumCasualties").toString() : "0";

                // System.out.println("Region: " + region + ", Year: " + year + ", Count: " + count
                //         + ", Total Property Damage: " + sumProperty
                //         + ", Total Deaths: " + sumDeaths + ", Total Injuries: " + sumInjuries + ", Total Casualties: "
                //         + sumCasualties);

                saveFireStatisticsDB(year, "화재발생건수(지역) " + region, count);
                saveFireStatisticsDB(year, "재산피해합계(지역) " + region, sumProperty);
                saveFireStatisticsDB(year, "사망자 수(지역) " + region, sumDeaths);
                saveFireStatisticsDB(year, "부상자 수(지역) " + region, sumInjuries);
                saveFireStatisticsDB(year, "인명피해 합계(지역) " + region, sumCasualties);
            }
        }
    }

    // 연간 화재 유형별 통합 통계
    private void analyzeYearlyFiresByTypeIds() {
        // System.out.println("analyzeYearlyFiresByTypeIds");
        List<String> fireTypes = getFireTypes();

        for (String type : fireTypes) {
            List<FireTypeData> fireTypeDataList = fireTypeDataRepository.findByType(type);
            List<Integer> fireTypeIds = fireTypeDataList.stream().map(FireTypeData::getId)
                    .collect(Collectors.toList());

            List<Map<String, Object>> results = firesDataRepository.analyzeYearlyFiresByTypeIds(fireTypeIds);
            for (Map<String, Object> row : results) {
                // NULL 체크만 수행하고 String으로 변환
                String year = row.get("year") != null ? row.get("year").toString() : "N/A";
                String count = row.get("count") != null ? row.get("count").toString() : "0";
                String sumProperty = row.get("sumProperty") != null ? row.get("sumProperty").toString() : "0";
                String sumDeaths = row.get("sumDeaths") != null ? row.get("sumDeaths").toString() : "0";
                String sumInjuries = row.get("sumInjuries") != null ? row.get("sumInjuries").toString() : "0";
                String sumCasualties = row.get("sumCasualties") != null ? row.get("sumCasualties").toString() : "0";

                // System.out.println("Type: " + type + ", Year: " + year + ", Count: " + count
                //         + ", Total Property Damage: " + sumProperty
                //         + ", Total Deaths: " + sumDeaths + ", Total Injuries: " + sumInjuries + ", Total Casualties: "
                //         + sumCasualties);

                saveFireStatisticsDB(year, "화재발생건수(유형) " + type, count);
                saveFireStatisticsDB(year, "재산피해합계(유형) " + type, sumProperty);
                saveFireStatisticsDB(year, "사망자 수(유형) " + type, sumDeaths);
                saveFireStatisticsDB(year, "부상자 수(유형) " + type, sumInjuries);
                saveFireStatisticsDB(year, "인명피해 합계(유형) " + type, sumCasualties);
            }
        }
    }

    // ------------------------------
    // 통계 카테고리 검색 기능

    // 화재 원인 카테고리 목록
    private List<String> getFireCauseCategories() {
        // System.out.println("getFireCauseCetegories");
        List<String> fireCauses = fireCauseDataRepository.findDistinctCauseCategory();
        // System.out.println("fireCausesCategories : " + fireCauses);
        return fireCauses;
    }

    // 화재 원인 서브 카테고리 목록
    private List<String> getFireCauseSubcategories() {
        // System.out.println("getFireCauseSubcategories");
        List<String> fireCauses = fireCauseDataRepository.findDistinctCauseSubcategory();
        // System.out.println("fireCauseSubcategories : " + fireCauses);
        return fireCauses;
    }

    // 화재 열원 카테고리 목록
    private List<String> getFireIgnitionCategories() {
        // System.out.println("getFireIgnitionCategories");
        return fireIgnitionDataRepository.findDistinctIgnitionCategory();
    }

    // 화재 열원 서브 카테고리 목록
    private List<String> getFireIgnitionSubcategories() {
        // System.out.println("getFireIgnitionSubcategories");
        return fireIgnitionDataRepository.findDistinctIgnitionSubcategory();
    }

    // 화재 착화물 카테고리 목록
    private List<String> getFireItemCategories() {
        // System.out.println("getFireItemCategories");
        return fireItemDataRepository.findDistinctItemCategory();
    }

    // 화재 착화물 상세 목록
    private List<String> getFireItemDetails() {
        // System.out.println("getFireItemDetails");
        return fireItemDataRepository.findDistinctItemDetail();
    }

    // 화재 발화 장소 주요 카테고리 목록
    private List<String> getFireLocationMainCategories() {
        // System.out.println("getFireLocationMainCategories");
        return fireLocationDataRepository.findDistinctLocationMainCategory();
    }

    // 화재 발화 장소 서브 카테고리 목록
    private List<String> getFireLocationSubCategories() {
        // System.out.println("GetFireLocationSubCategories");
        return fireLocationDataRepository.findDistinctLocationSubCategory();
    }

    // 화재 발화 장소 세보 목록
    private List<String> getFireLocationDetails() {
        // System.out.println("getFireLocationDetails");
        return fireLocationDataRepository.findDistinctLocationDetail();
    }

    // 화재 시도 목록
    private List<String> getFireRegionProvinces() {
        // System.out.println("getFireRegionProvince");
        return fireRegionDataRepository.findDistinctRegionProvince();
    }

    // 화재 시군구 목록
    private List<String> getFireRegionCities() {
        // System.out.println("getFireRegionCities");
        return fireRegionDataRepository.findDistinctRegionCity();
    }

    // 화재 유형 목록
    private List<String> getFireTypes() {
        // System.out.println("getFireTypes");
        return fireTypeDataRepository.findDistinctFireType();
    }

    // ------------------------------
    // DB 업데이트 기능
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

    // ------------------------------
    // 백업 기능 작동하지만 사용하지 말 것

    // ------------------------------
    // 분할 통계 기능 (사용하지 않음)
    // 연간 화재 발생 건수 분석
    private void countYearlyFiresData() {
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
    private void countYearlyFiresByCause(String keyword) {
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
    private void countYearlyFiresByCauses() {
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

    // 연간 재산피해 분석
    private void sumYearlyDamageProperty() {
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
    private void sumYearlyDamagePropertyByCause(String keyword) {
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
    private void sumYearlyDamagePropertyByCauses() {
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
    private void countYearlyCasualties(Integer year) {
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
    private void countYearlyCasualtiesByCause(String keyword) {
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
    private void countYearlyCasualtiesByCauses() {
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

    // 화재 원인 카테고리 ID 정보
    private List<Integer> getFireCauseCategoryIds(String keyword) {
        // System.out.println("getFireCauseCategoryIds");
        List<FireCauseData> fireCauseDataList = fireCauseDataRepository.findLikeCauseCategory(keyword);

        // 검색된 결과 출력
        if (!fireCauseDataList.isEmpty()) {
            List<Integer> fireCauseIds = fireCauseDataList.stream().map(FireCauseData::getId)
                    .collect(Collectors.toList());
            // System.out.println("검색된 화재 원인 카테고리 ID 리스트 : " + fireCauseIds);
            return fireCauseIds;
        } else {
            // System.out.println("검색된 화재 원인 카테고리 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    private List<Integer> getFireCauseSubcategoryIds(String keyword) {
        // System.out.println("getFireCauseSubcategoryIds");
        List<FireCauseData> fireCauseDataList = fireCauseDataRepository.findLikeCauseSubcategory(keyword);

        if (!fireCauseDataList.isEmpty()) {
            List<Integer> fireCauseIds = fireCauseDataList.stream().map(FireCauseData::getId)
                    .collect(Collectors.toList());
            // System.out.println("검색된 화재 원인 서브 카테고리 ID 리스트 : " + fireCauseIds);
            return fireCauseIds;
        } else {
            // System.out.println("검색된 하재 원인 서브 카테고리 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 열원 카테고리 ID 정보
    private List<Integer> getFireIgnitionCategoryIds(String keyword) {
        // System.out.println("getFireIgnitionCategoryIds");
        List<FireIgnitionData> fireIgnitionDataList = fireIgnitionDataRepository.findLikeIgnitionCategory(keyword);

        if (!fireIgnitionDataList.isEmpty()) {
            List<Integer> fireIgnitionIds = fireIgnitionDataList.stream().map(FireIgnitionData::getId)
                    .collect(Collectors.toList());
            // System.out.println("검색된 화재 열원 카테고리 ID 리스트 : " + fireIgnitionIds);
            return fireIgnitionIds;
        } else {
            // System.out.println("검색된 화재 열원 카테고리 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 열원 서브 카테고리 ID 정보
    private List<Integer> getFireIgnitionSubcategoryIds(String keyword) {
        // System.out.println("getFireIgnitionSubcategoryIds");
        List<FireIgnitionData> fireIgnitionDataList = fireIgnitionDataRepository.findLikeIgnitionSubcategory(keyword);

        if (!fireIgnitionDataList.isEmpty()) {
            List<Integer> fireIgnitionIds = fireIgnitionDataList.stream().map(FireIgnitionData::getId)
                    .collect(Collectors.toList());
            // System.out.println("검색된 화재 열원 서브 카테고리 ID 리스트 : " + fireIgnitionIds);
            return fireIgnitionIds;
        } else {
            // System.out.println("검색된 화재 열원 서브 카테고리 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 발화 착화물 카테고리 ID 정보
    private List<Integer> getFireItemCategoryIds(String keyword) {
        // System.out.println("getFireItemCategoryIds");
        List<FireItemData> fireItemDataList = fireItemDataRepository.findLikeItemCategory(keyword);

        if (!fireItemDataList.isEmpty()) {
            List<Integer> fireItemIds = fireItemDataList.stream().map(FireItemData::getId).collect(Collectors.toList());
            // System.out.println("검색된 화재 착화물 카테고리 ID 리스트 : " + fireItemIds);
            return fireItemIds;
        } else {
            // System.out.println("검색된 화재 착화물 카테고리 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 발화 착화물 디테일 ID 정보
    private List<Integer> getFireItemDetailIds(String keyword) {
        // System.out.println("getFireItemDetailIds");
        List<FireItemData> fireItemDataList = fireItemDataRepository.findLikeItemDetail(keyword);

        if (!fireItemDataList.isEmpty()) {
            List<Integer> fireItemIds = fireItemDataList.stream().map(FireItemData::getId).collect(Collectors.toList());
            // System.out.println("검색된 화재 착화물 디테일 ID 리스트 : " + fireItemIds);
            return fireItemIds;
        } else {
            // System.out.println("검색된 화재 착화물 디테일 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 발화장소 대분류 ID 정보
    private List<Integer> getFireLocationMainCategoryIds(String keyword) {
        // System.out.println("getFireLocationMainCategoryIds");
        List<FireLocationData> fireLocationDataList = fireLocationDataRepository.findLikeLocationMainCategory(keyword);

        if (!fireLocationDataList.isEmpty()) {
            List<Integer> fireLocationIds = fireLocationDataList.stream().map(FireLocationData::getId)
                    .collect(Collectors.toList());
            // System.out.println("검색된 발화장소 대분류 ID 리스트 : " + fireLocationIds);
            return fireLocationIds;
        } else {
            // System.out.println("검색된 발화장소 대분류 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 발화장소 분류 ID 정보
    private List<Integer> getFireLocationSubcategoryIds(String keyword) {
        // System.out.println("getFireLocationSubcategoryIds");
        List<FireLocationData> fireLocationDataList = fireLocationDataRepository.findLikeLocationSubcategory(keyword);

        if (!fireLocationDataList.isEmpty()) {
            List<Integer> fireLocationIds = fireLocationDataList.stream().map(FireLocationData::getId)
                    .collect(Collectors.toList());
            // System.out.println("검색된 발화장소 분류 ID 리스트 : " + fireLocationIds);
            return fireLocationIds;
        } else {
            // System.out.println("검색된 발화장소 분류 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 발화장소 세부분류 ID 정보
    private List<Integer> getFireLocationDetailIds(String keyword) {
        // System.out.println("getFireLocationDetailIds");
        List<FireLocationData> fireLocationDataList = fireLocationDataRepository.findLikeLocationDetail(keyword);

        if (!fireLocationDataList.isEmpty()) {
            List<Integer> fireLocationIds = fireLocationDataList.stream().map(FireLocationData::getId)
                    .collect(Collectors.toList());
            // System.out.println("검색된 발화장소 세부분류 ID 리스트 : " + fireLocationIds);
            return fireLocationIds;
        } else {
            // System.out.println("검색된 발화장소 세부분류 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 발화 지역 시도 ID 정보
    private List<Integer> getFireRegionProvinceIds(String keyword) {
        // System.out.println("getFireRegionProvinceIds");
        List<FireRegionData> fireRegionDataList = fireRegionDataRepository.findLikeRegionProvince(keyword);

        if (!fireRegionDataList.isEmpty()) {
            List<Integer> fireRegionIds = fireRegionDataList.stream().map(FireRegionData::getId)
                    .collect(Collectors.toList());
            // System.out.println("검색된 발화 지역 시도 ID 리스트 : " + fireRegionIds);
            return fireRegionIds;
        } else {
            // System.out.println("검색된 발화 지역 시도 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 발화 지역 시군구 ID 정보
    private List<Integer> getFireRegionCityIds(String keyword) {
        // System.out.println("getFireRegionCityIds");
        List<FireRegionData> fireRegionDataList = fireRegionDataRepository.findLikeRegionCity(keyword);

        if (!fireRegionDataList.isEmpty()) {
            List<Integer> fireRegionIds = fireRegionDataList.stream().map(FireRegionData::getId)
                    .collect(Collectors.toList());
            // System.out.println("검색된 발화 지역 시군구 ID 리스트 : " + fireRegionIds);
            return fireRegionIds;
        } else {
            // System.out.println("검색된 발화 지역 시군구 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

    // 화재 발화 유형 ID 정보
    private List<Integer> getFireTypeIds(String keyword) {
        // System.out.println("getFireTypeIds");
        List<FireTypeData> fireTypeDataList = fireTypeDataRepository.findLikeType(keyword);

        if (!fireTypeDataList.isEmpty()) {
            List<Integer> fireTypeIds = fireTypeDataList.stream().map(FireTypeData::getId).collect(Collectors.toList());
            // System.out.println("검색된 화재 유형 ID 리스트 : " + fireTypeIds);
            return fireTypeIds;
        } else {
            // System.out.println("검색된 화재 유형 ID가 없습니다.");
            return Collections.emptyList();
        }
    }

}
