package com.example.demo.PHG_DeepSeek.AgentParts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.stereotype.Component;

@Component
public class DRLAIntegrator {
    // DQN 및 학습 관련 파라미터
    private static final double DISCOUNT_FACTOR = 0.95; // 미래 보상에 대한 할인율
    private static final double EXPLORATION_RATE = 0.2; // 탐험 비율 (epsilon-greedy 정책)
    private static final int REPLAY_BUFFER_CAPACITY = 1000; // 경험 재생 버퍼의 최대 용량
    private static final int BATCH_SIZE = 32; // 미니배치 크기
    private static final int TARGET_UPDATE_FREQUENCY = 100; // 타깃 네트워크 업데이트 주기
    private static final int INPUT_SIZE = 10; // 입력 상태의 차원
    private static final int OUTPUT_SIZE = 4; // 출력 행동의 차원

    private final Random random = new Random(); // 무작위 선택을 위한 랜덤 객체
    private int trainingStepCounter = 0; // 학습 단계 카운터

    // 경험 재생 버퍼
    private final List<Transition> replayBuffer = new ArrayList<>();
    // 세션별 임시 전이 저장 (refinePlan에서 생성된 전이를 updatePolicy에서 사용)
    private final Map<String, Transition> transitionMap = new HashMap<>();

    // DQN 네트워크와 타깃 네트워크
    private MultiLayerNetwork dqn;
    private MultiLayerNetwork targetDqn;

    // [1] DRLAIntegrator 생성자: DQN 네트워크 초기화 [참조번호: 2]
    public DRLAIntegrator() {
        initializeNetworks();
    }

