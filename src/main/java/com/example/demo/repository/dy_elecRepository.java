package com.example.demo.repository;

import com.example.demo.entity.dy_elecData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface dy_elecRepository extends JpaRepository<dy_elecData, Long> {
    List<dy_elecData> findByYear(Integer year);
    List<dy_elecData> findByRegion(String region);
    List<dy_elecData> findByYearAndRegion(Integer year, String region);
}
