package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireIgnitionData;

@Repository
public interface FireIgnitionDataRepository extends JpaRepository<FireIgnitionData, Integer> {

        // 카테고리 검색
        @Query ("SELECT i FROM FireIgnitionData i WHERE i.ignitionSourceCategory LIKE %:ignitionSourceCategory%")
        List<FireIgnitionData> findLikeIgnitionCategory(String ignitionSourceCategory);

        // 서브 카테고리 검색
        @Query ("SELECT i FROM FireIgnitionData i WHERE i.ignitionSourceSubcategory LIKE %:ignitionSourceSubcategory%")
        List<FireIgnitionData> findLikeIgnitionSubcategory(String ignitionSourceSubcategory);

        // 카테고리 중복 확인
        @Query("SELECT i FROM FireIgnitionData i WHERE i.ignitionSourceCategory = :ignitionSourceCategory "
                        + "AND i.ignitionSourceSubcategory = :ignitionSourceSubcategory")
        Optional<FireIgnitionData> findByDetails(
                        String ignitionSourceCategory,
                        String ignitionSourceSubcategory);

}
