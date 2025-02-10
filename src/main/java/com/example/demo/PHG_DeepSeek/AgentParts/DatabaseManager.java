package com.example.demo.PHG_DeepSeek.AgentParts;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

// [1] 데이터베이스 스키마 관리 컴포넌트 - 메타데이터 추출 및 구조 분석 [참조번호 : 2,4,6]
@Component
public class DatabaseManager {
    // [2] 의존성 주입 필드
    // - JDBC 템플릿 인스턴스
    // - 데이터소스 URL 구성값
    private final JdbcTemplate jdbcTemplate;
    private final String dbUrl;

    // [3] 생성자: 의존성 주입 및 초기화 [참조번호 : 1]
    public DatabaseManager(JdbcTemplate jdbcTemplate, @Value("${spring.datasource.url}") String dbUrl) {
        this.jdbcTemplate = jdbcTemplate;
        this.dbUrl = dbUrl;
    }

    // [4] 데이터베이스 스키마 로드 메인 메서드 - 전체 구조 분석 [참조번호 : 5,7,8]
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

    // [5] 개별 테이블 처리 로직 - 컬럼/기본키 정보 수집 [참조번호 : 6,9]
    private void processTable(DatabaseMetaData metaData, ResultSet tables,
            StringBuilder schema) throws SQLException {
        String tableName = tables.getString("TABLE_NAME");
        schema.append("\n테이블: ").append(tableName);
        appendColumns(metaData, tableName, schema);
        appendPrimaryKeys(metaData, tableName, schema);
    }

    // [6] 컬럼 정보 추출 메서드 - 데이터 타입 포함 상세 정보 작성 [참조번호 : 5]
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

    // [7] 기본키 정보 추출 메서드 - PK 컬럼 식별 [참조번호 : 5]
    private void appendPrimaryKeys(DatabaseMetaData metaData, String tableName,
            StringBuilder schema) throws SQLException {
        try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName)) {
            if (primaryKeys.next()) {
                schema.append("\n  기본키: ").append(primaryKeys.getString("COLUMN_NAME"));
            }
        }
    }

    // [8] URL 파싱 유틸리티 메서드 - 데이터베이스 이름 추출 [참조번호 : 4]
    private String extractDatabaseName(String url) {
        String[] parts = url.split("/");
        String dbWithParams = parts[parts.length - 1];
        return dbWithParams.split("\\?")[0];
    }
}
