package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FiresData;

@Repository
public interface FiresDataRepository extends JpaRepository<FiresData, Long> {

        // 연간 화재 발생 건수
        @Query("SELECT YEAR(f.dateTime), COUNT(f)"
                        + " FROM FiresData f"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> countYearlyFires();

        // 연간 원인별 화재 발생 건수
        @Query("SELECT YEAR(f.dateTime), COUNT(f)"
                        + " FROM FiresData f"
                        + " WHERE f.fireCause.id IN :fireCauseIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> countYearlyFiresByCauseIds(@Param("fireCauseIds") List<Integer> fireCauseIds);

        // 연간 재산피해 합계
        @Query("SELECT YEAR(f.dateTime), SUM(f.damageProperty)"
                        + " FROM FiresData f"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> sumYearlyDamageProperty();

        // 연간 원인별 재산피해 합계
        @Query ("SELECT YEAR(f.dateTime), SUM(f.damageProperty)"
                + " FROM FiresData f"
                + " WHERE f.fireCause.id IN :fireCauseIds"
                + " GROUP BY YEAR(f.dateTime)"
                + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> sumYearlyDamagePropertyByCauseIds(@Param("fireCauseIds") List<Integer> fireCauseIds);

        // 연간 사망자 부상자 합계
        // 특정 연도의 사망자와 부상자 통계
        @Query("SELECT YEAR(f.dateTime) AS year, SUM(f.deaths) AS totalDeaths, SUM(f.injuries) AS totalInjuries, SUM(f.casualtiesTotal) AS totalCasualties"
                        + " FROM FiresData f"
                        + " WHERE YEAR(f.dateTime) = :year"
                        + " GROUP BY YEAR(f.dateTime)")
        List<Object[]> countYearlyCasualties(Integer year);

        // 모든 연도의 사망자와 부상자 통계
        @Query("SELECT YEAR(f.dateTime) AS year, SUM(f.deaths) AS totalDeaths, SUM(f.injuries) AS totalInjuries, SUM(f.casualtiesTotal) AS totalCasualties"
                        + " FROM FiresData f"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> countYearlyCasualties();

        // 연간 원인별 사망자 부상자 합계
        @Query("SELECT YEAR(f.dateTime), SUM(f.deaths), SUM(f.injuries), SUM(f.casualtiesTotal)"
                        + " FROM FiresData f"
                        + " WHERE f.fireCause.id IN :fireCauseIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> countYearlyCasualtiesByCausesIds(@Param("fireCauseIds") List<Integer> fireCauseIds);

        // 년도별 데이터 존재 확인 (DB 입력용)
        @Query("SELECT COUNT(f) > 0 FROM FiresData f WHERE FUNCTION('YEAR', f.dateTime) = :year")
        Boolean existsByYear(@Param("year") int year);

        @Query(value = "SELECT EXISTS (SELECT 1 FROM fires WHERE YEAR(date_time) = :year)", nativeQuery = true)
        Long existsByYearAsLong(@Param("year") int year);

}
