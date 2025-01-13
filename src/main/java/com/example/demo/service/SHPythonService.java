package com.example.demo.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.EDStatisticsData;
import com.example.demo.entity.EDStatisticsJsonData;
import com.example.demo.entity.FireCauseData;
import com.example.demo.entity.FireItemData;
import com.example.demo.entity.FireLocationData;
import com.example.demo.entity.FiresData;
import com.example.demo.repository.EDStatisticsRepository;
import com.example.demo.repository.FireCauseDataRepository;
import com.example.demo.repository.FireItemDataRepository;
import com.example.demo.repository.FireLocationDataRepository;
import com.example.demo.repository.FiresDataRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SHPythonService {

    // 가상환경 Python 경로
    private static final String VENV_PATH = "src/main/python/venv";

    // 연간 화재 데이터 경로
    private static final String FireDataPath = "src/main/python/data/processed/fire_statistics";

    // Fires Data Repository
    @Autowired
    private FiresDataRepository firesDataRepository;
    @Autowired
    private FireCauseDataRepository fireCauseDataRepository;
    @Autowired
    private FireLocationDataRepository fireLocationDataRepository;
    @Autowired
    private FireItemDataRepository fireItemDataRepository;

    // ED DB Repository
    @Autowired
    private EDStatisticsRepository edStatisticsRepository;

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

    // Fires Data 확인
    public boolean checkFiresData() {
        File[] files = checkFiles(FireDataPath);
        for (File file : files) {
            if (!file.isFile() || !file.getName().endsWith(".csv")) {
                continue;
            }

            String fileNameWithoutExtension = file.getName().replace(".csv", "");
            System.out.println(fileNameWithoutExtension);
            if (firesDataRepository.existsByYear(Integer.parseInt(fileNameWithoutExtension))) {
                System.out.println("DB에 데이터가 존재합니다." + fileNameWithoutExtension);
                continue;
            }

            String jsonResult = callFiresData("insert_fires_data", fileNameWithoutExtension);
            System.out.println("jsonResult" + jsonResult);
            if (jsonResult != null) {
                saveFiresData(jsonResult);
                System.out.println("새로운 데이터가 입력되었습니다. " + fileNameWithoutExtension);
            } else {
                System.out.println("데이터 처리중 오류 발생 : " + fileNameWithoutExtension);
            }
        }

        return true;
    }

    // Fires Data Python Script 실행
    public String callFiresData(String functionName, String... args) {
        System.out.println("callFiresData");
        String pythonScript = "src/main/python/data/fires_data.py";

        if (!new File(pythonScript).exists()) {
            return "[ERROR] Python script not found at " + pythonScript;
        }

        String pythonExecutable = getPythonExecutablePath();
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
                System.out.println("스크립트 실행 성공!");
                return output;
            } else {
                System.out.println("스크립트 실행 실패!");
                return "[ERROR] 스크립트 실행 실패";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "[ERROR] 스크립트 실행중 예외 발생!" + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "[ERROR] 스크립트 실행중 예외 발생!" + e.getMessage();
        }

    }

    // ED Statistic DB 확인
    public boolean checkEDStatistics() {
        String dataPath = "src/main/python/data/processed/fire_statistics";

        File[] files = checkFiles(dataPath);

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
                saveEDStatisticsJsonData(jsonResult);
                System.out.println("새로운 데이터가 입력되었습니다. " + fileNameWithoutExtension);
            } else {
                System.err.println("데이터 처리중 오류 발생 : " + fileNameWithoutExtension);
            }
        }

        return true;
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
            // System.out.println("commandBuilder : " + commandBuilder.toString());
            Process process = Runtime.getRuntime().exec(commandBuilder.toString());
            String output = getProcessOutput(process);
            // System.out.println("Output : " + output);

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Script executed successfully!");
                return output;
            } else {
                return "Error: Script execution failed with exit code " + exitCode + ".\n" + output;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error: Exception occurred while executing script.\n" + e.getMessage();
        }
    }

    // 화재 데이터 DB 저장
    public void saveFiresData(String jsonResult) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> firesDataList = objectMapper.readValue(jsonResult, new TypeReference<>() {
            });
            // List<Map<String, Object>> firesDataList = objectMapper.readValue(jsonResult,
            // List.class);

            List<FiresData> firesDataBatch = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm");

            for (Map<String, Object> fireDataMap : firesDataList) {
                FiresData firesData = new FiresData();

                // 일시 파싱
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(fireDataMap.get("dateTime").toString(), formatter);
                    firesData.setDateTime(dateTime);
                } catch (DateTimeParseException e) {
                    System.out.println("[ERROR] 날짜 형식 파싱 오류 : " + fireDataMap.get("dateTime").toString());
                    continue;
                }
                firesData.setRegionProvince(fireDataMap.get("regionProvince").toString());
                firesData.setRegionCity(fireDataMap.get("regionCity").toString());
                firesData.setFireType(fireDataMap.get("fireType").toString());
                firesData.setDamageProperty(Integer.parseInt(fireDataMap.get("damageProperty").toString()));
                firesData.setDeaths(Integer.parseInt(fireDataMap.get("deaths").toString()));
                firesData.setInjuries(Integer.parseInt(fireDataMap.get("injuries").toString()));

                Map<String, String> fireCausesData = (Map<String, String>) fireDataMap.get("fireCauses");
                Optional<FireCauseData> existingCause = fireCauseDataRepository
                        .findByDetails(
                                fireCausesData.get("causeCategory"),
                                fireCausesData.get("causeSubcategory"),
                                fireCausesData.get("ignitionSourceCategory"),
                                fireCausesData.get("ignitionSourceSubcategory"));
                FireCauseData fireCauseData;
                if (existingCause.isPresent()) {
                    fireCauseData = existingCause.get();
                } else {
                    fireCauseData = new FireCauseData();
                    fireCauseData.setCauseCategory(fireCausesData.get("causeCategory"));
                    fireCauseData.setCauseSubcategory(fireCausesData.get("causeSubcategory"));
                    fireCauseData.setIgnitionSourceCategory(fireCausesData.get("ignitionSourceCategory"));
                    fireCauseData.setIgnitionSourceSubcategory(fireCausesData.get("ignitionSourceSubcategory"));
                    fireCauseData = fireCauseDataRepository.save(fireCauseData);
                }
                firesData.setFireCause(fireCauseData);

                Map<String, String> fireLocationsData = (Map<String, String>) fireDataMap.get("fireLocations");
                Optional<FireLocationData> existingLocation = fireLocationDataRepository
                        .findByDetails(
                                fireLocationsData.get("locationMainCategory"),
                                fireLocationsData.get("locationSubCategory"),
                                fireLocationsData.get("locationDetail"));
                FireLocationData fireLocationData;
                if (existingLocation.isPresent()) {
                    fireLocationData = existingLocation.get();
                } else {
                    fireLocationData = new FireLocationData();
                    fireLocationData.setLocationMainCategory(fireLocationsData.get("locationMainCategory"));
                    fireLocationData.setLocationSubCategory(fireLocationsData.get("locationSubCategory"));
                    fireLocationData.setLocationDetail(fireLocationsData.get("locationDetail"));
                    fireLocationData = fireLocationDataRepository.save(fireLocationData);
                }
                firesData.setFireLocation(fireLocationData);

                Map<String, String> fireItemsData = (Map<String, String>) fireDataMap.get("fireItems");
                Optional<FireItemData> existingItem = fireItemDataRepository
                        .findByDetails(
                                fireItemsData.get("itemCategory"),
                                fireItemsData.get("itemDetail"));
                FireItemData fireItemData;
                if (existingItem.isPresent()) {
                    fireItemData = existingItem.get();
                } else {
                    fireItemData = new FireItemData();
                    fireItemData.setItemCategory(fireItemsData.get("itemCategory"));
                    fireItemData.setItemDetail(fireItemsData.get("itemDetail"));
                    fireItemData = fireItemDataRepository.save(fireItemData);
                }
                firesData.setFireItem(fireItemData);


                firesDataBatch.add(firesData);
            }

            if (!firesDataBatch.isEmpty()) {
                firesDataRepository.saveAll(firesDataBatch);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] 데이터 저장 중 오류 발생 : " + e.getMessage());
        }
    }

    // 전기재해 통계 데이터 DB 저장 JSON to DB
    public void saveEDStatisticsJsonData(String jsonResult) {
        // System.out.println(jsonResult);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            EDStatisticsJsonData jsonData = objectMapper.readValue(jsonResult, EDStatisticsJsonData.class);

            // DB에 저장하려면 EDStatisticsData로 변환하여 저장
            EDStatisticsData edStatisticsData = new EDStatisticsData();
            edStatisticsData.setYear(jsonData.getYear());
            edStatisticsData.setProvince(jsonData.getProvince().toString()); // 필요에 따라 String으로 변환
            edStatisticsData.setDistrict(jsonData.getDistrict().toString());
            edStatisticsData.setFire_type(jsonData.getFire_type().toString());
            edStatisticsData.setHeat_source_main(jsonData.getHeat_source_main().toString());
            edStatisticsData.setHeat_source_sub(jsonData.getHeat_source_sub().toString());
            edStatisticsData.setCause_main(jsonData.getCause_main().toString());
            edStatisticsData.setCause_sub(jsonData.getCause_sub().toString());
            edStatisticsData.setIgnition_material_main(jsonData.getIgnition_material_main().toString());
            edStatisticsData.setIgnition_material_sub(jsonData.getIgnition_material_sub().toString());
            edStatisticsData.setCasualties_total(jsonData.getCasualties_total().toString());
            edStatisticsData.setDeaths(jsonData.getDeaths().toString());
            edStatisticsData.setInjuries(jsonData.getInjuries().toString());
            edStatisticsData.setTotal_property_damage(jsonData.getTotal_property_damage().toString());
            edStatisticsData.setLocation_main(jsonData.getLocation_counts().toString());
            edStatisticsData.setLocation_mid(jsonData.getLocation_mid().toString());
            edStatisticsData.setLocation_sub(jsonData.getLocation_sub().toString());

            // 데이터베이스에 저장
            edStatisticsRepository.save(edStatisticsData);
        } catch (Exception e) {
            System.err.println("JSON 처리 중 오류 발생: " + e.getMessage());
        }
    }

    // 데이터 파일 확인
    private File[] checkFiles(String dataPath) {
        File[] files = new File[0];
        File folder = new File(dataPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("DATA 경로를 확인해주세요.");
            return files;
        }

        files = folder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("파일이 존재하지 않습니다.");
            return files;
        }

        return files;
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
