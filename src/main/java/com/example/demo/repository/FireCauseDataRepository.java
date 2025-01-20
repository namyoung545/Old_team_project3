package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireCauseData;

@Repository
public interface FireCauseDataRepository extends JpaRepository<FireCauseData, Integer> {
        // 화재 원인 카테고리 목록
        @Query("SELECT DISTINCT c.causeCategory FROM FireCauseData c")
        List<String> findDistinctCauseCategory();

        // 화재 원인 서브 카테고리 목록
        @Query("SELECT DISTINCT c.causeSubcategory FROM FireCauseData c")
        List<String> findDistinctCauseSubcategory();

        // 화재 원인 카테고리 확인
        @Query("SELECT c FROM FireCauseData c WHERE c.causeCategory = :causeCategory")
        List<FireCauseData> findByCauseCategory(String causeCategory);

        // 화재 원인 서브 카테고리 확인
        @Query("SELECT c FROM FireCauseData c WHERE c.causeSubcategory = :causeSubcategory")
        List<FireCauseData> findByCauseSubcategory(String causeSubcategory);

        // 화재 원인 카테고리 검색
        @Query("SELECT c FROM FireCauseData c WHERE c.causeCategory LIKE %:causeCategory%")
        List<FireCauseData> findLikeCauseCategory(String causeCategory);

        // 화재 원인 서브 카테고리 검색
        @Query("SELECT c FROM FireCauseData c WHERE c.causeSubcategory LIKE %:causeSubcategory%")
        List<FireCauseData> findLikeCauseSubcategory(String causeSubcategory);

        // 화재 원인 중복 화인
        @Query("SELECT c FROM FireCauseData c WHERE c.causeCategory = :causeCategory "
                        + "AND c.causeSubcategory = :causeSubcategory")
        Optional<FireCauseData> findByDetails(
                        String causeCategory,
                        String causeSubcategory);
}
