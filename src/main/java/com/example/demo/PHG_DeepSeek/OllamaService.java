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
    private static final String OLLAMA_API_URL = "http://localhost:11434/api/generate";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String generateResponse(String message, String model, String sessionId) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("prompt", message);
            requestBody.put("stream", false);

            String jsonRequest = objectMapper.writeValueAsString(requestBody);

            URL url = new URL(OLLAMA_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine);
                }
            }

            JsonNode jsonResponse = objectMapper.readTree(response.toString());
            String responseText = jsonResponse.get("response").asText();

            // Clean up any remaining escape sequences
            responseText = responseText.replace("\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("\\t", "\t")
                    .replace("\\\\", "\\");

            return responseText;

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate response: " + e.getMessage(), e);
        }
    }
}