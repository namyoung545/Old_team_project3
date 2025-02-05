package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.SHDisasterMessageEntity;
import java.util.List;
import java.time.LocalDateTime;


public interface SHDisasterMessageRepository extends JpaRepository<SHDisasterMessageEntity, Integer> {
    @Query("SELECT d FROM SHDisasterMessageEntity d WHERE d.sn = :sn")
    Optional<SHDisasterMessageEntity> findBySN(Integer sn);

    @Query ("SELECT d FROM SHDisasterMessageEntity d WHERE d.crt_dt > :date")
    List<SHDisasterMessageEntity> findCrtDtAfter(LocalDateTime date);
}
