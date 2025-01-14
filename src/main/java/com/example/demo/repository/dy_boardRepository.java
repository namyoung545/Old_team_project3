package com.example.demo.repository;

import com.example.demo.entity.dy_boardData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface dy_boardRepository extends JpaRepository<dy_boardData, Long>{
    
}