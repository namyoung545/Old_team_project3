// OllamaService.java: AI 응답을 생성하는 서비스 클래스
package com.example.demo.PHG_DeepSeek;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OllamaService {
    // Ollama AI API의 기본 엔드포인트 주소 (로컬 서버)
    private static final String OLLAMA_API_URL = "http://localhost:11434/api/generate";

    // JSON 데이터를 처리하기 위한 Jackson 라이브러리의 객체 매퍼
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // AI 모델에서 응답을 생성하는 주요 메서드
    public String generateResponse(String message, String model, String sessionId) {
        try {
            // AI 요청을 위한 파라미터들을 담은 맵 생성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model); // 사용할 AI 모델
            requestBody.put("prompt", message); // 사용자 입력 메시지
            requestBody.put("stream", false); // 스트리밍 비활성화

            // 요청 데이터를 JSON 문자열로 변환
            String jsonRequest = objectMapper.writeValueAsString(requestBody);

            // HTTP 연결 설정
            URL url = new URL(OLLAMA_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 요청 데이터 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // AI 응답 읽기
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine);
                }
            }

            // JSON 응답에서 텍스트 추출 및 특수 문자 처리
            JsonNode jsonResponse = objectMapper.readTree(response.toString());
            return jsonResponse.get("response").asText()
                    .replace("\\n", "\n") // 개행 문자 복원
                    .replace("\\\"", "\"") // 따옴표 복원
                    .replace("\\t", "\t") // 탭 문자 복원
                    .replace("\\\\", "\\"); // 백슬래시 복원

        } catch (IOException e) {
            // 오류 발생 시 예외 처리
            throw new RuntimeException("Failed to generate response: " + e.getMessage(), e);
        }
    }
}