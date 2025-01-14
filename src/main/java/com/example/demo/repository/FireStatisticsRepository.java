package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireStatistics;

@Repository
public interface FireStatisticsRepository extends JpaRepository<FireStatistics, Integer> {

    // year와 statName으로 FireStatistics를 찾는 메서드
    Optional<FireStatistics> findByYearAndStatName(String year, String statName);
}
