package com.example.demo.PHG_DeepSeek;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

@Service
public class OllamaManager {
    private Process ollamaProcess;
    private static final int OLLAMA_PORT = 11434;

    /**
     * Ollama 서비스가 실행 중인지 확인하고, 실행 중이 아니면 시작합니다.
     */
    public synchronized void startOllamaIfNotRunning() {
        if (isPortInUse()) {
            System.out.println("Ollama is already running.");
            return;
        }

        try {
            String[] command = { "ollama", "run", "deepseek-r1:14b" };
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            ollamaProcess = processBuilder.start();
            new Thread(this.monitorProcess).start();
            System.out.println("Ollama started successfully.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to start Ollama: " + e.getMessage(), e);
        }
    }

    /**
     * Ollama 프로세스의 출력을 모니터링하는 스레드
     */
    private Runnable monitorProcess = () -> {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(ollamaProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Ollama] " + line);
            }
        } catch (Exception e) {
            System.err.println("Error monitoring Ollama process: " + e.getMessage());
        }
    };

    /**
     * Ollama 서비스가 실행 중인지 포트를 통해 확인합니다.
     */
    private boolean isPortInUse() {
        try (java.net.Socket s = new java.net.Socket("localhost", OLLAMA_PORT)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Spring Boot 종료 시 Ollama 서비스를 종료합니다.
     */
    @PreDestroy
    public void shutdownOllama() {
        if (ollamaProcess != null && ollamaProcess.isAlive()) {
            System.out.println("Shutting down Ollama...");
            ollamaProcess.destroy();
            try {
                ollamaProcess.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.err.println("Error waiting for Ollama to shutdown: " + e.getMessage());
            }
        }
    }
}