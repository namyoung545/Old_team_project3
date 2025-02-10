package com.example.demo.PHG_DeepSeek;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

// [1] Ollama 서비스 생명주기 관리 컴포넌트 - 모델 실행/종료 관리 및 프로세스 모니터링 [참조번호 : 2,5,7]
@Component
public class OllamaManager {
    // [2] 서비스 구성 상수
    // - API 통신 포트 번호
    // - 안전 종료 대기 시간
    // - 기본 사용 모델 이름
    private static final int OLLAMA_PORT = 11434;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;
    private static final String OLLAMA_MODEL = "deepseek-r1:8b";

    // [3] 프로세스 제어 필드
    // - 실행 중인 Ollama 프로세스 인스턴스
    // - 프로세스 빌더 인스턴스
    private Process ollamaProcess;
    private final ProcessBuilder processBuilder;

    // [4] 생성자: 프로세스 실행 명령어 초기화 [참조번호 : 5]
    public OllamaManager() {
        this.processBuilder = new ProcessBuilder("ollama", "run", OLLAMA_MODEL);
        this.processBuilder.redirectErrorStream(true); // 표준/에러 스트림 통합
    }

    // [5] 서비스 실행 관리 메인 메서드 - 중복 실행 방지 로직 포함 [참조번호 : 6]
    public synchronized void startOllamaIfNotRunning() {
        if (isOllamaRunning()) {
            System.out.println("Ollama가 이미 실행 중입니다.");
            return;
        }
        startOllamaProcess();
    }

    // [6] 포트 연결 테스트를 통한 서비스 실행 상태 확인 [참조번호 : 7]
    private boolean isOllamaRunning() {
        try (java.net.Socket socket = new java.net.Socket("localhost", OLLAMA_PORT)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // [7] Ollama 프로세스 실행 로직 - 예외 처리 및 모니터링 시작 [참조번호 : 8,9]
    private void startOllamaProcess() {
        try {
            ollamaProcess = processBuilder.start();
            startProcessMonitor();
            System.out.println("Ollama가 성공적으로 시작되었습니다.");
        } catch (IOException e) {
            throw new RuntimeException("Ollama 시작 실패: " + e.getMessage(), e);
        }
    }

    // [8] 백그라운드 모니터링 스레드 초기화 [참조번호 : 9]
    private void startProcessMonitor() {
        Thread monitorThread = new Thread(this::monitorOllamaProcess);
        monitorThread.setDaemon(true); // 데몬 스레드 설정
        monitorThread.start();
    }

    // [9] 프로세스 출력 스트림 실시간 모니터링 [참조번호 : 7]
    private void monitorOllamaProcess() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(ollamaProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Ollama] " + line); // 로그 포매팅 적용
            }
        } catch (IOException e) {
            System.err.println("Ollama 프로세스 모니터링 오류: " + e.getMessage());
        }
    }

    // [10] 어플리케이션 종료 시 안전한 프로세스 정리 [참조번호 : 2,5]
    @PreDestroy
    public void shutdownOllama() {
        if (ollamaProcess != null && ollamaProcess.isAlive()) {
            System.out.println("Ollama 서비스 종료 중...");
            ollamaProcess.destroy(); // 강제 종료 신호 전송

            try {
                boolean terminated = ollamaProcess.waitFor(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (!terminated) {
                    System.err.println("타임아웃 내 Ollama 종료 실패");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Ollama 종료 과정 중단: " + e.getMessage());
            }
        }
    }
}
