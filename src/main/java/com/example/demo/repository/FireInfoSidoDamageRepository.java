package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireInfoSidoDamageEntity;

@Repository
public interface FireInfoSidoDamageRepository extends JpaRepository<FireInfoSidoDamageEntity, Integer> {
    List<FireInfoSidoDamageEntity> findByOcrnYmd(LocalDate ocrnYmd);

    @Query("SELECT f FROM FireInfoSidoDamageEntity f WHERE ocrnYmd >= :ocrnYmd")
    List<FireInfoSidoDamageEntity> findByOcrnYmdAfter(LocalDate ocrnYmd);

    Optional<FireInfoSidoDamageEntity> findBySidoNmAndOcrnYmd(String sidoNm, LocalDate ocrnYmd);
}
