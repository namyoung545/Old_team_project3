// [1] OllamaService 클래스: Ollama AI API와 통신하여 응답을 생성하는 서비스입니다.
package com.example.demo.PHG_DeepSeek;

//
// [2] 필요한 라이브러리 import
//
import java.io.BufferedReader; // 버퍼를 사용한 문자 입력
import java.io.IOException; // 입출력 예외 처리
import java.io.InputStreamReader; // 바이트 스트림을 문자 스트림으로 변환
import java.io.OutputStream; // 출력 스트림 처리
import java.net.HttpURLConnection; // HTTP 연결 처리
import java.net.URL; // URL 객체 생성
import java.nio.charset.StandardCharsets; // 표준 문자셋 (UTF-8) 사용
import java.util.HashMap; // 키-값 저장을 위한 HashMap 사용
import java.util.Map; // Map 인터페이스 사용
import java.util.function.Consumer; // 함수형 인터페이스 (콜백) 사용

import org.springframework.stereotype.Service; // 스프링 서비스 컴포넌트 어노테이션

import com.fasterxml.jackson.databind.JsonNode; // JSON 트리 구조 처리
import com.fasterxml.jackson.databind.ObjectMapper; // JSON 직렬화/역직렬화를 위한 ObjectMapper

//
// [3] @Service 어노테이션: 스프링 빈으로 등록되어 서비스 계층에서 사용됩니다.
//
@Service
public class OllamaService {

    // [3-1] Ollama AI API의 기본 엔드포인트: 로컬 서버 주소를 지정합니다.
    private static final String OLLAMA_API_URL = "http://localhost:11434/api/generate";

    // [3-2] Jackson ObjectMapper: JSON 데이터를 처리하기 위한 객체 매퍼입니다.
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // [4] 스트리밍 응답 처리 메서드: 메시지를 전송하여 실시간으로 응답을 받아 콜백으로 전달합니다.
    public void generateResponseStream(String message, String model, String sessionId, Consumer<String> onResponse) {
        try {
            // [4-1] 요청 파라미터 구성: 모델, 프롬프트, 스트리밍 활성화 여부 설정
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("prompt", message);
            requestBody.put("stream", true); // 스트리밍 모드 활성화

            // [4-2] 요청 데이터를 JSON 문자열로 직렬화
            String jsonRequest = objectMapper.writeValueAsString(requestBody);

            // [4-3] HTTP 연결 설정: Ollama API 엔드포인트에 POST 요청을 준비합니다.
            URL url = new URL(OLLAMA_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST"); // POST 방식 지정
            conn.setRequestProperty("Content-Type", "application/json"); // JSON 데이터 전송
            conn.setDoOutput(true); // 출력 스트림 사용 활성화

            // [4-4] 요청 데이터 전송: JSON 문자열을 바이트 배열로 변환하여 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // [4-5] 스트리밍 응답 처리: 서버로부터 응답을 한 줄씩 읽어 처리합니다.
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) { // 응답 청크를 한 줄씩 읽음
                    if (!responseLine.isEmpty()) { // 빈 줄이 아니면 처리
                        // [4-5-1] JSON 파싱: 응답 문자열을 JSON 트리로 변환
                        JsonNode jsonResponse = objectMapper.readTree(responseLine);
                        if (jsonResponse.has("response")) { // "response" 필드가 존재하면
                            String response = jsonResponse.get("response").asText()
                                    .replace("\\n", "\n") // 이스케이프 문자 변환
                                    .replace("\\\"", "\"")
                                    .replace("\\t", "\t")
                                    .replace("\\\\", "\\");
                            onResponse.accept(response); // 콜백으로 응답 전달
                        }
                        // [4-5-2] 스트리밍 종료 체크: "done" 필드가 true이면 반복 종료
                        if (jsonResponse.has("done") && jsonResponse.get("done").asBoolean()) {
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) { // 입출력 예외 발생 시
            throw new RuntimeException("Failed to generate streaming response: " + e.getMessage(), e);
        }
    }

    // [5] 비스트리밍 응답 메서드: 스트리밍 메서드를 사용해 전체 응답을 하나의 문자열로 반환합니다.
    public String generateResponse(String message, String model, String sessionId) {
        StringBuilder fullResponse = new StringBuilder(); // 응답 누적용 StringBuilder 생성
        generateResponseStream(message, model, sessionId, response -> fullResponse.append(response));
        return fullResponse.toString(); // 누적된 응답 문자열 반환
    }
}