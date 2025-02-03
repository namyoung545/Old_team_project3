package com.example.demo.PHG_DeepSeek;

public class ChatRequestDTO {
    private String message;
    private String model;
    private String sessionId; // 세션별 대화 구분을 위한 필드

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}