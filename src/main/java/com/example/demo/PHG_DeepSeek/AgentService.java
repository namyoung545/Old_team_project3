// [1] 패키지 선언: 클래스가 속한 패키지를 지정합니다.
package com.example.demo.PHG_DeepSeek;

//
// [2] 필요한 클래스와 인터페이스를 import 합니다.
//
import java.io.IOException; // 입출력 예외 처리
import java.nio.file.Files; // 파일 읽기/쓰기 관련 클래스
import java.nio.file.Path; // 파일 시스템 경로 표현
import java.nio.file.Paths; // 파일 시스템 경로 생성 도우미
import java.sql.DatabaseMetaData; // 데이터베이스 메타데이터 접근
import java.sql.ResultSet; // SQL 결과 집합 처리
import java.util.ArrayList; // 동적 배열 사용
import java.util.HashMap; // 키-값 저장소(HashMap) 사용
import java.util.List; // 리스트 인터페이스 사용
import java.util.Map; // 맵 인터페이스 사용
import java.util.Optional; // null 안전 연산
import java.util.Set; // 집합 데이터 타입 사용
import java.util.function.Consumer; // 함수형 인터페이스(콜백) 사용
import java.util.stream.Stream; // 스트림 API 사용

import org.springframework.beans.factory.annotation.Autowired; // 스프링 의존성 주입
import org.springframework.beans.factory.annotation.Value; // 프로퍼티 값 주입
import org.springframework.jdbc.core.JdbcTemplate; // 데이터베이스 작업 도우미 클래스
import org.springframework.stereotype.Service; // 서비스 컴포넌트 어노테이션

import jakarta.annotation.PostConstruct; // 초기화 후 실행 어노테이션

//
// [3] AgentService 클래스: 에이전트 로직을 수행하는 서비스 클래스입니다.
//
@Service
public class AgentService {

    // [3-1] 시스템 프롬프트: 에이전트가 응답할 때 사용할 기본 시스템 지시사항을 정의합니다.
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

    // [3-2] 사고 과정 프롬프트: 에이전트의 사고 과정과 실행 계획을 표현하는 템플릿입니다.
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

    // [3-3] 최대 대화 기록 길이: 한 세션에서 유지할 최대 메시지 개수를 지정합니다.
    private static final int MAX_HISTORY = 10;

    // [3-4] 관련 파일 확장자 집합: 프로젝트 구조 로딩 시 관심 있는 파일 확장자를 지정합니다.
    private static final Set<String> RELEVANT_FILE_EXTENSIONS = Set.of(
            ".java", ".xml", ".properties", ".yml", ".yaml", ".json",
            ".html", ".css", ".js", ".jsx", ".ts", ".tsx", ".sql");

    // [4] JdbcTemplate: 데이터베이스 작업을 위한 JdbcTemplate을 자동 주입받습니다.
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // [5] 데이터베이스 URL: 스프링 프로퍼티에서 데이터베이스 URL을 주입받습니다.
    @Value("${spring.datasource.url}")
    private String dbUrl;

    // [6] OllamaService: 에이전트의 응답 생성을 위한 외부 서비스 객체를 주입받습니다.
    private final OllamaService ollamaService;

    // [7] 대화 기록 저장소: 세션별 대화 기록을 저장하는 맵입니다.
    private final Map<String, List<ChatMessage>> conversations = new HashMap<>();

    // [8] 프로젝트 컨텍스트: 프로젝트 구조 정보를 저장할 문자열 변수입니다.
    private String projectContext;

    // [9] 데이터베이스 컨텍스트: 데이터베이스 스키마 정보를 저장할 문자열 변수입니다.
    private String databaseContext;

