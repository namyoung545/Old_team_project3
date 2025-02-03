package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireRegionData;

@Repository
public interface FireRegionDataRepository extends JpaRepository<FireRegionData, Integer> {
        // 화재 시도 목록
        @Query("SELECT DISTINCT r.regionProvince FROM FireRegionData r")
        List<String> findDistinctRegionProvince();

        // 화재 시군구 목록
        @Query("SELECT DISTINCT r.regionCity FROM FireRegionData r")
        List<String> findDistinctRegionCity();

        // 화재 시도 확인
        @Query("SELECT r FROM FireRegionData r WHERE r.regionProvince = :regionProvince")
        List<FireRegionData> findByRegionProvince(String regionProvince);

        // 화재 시군구 확인
        @Query("SELECT r FROM FireRegionData r WHERE r.regionCity = :regionCity")
        List<FireRegionData> findByRegionCity(String regionCity);

        // 카테고리 검색
        @Query("SELECT r FROM FireRegionData r WHERE r.regionProvince LIKE %:regionProvince%")
        List<FireRegionData> findLikeRegionProvince(String regionProvince);

        // 서브 카테고리 검색
        @Query("SELECT r FROM FireRegionData r WHERE r.regionCity LIKE %:regionCity%")
        List<FireRegionData> findLikeRegionCity(String regionCity);

        // 데이터 중복 확인
        @Query("SELECT r FROM FireRegionData r WHERE r.regionProvince = :regionProvince "
                        + "AND r.regionCity = :regionCity")
        Optional<FireRegionData> findByDetails(
                        String regionProvince,
                        String regionCity);

}
