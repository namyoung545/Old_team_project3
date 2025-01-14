package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireTypeData;

@Repository
public interface FireTypeDataRepository extends JpaRepository<FireTypeData, Integer>{
    @Query("SELECT t FROM FireTypeData t WHERE t.fireType = :fireType")
    Optional<FireTypeData> findByDetails(String fireType);
}
