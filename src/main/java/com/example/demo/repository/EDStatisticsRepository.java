package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.EDStatisticsData;

public interface EDStatisticsRepository extends JpaRepository<EDStatisticsData, Long>{
    @Query("SELECT COUNT(e) > 0 FROM EDStatisticsData e WHERE e.year = :year")
    boolean existsByYear(String year);

    EDStatisticsData findByYear(Integer year);

    void deleteByYear(Integer year);
    
    // @Query (value = "INSERT INTO ed_statistics (...) VALUES (...)", nativeQuery =true)
    // void insertStatisticsFromJson(String json);
}
