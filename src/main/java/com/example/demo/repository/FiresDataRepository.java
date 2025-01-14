package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FiresData;

@Repository
public interface FiresDataRepository extends JpaRepository<FiresData, Long> {
    // 연간 사망자 통계

    // 모든 연도의 사망자와 부상자 통계
    @Query("SELECT YEAR(f.dateTime) AS year, SUM(f.deaths) AS totalDeaths, SUM(f.injuries) AS totalInjuries " +
           "FROM FiresData f " +
           "GROUP BY YEAR(f.dateTime) " +
           "ORDER BY YEAR(f.dateTime)")
    List<Object[]> findYearlyCasualtyStatistics();

    // 특정 연도의 사망자와 부상자 통계
    @Query("SELECT YEAR(f.dateTime) AS year, SUM(f.deaths) AS totalDeaths, SUM(f.injuries) AS totalInjuries " +
           "FROM FiresData f " +
           "WHERE YEAR(f.dateTime) = :year " +
           "GROUP BY YEAR(f.dateTime)")
    List<Object[]> findYearlyCasualtyStatistics(Integer year);
    // @Query("SELECT f.year, COUNT(f), SUM(f.casualties_deaths), SUM(f.casualties_injuries) FROM FiresData f WHERE (:year IS NULL OR f.year = :year) "
    //         + "GROUP BY f.year")
    // List<Object[]> findStatisticsByYear(Integer year);

    // 년도별 데이터 존재 확인
    @Query("SELECT COUNT(f) > 0 FROM FiresData f WHERE FUNCTION('YEAR', f.dateTime) = :year")
    boolean existsByYear(@Param("year") int year);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM fires WHERE YEAR(date_time) = :year)", nativeQuery = true)
    Long existsByYearAsLong(@Param("year") int year);

}
