package com.example.demo.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Repository
@Transactional
public class dy_elecRepository { 

    // @PersistenceContext
    // private EntityManager entityManager;

    // public List<Object[]> getFireDataByYearAndRegion() {
    //     String sqlQuery = """
    //       SELECT
    //         YEAR(f.date_time) AS year,
    //         fr.region_province AS region,
    //         COUNT(f.id) AS total_incidents,
    //         SUM(f.damage_property) AS total_damage
    //       FROM
    //         fires f
    //       JOIN
    //         fire_regions fr ON f.fire_region_id = fr.region_id
    //       JOIN
    //         fire_causes fc ON f.fire_cause_id = fc.cause_id
    //       WHERE
    //         f.date_time BETWEEN '2015-01-01' AND '2023-12-31'
    //         AND fc.cause_category = '전기적 요인'
    //       GROUP BY
    //         YEAR(f.date_time), fr.region_province
    //       ORDER BY
    //         year, region;
    //     """;

    //     return entityManager.createNativeQuery(sqlQuery).getResultList();
    // }
}
