package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireCauseData;

@Repository
public interface FireCauseDataRepository extends JpaRepository<FireCauseData, Integer> {
    @Query("SELECT c FROM FireCauseData c WHERE c.causeCategory = :causeCategory "
            + "AND c.causeSubcategory = :causeSubcategory "
            + "AND c.ignitionSourceCategory = :ignitionSourceCategory "
            + "AND c.ignitionSourceSubcategory = :ignitionSourceSubcategory")
    Optional<FireCauseData> findByDetails(
            String causeCategory,
            String causeSubcategory,
            String ignitionSourceCategory,
            String ignitionSourceSubcategory);
}
