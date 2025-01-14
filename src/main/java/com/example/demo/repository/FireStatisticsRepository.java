package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireStatistics;

@Repository
public interface FireStatisticsRepository extends JpaRepository <FireStatistics, Integer>{
    
}
