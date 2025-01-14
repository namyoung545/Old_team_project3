package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireItemData;

@Repository
public interface FireItemDataRepository extends JpaRepository<FireItemData, Integer> {
    @Query("SELECT i FROM FireItemData i WHERE i.itemCategory = :itemCategory "
            + "AND i.itemCategory = :itemCategory "
            + "AND i.itemDetail = :itemDetail")
    Optional<FireItemData> findByDetails(
            String itemCategory,
            String itemDetail);
}
