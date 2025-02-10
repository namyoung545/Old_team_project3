package com.example.demo.PHG_DeepSeek.AgentParts;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

// [1] 데이터베이스 관리 컴포넌트 - 스키마 로딩 및 임베딩 검색 관리 [참조번호: 2,3,4]
@Component
public class DatabaseManager {
    // [2] 의존성 주입 필드 및 임베딩 저장 변수
    private final JdbcTemplate jdbcTemplate;
    private final String dbUrl;
    private Word2Vec word2Vec;
    private Map<String, INDArray> tableEmbeddings;
    private Map<String, List<String>> tableColumns;
    private Map<String, INDArray> columnEmbeddings;
    private static final int EMBEDDING_SIZE = 100;

    // [3] 생성자: 의존성 주입 및 Word2Vec 초기화 [참조번호: 4]
    public DatabaseManager(JdbcTemplate jdbcTemplate, @Value("${spring.datasource.url}") String dbUrl) {
        this.jdbcTemplate = jdbcTemplate;
        this.dbUrl = dbUrl;
        this.tableEmbeddings = new HashMap<>();
        this.columnEmbeddings = new HashMap<>();
        this.tableColumns = new HashMap<>();
        initializeWord2Vec();
    }

    // [4] Word2Vec 모델 초기화 메서드 [참조번호: 5]
    private void initializeWord2Vec() {
        try {
            // 임시 파일 생성
            File tempFile = File.createTempFile("vocab", ".txt");
            tempFile.deleteOnExit();

            // 기본 DB 관련 어휘 준비
            List<String> basicVocab = Arrays.asList(
                    "table", "column", "key", "index", "foreign", "primary",
                    "database", "schema", "relation", "entity", "attribute",
                    "varchar", "integer", "datetime", "boolean", "numeric");

            // 임시 파일에 어휘 쓰기
            FileUtils.writeLines(tempFile, basicVocab);

            // TokenizerFactory 설정
            TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
            tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

            // Word2Vec 모델 구성
            word2Vec = new Word2Vec.Builder()
                    .minWordFrequency(1)
                    .iterations(5)
                    .layerSize(100)
                    .seed(42)
                    .windowSize(5)
                    .iterate(new BasicLineIterator(tempFile))
                    .tokenizerFactory(tokenizerFactory)
                    .build();

            word2Vec.fit();

        } catch (Exception e) {
            throw new RuntimeException("Word2Vec initialization failed", e);
        }
    }

    // [5] 데이터베이스 스키마 로딩 메서드 [참조번호: 6,7,8]
    public String loadDatabaseSchema() {
        String dbName = extractDatabaseName(dbUrl);
        StringBuilder schema = new StringBuilder("DB 스키마 (" + dbName + "):\n");
        try {
            var dataSource = Optional.ofNullable(jdbcTemplate.getDataSource())
                    .orElseThrow(() -> new IllegalStateException("DataSource 오류"));
            try (var connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                try (ResultSet tables = metaData.getTables(dbName, null, "%", new String[] { "TABLE" })) {
                    while (tables.next()) {
                        processTable(metaData, tables, schema);
                    }
                }
            }
            generateEmbeddings();
        } catch (Exception e) {
            return "DB 스키마 로딩 실패: " + e.getMessage();
        }
        return schema.toString();
    }

    // [6] 테이블 처리 메서드 [참조번호: 7]
    private void processTable(DatabaseMetaData metaData, ResultSet tables, StringBuilder schema) throws SQLException {
        String tableName = tables.getString("TABLE_NAME");
        schema.append("\n테이블: ").append(tableName);

        // 테이블별 컬럼 리스트 초기화
        tableColumns.put(tableName, new ArrayList<>());

        appendColumns(metaData, tableName, schema);
        appendPrimaryKeys(metaData, tableName, schema);
    }