    // [10] 생성자: OllamaService를 주입받아 AgentService 객체를 초기화합니다.
    public AgentService(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    // [11] 초기화 메서드: 빈 생성 후 프로젝트와 데이터베이스 컨텍스트를 로딩합니다.
    @PostConstruct
    public void initialize() {
        projectContext = loadProjectStructure(); // 프로젝트 구조 정보 로드
        databaseContext = loadDatabaseSchema(); // 데이터베이스 스키마 정보 로드
    }

    // [12] 사용자 입력 스트림 처리: 사용자 입력을 스트리밍 방식으로 처리하며 응답을 전달합니다.
    public void processUserInputStream(String userInput, String sessionId, String model, Consumer<String> onResponse) {
        // [12-1] 대화 기록 가져오기 또는 초기화: 세션별 기록을 유지합니다.
        List<ChatMessage> history = conversations.computeIfAbsent(sessionId, k -> new ArrayList<>());

        // [12-2] 사고, 컨텍스트, 계획 결과를 누적할 StringBuilder 생성
        StringBuilder analysis = new StringBuilder();
        StringBuilder context = new StringBuilder();
        StringBuilder plan = new StringBuilder();

        // [12-3] 1단계: 사용자 입력 분석 - 입력의 의도와 필요한 정보를 분석하는 프롬프트 생성
        String analysisPrompt = "분석할 사용자 입력: " + userInput + "\n이 입력의 의도와 필요한 정보를 분석하세요.";
        ollamaService.generateResponseStream(analysisPrompt, model, sessionId, analysis::append);

        // [12-4] 2단계: 컨텍스트 분석 - 이전 대화와 관련된 컨텍스트 정보를 분석
        String relevantHistory = findRelevantHistory(userInput, history, model);
        String contextPrompt = "사용자 입력: " + userInput + "\n" +
                (relevantHistory.isEmpty() ? "" : "관련된 이전 대화: " + relevantHistory + "\n") +
                "현재 질문과 관련된 컨텍스트 정보를 분석하세요.";
        ollamaService.generateResponseStream(contextPrompt, model, sessionId, context::append);

        // [12-5] 3단계: 실행 계획 수립 - 분석 결과와 컨텍스트를 바탕으로 응답 실행 계획을 수립
        String planPrompt = "계획 수립을 위한 정보:\n" +
                "- 사용자 입력: " + userInput + "\n" +
                "- 분석 결과: " + analysis.toString() + "\n" +
                "- 컨텍스트 고려사항: " + context.toString() + "\n" +
                "어떻게 응답할지 계획을 세우세요.";
        ollamaService.generateResponseStream(planPrompt, model, sessionId, plan::append);

        // [12-6] 최종 프롬프트 생성: 시스템 지시사항 및 사고 과정을 포함한 최종 프롬프트 구성
        String finalPrompt = String.format(SYSTEM_PROMPT, projectContext, databaseContext)
                + String.format(REASONING_PROMPT,
                        analysis.toString(),
                        context.toString(),
                        plan.toString());
        // [12-7] 최종 응답 누적을 위한 StringBuilder 생성
        StringBuilder finalResponse = new StringBuilder();
        ollamaService.generateResponseStream(finalPrompt, model, sessionId, response -> {
            onResponse.accept(response); // 콜백으로 실시간 응답 전달
            finalResponse.append(response); // 누적 응답 업데이트
        });

        // [12-8] 대화 기록 업데이트: 사용자와 에이전트의 메시지를 기록에 추가
        String finalRespStr = finalResponse.toString();
        history.add(new ChatMessage("user", userInput)); // 사용자 메시지 기록
        history.add(new ChatMessage("assistant", finalRespStr)); // 에이전트 응답 기록
        updateHistory(history, sessionId); // 대화 기록 정리(최대 길이 유지)
    }

    // [13] 동기식 사용자 입력 처리: 스트림 방식의 결과를 하나의 문자열로 반환합니다.
    public String processUserInput(String userInput, String sessionId, String model) {
        StringBuilder response = new StringBuilder(); // 응답 누적용 StringBuilder 생성
        processUserInputStream(userInput, sessionId, model, response::append);
        return response.toString(); // 최종 응답 문자열 반환
    }

    // [14] 관련 대화 기록 찾기: 최근 대화 중 현재 질문과 관련된 항목을 찾습니다.
    private String findRelevantHistory(String userInput, List<ChatMessage> history, String model) {
        if (history.isEmpty()) { // 대화 기록이 없으면 빈 문자열 반환
            return "";
        }

        StringBuilder relevantHistory = new StringBuilder(); // 관련 기록 누적용 StringBuilder 생성
        int start = Math.max(0, history.size() - 5); // 최근 5개의 메시지부터 검색
        List<ChatMessage> recentHistory = history.subList(start, history.size());

        // [14-1] 짝수 인덱스로 질문과 답변을 묶어 검사합니다.
        for (int i = 0; i < recentHistory.size() - 1; i += 2) {
            ChatMessage question = recentHistory.get(i); // 질문 메시지
            ChatMessage answer = recentHistory.get(i + 1); // 답변 메시지
            // [14-2] 현재 질문과 이전 대화를 비교하는 프롬프트 구성
            String prompt = String.format("""
                    현재 질문: %s
                    이전 질문: %s
                    이전 답변: %s

                    위 대화가 현재 질문과 관련이 있는지 "예" 또는 "아니오"로만 답하세요.
                    """, userInput, question.content(), answer.content());
            // [14-3] 관련성 확인을 위한 응답 생성
            String relevanceCheck = ollamaService.generateResponse(prompt, model, "relevance-check");
            if (relevanceCheck.toLowerCase().contains("예")) { // "예"라는 답변이면 관련 대화로 판단
                relevantHistory.append("Q: ").append(question.content()).append("\n");
                relevantHistory.append("A: ").append(answer.content()).append("\n");
            }
        }
        return relevantHistory.toString(); // 관련 대화 기록 반환
    }

    // [15] 프로젝트 구조 로딩: 파일 시스템을 탐색하여 프로젝트 구조 정보를 수집합니다.
    private String loadProjectStructure() {
        StringBuilder context = new StringBuilder("프로젝트 구조:\n"); // 결과 누적용 StringBuilder 생성
        Path projectRoot = Paths.get("").toAbsolutePath(); // 현재 프로젝트의 루트 경로 계산
        try (Stream<Path> paths = Files.walk(projectRoot)) { // 루트 경로부터 모든 파일 탐색
            paths.filter(Files::isRegularFile) // 일반 파일만 필터링
                    .filter(this::isRelevantFile) // 관심 있는 파일만 선택
                    .forEach(path -> appendFileInfo(path, context, projectRoot)); // 파일 정보를 context에 추가
        } catch (IOException e) { // 예외 발생 시
            return "프로젝트 구조 로딩 실패: " + e.getMessage(); // 에러 메시지 반환
        }
        return context.toString(); // 누적된 프로젝트 구조 반환
    }

    // [16] 파일 정보 추가: 파일의 상대 경로와 내용을 프로젝트 구조 정보에 추가합니다.
    private void appendFileInfo(Path filePath, StringBuilder context, Path projectRoot) {
        String relativePath = projectRoot.relativize(filePath).toString(); // 파일의 상대 경로 계산
        context.append("\n파일: ").append(relativePath); // 파일 경로 추가
        if (shouldIncludeContent(filePath)) { // 파일 내용을 포함할지 여부 확인
            try {
                String content = Files.readString(filePath); // 파일 전체 내용을 읽음
                context.append("\n내용:\n").append(content).append("\n"); // 파일 내용을 추가
            } catch (IOException e) { // 파일 읽기 실패 시
                context.append("\n파일 읽기 실패: ").append(e.getMessage()); // 에러 메시지 추가
            }
        }
    }

    // [17] 관심 있는 파일 여부 판단: 파일명이 지정한 확장자 중 하나인지 확인합니다.
    private boolean isRelevantFile(Path path) {
        String filename = path.getFileName().toString().toLowerCase(); // 파일명을 소문자로 변환
        return RELEVANT_FILE_EXTENSIONS.stream().anyMatch(filename::endsWith); // 확장자 매칭 검사
    }

    // [18] 파일 내용 포함 여부 판단: 특정 조건에 부합하는 파일의 내용을 포함할지 결정합니다.
    private boolean shouldIncludeContent(Path path) {
        String filename = path.getFileName().toString().toLowerCase(); // 파일명을 소문자로 변환
        return filename.contains("application")
                || filename.endsWith(".properties")
                || filename.endsWith(".yml")
                || filename.endsWith(".yaml")
                || filename.endsWith(".env"); // 조건에 따라 true 반환
    }

    // [19] 데이터베이스 스키마 로딩: 데이터베이스 메타데이터를 이용해 스키마 정보를 수집합니다.
    private String loadDatabaseSchema() {
        String dbName = extractDatabaseName(dbUrl); // 데이터베이스 이름 추출
        StringBuilder schema = new StringBuilder("데이터베이스 스키마 (").append(dbName).append("):\n");
        try {
            // [19-1] DataSource 확보: JdbcTemplate에서 DataSource를 가져옴
            var dataSource = Optional.ofNullable(jdbcTemplate.getDataSource())
                    .orElseThrow(() -> new IllegalStateException("DataSource가 구성되지 않았습니다."));
            try (var connection = dataSource.getConnection()) { // 데이터베이스 연결 획득
                DatabaseMetaData metaData = connection.getMetaData(); // 메타데이터 객체 생성
                // [19-2] 테이블 목록 조회: 지정된 데이터베이스의 테이블 정보를 가져옴
                try (ResultSet tables = metaData.getTables(dbName, null, "%", new String[] { "TABLE" })) {
                    while (tables.next()) { // 각 테이블에 대해
                        String tableName = tables.getString("TABLE_NAME"); // 테이블 이름 추출
                        schema.append("\n테이블: ").append(tableName); // 테이블 이름 추가
                        appendTableDetails(metaData, tableName, schema); // 테이블 세부 정보 추가
                    }
                }
            }
        } catch (Exception e) { // 예외 발생 시
            return "데이터베이스 스키마 로딩 실패: " + e.getMessage(); // 에러 메시지 반환
        }
        return schema.toString(); // 누적된 스키마 정보 반환
    }

    // [20] 테이블 세부 정보 추가: 각 테이블의 컬럼 및 기본키 정보를 스키마에 추가합니다.
    private void appendTableDetails(DatabaseMetaData metaData, String tableName, StringBuilder schema) {
        try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) { // 테이블의 컬럼 정보 조회
            schema.append("\n  컬럼:"); // 컬럼 목록 제목 추가
            while (columns.next()) { // 각 컬럼에 대해
                schema.append("\n    - ")
                        .append(columns.getString("COLUMN_NAME")) // 컬럼 이름 추가
                        .append(" (")
                        .append(columns.getString("TYPE_NAME")) // 컬럼 데이터 타입 추가
                        .append(")");
            }
            // [20-1] 기본키 정보 조회: 테이블의 기본키 정보를 확인
            try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName)) {
                if (primaryKeys.next()) { // 기본키가 존재하면
                    schema.append("\n  기본키: ").append(primaryKeys.getString("COLUMN_NAME"));
                }
            }
        } catch (Exception e) { // 예외 발생 시
            schema.append("\n  컬럼 정보 로딩 실패: ").append(e.getMessage()); // 에러 메시지 추가
        }
    }

    // [21] 데이터베이스 이름 추출: 데이터베이스 URL에서 이름을 추출합니다.
    private String extractDatabaseName(String url) {
        String[] parts = url.split("/"); // URL을 "/"로 분리
        String dbWithParams = parts[parts.length - 1]; // 마지막 부분 추출 (파라미터 포함)
        return dbWithParams.split("\\?")[0]; // 파라미터 제거 후 데이터베이스 이름 반환
    }

    // [22] 대화 기록 업데이트: 최대 기록 길이를 초과하면 오래된 메시지를 제거합니다.
    private void updateHistory(List<ChatMessage> history, String sessionId) {
        if (history.size() > MAX_HISTORY) { // 기록 길이가 최대치를 초과하면
            List<ChatMessage> trimmedHistory = new ArrayList<>(
                    history.subList(history.size() - MAX_HISTORY, history.size())); // 최신 MAX_HISTORY 만큼만 유지
            conversations.put(sessionId, trimmedHistory); // 갱신된 기록을 저장
        }
    }

    // [23] 대화 기록 삭제: 특정 세션의 대화 기록을 삭제합니다.
    public void clearConversation(String sessionId) {
        conversations.remove(sessionId); // 세션 ID에 해당하는 기록 제거
    }

    // [24] ChatMessage 레코드: 역할(사용자/에이전트)와 메시지 내용을 저장하는 불변 데이터 클래스입니다.
    private record ChatMessage(String role, String content) {
    }
}