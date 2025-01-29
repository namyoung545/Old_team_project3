package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@MapperScan("com.example.demo.dao")
public class DemoApplication {

	public static void main(String[] args) {
		// Spring 컨텍스트 초기화
		ApplicationContext context = SpringApplication.run(DemoApplication.class, args);
		
		// SHApplication 빈 가져오기
		DYApplication dyApplication = context.getBean(DYApplication.class);
		dyApplication.dy_start();

		// SHApplication 빈 가져오기
		SHApplication shApplication = context.getBean(SHApplication.class);

		// runSHApplication() 호출
		shApplication.runSHApplication();
	}

}