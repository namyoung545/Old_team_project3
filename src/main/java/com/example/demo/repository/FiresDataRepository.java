package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FiresData;

@Repository
public interface FiresDataRepository extends JpaRepository<FiresData, Long> {
    // 년도별 데이터 존재 확인
    @Query("SELECT COUNT(f) > 0 FROM FiresData f WHERE FUNCTION('YEAR', f.dateTime) = :year")
    boolean existsByYear(@Param("year") int year);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM fires WHERE YEAR(date_time) = :year)", nativeQuery = true)
    Long existsByYearAsLong(@Param("year") int year);

}
