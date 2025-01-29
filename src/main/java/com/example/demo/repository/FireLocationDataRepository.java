package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireLocationData;

@Repository
public interface FireLocationDataRepository extends JpaRepository<FireLocationData, Integer> {

        // 화재 발화 장소별 주요 카테고리 목록
        @Query("SELECT DISTINCT l.locationMainCategory FROM FireLocationData l")
        List<String> findDistinctLocationMainCategory();

        // 화재 발화 장소별 서브 카테고리 목록
        @Query("SELECT DISTINCT l.locationSubCategory FROM FireLocationData l")
        List<String> findDistinctLocationSubCategory();

        // 화재 발화 장소별 세부명 목록
        @Query("SELECT DISTINCT l.locationDetail FROM FireLocationData l")
        List<String> findDistinctLocationDetail();

        // 화재 발화 장소별 카테고리 확인
        @Query("SELECT l FROM FireLocationData l WHERE l.locationMainCategory = :locationMainCategory")
        List<FireLocationData> findByLocationMainCategory(String locationMainCategory);

        // 화재 발화 장소별 서브 카테고리 확인
        @Query("SELECT l FROM FireLocationData l WHERE l.locationSubCategory = :locationSubCategory")
        List<FireLocationData> findByLocationSubcategory(String locationSubCategory);

        // 화재 발화 장소별 디테일 확인
        @Query("SELECT l FROM FireLocationData l WHERE l.locationDetail = :locationDetail")
        List<FireLocationData> findByLocationDetail(String locationDetail);

        // 카테고리 검색
        @Query("SELECT l FROM FireLocationData l WHERE l.locationMainCategory LIKE %:locationMainCategory%")
        List<FireLocationData> findLikeLocationMainCategory(String locationMainCategory);

        // 서브 카테고리 검색
        @Query("SELECT l FROM FireLocationData l WHERE l.locationSubCategory LIKE %:locationSubCategory%")
        List<FireLocationData> findLikeLocationSubcategory(String locationSubCategory);

        // 디테일 검색
        @Query("SELECT l FROM FireLocationData l WHERE l.locationDetail LIKE %:locationDetail%")
        List<FireLocationData> findLikeLocationDetail(String locationDetail);

        // 데이터 중복확인
        @Query("SELECT l FROM FireLocationData l WHERE l.locationMainCategory = :locationMainCategory "
                        + "AND l.locationSubCategory = :locationSubCategory "
                        + "AND l.locationDetail = :locationDetail")
        Optional<FireLocationData> findByDetails(
                        String locationMainCategory,
                        String locationSubCategory,
                        String locationDetail);
}
