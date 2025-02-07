package com.example.demo.PHG_DeepSeek;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class AgentService {
    // 내부 클래스 인스턴스
    private final ProjectLoader projectLoader = new ProjectLoader();
    private final DatabaseManager databaseManager = new DatabaseManager();
    private final HistoryManager historyManager = new HistoryManager();
    private final DRLAIntegrator drlIntegrator = new DRLAIntegrator();

    // 의존성 주입 필드
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Value("${spring.datasource.url}")
    private String dbUrl;
    private final OllamaService ollamaService;
    private String projectContext;
    private String databaseContext;
    private final Map<String, List<ChatMessage>> conversations = new HashMap<>();

    public AgentService(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostConstruct
    public void initialize() {
        projectContext = projectLoader.loadProjectStructure();
        databaseContext = databaseManager.loadDatabaseSchema();
    }

    // 시스템 프롬프트 상수
    private static final String SYSTEM_PROMPT = """
            [시스템 지시사항]
            1. 사용자의 요구사항을 논리적으로 생각 후 이행
            2. 대화 맥락 유지 및 이전 대화 참조
            3. 관련된 경우 데이터베이스 정보 활용
            4. 명확하고 간단한 응답 제공
            5. 중국어 사용을 금지

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

    public void processUserInputStream(String userInput, String sessionId, String model, Consumer<String> onResponse) {
        List<ChatMessage> history = conversations.computeIfAbsent(sessionId, k -> new ArrayList<>());
        StringBuilder analysis = new StringBuilder();
        StringBuilder context = new StringBuilder();
        StringBuilder plan = new StringBuilder();

        // 분석 단계
        String analysisPrompt = "분석할 사용자 입력: " + userInput + "\n이 입력의 의도와 필요한 정보를 분석하세요.";
        ollamaService.generateResponseStream(analysisPrompt, model, sessionId, analysis::append);

        // 컨텍스트 분석
        String relevantHistory = historyManager.findRelevantHistory(userInput, history, model);
        String contextPrompt = "사용자 입력: " + userInput + "\n" +
                (relevantHistory.isEmpty() ? "" : "관련된 이전 대화: " + relevantHistory + "\n") +
                "현재 질문과 관련된 컨텍스트 정보를 분석하세요.";
        ollamaService.generateResponseStream(contextPrompt, model, sessionId, context::append);

        // 계획 수립
        String planPrompt = "계획 수립을 위한 정보:\n" +
                "- 사용자 입력: " + userInput + "\n" +
                "- 분석 결과: " + analysis.toString() + "\n" +
                "- 컨텍스트 고려사항: " + context.toString() + "\n" +
                "어떻게 응답할지 계획을 세우세요.";
        ollamaService.generateResponseStream(planPrompt, model, sessionId, plan::append);

        // DRL 개선 적용
        String refinedPlan = drlIntegrator.refinePlan(plan.toString(), userInput, sessionId);

        // 최종 응답 생성
        String finalPrompt = String.format(SYSTEM_PROMPT, projectContext, databaseContext)
                + String.format(REASONING_PROMPT, analysis.toString(), context.toString(), refinedPlan);
        StringBuilder finalResponse = new StringBuilder();
        ollamaService.generateResponseStream(finalPrompt, model, sessionId, response -> {
            onResponse.accept(response);
            finalResponse.append(response);
        });

        // 대화 기록 업데이트
        String finalRespStr = finalResponse.toString();
        history.add(new ChatMessage("user", userInput));
        history.add(new ChatMessage("assistant", finalRespStr));
        historyManager.updateHistory(history, sessionId);

        // DRL 정책 업데이트
        double reward = drlIntegrator.computeReward(userInput, finalRespStr);
        drlIntegrator.updatePolicy(sessionId, reward);
    }

    public String processUserInput(String userInput, String sessionId, String model) {
        StringBuilder response = new StringBuilder();
        processUserInputStream(userInput, sessionId, model, response::append);
        return response.toString();
    }

    public void clearConversation(String sessionId) {
        conversations.remove(sessionId);
    }

    // 내부 클래스 1: 프로젝트 구조 분석
    private class ProjectLoader {
        private static final Set<String> RELEVANT_EXT = Set.of(
                ".java", ".xml", ".properties", ".yml", ".yaml", ".json",
                ".html", ".css", ".js", ".jsx", ".ts", ".tsx", ".sql");

        public String loadProjectStructure() {
            StringBuilder context = new StringBuilder("프로젝트 구조:\n");
            Path projectRoot = Paths.get("").toAbsolutePath();
            try (Stream<Path> paths = Files.walk(projectRoot)) {
                paths.filter(Files::isRegularFile)
                        .filter(this::isRelevantFile)
                        .forEach(path -> appendFileInfo(path, context, projectRoot));
            } catch (IOException e) {
                return "프로젝트 구조 로딩 실패: " + e.getMessage();
            }
            return context.toString();
        }

        private boolean isRelevantFile(Path path) {
            return RELEVANT_EXT.stream().anyMatch(ext -> path.getFileName().toString().endsWith(ext));
        }

        private void appendFileInfo(Path path, StringBuilder context, Path root) {
            String relativePath = root.relativize(path).toString();
            context.append("\n파일: ").append(relativePath);
            if (shouldIncludeContent(path)) {
                try {
                    String content = Files.readString(path);
                    context.append("\n내용:\n").append(content).append("\n");
                } catch (IOException e) {
                    context.append("\n파일 읽기 실패: ").append(e.getMessage());
                }
            }
        }

        private boolean shouldIncludeContent(Path path) {
            String filename = path.getFileName().toString().toLowerCase();
            return filename.contains("application")
                    || filename.endsWith(".properties")
                    || filename.endsWith(".yml")
                    || filename.endsWith(".yaml")
                    || filename.endsWith(".env");
        }
    }

    // 내부 클래스 2: 데이터베이스 관리
    private class DatabaseManager {
        public String loadDatabaseSchema() {
            String dbName = extractDatabaseName(dbUrl);
            StringBuilder schema = new StringBuilder("DB 스키마 (" + dbName + "):\n");
            try {
                var dataSource = Optional.ofNullable(jdbcTemplate.getDataSource())
                        .orElseThrow(() -> new IllegalStateException("DataSource 오류"));
                try (var connection = dataSource.getConnection()) {
                    DatabaseMetaData metaData = connection.getMetaData();
                    try (ResultSet tables = metaData.getTables(dbName, null, "%", new String[] { "TABLE" })) {
                        while (tables.next())
                            processTable(metaData, tables, schema);
                    }
                }
            } catch (Exception e) {
                return "DB 스키마 로딩 실패: " + e.getMessage();
            }
            return schema.toString();
        }

        private void processTable(DatabaseMetaData metaData, ResultSet tables,
                StringBuilder schema) throws SQLException {
            String tableName = tables.getString("TABLE_NAME");
            schema.append("\n테이블: ").append(tableName);
            appendColumns(metaData, tableName, schema);
            appendPrimaryKeys(metaData, tableName, schema);
        }

        private void appendColumns(DatabaseMetaData metaData, String tableName,
                StringBuilder schema) throws SQLException {
            try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
                schema.append("\n  컬럼:");
                while (columns.next()) {
                    schema.append("\n    - ")
                            .append(columns.getString("COLUMN_NAME"))
                            .append(" (")
                            .append(columns.getString("TYPE_NAME"))
                            .append(")");
                }
            }
        }

        private void appendPrimaryKeys(DatabaseMetaData metaData, String tableName,
                StringBuilder schema) throws SQLException {
            try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName)) {
                if (primaryKeys.next()) {
                    schema.append("\n  기본키: ").append(primaryKeys.getString("COLUMN_NAME"));
                }
            }
        }

        private String extractDatabaseName(String url) {
            String[] parts = url.split("/");
            String dbWithParams = parts[parts.length - 1];
            return dbWithParams.split("\\?")[0];
        }
    }

    // 내부 클래스 3: 대화 기록 관리
    private class HistoryManager {
        private static final int MAX_HISTORY = 10;

        public String findRelevantHistory(String userInput, List<ChatMessage> history, String model) {
            if (history.isEmpty())
                return "";

            StringBuilder relevantHistory = new StringBuilder();
            int start = Math.max(0, history.size() - 5);
            List<ChatMessage> recentHistory = history.subList(start, history.size());

            for (int i = 0; i < recentHistory.size() - 1; i += 2) {
                ChatMessage question = recentHistory.get(i);
                ChatMessage answer = recentHistory.get(i + 1);
                String prompt = String.format("""
                        현재 질문: %s
                        이전 질문: %s
                        이전 답변: %s
                        위 대화가 현재 질문과 관련이 있는지 "예" 또는 "아니오"로만 답하세요.
                        """, userInput, question.content(), answer.content());

                String relevanceCheck = ollamaService.generateResponse(prompt, model, "relevance-check");
                if (relevanceCheck.toLowerCase().contains("예")) {
                    relevantHistory.append("Q: ").append(question.content()).append("\n")
                            .append("A: ").append(answer.content()).append("\n");
                }
            }
            return relevantHistory.toString();
        }

        public void updateHistory(List<ChatMessage> history, String sessionId) {
            if (history.size() > MAX_HISTORY) {
                List<ChatMessage> trimmed = new ArrayList<>(
                        history.subList(history.size() - MAX_HISTORY, history.size()));
                conversations.put(sessionId, trimmed);
            }
        }
    }

    // 내부 클래스 4: DRL 통합 관리
    private static class DRLAIntegrator {
        public String refinePlan(String plan, String userInput, String sessionId) {
            return plan + "\n[DRL 최적화 적용]";
        }

        public void updatePolicy(String sessionId, double reward) {
            System.out.println("DRL 정책 업데이트 - 세션: " + sessionId + ", 보상: " + reward);
        }

        public double computeReward(String input, String response) {
            return response.length() / 100.0;
        }
    }

    // 레코드 유지
    private record ChatMessage(String role, String content) {
    }
}
