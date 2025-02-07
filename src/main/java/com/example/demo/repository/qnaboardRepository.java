package com.example.demo.repository;

import com.example.demo.entity.qnaboardData;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface qnaboardRepository extends JpaRepository<qnaboardData, Long> {
        List<qnaboardData> findAllByOrderByBnumDesc(); // bnum 기준 내림차순 정렬
}
