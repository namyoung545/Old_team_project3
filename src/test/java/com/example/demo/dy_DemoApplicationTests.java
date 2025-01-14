package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootTest
@MapperScan("com.example.demo.mapper") // 매퍼 패키지 경로 지정
	public class dy_DemoApplicationTests {
 public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}