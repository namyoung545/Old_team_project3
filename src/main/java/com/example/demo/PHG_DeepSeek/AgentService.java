package com.example.demo.PHG_DeepSeek;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.demo.PHG_DeepSeek.AgentParts.DatabaseManager;
import com.example.demo.PHG_DeepSeek.AgentParts.HistoryManager;
import com.example.demo.PHG_DeepSeek.AgentParts.ProjectLoader;

import jakarta.annotation.PostConstruct;

@Component
public class AgentService {
    private final OllamaService ollamaService;
    private final ProjectLoader projectLoader;
    private final DatabaseManager databaseManager;
    private final HistoryManager historyManager;
    private final UserInputProcessor userInputProcessor;

    String projectContext;
    String databaseContext;

    public AgentService(OllamaService ollamaService, ProjectLoader projectLoader,
            DatabaseManager databaseManager, HistoryManager historyManager) {
        this.ollamaService = ollamaService;
        this.projectLoader = projectLoader;
        this.databaseManager = databaseManager;
        this.historyManager = historyManager;
        this.userInputProcessor = new UserInputProcessor();
    }

    @PostConstruct
    public void initialize() {
        projectContext = projectLoader.loadProjectStructure();
        databaseContext = databaseManager.loadDatabaseSchema();
    }

    public void processUserInputStream(String userInput, String sessionId, String model, Consumer<String> onResponse) {
        userInputProcessor.process(userInput, sessionId, model, onResponse);
    }

    public String processUserInput(String userInput, String sessionId, String model) {
        StringBuilder response = new StringBuilder();
        processUserInputStream(userInput, sessionId, model, response::append);
        return response.toString();
    }

    public void clearConversation(String sessionId) {
        historyManager.clearHistory(sessionId);
    }

    private class UserInputProcessor {
        private static final String SYSTEM_PROMPT = """
                [시스템 역할]
                당신은 소프트웨어 개발 프로젝트의 지원 AI 에이전트입니다.

                [컨텍스트 정보]
                프로젝트 정보: %s
                데이터베이스 정보: %s

                [응답 규칙]
                1. 응답 구조:
                   - '질문 이해 💡': 질문 의도 파악
                   - '생각 과정 🤔': 복잡한 질문에 대한 단계별 사고 과정
                   - '자아성찰 🔍': 내 생각이 논리적이고 적절한지 검토
                   - '답변 📝': 질문 유형에 맞는 적절한 답변 제공
                   - '추가 설명 ℹ️': 필요한 경우에만 제공

                2. 답변 원칙:
                   - DB 관련 질문: 실제 데이터를 기반으로 정확한 답변 제공
                   - 일반 질문: 컨텍스트와 관계없이 유연하게 답변
                   - 복잡한 질문: 단계별 사고와 자아성찰을 거쳐 신중하게 답변
                   - 모호한 질문: 구체적인 의도 파악을 위한 확인 질문

                [이전 대화]
                %s

                [현재 질문]
                %s
                """;

        public void process(String userInput, String sessionId, String model, Consumer<String> onResponse) {
            try {
                QuestionAnalysis analysis = analyzeInput(userInput);
                String context = buildContext(userInput, analysis);
                String prompt = String.format(SYSTEM_PROMPT, context,
                        databaseContext + getQueryResults(userInput, analysis),
                        historyManager.findRelevantHistory(userInput, sessionId, model),
                        userInput);

                generateResponse(prompt, model, sessionId, analysis.isComplex, onResponse);
                updateHistory(sessionId, userInput);
            } catch (Exception e) {
                onResponse.accept("오류가 발생했습니다: " + e.getMessage());
            }
        }

        private QuestionAnalysis analyzeInput(String input) {
            return new QuestionAnalysis(
                    List.of("데이터", "조회", "검색", "기록", "통계", "현황", "조사").stream()
                            .anyMatch(k -> input.toLowerCase().contains(k.toLowerCase())),
                    input.split("\\s+").length > 15 || input.split("[?？]").length > 2 ||
                            input.matches(".*(만약|경우|조건|다만).*"));
        }

        private String buildContext(String input, QuestionAnalysis analysis) {
            List<String> files = projectLoader.findSimilarFiles(input);
            return files.stream()
                    .limit(3)
                    .map(f -> "파일: " + f + "\n" + projectLoader.getFileContent(f))
                    .collect(Collectors.joining("\n\n"));
        }

        private String getQueryResults(String input, QuestionAnalysis analysis) {
            List<String> tables = databaseManager.findRelatedTables(input);
            if (tables.isEmpty())
                return "\n\n[데이터베이스 조회 결과]\n관련 데이터 없음";

            return tables.stream()
                    .limit(2)
                    .map(table -> getTableData(table, analysis.isComplex))
                    .collect(Collectors.joining("\n"));
        }

        private String getTableData(String table, boolean isComplex) {
            long count = databaseManager.getTableCount(table);
            StringBuilder result = new StringBuilder(String.format("테이블 '%s' 통계:\n- 총 레코드 수: %d\n", table, count));

            if (isComplex) {
                addDetailedAnalysis(result, table);
            } else {
                addBasicSample(result, table);
            }
            return result.toString();
        }

        private void addDetailedAnalysis(StringBuilder analysis, String table) {
            Map<String, List<String>> tableColumns = databaseManager.getTableColumns();
            String primaryColumn = tableColumns.get(table).get(0);

            DatabaseManager.QueryResult trendResult = databaseManager.executeSelectQuery(
                    String.format("SELECT %s, COUNT(*) as count FROM %s GROUP BY %s ORDER BY count DESC LIMIT 5",
                            primaryColumn, table, primaryColumn));

            if (trendResult.isSuccess() && trendResult.getData() != null) {
                analysis.append("주요 데이터 패턴:\n");
                trendResult.getData().forEach(row -> {
                    analysis.append(String.format("- %s: %s건\n",
                            row.get(primaryColumn), row.get("count")));
                });
            }
        }

        private void addBasicSample(StringBuilder sample, String table) {
            Map<String, List<String>> tableColumns = databaseManager.getTableColumns();
            String orderByColumn = tableColumns.get(table).get(0);

            DatabaseManager.QueryResult queryResult = databaseManager.executeSelectQuery(
                    String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 3",
                            table, orderByColumn));

            if (queryResult.isSuccess() && queryResult.getData() != null) {
                sample.append("최근 데이터 샘플:\n");
                queryResult.getData().forEach(row -> {
                    sample.append("- ");
                    row.forEach((column, value) -> sample.append(String.format("%s=%s, ", column, value)));
                    sample.append("\n");
                });
            }
        }

        private void generateResponse(String prompt, String model, String sessionId,
                boolean isComplex, Consumer<String> onResponse) {
            String finalPrompt = isComplex ? prompt + "\n\n[추가 지침]\n복잡한 질문 분석 필요" : prompt;
            ollamaService.generateResponseStream(finalPrompt, model, sessionId,
                    response -> {
                        if (response != null && !response.trim().isEmpty()) {
                            onResponse.accept(response);
                        }
                    });
        }

        private void updateHistory(String sessionId, String userInput) {
            historyManager.addMessage(sessionId, new HistoryManager.ChatMessage("user", userInput));
            historyManager.addMessage(sessionId, new HistoryManager.ChatMessage("assistant", "응답 완료"));
        }

        private record QuestionAnalysis(boolean requiresDbData, boolean isComplex) {
        }
    }
}