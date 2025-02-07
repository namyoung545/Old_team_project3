import os
import pandas as pd
import numpy as np
from sqlalchemy import create_engine
import pymysql

# MySQL 연결 설정
DB_USER = "root"  # MySQL 사용자명 (본인 환경에 맞게 변경)
DB_PASSWORD = "1234"  # MySQL 비밀번호
DB_HOST = "192.168.30.89"  # MySQL 호스트 (로컬 실행 시 "localhost")
DB_PORT = "3306"  # MySQL 포트 (기본값 3306)
DB_NAME = "old_team_project3"  # 사용할 데이터베이스명

# MySQL 연결 엔진 생성
engine = create_engine(f"mysql+pymysql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}")

# 현재 실행 디렉토리 확인
BASE_DIR = os.getcwd()
print("현재 실행 디렉토리:", BASE_DIR)

# 데이터 파일 경로 (절대경로 → 상대경로)
DATA_PATH = os.path.join(BASE_DIR, ".vscode", "dy_AiData", "electric_ai_data.csv")

# 데이터 로드
if not os.path.exists(DATA_PATH):
    print(f"❌ 데이터 파일이 없습니다: {DATA_PATH}")
    exit()

data = pd.read_csv(DATA_PATH)

# 위험 점수 계산 및 정규화
data["risk_score"] = data["total_incidents"] * data["total_damage"]
data["log_risk_score"] = np.log1p(data["risk_score"])

# SHAP 분석 결과를 저장할 데이터프레임 생성
shap_df = pd.DataFrame({
    "Region": data["region_province"],
    "Cause": data["cause_subcategory"],
    "Reduction (%)": np.random.uniform(0, 100, len(data))  # 샘플 데이터 (랜덤값)
})

# MySQL 테이블에 데이터 저장
shap_df.to_sql("dy_risk_analysis", con=engine, if_exists="replace", index=False)

print("✅ MySQL 데이터베이스에 shap_df 저장 완료.")
