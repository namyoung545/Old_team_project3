package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireTypeData;

@Repository
public interface FireTypeDataRepository extends JpaRepository<FireTypeData, Integer> {

    // 타입 검색
    @Query("SELECT t FROM FireTypeData t WHERE t.fireType LIKE %:fireType%")
    List<FireTypeData> findLikeType(String fireType);

    // 데이터 중복 확인
    @Query("SELECT t FROM FireTypeData t WHERE t.fireType = :fireType")
    Optional<FireTypeData> findByDetails(String fireType);
}
