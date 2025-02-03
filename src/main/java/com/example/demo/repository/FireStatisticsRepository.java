package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireStatistics;

@Repository
public interface FireStatisticsRepository extends JpaRepository<FireStatistics, Integer> {
    // 년도 검색
    List<FireStatistics> findByYear(String year);

    // 통계명 검색
    List<FireStatistics> findByStatName(String statName);

    // 통계명 유사 검색
    @Query("SELECT f FROM FireStatistics f"
            + " WHERE f.statName LIKE %:statName%")
    List<FireStatistics> findByStatNameLike(String statName);

    // 년도 통계명 복합 검색
    // @Query("SELECT f FROM FireStatistics f"
    // + " WHERE f.year = :year AND f.statName LIKE %:statName%")
    @Query("SELECT f FROM FireStatistics f"
            + " WHERE (:year IS NULL OR f.year = :year) AND (:statName IS NULL OR f.statName LIKE %:statName%)")
    List<FireStatistics> findByYearAndStatNameLike(@Param("year") String year, @Param("statName") String statName);

    List<FireStatistics> findByYearAndStatNameContaining(String year, String statName);

    // 통계값 검색 (일치해야함)
    List<FireStatistics> findByStatValue(String statValue);

    // 데이터 중복 확인 (Year와 StatName으로 검색)
    Optional<FireStatistics> findByYearAndStatName(String year, String statName);
}