    // [7] 컬럼 정보 추가 메서드 [참조번호: 8,9]
    private void appendColumns(DatabaseMetaData metaData, String tableName, StringBuilder schema) throws SQLException {
        try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
            schema.append("\n  컬럼:");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String typeName = columns.getString("TYPE_NAME");

                schema.append("\n    - ")
                        .append(columnName)
                        .append(" (")
                        .append(typeName)
                        .append(")");

                // 컬럼 정보 저장
                tableColumns.get(tableName).add(columnName);

                // 컬럼 설명 텍스트 생성 및 임베딩
                String columnDesc = String.format("%s %s %s", tableName, columnName, typeName);
                generateColumnEmbedding(tableName, columnName, columnDesc);
            }
        }
    }

    // [8] 컬럼 임베딩 생성 메서드 [참조번호: 9]
    private void generateColumnEmbedding(String tableName, String columnName, String columnDesc) {
        INDArray embedding = createEmbedding(columnDesc);
        columnEmbeddings.put(tableName + "." + columnName, embedding);
    }

    // [9] 테이블 임베딩 생성 메서드 [참조번호: 10]
    private void generateEmbeddings() {
        for (Map.Entry<String, List<String>> entry : tableColumns.entrySet()) {
            String tableName = entry.getKey();
            List<String> columns = entry.getValue();

            // 테이블 설명 텍스트 생성
            StringBuilder tableDesc = new StringBuilder(tableName);
            columns.forEach(col -> tableDesc.append(" ").append(col));

            // 테이블 임베딩 생성
            INDArray embedding = createEmbedding(tableDesc.toString());
            tableEmbeddings.put(tableName, embedding);
        }
    }

    // [10] 텍스트 임베딩 생성 유틸리티 메서드 [참조번호: 11]
    private INDArray createEmbedding(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        INDArray embedding = Nd4j.zeros(EMBEDDING_SIZE);
        int count = 0;

        for (String word : words) {
            if (word2Vec.hasWord(word)) {
                embedding.addi(Nd4j.create(word2Vec.getWordVector(word)));
                count++;
            }
        }

        if (count > 0) {
            embedding.divi(count);
        }

        return embedding;
    }

    // [11] 관련 테이블 검색 메서드 [참조번호: 12,13]
    public List<String> findRelatedTables(String query) {
        INDArray queryEmbedding = createEmbedding(query);
        return findSimilarItems(queryEmbedding, tableEmbeddings);
    }

    // [12] 관련 컬럼 검색 메서드 [참조번호: 13]
    public List<String> findRelatedColumns(String query) {
        INDArray queryEmbedding = createEmbedding(query);
        return findSimilarItems(queryEmbedding, columnEmbeddings);
    }

    // [13] 유사도 기반 항목 검색 메서드 [참조번호: 14]
    private List<String> findSimilarItems(INDArray queryEmbedding, Map<String, INDArray> embeddings) {
        List<Map.Entry<String, Double>> similarities = new ArrayList<>();

        for (Map.Entry<String, INDArray> entry : embeddings.entrySet()) {
            double similarity = cosineSimilarity(queryEmbedding, entry.getValue());
            similarities.add(new AbstractMap.SimpleEntry<>(entry.getKey(), similarity));
        }

        similarities.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return similarities.stream()
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();
    }

    // [14] 코사인 유사도 계산 메서드
    private double cosineSimilarity(INDArray vec1, INDArray vec2) {
        double dotProduct = vec1.mul(vec2).sumNumber().doubleValue();
        double norm1 = vec1.norm2Number().doubleValue();
        double norm2 = vec2.norm2Number().doubleValue();
        return dotProduct / (norm1 * norm2);
    }

    // [15] 기본키 정보 추가 메서드
    private void appendPrimaryKeys(DatabaseMetaData metaData, String tableName, StringBuilder schema)
            throws SQLException {
        try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName)) {
            if (primaryKeys.next()) {
                schema.append("\n  기본키: ").append(primaryKeys.getString("COLUMN_NAME"));
            }
        }
    }

    // [16] 데이터베이스 이름 추출 유틸리티 메서드
    private String extractDatabaseName(String url) {
        String[] parts = url.split("/");
        String dbWithParams = parts[parts.length - 1];
        return dbWithParams.split("\\?")[0];
    }

    // [17] 테이블-컬럼 매핑 조회 메서드
    public Map<String, List<String>> getTableColumns() {
        return Collections.unmodifiableMap(tableColumns);
    }

    // [18] 데이터베이스 통합 검색 메서드 [참조번호: 14]
    public List<Map.Entry<String, Double>> searchDatabase(String query) {
        INDArray queryEmbedding = createEmbedding(query);
        List<Map.Entry<String, Double>> results = new ArrayList<>();

        // 테이블 검색
        for (Map.Entry<String, INDArray> entry : tableEmbeddings.entrySet()) {
            double similarity = cosineSimilarity(queryEmbedding, entry.getValue());
            results.add(new AbstractMap.SimpleEntry<>(
                    "Table: " + entry.getKey(),
                    similarity));
        }

        // 컬럼 검색
        for (Map.Entry<String, INDArray> entry : columnEmbeddings.entrySet()) {
            double similarity = cosineSimilarity(queryEmbedding, entry.getValue());
            results.add(new AbstractMap.SimpleEntry<>(
                    "Column: " + entry.getKey(),
                    similarity));
        }

        results.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return results.stream().limit(10).toList();
    }
}