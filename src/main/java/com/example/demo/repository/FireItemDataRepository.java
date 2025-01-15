package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireItemData;

@Repository
public interface FireItemDataRepository extends JpaRepository<FireItemData, Integer> {

        // 카테고리 검색
        @Query("SELECT i FROM FireItemData i WHERE i.itemCategory LIKE %:itemCategory%")
        List<FireItemData> findLikeItemCategory(String itemCategory);

        // 디테일 검색
        @Query("SELECT i FROM FireItemData i WHERE i.itemDetail LIKE %:itemDetail%")
        List<FireItemData> findLikeItemDetail(String itemDetail);

        // 카테고리 중복 화인
        @Query("SELECT i FROM FireItemData i WHERE i.itemCategory = :itemCategory "
                        + "AND i.itemCategory = :itemCategory "
                        + "AND i.itemDetail = :itemDetail")
        Optional<FireItemData> findByDetails(
                        String itemCategory,
                        String itemDetail);
}
