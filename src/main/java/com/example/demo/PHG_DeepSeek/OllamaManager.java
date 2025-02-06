// OllamaManager.java
// 이 파일은 Ollama라는 AI 모델을 관리하는 클래스를 정의합니다.
// Ollama를 시작하고, 모니터링하며, 종료하는 기능을 제공합니다.
package com.example.demo.PHG_DeepSeek;

import org.springframework.stereotype.Service;
import jakarta.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Service
public class OllamaManager {
    // Ollama가 사용할 포트 번호
    private static final int OLLAMA_PORT = 11434;
    // Ollama 종료 시 최대 대기 시간(초)
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;
    // 사용할 Ollama AI 모델의 이름
    private static final String OLLAMA_MODEL = "deepseek-r1:8b";
    // Ollama 프로세스를 저장할 변수
    private Process ollamaProcess;
    // Ollama 실행을 위한 명령어 설정
    private final ProcessBuilder processBuilder;

    // 생성자: Ollama 실행 명령어를 설정합니다.
    public OllamaManager() {
        this.processBuilder = new ProcessBuilder("ollama", "run", OLLAMA_MODEL);
        // 에러 출력을 표준 출력으로 리다이렉트
        this.processBuilder.redirectErrorStream(true);
    }

    // Ollama가 실행 중이 아니면 시작하는 메소드
    public synchronized void startOllamaIfNotRunning() {
        if (isOllamaRunning()) {
            System.out.println("Ollama가 이미 실행 중입니다.");
            return;
        }
        startOllamaProcess();
    }

    // Ollama가 실행 중인지 확인하는 메소드
    private boolean isOllamaRunning() {
        try (java.net.Socket socket = new java.net.Socket("localhost", OLLAMA_PORT)) {
            return true; // 연결 성공 시 실행 중
        } catch (IOException e) {
            return false; // 연결 실패 시 실행 중이 아님
        }
    }

    // Ollama 프로세스를 시작하는 메소드
    private void startOllamaProcess() {
        try {
            ollamaProcess = processBuilder.start();
            startProcessMonitor();
            System.out.println("Ollama가 성공적으로 시작되었습니다.");
        } catch (IOException e) {
            throw new RuntimeException("Ollama 시작 실패: " + e.getMessage(), e);
        }
    }

    // Ollama 프로세스 모니터링을 시작하는 메소드
    private void startProcessMonitor() {
        Thread monitorThread = new Thread(this::monitorOllamaProcess);
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    // Ollama 프로세스의 출력을 모니터링하는 메소드
    private void monitorOllamaProcess() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(ollamaProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Ollama] " + line);
            }
        } catch (IOException e) {
            System.err.println("Error monitoring Ollama process: " + e.getMessage());
        }
    }

    @PreDestroy
    // 애플리케이션 종료 시 Ollama를 안전하게 종료하는 메소드
    public void shutdownOllama() {
        if (ollamaProcess != null && ollamaProcess.isAlive()) {
            System.out.println("Shutting down Ollama...");
            ollamaProcess.destroy();

            try {
                // 지정된 시간 동안 프로세스 종료 대기
                boolean terminated = ollamaProcess.waitFor(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (!terminated) {
                    System.err.println("Ollama did not terminate within timeout period.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Interrupted while shutting down Ollama: " + e.getMessage());
            }
        }
    }
}