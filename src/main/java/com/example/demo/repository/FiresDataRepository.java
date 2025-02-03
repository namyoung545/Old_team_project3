package com.example.demo.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FiresData;

@Repository
public interface FiresDataRepository extends JpaRepository<FiresData, Long> {
        // ------------------------------
        // 연간 화재 통합 통계 기능

        // 연간 통합 통계
        @Query("SELECT YEAR(f.dateTime) AS year, COUNT(f) AS count, SUM(f.damageProperty) AS sumProperty, SUM(f.deaths) AS sumDeaths, SUM(f.injuries) AS sumInjuries, SUM(f.casualtiesTotal) AS sumCasualties"
                        + " FROM FiresData f"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Map<String, Object>> analyzeYearlyFires();

        // 연간 원인별 통합 통계
        @Query("SELECT YEAR(f.dateTime) AS year, COUNT(f) AS count, SUM(f.damageProperty) AS sumProperty, SUM(f.deaths) AS sumDeaths, SUM(f.injuries) AS sumInjuries, SUM(f.casualtiesTotal) AS sumCasualties"
                        + " FROM FiresData f"
                        + " WHERE f.fireCause.id IN :fireCauseIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Map<String, Object>> analyzeYearlyFiresByCauseIds(@Param("fireCauseIds") List<Integer> fireCauseIds);

        // 연간 열원별 통합 통계
        @Query("SELECT YEAR(f.dateTime) AS year, COUNT(f) AS count, SUM(f.damageProperty) AS sumProperty, SUM(f.deaths) AS sumDeaths, SUM(f.injuries) AS sumInjuries, SUM(f.casualtiesTotal) AS sumCasualties"
                        + " FROM FiresData f"
                        + " WHERE f.fireIgnition.id IN :fireIgnitionIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Map<String, Object>> analyzeYearlyFiresByIgnitionIds(
                        @Param("fireIgnitionIds") List<Integer> fireIgnitionIds);

        // 연간 착화물별 통합 통계
        @Query("SELECT YEAR(f.dateTime) AS year, COUNT(f) AS count, SUM(f.damageProperty) AS sumProperty, SUM(f.deaths) AS sumDeaths, SUM(f.injuries) AS sumInjuries, SUM(f.casualtiesTotal) AS sumCasualties"
                        + " FROM FiresData f"
                        + " WHERE f.fireItem.id IN :fireItemIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Map<String, Object>> analyzeYearlyFiresByItemIds(@Param("fireItemIds") List<Integer> fireItemIds);

        // 연간 발화 장소별 통합 통계
        @Query("SELECT YEAR(f.dateTime) AS year, COUNT(f) AS count, SUM(f.damageProperty) AS sumProperty, SUM(f.deaths) AS sumDeaths, SUM(f.injuries) AS sumInjuries, SUM(f.casualtiesTotal) AS sumCasualties"
                        + " FROM FiresData f"
                        + " WHERE f.fireLocation.id IN :fireLocationIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Map<String, Object>> analyzeYearlyFiresByLocationIds(
                        @Param("fireLocationIds") List<Integer> fireLocationIds);

        // 연간 발화 지역별 통합 통계
        @Query("SELECT YEAR(f.dateTime) AS year, COUNT(f) AS count, SUM(f.damageProperty) AS sumProperty, SUM(f.deaths) AS sumDeaths, SUM(f.injuries) AS sumInjuries, SUM(f.casualtiesTotal) AS sumCasualties"
                        + " FROM FiresData f"
                        + " WHERE f.fireRegion.id IN :fireRegionIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Map<String, Object>> analyzeYearlyFiresByRegionIds(@Param("fireRegionIds") List<Integer> fireRegionIds);

        // 연간 화재 유형별 통합 통계
        @Query("SELECT YEAR(f.dateTime) AS year, COUNT(f) AS count, SUM(f.damageProperty) AS sumProperty, SUM(f.deaths) AS sumDeaths, SUM(f.injuries) AS sumInjuries, SUM(f.casualtiesTotal) AS sumCasualties"
                        + " FROM FiresData f"
                        + " WHERE f.fireType.id IN :fireTypeIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Map<String, Object>> analyzeYearlyFiresByTypeIds(@Param("fireTypeIds") List<Integer> fireTypeIds);

        // ------------------------------
        // 화재 발생 건수 통계 기능

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

        // 연간 열원별 화재 발생 건수
        @Query("SELECT YEAR(f.dateTime), COUNT(f)"
                        + " FROM FiresData f"
                        + " WHERE f.fireIgnition.id IN :fireIgnitionIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> countYearlyFiresByIgnitionIds(@Param("fireIgnitionIds") List<Integer> fireIgnitionIds);

        // 연간 착화물별 화재 발생 건수
        @Query("SELECT YEAR(f.dateTime), COUNT(f)"
                        + " FROM FiresData f"
                        + " WHERE f.fireItem.id IN :fireItemIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> countYearlyFiresByItemIds(@Param("fireItemIds") List<Integer> fireItemIds);

        // 연간 발화 장소별 화재 발생 건수
        @Query("SELECT YEAR(f.dateTime), COUNT(f)"
                        + " FROM FiresData f"
                        + " WHERE f.fireLocation.id IN :fireLocationIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> countYearlyFiresByLocationIds(@Param("fireLocationIds") List<Integer> fireLocationIds);

        // 연간 발화 지역별 화재 발생 건수
        @Query("SELECT YEAR(f.dateTime), COUNT(f)"
                        + " FROM FiresData f"
                        + " WHERE f.fireRegion.id IN :fireRegionIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> countYearlyFiresByRegionIds(@Param("fireRegionIds") List<Integer> fireRegionIds);

        // 연간 화재 유형별 화재 발생 건수
        @Query("SELECT YEAR(f.dateTime), COUNT(f)"
                        + " FROM FiresData f"
                        + " WHERE f.fireType.id IN :fireTypeIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> countYearlyFiresByTypeIds(@Param("fireTypeIds") List<Integer> fireTypeIds);

        // ------------------------------
        // 화재 재산피해 통계 기능

        // 연간 재산피해 합계
        @Query("SELECT YEAR(f.dateTime), SUM(f.damageProperty)"
                        + " FROM FiresData f"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> sumYearlyDamageProperty();

        // 연간 원인별 재산피해 합계
        @Query("SELECT YEAR(f.dateTime), SUM(f.damageProperty)"
                        + " FROM FiresData f"
                        + " WHERE f.fireCause.id IN :fireCauseIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> sumYearlyDamagePropertyByCauseIds(@Param("fireCauseIds") List<Integer> fireCauseIds);

        // 연간 열원별 재산피해 합계
        @Query("SELECT YEAR(f.dateTime), SUM(f.damageProperty)"
                        + " FROM FiresData f"
                        + " WHERE f.fireIgnition.id IN :fireIgnitionIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> sumYearlyDamagePropertyByIgnitionIds(@Param("fireIgnitionIds") List<Integer> fireIgnitionIds);

        // 연간 착화물별 재산피해 합계
        @Query("SELECT YEAR(f.dateTime), SUM(f.damageProperty)"
                        + " FROM FiresData f"
                        + " WHERE f.fireItem.id IN :fireItemIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> sumYearlyDamagePropertyByItemIds(@Param("fireItemIds") List<Integer> fireItemIds);

        // 연간 발화 장소별 재산피해 합계
        @Query("SELECT YEAR(f.dateTime), SUM(f.damageProperty)"
                        + " FROM FiresData f"
                        + " WHERE f.fireLocation.id IN :fireLocationIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> sumYearlyDamagePropertyByLocationIds(@Param("fireLocationIds") List<Integer> fireLocationIds);

        // 연간 발화 지역별 재산피해 합계
        @Query("SELECT YEAR(f.dateTime), SUM(f.damageProperty)"
                        + " FROM FiresData f"
                        + " WHERE f.fireRegion.id IN :fireRegionIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> sumYearlyDamagePropertyByRegionIds(@Param("fireRegionIds") List<Integer> fireRegionIds);

        // 연간 화재 유형별 재산피해 합계
        @Query("SELECT YEAR(f.dateTime), SUM(f.damageProperty)"
                        + " FROM FiresData f"
                        + " WHERE f.fireType.id IN :fireTypeIds"
                        + " GROUP BY YEAR(f.dateTime)"
                        + " ORDER BY YEAR(f.dateTime)")
        List<Object[]> sumYearlyDamagePropertyByTypeIds(@Param("fireTypeIds") List<Integer> fireTypeIds);

        // ------------------------------
        // 연간 사망자 부상자 합계

        // 특정 연도의 사망자와 부상자 통계
        @Query("SELECT YEAR(f.dateTime), SUM(f.deaths), SUM(f.injuries), SUM(f.casualtiesTotal)"
                        + " FROM FiresData f"
                        + " WHERE YEAR(f.dateTime) = :year"
                        + " GROUP BY YEAR(f.dateTime)")
        List<Object[]> countYearlyCasualties(Integer year);

        // 모든 연도의 사망자와 부상자 통계
        @Query("SELECT YEAR(f.dateTime), SUM(f.deaths), SUM(f.injuries), SUM(f.casualtiesTotal)"
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

        // 연간 열원별 사망자와 부상자 통계
        @Query("SELECT YEAR(f.dateTime), SUM(f.deaths), SUM(f.injuries), SUM(f.casualtiesTotal) "
                        + "FROM FiresData f "
                        + "WHERE f.fireIgnition.id IN :fireIgnitionIds "
                        + "GROUP BY YEAR(f.dateTime) "
                        + "ORDER BY YEAR(f.dateTime)")
        List<Object[]> countYearlyCasualtiesByIgnitionIds(@Param("fireIgnitionIds") List<Integer> fireIgnitionIds);

        // 연간 착화물별 사망자와 부상자 통계
        @Query("SELECT YEAR(f.dateTime), SUM(f.deaths), SUM(f.injuries), SUM(f.casualtiesTotal) "
                        + "FROM FiresData f "
                        + "WHERE f.fireItem.id IN :fireItemIds "
                        + "GROUP BY YEAR(f.dateTime) "
                        + "ORDER BY YEAR(f.dateTime)")
        List<Object[]> countYearlyCasualtiesByItemIds(@Param("fireItemIds") List<Integer> fireItemIds);

        // 연간 발화 장소별 사망자와 부상자 통계
        @Query("SELECT YEAR(f.dateTime), SUM(f.deaths), SUM(f.injuries), SUM(f.casualtiesTotal) "
                        + "FROM FiresData f "
                        + "WHERE f.fireLocation.id IN :fireLocationIds "
                        + "GROUP BY YEAR(f.dateTime) "
                        + "ORDER BY YEAR(f.dateTime)")
        List<Object[]> countYearlyCasualtiesByLocationIds(@Param("fireLocationIds") List<Integer> fireLocationIds);

        // 연간 발화 지역별 사망자와 부상자 통계
        @Query("SELECT YEAR(f.dateTime), SUM(f.deaths), SUM(f.injuries), SUM(f.casualtiesTotal) "
                        + "FROM FiresData f "
                        + "WHERE f.fireRegion.id IN :fireRegionIds "
                        + "GROUP BY YEAR(f.dateTime) "
                        + "ORDER BY YEAR(f.dateTime)")
        List<Object[]> countYearlyCasualtiesByRegionIds(@Param("fireRegionIds") List<Integer> fireRegionIds);

        // 연간 화재 유형별 사망자와 부상자 통계
        @Query("SELECT YEAR(f.dateTime), SUM(f.deaths), SUM(f.injuries), SUM(f.casualtiesTotal) "
                        + "FROM FiresData f "
                        + "WHERE f.fireType.id IN :fireTypeIds "
                        + "GROUP BY YEAR(f.dateTime) "
                        + "ORDER BY YEAR(f.dateTime)")
        List<Object[]> countYearlyCasualtiesByTypeIds(@Param("fireTypeIds") List<Integer> fireTypeIds);

        // ------------------------------
        // DB 입력 출력 기능

        // 년도별 데이터 존재 확인 (DB 입력용)
        @Query("SELECT COUNT(f) > 0 FROM FiresData f WHERE FUNCTION('YEAR', f.dateTime) = :year")
        Boolean existsByYear(@Param("year") int year);

        @Query(value = "SELECT EXISTS (SELECT 1 FROM fires WHERE YEAR(date_time) = :year)", nativeQuery = true)
        Long existsByYearAsLong(@Param("year") int year);

}
