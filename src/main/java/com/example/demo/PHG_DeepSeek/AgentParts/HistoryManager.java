package com.example.demo.PHG_DeepSeek.AgentParts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.example.demo.PHG_DeepSeek.OllamaService;

// [1] 대화 기록 관리 컴포넌트 - 세션별 대화 내역 저장 및 관련 기록 검색 [참조번호: 2,4,6]
@Component
public class HistoryManager {
    // [2] 상수 및 의존성 주입 필드
    private static final int MAX_HISTORY = 10;
    private static final int RECENT_HISTORY_SIZE = 5;
    private final Map<String, List<ChatMessage>> conversations;
    private final OllamaService ollamaService;

    // [3] 생성자: 의존성 주입 및 초기화 [참조번호: 1]
    public HistoryManager(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
        this.conversations = new ConcurrentHashMap<>();
    }

    // [4] 관련 대화 기록 검색 메서드 - 사용자 입력과 연관된 이전 대화 추출 [참조번호: 5,7,8]
    public String findRelevantHistory(String userInput, String sessionId, String model) {
        List<ChatMessage> history = getHistory(sessionId);
        if (history.isEmpty()) {
            return "";
        }

        StringBuilder relevantHistory = new StringBuilder();
        int start = Math.max(0, history.size() - RECENT_HISTORY_SIZE);
        List<ChatMessage> recentHistory = history.subList(start, history.size());

        for (int i = 0; i < recentHistory.size() - 1; i += 2) {
            ChatMessage question = recentHistory.get(i);
            ChatMessage answer = recentHistory.get(i + 1);
            String prompt = createRelevanceCheckPrompt(userInput, question.content(), answer.content());

            if (isRelevant(prompt, model)) {
                appendHistoryEntry(relevantHistory, question.content(), answer.content());
            }
        }
        return relevantHistory.toString();
    }

    // [5] 관련성 확인 프롬프트 생성 메서드 [참조번호: 4]
    private String createRelevanceCheckPrompt(String currentQuestion, String previousQuestion, String previousAnswer) {
        return String.format("""
                현재 질문: %s
                이전 질문: %s
                이전 답변: %s
                위 대화가 현재 질문과 관련이 있는지 "예" 또는 "아니오"로만 답하세요.
                """, currentQuestion, previousQuestion, previousAnswer);
    }

    // [6] 관련성 확인 메서드 - Ollama 서비스 활용 [참조번호: 4]
    private boolean isRelevant(String prompt, String model) {
        String relevanceCheck = ollamaService.generateResponse(prompt, model, "relevance-check");
        return relevanceCheck.toLowerCase().contains("예");
    }

    // [7] 대화 기록 추가 메서드 [참조번호: 4]
    private void appendHistoryEntry(StringBuilder builder, String question, String answer) {
        builder.append("Q: ").append(question).append("\n")
                .append("A: ").append(answer).append("\n");
    }

    // [8] 대화 기록 업데이트 메서드 - 새로운 메시지 리스트 추가 [참조번호: 4]
    public void updateHistory(List<ChatMessage> messages, String sessionId) {
        List<ChatMessage> history = getOrCreateHistory(sessionId);
        history.addAll(messages);
        conversations.put(sessionId, trimHistory(history));
    }

    // [9] 단일 메시지 추가 메서드 [참조번호: 8]
    public void addMessage(String sessionId, ChatMessage message) {
        List<ChatMessage> history = getOrCreateHistory(sessionId);
        history.add(message);
        conversations.put(sessionId, trimHistory(history));
    }

    // [10] 세션별 대화 기록 조회 또는 생성 메서드 [참조번호: 8,9]
    private List<ChatMessage> getOrCreateHistory(String sessionId) {
        return conversations.computeIfAbsent(sessionId, k -> new ArrayList<>());
    }

    // [11] 대화 기록 정리 메서드 - 최대 기록 수 유지 [참조번호: 8,9]
    private List<ChatMessage> trimHistory(List<ChatMessage> history) {
        if (history.size() > MAX_HISTORY) {
            return new ArrayList<>(
                    history.subList(history.size() - MAX_HISTORY, history.size()));
        }
        return new ArrayList<>(history);
    }

    // [12] 세션별 대화 기록 조회 메서드 [참조번호: 4]
    public List<ChatMessage> getHistory(String sessionId) {
        return new ArrayList<>(conversations.getOrDefault(sessionId, new ArrayList<>()));
    }

    // [13] 세션별 대화 기록 삭제 메서드
    public void clearHistory(String sessionId) {
        conversations.remove(sessionId);
    }

    // [14] 채팅 메시지 레코드 정의 - 역할과 내용 포함
    public record ChatMessage(String role, String content) {
    }
}