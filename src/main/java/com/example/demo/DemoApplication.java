package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.service.SHPythonService;
import com.example.demo.service.SHVirtualEnvService;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner{

	@Autowired
	private SHVirtualEnvService virtualEnvService;
	@Autowired
	private SHPythonService pythonService;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) {
		// Python 가상환경 설정
		virtualEnvService.setupVirtualEnv();
		System.out.println("Python Service");
		Boolean result = pythonService.checkEDStatistics();
		System.out.println(result);
		// String result = pythonService.callEDStatistics("analyze_statistics", "2023");
		// System.out.println(result);
	}

}