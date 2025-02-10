package com.example.demo.PHG_DeepSeek.AgentParts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Component;

// [1] 프로젝트 로더 컴포넌트 - 프로젝트 파일 분석 및 임베딩 생성 관리 [참조번호: 2,3,4]
@Component
public class ProjectLoader {
    // [2] 관련 파일 확장자 및 설정 파일 정의
    private static final Set<String> RELEVANT_EXT = Set.of(
            ".java", ".xml", ".properties", ".yml", ".yaml", ".json",
            ".html", ".css", ".js", ".jsx", ".ts", ".tsx", ".sql");

    private static final Set<String> CONFIG_FILES = Set.of(
            ".properties", ".yml", ".yaml", ".env");

    private static final String CONFIG_FILE_PREFIX = "application";
    private static final int EMBEDDING_SIZE = 100;

    // [3] Word2Vec 모델 및 데이터 저장소 필드 선언
    private Word2Vec word2Vec;
    private Map<String, INDArray> fileEmbeddings;
    private Map<String, String> fileContents;

    // [4] 생성자: 초기화 및 Word2Vec 모델 설정 [참조번호: 5]
    public ProjectLoader() {
        this.fileEmbeddings = new HashMap<>();
        this.fileContents = new HashMap<>();
        initializeWord2Vec();
    }

    // [5] Word2Vec 모델 초기화 메서드 - 기본 어휘 설정 및 모델 학습 [참조번호: 6,7]
    private void initializeWord2Vec() {
        try {
            File tempFile = File.createTempFile("project_vocab", ".txt");
            tempFile.deleteOnExit();

            List<String> basicVocab = Arrays.asList(
                    "project", "file", "directory", "class", "interface",
                    "method", "function", "variable", "import", "package",
                    "public", "private", "protected", "static", "final",
                    "controller", "service", "repository", "component", "configuration",
                    "database", "model", "view", "template", "style",
                    "javascript", "typescript", "java", "spring", "web",
                    "api", "rest", "security", "test", "build");

            FileUtils.writeLines(tempFile, basicVocab);

            TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
            tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

            word2Vec = new Word2Vec.Builder()
                    .minWordFrequency(1)
                    .iterations(5)
                    .layerSize(EMBEDDING_SIZE)
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

    // [6] 프로젝트 구조 로딩 메서드 - 파일 탐색 및 임베딩 생성 [참조번호: 8,9]
    public String loadProjectStructure() {
        StringBuilder context = new StringBuilder("프로젝트 구조:\n");
        Path projectRoot = Paths.get("").toAbsolutePath();

        try (Stream<Path> paths = Files.walk(projectRoot)) {
            paths.filter(Files::isRegularFile)
                    .filter(this::isRelevantFile)
                    .forEach(path -> {
                        appendFileInfo(path, context, projectRoot);
                        generateFileEmbedding(path);
                    });
        } catch (IOException e) {
            return "프로젝트 구조 로딩 실패: " + e.getMessage();
        }

        return context.toString();
    }

    // [7] 파일 임베딩 생성 메서드 - 파일 내용 분석 및 벡터화 [참조번호: 10,11]
    private void generateFileEmbedding(Path path) {
        try {
            String content = Files.readString(path);
            String fileName = path.getFileName().toString();

            fileContents.put(fileName, content);

            StringBuilder fileDesc = new StringBuilder();
            fileDesc.append(fileName).append(" ");
            fileDesc.append(getFileType(fileName)).append(" ");
            fileDesc.append(extractKeywords(content));

            INDArray embedding = createEmbedding(fileDesc.toString());
            fileEmbeddings.put(fileName, embedding);
        } catch (IOException e) {
            throw new RuntimeException("File embedding generation failed: " + e.getMessage());
        }
    }

    // [8] 파일 타입 추출 메서드 [참조번호: 12]
    private String getFileType(String fileName) {
        return RELEVANT_EXT.stream()
                .filter(fileName::endsWith)
                .findFirst()
                .orElse("unknown")
                .substring(1);
    }

    // [9] 키워드 추출 메서드 - 파일 내용에서 주요 키워드 추출 [참조번호: 13]
    private String extractKeywords(String content) {
        return Arrays.stream(content.split("\\s+"))
                .filter(word -> word.length() > 3)
                .limit(100)
                .reduce("", (a, b) -> a + " " + b);
    }

    // [10] 임베딩 생성 메서드 - 텍스트를 벡터로 변환 [참조번호: 14]
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

    // [11] 유사 파일 검색 메서드 [참조번호: 15]
    public List<String> findSimilarFiles(String query) {
        INDArray queryEmbedding = createEmbedding(query);
        return findSimilarItems(queryEmbedding);
    }

    // [12] 유사도 기반 파일 검색 메서드 [참조번호: 16]
    private List<String> findSimilarItems(INDArray queryEmbedding) {
        List<Map.Entry<String, Double>> similarities = new ArrayList<>();

        for (Map.Entry<String, INDArray> entry : fileEmbeddings.entrySet()) {
            double similarity = cosineSimilarity(queryEmbedding, entry.getValue());
            similarities.add(new AbstractMap.SimpleEntry<>(entry.getKey(), similarity));
        }

        similarities.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return similarities.stream()
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();
    }

    // [13] 코사인 유사도 계산 메서드 [참조번호: 17]
    private double cosineSimilarity(INDArray vec1, INDArray vec2) {
        double dotProduct = vec1.mul(vec2).sumNumber().doubleValue();
        double norm1 = vec1.norm2Number().doubleValue();
        double norm2 = vec2.norm2Number().doubleValue();
        return dotProduct / (norm1 * norm2);
    }

    // [14] 관련 파일 필터링 메서드 [참조번호: 18]
    private boolean isRelevantFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return RELEVANT_EXT.stream()
                .anyMatch(fileName::endsWith);
    }

    // [15] 파일 정보 추가 메서드 [참조번호: 19]
    private void appendFileInfo(Path path, StringBuilder context, Path root) {
        String relativePath = root.relativize(path).toString();
        context.append("\n파일: ").append(relativePath);

        if (shouldIncludeContent(path)) {
            appendFileContent(path, context);
        }
    }

    // [16] 파일 내용 추가 메서드 [참조번호: 20]
    private void appendFileContent(Path path, StringBuilder context) {
        try {
            String content = Files.readString(path);
            context.append("\n내용:\n").append(content).append("\n");
        } catch (IOException e) {
            context.append("\n파일 읽기 실패: ").append(e.getMessage());
        }
    }

    // [17] 파일 내용 포함 여부 확인 메서드 [참조번호: 21]
    private boolean shouldIncludeContent(Path path) {
        String filename = path.getFileName().toString().toLowerCase();
        return filename.contains(CONFIG_FILE_PREFIX) ||
                CONFIG_FILES.stream().anyMatch(filename::endsWith);
    }

    // [18] 파일 내용 조회 메서드 [참조번호: 22]
    public String getFileContent(String fileName) {
        return fileContents.getOrDefault(fileName, "File not found");
    }

    // [19] 파일 임베딩 조회 메서드 [참조번호: 23]
    public Map<String, INDArray> getFileEmbeddings() {
        return Collections.unmodifiableMap(fileEmbeddings);
    }
}
