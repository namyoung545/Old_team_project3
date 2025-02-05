package com.example.demo.PHG_DeepSeek;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class AgentService {
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

    private static final int MAX_HISTORY = 10;
    private static final Set<String> RELEVANT_FILE_EXTENSIONS = Set.of(
            ".java", ".xml", ".properties", ".yml", ".yaml", ".json",
            ".html", ".css", ".js", ".jsx", ".ts", ".tsx", ".sql");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    private final OllamaService ollamaService;
    private final Map<String, List<ChatMessage>> conversations = new HashMap<>();
    private String projectContext;
    private String databaseContext;

    public AgentService(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostConstruct
    public void initialize() {
        projectContext = loadProjectStructure();
        databaseContext = loadDatabaseSchema();
    }

    public String processUserInput(String userInput, String sessionId, String model) {
        // 대화 기록 가져오기
        List<ChatMessage> history = conversations.computeIfAbsent(sessionId, k -> new ArrayList<>());

        // 사용자의 입력이 “현재 (테이블 이름)테이블에 저장된 (어떤)값 출력해 주세요.”와 같이
        // 실제 테이블의 데이터를 출력하라는 요청인 경우, SQL 쿼리를 직접 실행해서 결과를 반환하도록 한다.
        if (userInput.contains("테이블") && userInput.contains("저장된") && userInput.contains("출력해")) {
            String output = executeDisplayQuery(userInput);
            // 대화 기록 업데이트
            history.add(new ChatMessage("user", userInput));
            history.add(new ChatMessage("assistant", output));
            // 최근 대화 기록만 유지
            if (history.size() > MAX_HISTORY) {
                history = new ArrayList<>(history.subList(history.size() - MAX_HISTORY, history.size()));
                conversations.put(sessionId, history);
            }
            return output;
        }

        // --- 이하 기존 로직 (대화형 AI 처리) ---

        // 1단계: 사용자 입력 분석
        String inputAnalysis = ollamaService.generateResponse(
                "분석할 사용자 입력: " + userInput + "\n이 입력의 의도와 필요한 정보를 분석하세요.",
                model,
                sessionId);

        // 2단계: 관련성 있는 이전 대화 찾기
        String relevantHistory = findRelevantHistory(userInput, history, model);

        // 2단계: 컨텍스트 고려사항 (관련된 이전 대화만 포함)
        String contextConsideration = ollamaService.generateResponse(
                "사용자 입력: " + userInput + "\n" +
                        (relevantHistory.isEmpty() ? "" : "관련된 이전 대화: " + relevantHistory + "\n") +
                        "현재 질문과 관련된 컨텍스트 정보를 분석하세요.",
                model,
                sessionId);

        // 3단계: 실행 계획
        String executionPlan = ollamaService.generateResponse(
                "계획 수립을 위한 정보:\n" +
                        "- 사용자 입력: " + userInput + "\n" +
                        "- 분석 결과: " + inputAnalysis + "\n" +
                        "- 컨텍스트 고려사항: " + contextConsideration + "\n" +
                        "어떻게 응답할지 계획을 세우세요.",
                model,
                sessionId);

        // 최종 응답 생성
        String prompt = String.format(SYSTEM_PROMPT, projectContext, databaseContext) +
                String.format(REASONING_PROMPT, inputAnalysis, contextConsideration, executionPlan);

        String finalResponse = ollamaService.generateResponse(prompt, model, sessionId);

        // 대화 기록 업데이트
        history.add(new ChatMessage("user", userInput));
        history.add(new ChatMessage("assistant", finalResponse));

        // 최근 대화 기록만 유지
        if (history.size() > MAX_HISTORY) {
            history = new ArrayList<>(history.subList(history.size() - MAX_HISTORY, history.size()));
            conversations.put(sessionId, history);
        }

        return finalResponse;
    }

    private String executeDisplayQuery(String userInput) {
        // 예시: "현재 회원정보테이블에 저장된 모든 값 출력해 주세요."
        // "현재"와 "테이블" 사이의 내용을 테이블 이름으로 가정
        int startIdx = userInput.indexOf("현재");
        int endIdx = userInput.indexOf("테이블");
        if (startIdx == -1 || endIdx == -1 || endIdx <= startIdx) {
            return "요청 형식이 올바르지 않습니다.";
        }
        String tableName = userInput.substring(startIdx + "현재".length(), endIdx).trim();
        if (tableName.isEmpty()) {
            return "테이블 이름을 확인할 수 없습니다.";
        }

        String query = "SELECT * FROM " + tableName;
        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(query);
            if (results.isEmpty()) {
                return tableName + " 테이블에 저장된 데이터가 없습니다.";
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(tableName).append(" 테이블의 데이터:\n");
                for (Map<String, Object> row : results) {
                    sb.append(row.toString()).append("\n");
                }
                return sb.toString();
            }
        } catch (Exception e) {
            return "SQL 실행 오류: " + e.getMessage();
        }
    }

    private String findRelevantHistory(String userInput, List<ChatMessage> history, String model) {
        if (history.isEmpty()) {
            return "";
        }

        StringBuilder relevantHistory = new StringBuilder();

        // 최근 5개의 대화만 검토
        int start = Math.max(0, history.size() - 5);
        List<ChatMessage> recentHistory = history.subList(start, history.size());
        for (int i = 0; i < recentHistory.size() - 1; i += 2) {
            ChatMessage question = recentHistory.get(i);
            ChatMessage answer = recentHistory.get(i + 1);

            String relevanceCheck = ollamaService.generateResponse(
                    String.format("""
                            현재 질문: %s
                            이전 질문: %s
                            이전 답변: %s

                            위 대화가 현재 질문과 관련이 있는지 "예" 또는 "아니오"로만 답하세요.
                            """,
                            userInput, question.content(), answer.content()),
                    model,
                    "relevance-check");

            if (relevanceCheck.toLowerCase().contains("예")) {
                relevantHistory.append("Q: ").append(question.content()).append("\n");
                relevantHistory.append("A: ").append(answer.content()).append("\n");
            }
        }

        return relevantHistory.toString();
    }

    private String loadProjectStructure() {
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

    private void appendFileInfo(Path filePath, StringBuilder context, Path projectRoot) {
        String relativePath = projectRoot.relativize(filePath).toString();
        context.append("\n파일: ").append(relativePath);
        if (shouldIncludeContent(filePath)) {
            try {
                String content = Files.readString(filePath);
                context.append("\n내용:\n").append(content).append("\n");
            } catch (IOException e) {
                context.append("\n파일 읽기 실패: ").append(e.getMessage());
            }
        }
    }

    private boolean isRelevantFile(Path path) {
        String filename = path.getFileName().toString().toLowerCase();
        return RELEVANT_FILE_EXTENSIONS.stream().anyMatch(filename::endsWith);
    }

    private boolean shouldIncludeContent(Path path) {
        String filename = path.getFileName().toString().toLowerCase();
        return filename.contains("application")
                || filename.endsWith(".properties")
                || filename.endsWith(".yml")
                || filename.endsWith(".yaml")
                || filename.endsWith(".env");
    }

    private String loadDatabaseSchema() {
        String dbName = extractDatabaseName(dbUrl);
        StringBuilder schema = new StringBuilder("데이터베이스 스키마 (")
                .append(dbName)
                .append("):\n");

        try {
            var dataSource = Optional.ofNullable(jdbcTemplate.getDataSource())
                    .orElseThrow(() -> new IllegalStateException("DataSource가 구성되지 않았습니다."));
            try (var connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                try (ResultSet tables = metaData.getTables(dbName, null, "%", new String[] { "TABLE" })) {
                    while (tables.next()) {
                        String tableName = tables.getString("TABLE_NAME");
                        schema.append("\n테이블: ").append(tableName);
                        appendTableDetails(metaData, tableName, schema);
                    }
                }
            }
        } catch (Exception e) {
            return "데이터베이스 스키마 로딩 실패: " + e.getMessage();
        }
        return schema.toString();
    }

    private void appendTableDetails(DatabaseMetaData metaData, String tableName, StringBuilder schema) {
        try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
            schema.append("\n  컬럼:");
            while (columns.next()) {
                schema.append("\n    - ")
                        .append(columns.getString("COLUMN_NAME"))
                        .append(" (")
                        .append(columns.getString("TYPE_NAME"))
                        .append(")");
            }
            try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName)) {
                if (primaryKeys.next()) {
                    schema.append("\n  기본키: ").append(primaryKeys.getString("COLUMN_NAME"));
                }
            }
        } catch (Exception e) {
            schema.append("\n  컬럼 정보 로딩 실패: ").append(e.getMessage());
        }
    }

    private String extractDatabaseName(String url) {
        String[] parts = url.split("/");
        String dbWithParams = parts[parts.length - 1];
        return dbWithParams.split("\\?")[0];
    }

    public void clearConversation(String sessionId) {
        conversations.remove(sessionId);
    }

    private record ChatMessage(String role, String content) {
    }
}