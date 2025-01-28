package com.example.demo.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireInfoSidoEntity;

@Repository
public interface FireInfoSidoRepository extends JpaRepository<FireInfoSidoEntity, Integer> {
    // 날짜별 검색
    List<FireInfoSidoEntity> findByOcrn_ymd(LocalDate ocrn_ymd);

    // 시도 날짜 검색
    @Query("SELECT f FROM FireInfoSidoEntity f WHERE f.sido_nm = :sido_nm AND f.ocrn_ymd = :ocrn_ymd")
    Optional<FireInfoSidoEntity> findBySidoNmAndOcrnYmd(String sido_nm, LocalDate ocrn_ymd);
}
