package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireItemData;

@Repository
public interface FireItemDataRepository extends JpaRepository<FireItemData, Integer> {
        // 화재 착화물 카테고리 목록
        @Query("SELECT DISTINCT i.itemCategory FROM FireItemData i")
        List<String> findDistinctItemCategory();

        // 화재 착화물 상세 목록
        @Query("SELECT DISTINCT i.itemDetail FROM FireItemData i")
        List<String> findDistinctItemDetail();

        // 화재 착화물 카테고리 확인
        @Query("SELECT i FROM FireItemData i WHERE i.itemCategory = :itemCategory")
        List<FireItemData> findByItemCategory(String itemCategory);

        // 화재 착화물 디테일 확인
        @Query("SELECT i FROM FireItemData i WHERE i.itemDetail = :itemDetail")
        List<FireItemData> findByItemDetail(String itemDetail);

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
