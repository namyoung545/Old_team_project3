package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireLocationData;

@Repository
public interface FireLocationDataRepository extends JpaRepository<FireLocationData, Integer> {
    @Query("SELECT l FROM FireLocationData l WHERE l.locationMainCategory = :locationMainCategory "
            + "AND l.locationSubCategory = :locationSubCategory "
            + "AND l.locationDetail = :locationDetail")
    Optional<FireLocationData> findByDetails(
            String locationMainCategory,
            String locationSubCategory,
            String locationDetail);
}
