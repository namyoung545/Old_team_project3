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
import java.util.function.Consumer;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OllamaService {
    // Ollama AI API의 기본 엔드포인트 주소 (로컬 서버)
    private static final String OLLAMA_API_URL = "http://localhost:11434/api/generate";

    // JSON 데이터를 처리하기 위한 Jackson 라이브러리의 객체 매퍼
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 스트리밍 응답을 처리하는 메서드
    public void generateResponseStream(String message, String model, String sessionId, Consumer<String> onResponse) {
        try {
            // AI 요청을 위한 파라미터 설정
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("prompt", message);
            requestBody.put("stream", true); // 스트리밍 활성화

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

            // 스트리밍 응답 처리
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    if (!responseLine.isEmpty()) {
                        JsonNode jsonResponse = objectMapper.readTree(responseLine);
                        if (jsonResponse.has("response")) {
                            String response = jsonResponse.get("response").asText()
                                    .replace("\\n", "\n")
                                    .replace("\\\"", "\"")
                                    .replace("\\t", "\t")
                                    .replace("\\\\", "\\");
                            onResponse.accept(response);
                        }

                        // 스트리밍 종료 체크
                        if (jsonResponse.has("done") && jsonResponse.get("done").asBoolean()) {
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate streaming response: " + e.getMessage(), e);
        }
    }

    // 비스트리밍 응답을 위한 메서드 (기존 코드를 StringBuilder로 수정)
    public String generateResponse(String message, String model, String sessionId) {
        StringBuilder fullResponse = new StringBuilder();
        generateResponseStream(message, model, sessionId, response -> fullResponse.append(response));
        return fullResponse.toString();
    }
}