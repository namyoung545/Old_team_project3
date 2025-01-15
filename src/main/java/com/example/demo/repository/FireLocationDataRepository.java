package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireLocationData;

@Repository
public interface FireLocationDataRepository extends JpaRepository<FireLocationData, Integer> {

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
