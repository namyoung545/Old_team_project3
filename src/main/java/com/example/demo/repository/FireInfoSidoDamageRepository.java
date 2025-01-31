package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireInfoSidoDamageEntity;

@Repository
public interface FireInfoSidoDamageRepository extends JpaRepository<FireInfoSidoDamageEntity, Integer> {
    List<FireInfoSidoDamageEntity> findByOcrnYmd(LocalDate ocrnYmd);

    Optional<FireInfoSidoDamageEntity> findBySidoNmAndOcrnYmd(String sidoNm, LocalDate ocrnYmd);
}