    // [2] DQN 네트워크 초기화 [참조번호: 1]
    private void initializeNetworks() {
        try {
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                    .seed(123) // 재현성을 위한 시드 설정
                    .weightInit(WeightInit.XAVIER) // Xavier 초기화
                    .updater(new Nesterovs(0.01, 0.9)) // Nesterov Momentum 옵티마이저
                    .list()
                    .layer(0, new DenseLayer.Builder()
                            .nIn(INPUT_SIZE)
                            .nOut(16)
                            .activation(Activation.RELU)
                            .build())
                    .layer(1, new DenseLayer.Builder()
                            .nIn(16)
                            .nOut(16)
                            .activation(Activation.RELU)
                            .build())
                    .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                            .activation(Activation.IDENTITY)
                            .nIn(16)
                            .nOut(OUTPUT_SIZE)
                            .build())
                    .build();

            dqn = new MultiLayerNetwork(conf);
            dqn.init();
            dqn.setListeners(new ScoreIterationListener(10)); // 학습 과정 모니터링

            // 타깃 네트워크 초기화
            targetDqn = new MultiLayerNetwork(conf);
            targetDqn.init();
            targetDqn.setParams(dqn.params()); // 파라미터 복사
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize DQN networks: " + e.getMessage(), e);
        }
    }

    // 전이를 저장하기 위한 내부 클래스 (reward는 updatePolicy에서 채워짐)
    private class Transition {
        INDArray state;
        int action;
        double reward;
        INDArray nextState;

        Transition(INDArray state, int action, INDArray nextState) {
            this.state = state;
            this.action = action;
            this.nextState = nextState;
            this.reward = 0.0;
        }
    }

    // [3] 주어진 plan과 userInput으로 상태를 생성한 후, DQN을 통해 행동을 선택하여 계획을 개선 [참조번호: 4, 9]
    public String refinePlan(String plan, String userInput, String sessionId) {
        try {
            double[] stateArray = createState(userInput, plan);
            INDArray state = Nd4j.create(stateArray).reshape(1, INPUT_SIZE); // 배치 차원 추가
            int action = selectAction(state);
            String refinedPlan = applyRefinement(plan, action);
            double[] nextStateArray = createState(userInput, refinedPlan);
            INDArray nextState = Nd4j.create(nextStateArray).reshape(1, INPUT_SIZE); // 배치 차원 추가

            // 상태와 다음 상태를 복사하여 저장 (메모리 누수 방지)
            Transition trans = new Transition(
                    state.dup(),
                    action,
                    nextState.dup());
            transitionMap.put(sessionId, trans);
            return refinedPlan;
        } catch (Exception e) {
            // 오류 발생 시 원본 계획 반환
            System.err.println("Error in refinePlan: " + e.getMessage());
            return plan;
        }
    }

    // [4] epsilon-greedy 정책에 따라 행동을 선택 [참조번호: 3]
    private int selectAction(INDArray state) {
        try {
            if (random.nextDouble() < EXPLORATION_RATE) {
                return random.nextInt(OUTPUT_SIZE);
            }
            INDArray qValues = dqn.output(state);
            return Nd4j.argMax(qValues, 1).getInt(0);
        } catch (Exception e) {
            System.err.println("Error in selectAction: " + e.getMessage());
            return 0; // 기본 액션 반환
        }
    }

    // [5] 외부에서 보상(reward)이 계산되면 해당 전이를 replayBuffer에 추가하고, 미니배치 학습을 수행 [참조번호: 6, 7]
    public void updatePolicy(String sessionId, double reward) {
        Transition trans = transitionMap.remove(sessionId);
        if (trans != null) {
            trans.reward = reward;
            addTransition(trans);
            trainOnMiniBatch();
        }
    }

    // [6] 경험 재생 버퍼에 전이를 추가 (용량 초과 시 가장 오래된 전이 제거) [참조번호: 5]
    private void addTransition(Transition trans) {
        if (replayBuffer.size() >= REPLAY_BUFFER_CAPACITY) {
            replayBuffer.remove(0);
        }
        replayBuffer.add(trans);
    }

    // [7] 미니배치 샘플링 (무작위 추출) [참조번호: 5]
    private List<Transition> sampleMiniBatch() {
        List<Transition> miniBatch = new ArrayList<>();
        int size = replayBuffer.size();
        for (int i = 0; i < BATCH_SIZE; i++) {
            int index = random.nextInt(size);
            miniBatch.add(replayBuffer.get(index));
        }
        return miniBatch;
    }

    // [8] 미니배치 학습: 샘플된 전이들을 사용해 DQN을 업데이트하고, 일정 주기마다 타깃 네트워크를 업데이트 [참조번호: 5, 10]
    private void trainOnMiniBatch() {
        if (replayBuffer.size() < BATCH_SIZE) {
            return;
        }
        List<Transition> miniBatch = sampleMiniBatch();
        INDArray stateBatch = Nd4j.create(BATCH_SIZE, 10);
        INDArray targetBatch = Nd4j.create(BATCH_SIZE, 4);

        for (int i = 0; i < BATCH_SIZE; i++) {
            Transition t = miniBatch.get(i);
            INDArray currentQ = dqn.output(t.state, false);
            double maxNextQ = targetDqn.output(t.nextState, false).maxNumber().doubleValue();
            double targetValue = t.reward + DISCOUNT_FACTOR * maxNextQ;
            INDArray target = currentQ.dup();
            target.putScalar(t.action, targetValue);

            stateBatch.putRow(i, t.state);
            targetBatch.putRow(i, target);
        }
        dqn.fit(stateBatch, targetBatch);
        trainingStepCounter++;
        if (trainingStepCounter % TARGET_UPDATE_FREQUENCY == 0) {
            updateTargetNetwork();
        }
    }

    // [9] 타깃 네트워크를 현재 DQN 네트워크의 파라미터로 업데이트 [참조번호: 8]
    private void updateTargetNetwork() {
        targetDqn.setParams(dqn.params());
    }

    // [10] 상태 생성: 사용자 입력과 계획(plan)에서 10차원 벡터 생성 (기존 특징 추출 방식 유지) [참조번호: 3]
    private double[] createState(String input, String plan) {
        double[] state = new double[10];
        state[0] = normalizeLength(input);
        state[1] = calculateQuestionComplexity(input);
        state[2] = hasSpecialPunctuation(input);
        state[3] = normalizeLength(plan);
        state[4] = calculatePlanComplexity(plan);
        state[5] = calculateTechnicalLevel(input);
        state[6] = calculateSentimentScore(input);
        state[7] = 0.0;
        state[8] = 0.0;
        state[9] = 0.0;
        return state;
    }

    // [11] 텍스트 길이 정규화 (최대 1.0) [참조번호: 10]
    private double normalizeLength(String text) {
        return Math.min(text.length() / 1000.0, 1.0);
    }

    // [12] 질문 복잡성: 단어 수 기반 [참조번호: 10]
    private double calculateQuestionComplexity(String input) {
        String[] words = input.split("\\s+");
        return Math.min(words.length / 10.0, 1.0);
    }

    // [13] 특수 구두점 포함 여부 [참조번호: 10]
    private double hasSpecialPunctuation(String input) {
        return (input.contains("?") || input.contains("!")) ? 1.0 : 0.0;
    }

    // [14] 계획 복잡성: 줄 수 기반 [참조번호: 10]
    private double calculatePlanComplexity(String plan) {
        String[] lines = plan.split("\n");
        return Math.min(lines.length / 5.0, 1.0);
    }

    // [15] 기술적 용어 포함 여부 평가 (예시) [참조번호: 10]
    private double calculateTechnicalLevel(String input) {
        String[] technicalTerms = { "algorithm", "system", "architecture", "machine learning", "framework" };
        long technicalTermCount = Arrays.stream(technicalTerms)
                .filter(input.toLowerCase()::contains)
                .count();
        return Math.min(technicalTermCount / 3.0, 1.0);
    }

    // [16] 감성 점수 계산 (긍정/부정 단어 기반) [참조번호: 10]
    private double calculateSentimentScore(String input) {
        String[] positiveWords = { "good", "great", "excellent", "help", "solve" };
        String[] negativeWords = { "problem", "issue", "difficult", "complex" };
        long positiveCount = Arrays.stream(positiveWords)
                .filter(input.toLowerCase()::contains)
                .count();
        long negativeCount = Arrays.stream(negativeWords)
                .filter(input.toLowerCase()::contains)
                .count();
        return (positiveCount - negativeCount + 1.0) / 2.0;
    }

    // [17] 행동에 따른 계획(refined plan) 개선 [참조번호: 3]
    private String applyRefinement(String plan, int action) {
        return switch (action) {
            case 0 -> addDetail(plan);
            case 1 -> simplify(plan);
            case 2 -> addExamples(plan);
            case 3 -> addTechnical(plan);
            default -> plan;
        };
    }

    // [18] 계획에 세부 정보 추가 [참조번호: 17]
    private String addDetail(String plan) {
        return plan + "\n[상세 설명 추가]";
    }

    // [19] 계획 간소화 [참조번호: 17]
    private String simplify(String plan) {
        return plan + "\n[단순화된 설명]";
    }

    // [20] 계획에 예시 추가 [참조번호: 17]
    private String addExamples(String plan) {
        return plan + "\n[예시 추가]";
    }

    // [21] 계획에 기술적 정보 추가 [참조번호: 17]
    private String addTechnical(String plan) {
        return plan + "\n[기술적 상세 정보]";
    }

    // [22] 리워드 계산: 응답의 길이, 키워드 매칭, 관련성을 종합하여 보상 산출 [참조번호: 5]
    public double computeReward(String input, String response) {
        double lengthScore = Math.min(response.length() / 500.0, 1.0);
        double keywordScore = calculateKeywordScore(input, response);
        double relevanceScore = calculateRelevanceScore(input, response);
        return (lengthScore + keywordScore + relevanceScore) / 3.0;
    }

    // [23] 키워드 점수 계산 [참조번호: 22]
    private double calculateKeywordScore(String input, String response) {
        String[] keywords = input.toLowerCase().split("\\s+");
        int matches = 0;
        for (String keyword : keywords) {
            if (response.toLowerCase().contains(keyword)) {
                matches++;
            }
        }
        return matches / (double) Math.max(keywords.length, 1);
    }

    // [24] 관련성 점수 계산 [참조번호: 22]
    private double calculateRelevanceScore(String input, String response) {
        int commonWords = countCommonWords(input, response);
        return Math.min(commonWords / 10.0, 1.0);
    }

    // [25] 공통 단어 수 계산 [참조번호: 24]
    private int countCommonWords(String input, String response) {
        Set<String> inputWords = Arrays.stream(input.toLowerCase().split("\\s+"))
                .collect(Collectors.toSet());
        Set<String> responseWords = Arrays.stream(response.toLowerCase().split("\\s+"))
                .collect(Collectors.toSet());
        inputWords.retainAll(responseWords);
        return inputWords.size();
    }
}
