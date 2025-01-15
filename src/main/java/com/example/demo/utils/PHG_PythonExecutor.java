package com.example.demo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * PythonExecutor 클래스
 * 
 * 이 클래스는 Java 애플리케이션에서 Python 코드를 실행하고 그 결과를 가져오는 기능을 제공합니다.
 * Python 함수를 호출하고, 그 결과를 문자열이나 JSON 객체로 반환합니다.
 * 
 * Java에 사용될 함수명: PythonExecutor, PythonExecutor.executeFunction
 */
public class PHG_PythonExecutor {
    // Python 실행 명령어
    private static final String PYTHON_COMMAND = "python";

    // 디버그 모드 설정 (true면 디버그 정보 출력)
    private static final boolean DEBUG_MODE = true;

    // Python 스크립트 실행 제한 시간 (초)
    private static final int TIMEOUT_SECONDS = 30;

    // 초기 버퍼 크기
    private static final int INITIAL_BUFFER_SIZE = 1024;

    // 명령어 리스트의 초기 용량
    private static final int COMMAND_LIST_INITIAL_CAPACITY = 3;

    // JSON 데이터 처리를 위한 ObjectMapper 설정
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

    // JSON 배열을 파싱할 때 사용하는 타입 참조
    private static final TypeReference<List<Map<String, Object>>> JSON_TYPE_REFERENCE_LIST = new TypeReference<List<Map<String, Object>>>() {
    };

    // JSON 객체를 파싱할 때 사용하는 타입 참조
    private static final TypeReference<Map<String, Object>> JSON_TYPE_REFERENCE_OBJECT = new TypeReference<Map<String, Object>>() {
    };

    // Python 스크립트 실행 전 필요한 설정 (인코딩, 캐시 등)
    private static final String PYTHON_SETUP = String.join("\n",
            "import sys, io, json",
            "sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding='utf-8')",
            "sys.stderr = io.TextIOWrapper(sys.stderr.detach(), encoding='utf-8')",
            "__import__('os').environ['PYTHONPYCACHEPREFIX'] = '__pycache__'");

    // 동시 실행을 제어하기 위한 락
    private static final ReentrantLock LOCK = new ReentrantLock();

    // Python 스크립트 파일 경로
    private final String pythonPath;

    // 실행할 Python 모듈 이름
    private final String pythonModuleName;

    /**
     * PythonExecutor 생성자
     * 
     * @param pythonPath       Python 스크립트 파일 경로
     * @param pythonModuleName 실행할 Python 모듈 이름
     */
    public PHG_PythonExecutor(String pythonPath, String pythonModuleName) {
        if (pythonPath == null || pythonModuleName == null) {
            throw new IllegalArgumentException("파이썬 경로와 모듈 이름은 null일 수 없습니다.");
        }
        this.pythonPath = pythonPath.replace("\\", "/");
        this.pythonModuleName = pythonModuleName;
    }

    /**
     * Python 함수를 실행하고 결과를 반환합니다.
     * 
     * @param functionName 실행할 Python 함수 이름
     * @param args         함수에 전달할 인자들
     * @return 함수 실행 결과 (JSON 형식이면 파싱된 객체, 아니면 문자열)
     */
    public Object executeFunction(String functionName, Object... args) {
        long startTime = System.nanoTime();
        LOCK.lock();
        try {
            validateFunction(functionName);

            Process process = null;
            try {
                process = executeProcess(buildCommand(functionName, args));
                String result = captureOutput(process);

                if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                    return "실행 오류: 시간 초과";
                }

                if (process.exitValue() != 0) {
                    return "실행 오류: " + result;
                }

                return parseResultIfJson(result);
            } catch (Exception e) {
                return "실행 오류: " + e.getMessage();
            } finally {
                if (process != null) {
                    process.destroyForcibly();
                }
                if (DEBUG_MODE) {
                    long executionTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
                    System.out.println("[DEBUG] 함수 실행 시간: " + executionTime + "ms");
                }
            }
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 결과가 JSON 형식인 경우 파싱하여 Java 객체로 변환합니다.
     * 
     * @param result Python 함수 실행 결과 문자열
     * @return 파싱된 Java 객체 또는 원본 문자열
     */
    private Object parseResultIfJson(String result) {
        String trimmed = result.trim();

        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            try {
                return JSON_MAPPER.readValue(trimmed, JSON_TYPE_REFERENCE_LIST);
            } catch (Exception e) {
                if (DEBUG_MODE) {
                    System.out.println("[DEBUG] JSON 배열 파싱 실패: " + e.getMessage());
                }
                return result;
            }
        } else if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            try {
                return JSON_MAPPER.readValue(trimmed, JSON_TYPE_REFERENCE_OBJECT);
            } catch (Exception e) {
                if (DEBUG_MODE) {
                    System.out.println("[DEBUG] JSON 객체 파싱 실패: " + e.getMessage());
                }
                return result;
            }
        }
        return result;
    }

    /**
     * 함수 이름과 Python 파일 경로의 유효성을 검사합니다.
     * 
     * @param functionName 검사할 함수 이름
     */
    private void validateFunction(String functionName) {
        if (functionName == null || functionName.trim().isEmpty() || !new File(pythonPath).exists()) {
            throw new IllegalArgumentException("함수 이름이 유효하지 않거나, 파이썬 경로가 존재하지 않습니다.");
        }
    }

    /**
     * Python 실행 명령어를 생성합니다.
     * 
     * @param functionName 실행할 함수 이름
     * @param args         함수에 전달할 인자들
     * @return 실행할 명령어 리스트
     */
    private List<String> buildCommand(String functionName, Object... args) {
        StringBuilder codeBuilder = new StringBuilder(INITIAL_BUFFER_SIZE);

        codeBuilder.append(PYTHON_SETUP)
                .append("\nsys.path.insert(0, '").append(pythonPath).append("')")
                .append("\nfrom ").append(pythonModuleName).append(" import ").append(functionName)
                .append("\nprint(").append(functionName).append("(");

        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                codeBuilder.append(", ");
            }
            if (args[i] instanceof String) {
                codeBuilder.append("'").append(args[i].toString().replace("'", "\\'")).append("'");
            } else {
                codeBuilder.append(args[i]);
            }
        }
        codeBuilder.append("))");

        List<String> command = new ArrayList<>(COMMAND_LIST_INITIAL_CAPACITY);
        command.add(PYTHON_COMMAND);
        command.add("-c");
        command.add(codeBuilder.toString());

        if (DEBUG_MODE) {
            System.out.println("[DEBUG] 실행할 명령어: " + command);
        }
        return command;
    }

    /**
     * ProcessBuilder를 사용하여 Python 프로세스를 실행합니다.
     * 
     * @param command 실행할 명령어 리스트
     * @return 실행된 Process 객체
     */
    private Process executeProcess(List<String> command) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(command)
                .redirectErrorStream(true);

        Map<String, String> env = processBuilder.environment();
        env.put("PYTHONUNBUFFERED", "1");
        env.put("PYTHONIOENCODING", "utf-8");

        return processBuilder.start();
    }

    /**
     * 프로세스의 출력을 읽어 문자열로 반환합니다.
     * 
     * @param process 실행된 Process 객체
     * @return 프로세스의 출력 문자열
     */
    private String captureOutput(Process process) throws Exception {
        StringBuilder output = new StringBuilder(INITIAL_BUFFER_SIZE);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append('\n');
                if (DEBUG_MODE) {
                    System.out.println("[DEBUG] 출력: " + line);
                }
            }
        }
        return output.toString().trim();
    }
}
