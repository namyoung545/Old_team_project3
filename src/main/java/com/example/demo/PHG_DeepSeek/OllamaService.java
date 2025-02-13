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

// [1] Ollama API 통신 핵심 서비스 - 텍스트 생성 요청 처리 및 스트리밍 응답 관리 [참조번호 : 2,5,7]
@Service
public class OllamaService {

    // [2] API 설정 상수
    // - Ollama 서버 엔드포인트 URL
    // - JSON 파싱을 위한 ObjectMapper 인스턴스
    private static final String OLLAMA_API_URL = "http://localhost:11434/api/generate";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // [3] 스트리밍 응답 생성 메인 로직 - 실시간 텍스트 청크를 콜백으로 전달
    public void generateResponseStream(String message, String model, String sessionId, Consumer<String> onResponse) {
        try {
            // [4] 요청 바디 구성
            // - 사용할 모델 이름
            // - 사용자 프롬프트 메시지
            // - 스트리밍 활성화 플래그
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("prompt", message);
            requestBody.put("stream", true);

            // [5] HTTP 연결 초기화
            // - POST 메서드 설정
            // - JSON 컨텐츠 타입 헤더 추가
            String jsonRequest = objectMapper.writeValueAsString(requestBody);
            URL url = new URL(OLLAMA_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // [6] 요청 데이터 전송
            // - UTF-8 인코딩으로 바이트 변환
            // - 출력 스트림을 통해 서버에 전달
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // [7] 스트리밍 응답 처리
            // - 라인 단위 응답 읽기
            // - JSON 파싱 후 response 필드 추출
            // - 특수 문자 이스케이프 처리
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
                        if (jsonResponse.has("done") && jsonResponse.get("done").asBoolean()) {
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("스트리밍 응답 생성 실패: " + e.getMessage(), e);
        }
    }

    // [8] 동기식 응답 생성 메서드 - 전체 응답을 문자열로 수집 후 반환 [참조번호 : 3]
    public String generateResponse(String message, String model, String sessionId) {
        StringBuilder fullResponse = new StringBuilder();
        generateResponseStream(message, model, sessionId, response -> fullResponse.append(response));
        return fullResponse.toString();
    }
}
