package com.example.demo.repository;

import com.example.demo.entity.boardData;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface boardRepository extends JpaRepository<boardData, Long> {
    List<boardData> findAllByOrderByBnumDesc(); // bnum 기준 내림차순 정렬
}
