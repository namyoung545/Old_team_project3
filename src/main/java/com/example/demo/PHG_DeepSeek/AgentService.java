package com.example.demo.PHG_DeepSeek;

import java.util.function.Consumer;
import org.springframework.stereotype.Component;

import com.example.demo.PHG_DeepSeek.AgentParts.DRLAIntegrator;
import com.example.demo.PHG_DeepSeek.AgentParts.DatabaseManager;
import com.example.demo.PHG_DeepSeek.AgentParts.HistoryManager;
import com.example.demo.PHG_DeepSeek.AgentParts.ProjectLoader;

import jakarta.annotation.PostConstruct;

// [1] 에이전트 서비스 컴포넌트 - 사용자 입력 처리 및 응답 생성 관리 [참조번호: 2,4,6]
@Component
public class AgentService {
    // [2] 의존성 주입 필드 및 컨텍스트 저장 변수
    private final OllamaService ollamaService;
    private final ProjectLoader projectLoader;
    private final DatabaseManager databaseManager;
    private final HistoryManager historyManager;
    private final DRLAIntegrator drlIntegrator;
    private final UserInputProcessor userInputProcessor;

    private String projectContext;
    private String databaseContext;

    // [3] 생성자: 의존성 주입 및 초기화 [참조번호: 1]
    public AgentService(OllamaService ollamaService, ProjectLoader projectLoader,
            DatabaseManager databaseManager, HistoryManager historyManager,
            DRLAIntegrator drlIntegrator) {
        this.ollamaService = ollamaService;
        this.projectLoader = projectLoader;
        this.databaseManager = databaseManager;
        this.historyManager = historyManager;
        this.drlIntegrator = drlIntegrator;
        this.userInputProcessor = new UserInputProcessor();
    }

    // [4] 초기화 메서드 - 프로젝트 및 데이터베이스 컨텍스트 로드 [참조번호: 3]
    @PostConstruct
    public void initialize() {
        projectContext = projectLoader.loadProjectStructure();
        databaseContext = databaseManager.loadDatabaseSchema();
    }

    // [5] 사용자 입력 스트림 처리 메서드 [참조번호: 7]
    public void processUserInputStream(String userInput, String sessionId, String model, Consumer<String> onResponse) {
        userInputProcessor.process(userInput, sessionId, model, onResponse);
    }

    // [6] 사용자 입력 처리 메서드 - 동기식 응답 반환 [참조번호: 5]
    public String processUserInput(String userInput, String sessionId, String model) {
        StringBuilder response = new StringBuilder();
        processUserInputStream(userInput, sessionId, model, response::append);
        return response.toString();
    }

    // [7] 대화 기록 초기화 메서드
    public void clearConversation(String sessionId) {
        historyManager.clearHistory(sessionId);
    }

    // [8] 사용자 입력 처리기 내부 클래스 - 상세 처리 로직 구현 [참조번호: 5,6]
    private class UserInputProcessor {
        // [9] 시스템 프롬프트 및 추론 프롬프트 상수 정의
        private static final String SYSTEM_PROMPT = """
                [시스템 지시사항]
                1. 사용자의 요구사항을 논리적으로 생각 후 이행
                2. 대화 맥락 유지 및 이전 대화 참조
                3. 관련된 경우 데이터베이스 정보 활용
                4. 명확하고 간단한 응답 제공
                5. 중국어나 한자는 영어로 변환

                [현재 컨텍스트]
                프로젝트 정보: %s
                데이터베이스 스키마: %s
                """;

        private static final String REASONING_PROMPT = """
                [사고 과정]
                1단계: 사용자 입력 분석
                %s

                2단계: 컨텍스트 고려사항
                %s

                3단계: 실행 계획
                %s

                최종 응답:
                """;

        // [10] 사용자 입력 처리 메인 메서드 - 단계별 처리 및 응답 생성 [참조번호: 11,12,13,14]
        public void process(String userInput, String sessionId, String model, Consumer<String> onResponse) {
            StringBuilder analysis = new StringBuilder();
            StringBuilder context = new StringBuilder();
            StringBuilder plan = new StringBuilder();

            // [11] 1단계: 사용자 입력 분석
            String analysisPrompt = "분석할 사용자 입력: " + userInput + "\n이 입력의 의도와 필요한 정보를 분석하세요.";
            ollamaService.generateResponseStream(analysisPrompt, model, sessionId, analysis::append);

            // [12] 2단계: 컨텍스트 분석
            String relevantHistory = historyManager.findRelevantHistory(userInput, sessionId, model);
            String contextPrompt = "사용자 입력: " + userInput + "\n" +
                    (relevantHistory.isEmpty() ? "" : "관련된 이전 대화: " + relevantHistory + "\n") +
                    "현재 질문과 관련된 컨텍스트 정보를 분석하세요.";
            ollamaService.generateResponseStream(contextPrompt, model, sessionId, context::append);

            // [13] 3단계: 실행 계획 수립
            String planPrompt = "계획 수립을 위한 정보:\n" +
                    "- 사용자 입력: " + userInput + "\n" +
                    "- 분석 결과: " + analysis.toString() + "\n" +
                    "- 컨텍스트 고려사항: " + context.toString() + "\n" +
                    "어떻게 응답할지 계획을 세우세요.";
            ollamaService.generateResponseStream(planPrompt, model, sessionId, plan::append);

            // [14] 강화학습 기반 계획 정제 및 최종 응답 생성
            String refinedPlan = drlIntegrator.refinePlan(plan.toString(), userInput, sessionId);

            String finalPrompt = String.format(SYSTEM_PROMPT, projectContext, databaseContext)
                    + String.format(REASONING_PROMPT, analysis.toString(), context.toString(), refinedPlan);
            StringBuilder finalResponse = new StringBuilder();
            ollamaService.generateResponseStream(finalPrompt, model, sessionId, response -> {
                onResponse.accept(response);
                finalResponse.append(response);
            });

            // [15] 대화 기록 저장 및 강화학습 업데이트
            String finalRespStr = finalResponse.toString();
            historyManager.addMessage(sessionId, new HistoryManager.ChatMessage("user", userInput));
            historyManager.addMessage(sessionId, new HistoryManager.ChatMessage("assistant", finalRespStr));

            double reward = drlIntegrator.computeReward(userInput, finalRespStr);
            drlIntegrator.updatePolicy(sessionId, reward);
        }
    }
}