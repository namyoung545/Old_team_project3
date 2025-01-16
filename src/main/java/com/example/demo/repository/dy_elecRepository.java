package com.example.demo.repository;

import com.example.demo.entity.dy_elecData;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Repository
public interface dy_elecRepository extends JpaRepository<dy_elecData, Long> {
    // 추가 쿼리 메소드 작성 가능

@RestController
@RequestMapping("/api")
public class TestController {

    @Autowired
    private dy_elecRepository repository;

    @GetMapping("/test")
    public List<dy_elecData> getAllData() {
        return repository.findAll();
    }
}
}
