package com.example.demo.PHG_DeepSeek.AgentParts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

// [1] 프로젝트 구조 로더 컴포넌트 - 파일 시스템 탐색 및 구조 분석 [참조번호: 2,4,6]
@Component
public class ProjectLoader {
    // [2] 상수 정의 - 관련 파일 확장자 및 설정 파일 목록
    private static final Set<String> RELEVANT_EXT = Set.of(
            ".java", ".xml", ".properties", ".yml", ".yaml", ".json",
            ".html", ".css", ".js", ".jsx", ".ts", ".tsx", ".sql");

    private static final Set<String> CONFIG_FILES = Set.of(
            ".properties", ".yml", ".yaml", ".env");

    private static final String CONFIG_FILE_PREFIX = "application";

    // [3] 프로젝트 구조 로드 메인 메서드 - 전체 파일 시스템 탐색 [참조번호: 5,7,8]
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

    // [4] 관련 파일 필터링 메서드 - 확장자 기반 파일 선별 [참조번호: 3]
    private boolean isRelevantFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return RELEVANT_EXT.stream()
                .anyMatch(fileName::endsWith);
    }

    // [5] 파일 정보 추가 메서드 - 상대 경로 및 내용 포함 [참조번호: 3,6]
    private void appendFileInfo(Path path, StringBuilder context, Path root) {
        String relativePath = root.relativize(path).toString();
        context.append("\n파일: ").append(relativePath);

        if (shouldIncludeContent(path)) {
            appendFileContent(path, context);
        }
    }

    // [6] 파일 내용 추가 메서드 - 설정 파일 내용 읽기 [참조번호: 5]
    private void appendFileContent(Path path, StringBuilder context) {
        try {
            String content = Files.readString(path);
            context.append("\n내용:\n").append(content).append("\n");
        } catch (IOException e) {
            context.append("\n파일 읽기 실패: ").append(e.getMessage());
        }
    }

    // [7] 내용 포함 여부 결정 메서드 - 설정 파일 식별 [참조번호: 5]
    private boolean shouldIncludeContent(Path path) {
        String filename = path.getFileName().toString().toLowerCase();
        return filename.contains(CONFIG_FILE_PREFIX) ||
                CONFIG_FILES.stream().anyMatch(filename::endsWith);
    }
}