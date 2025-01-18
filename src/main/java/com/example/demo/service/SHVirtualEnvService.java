package com.example.demo.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

@Service
public class SHVirtualEnvService {

    private static final String VENV_PATH = "src/main/python/venv";
    private static final String REQUIREMENTS_FILE = "src/main/python/requirements.txt";

    // 파이썬 가상환경 시작점
    public void setupVirtualEnv() {
        createVirtualEnv();
        activateVirtualEnv();
        installDependencies();
    }

    // 가상환경 생성
    public void createVirtualEnv() {
        if (new File(VENV_PATH).exists()) {
            System.out.println("가상환경이 이미 존재합니다.");
            return;
        }

        System.out.println("가상환경이 존재하지 않습니다. 새로 생성합니다...");
        try {
            Process process = Runtime.getRuntime().exec("python -m venv " + VENV_PATH);
            printProcessOutput(process);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("가상환경 생성 완료!");
            } else {
                System.err.println("가상환경 생성 중 오류 발생. exitCode=" + exitCode);
                System.exit(1);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // 가상환경 활성화 (경로 설정)
    public void activateVirtualEnv() {
        if (System.getenv("VIRTUAL_ENV") != null) {
            System.out.println("가상환경이 이미 활성화되어 있습니다.");
            return;
        }

        String activateScript = VENV_PATH + (isWindows() ? "/Scripts/activate" : "/bin/activate");
        if (!new File(activateScript).exists()) {
            System.err.println("가상환경 활성화 스크립트를 찾을 수 없습니다.");
            System.exit(1);
        }

        System.setProperty("VIRTUAL_ENV", new File(VENV_PATH).getAbsolutePath());
        System.out.println("가상환경 활성화 완료!");
    }

    // 의존성 설치
    public void installDependencies() {
        File requirements = new File(REQUIREMENTS_FILE);
        if (!requirements.exists() || requirements.length() == 0) {
            System.out.println("requirements.txt 파일이 없거나 비어 있습니다. 의존성 설치를 건너뜁니다.");
            return;
        }

        System.out.println("의존성을 설치합니다...");
        try {
            String pipCommand = getPythonExecutablePath() + " -m pip install --upgrade pip";
            Process pipUpgrade = Runtime.getRuntime().exec(pipCommand);
            pipUpgrade.waitFor();

            String installCommand = getPythonExecutablePath() + " -m pip install -r " + REQUIREMENTS_FILE;
            Process installProcess = Runtime.getRuntime().exec(installCommand);
            printProcessOutput(installProcess);

            int exitCode = installProcess.waitFor();
            if (exitCode == 0) {
                System.out.println("의존성 설치 완료!");
            } else {
                System.err.println("의존성 설치 중 오류 발생. exitCode=" + exitCode);
                System.exit(1);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void printProcessOutput(Process process) throws IOException {
        // 프로세스 출력 처리
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }
        }
    }

    private String getPythonExecutablePath() {
        // Python 실행 파일 경로
        String pythonPath = isWindows() ? "Scripts/python" : "bin/python";
        return VENV_PATH + "/" + pythonPath;
    }

    private boolean isWindows() {
        // Windows 확인
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    // public void runPythonApp() {
    //     // Python 앱 실행
    //     try {
    //         String appCommand = getPythonExecutablePath() + " src/main/python/api/api_handler.py";
    //         Process process = Runtime.getRuntime().exec(appCommand);
    //         printProcessOutput(process);
    //         process.waitFor();
    //     } catch (IOException | InterruptedException e) {
    //         e.printStackTrace();
    //         System.exit(1);
    //     }
    // }

    // private static final String VENV_PATH = "src/main/python/venv";
    // // private static final String VENV_ACTIVATE_SCRIPT = VENV_PATH +
    // "/bin/activate";
    // private static final String REQUIREMENTS_PATH =
    // "src/main/python/requirements.txt";

    // // 가상환경 시작점
    // public void setupVirtualEnv() {
    // if (!isVirtualEnvPresent()) {
    // createVirtualEnv();
    // } else {
    // System.out.println("가상환경이 이미 설정되어 있습니다.");
    // }
    // }

    // // 가상환경 확인
    // private boolean isVirtualEnvPresent() {
    // File venvDir = new File(VENV_PATH);
    // return venvDir.exists();
    // }

    // // 실행환경 구분 (윈도우 / 리눅스)
    // private String getPythonCommand() {
    // String os = System.getProperty("os.name").toLowerCase();
    // return os.contains("win") ? "python" : "python3";
    // }

    // // 가상환경 생성
    // private void createVirtualEnv() {
    // try {
    // System.out.println("가상환경을 새로 생성합니다.");
    // String pythonCommand = getPythonCommand();
    // Process process = Runtime.getRuntime().exec(pythonCommand + " -m venv " +
    // VENV_PATH);
    // int exitCode = process.waitFor();
    // if (exitCode == 0) {
    // System.out.println("가상환경이 생성되었습니다.");
    // installPackages();
    // } else {
    // System.err.println("가상환경 생성에 실패하였습니다. exitCode=" + exitCode);
    // }
    // } catch (IOException | InterruptedException e) {
    // e.printStackTrace();
    // }
    // }

    // // 의존성 설치
    // private void installPackages() {
    // try {
    // System.out.println("필요한 패키지를 설치합니다.");
    // String pythonCommand = getPythonCommand();
    // Process process = Runtime.getRuntime()
    // .exec(VENV_PATH + "/Script/" + pythonCommand + " -m pip install -r " +
    // REQUIREMENTS_PATH);
    // printProcessOutput(process);
    // int exitCode = process.waitFor();
    // if (exitCode == 0) {
    // System.out.println("패키지 설치가 완료되었습니다.");
    // } else {
    // System.err.println("패키지 설치에 실패하였습니다. exitCode=" + exitCode);
    // }
    // } catch (IOException | InterruptedException e) {
    // e.printStackTrace();
    // }
    // }

    // // 의존성 설치 과정
    // private void printProcessOutput(Process process) throws IOException {
    // try (
    // BufferedReader reader = new BufferedReader(
    // new InputStreamReader(process.getInputStream()));
    // BufferedReader errorReader = new BufferedReader(
    // new InputStreamReader(process.getErrorStream()))) {

    // String line;
    // while ((line = reader.readLine()) != null) {
    // System.out.println(line);
    // }
    // while ((line = errorReader.readLine()) != null) {
    // System.err.println(line);
    // }

    // }
    // }
}
