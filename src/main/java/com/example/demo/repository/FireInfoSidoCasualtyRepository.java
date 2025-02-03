package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.FireInfoSidoCasualtyEntity;

public interface FireInfoSidoCasualtyRepository extends JpaRepository<FireInfoSidoCasualtyEntity, Integer> {
    List<FireInfoSidoCasualtyEntity> findByOcrnYmd(LocalDate ocrnYmd);

    @Query("SELECT f FROM FireInfoSidoCasualtyEntity f WHERE ocrnYmd >= :ocrnYmd")
    List<FireInfoSidoCasualtyEntity> findByOcrnYmdAfter(LocalDate ocrnYmd);
    
    Optional<FireInfoSidoCasualtyEntity> findBySidoNmAndOcrnYmd(String sidoNm, LocalDate ocrnYmd);
}
