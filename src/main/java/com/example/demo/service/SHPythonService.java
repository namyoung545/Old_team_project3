package com.example.demo.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.EDStatisticsData;
import com.example.demo.entity.EDStatisticsJsonData;
import com.example.demo.repository.EDStatisticsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SHPythonService {

    @Autowired
    private EDStatisticsRepository edStatisticsRepository;

    // 가상환경 Python 경로
    private static final String VENV_PATH = "src/main/python/venv";

    // 테스트 파일실행
    public String executePythonScript() {
        try {
            // Python 스크립트를 실행합니다.
            Process process = Runtime.getRuntime().exec("python3 src/main/python/api/api_handler.py");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Python 스크립트 실행 중 오류 발생!";
        }
    }

    public boolean checkEDStatistics() {
        String dataPath = "src/main/python/data/processed/ed_statistics";
        File folder = new File(dataPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("DATA 경로를 확인해주세요.");
            return false;
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("확인할 파일이 존재하지 않습니다.");
            return false;
        }

        for (File file : files) {
            if (!file.isFile() || !file.getName().endsWith(".csv")) {
                continue;
            }

            String fileNameWithoutExtension = file.getName().replace(".csv", "");

            if (edStatisticsRepository.existsByYear(fileNameWithoutExtension)) {
                System.out.println("DB에 데이터가 존재합니다." + fileNameWithoutExtension);
                continue;
            }

            String jsonResult = callEDStatistics("analyze_statistics", fileNameWithoutExtension);

            if (jsonResult != null) {
                // ObjectMapper objectMapper = new ObjectMapper();
                // EDStatisticsJsonData jsonData = objectMapper.readValue(jsonResult, EDStatisticsJsonData.class);

                // saveJsonData(jsonResult);

                // edStatisticsRepository.save(edStatisticsData);
                System.out.println("새로운 데이터가 입력되었습니다. " + fileNameWithoutExtension);
            } else {
                System.err.println("데이터 처리중 오류 발생 : " + fileNameWithoutExtension);
            }
        }

        return true;
    }

    public void saveJsonData(String jsonResult) {
        System.out.println(jsonResult);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            EDStatisticsJsonData jsonData = objectMapper.readValue(jsonResult, EDStatisticsJsonData.class);
            
            // DB에 저장하려면 EDStatisticsData로 변환하여 저장
            EDStatisticsData edStatisticsData = new EDStatisticsData();
            edStatisticsData.setYear(jsonData.getYear());
            // edStatisticsData.setProvince(jsonData.getProvince().toString());  // 필요에 따라 String으로 변환
            // edStatisticsData.setDistrict(jsonData.getDistrict().toString());
            // edStatisticsData.setFire_type(jsonData.getFire_type().toString());
            // edStatisticsData.setHeat_source_main(jsonData.getHeat_source_main().toString());
            // edStatisticsData.setHeat_source_sub(jsonData.getHeat_source_sub().toString());
            // edStatisticsData.setCause_main(jsonData.getCause_main().toString());
            // edStatisticsData.setCause_sub(jsonData.getCause_sub().toString());
            // edStatisticsData.setIgnition_material_main(jsonData.getIgnition_material_main().toString());
            // edStatisticsData.setIgnition_material_sub(jsonData.getIgnition_material_sub().toString());
            // edStatisticsData.setCasualties_total(jsonData.getCasualties_total().toString());
            // edStatisticsData.setDeaths(jsonData.getDeaths().toString());
            // edStatisticsData.setInjuries(jsonData.getInjuries().toString());
            // edStatisticsData.setTotal_property_damage(jsonData.getTotal_property_damage().toString());
            // edStatisticsData.setLocation_main(jsonData.getLocation_counts().toString());
            // edStatisticsData.setLocation_mid(jsonData.getLocation_mid().toString());
            // edStatisticsData.setLocation_sub(jsonData.getLocation_sub().toString());

            // 데이터베이스에 저장
            edStatisticsRepository.save(edStatisticsData);
        } catch (Exception e) {
            System.err.println("JSON 처리 중 오류 발생: " + e.getMessage());
        }
    }

    // 전기재해 통계 데이터 파이썬 함수 호출
    public String callEDStatistics(String functionName, String... args) {
        System.out.println("callEDStatistics");
        String pythonScript = "src/main/python/data/ed_statistics.py";

        if (!new File(pythonScript).exists()) {
            return "Error: Python script not found at " + pythonScript;
        }

        String pythonExecutable = getPythonExecutablePath();
        // String command = pythonExecutable + " " + pythonScript;
        StringBuilder commandBuilder = new StringBuilder(pythonExecutable)
                .append(" ").append(pythonScript)
                .append(" ").append(functionName);
        for (String arg : args) {
            commandBuilder.append(" ").append(arg);
        }

        try {
            Process process = Runtime.getRuntime().exec(commandBuilder.toString());
            String output = getProcessOutput(process);

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return "Script executed successfully!\n" + output;
            } else {
                return "Error: Script execution failed with exit code " + exitCode + ".\n" + output;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error: Exception occurred while executing script.\n" + e.getMessage();
        }
    }

    // 파이썬 경로 설정
    private String getPythonExecutablePath() {
        String pythonPath = isWindows() ? "Scripts/python" : "bin/python";
        return VENV_PATH + "/" + pythonPath;
    }

    // 실행환경 확인
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    // 실행 로그 출력
    private String getProcessOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            while ((line = errorReader.readLine()) != null) {
                output.append("[ERROR] ").append(line).append("\n");
            }
        }

        return output.toString();
    }
}
