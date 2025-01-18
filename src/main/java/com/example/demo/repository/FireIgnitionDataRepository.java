package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireIgnitionData;

@Repository
public interface FireIgnitionDataRepository extends JpaRepository<FireIgnitionData, Integer> {
    @Query("SELECT i FROM FireIgnitionData i WHERE i.ignitionSourceCategory = :ignitionSourceCategory "
            + "AND i.ignitionSourceSubcategory = :ignitionSourceSubcategory")
    Optional<FireIgnitionData> findByDetails(
            String ignitionSourceCategory,
            String ignitionSourceSubcategory);

}
