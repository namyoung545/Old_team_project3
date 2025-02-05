import os
import pandas as pd
import numpy as np
import mysql.connector
import shap
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import RobustScaler
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score
from xgboost import XGBRegressor

# ✅ MySQL 데이터베이스 설정
MYSQL_CONFIG = {
    "host": "192.168.30.89",
    "user": "root",
    "password": "1234",
    "database": "old_team_project3",
    "port": 3306
}

# ✅ MySQL 연결
conn = mysql.connector.connect(**MYSQL_CONFIG)
cursor = conn.cursor()

# ✅ 기존 테이블 삭제 후 생성
cursor.execute("DROP TABLE IF EXISTS dy_aiRisk")
cursor.execute("""
    CREATE TABLE dy_aiRisk (
        id INT AUTO_INCREMENT PRIMARY KEY,
        Region VARCHAR(255),
        Cause VARCHAR(255),
        RiskBefore FLOAT,
        RiskAfter FLOAT,
        ReductionPercentage FLOAT
    )
""")
conn.commit()
print("✅ 기존 테이블 삭제 후 새롭게 생성 완료")

# ✅ 데이터 로드
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DATA_PATH = os.path.join(BASE_DIR, "..", "dy_AiData", "electric_ai_data.csv")
data = pd.read_csv(DATA_PATH)

# ✅ 로그 변환 및 표준화
scaler = RobustScaler()
data["log_total_incidents"] = np.log1p(data["total_incidents"])
data["log_total_damage"] = np.log1p(data["total_damage"])
data[["z_log_total_incidents", "z_log_total_damage"]] = scaler.fit_transform(
    data[["log_total_incidents", "log_total_damage"]]
)
print("✅ 표준화 전후 데이터 확인:")
print(data[["total_incidents", "log_total_incidents", "z_log_total_incidents"]].head())

# ✅ 원-핫 인코딩 적용
data = pd.get_dummies(data, columns=["cause_subcategory"])

# ✅ XGBoost 모델 학습
X = data[["z_log_total_incidents", "z_log_total_damage"] + [col for col in data.columns if "cause_subcategory_" in col]]
y = data["z_log_total_incidents"]
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

model = XGBRegressor(n_estimators=100, max_depth=5, learning_rate=0.1, random_state=42)
model.fit(X_train, y_train)

# ✅ 모델 성능 평가 추가
y_pred = model.predict(X_test)
mae = mean_absolute_error(y_test, y_pred)
mse = mean_squared_error(y_test, y_pred)
rmse = np.sqrt(mse)
r2 = r2_score(y_test, y_pred)

print(f"✅ Mean Absolute Error (MAE): {mae:.4f}")
print(f"✅ Mean Squared Error (MSE): {mse:.4f}")
print(f"✅ Root Mean Squared Error (RMSE): {rmse:.4f}")
print(f"✅ R² Score: {r2:.4f}")

# ✅ 성능 평가 결과를 MySQL 테이블에 저장
cursor.execute("DROP TABLE IF EXISTS dy_modelPerformance")
cursor.execute("""
    CREATE TABLE dy_modelPerformance (
        id INT AUTO_INCREMENT PRIMARY KEY,
        MAE FLOAT,
        MSE FLOAT,
        RMSE FLOAT,
        R2_Score FLOAT
    )
""")
conn.commit()

cursor.execute("""
    INSERT INTO dy_modelPerformance (MAE, MSE, RMSE, R2_Score)
    VALUES (%s, %s, %s, %s)
""", (float(mae), float(mse), float(rmse), float(r2)))

conn.commit()

# ✅ SHAP 분석
explainer = shap.Explainer(model)
shap_values = explainer(X_test)

# ✅ 위험 점수 계산
shap_results = []
regions = data["region_province"].unique()
cause_columns = [col for col in data.columns if "cause_subcategory_" in col]

for region in regions:
    region_data = data[data["region_province"] == region]
    risk_before = float(model.predict(region_data[X.columns]).mean())  # 변환 추가
    risk_after = risk_before * 0.5  # 임시 감소율 적용 (추후 개선 가능)
    reduction_percentage = ((risk_before - risk_after) / risk_before) * 100
    
    shap_results.append({
        "Region": region,
        "Cause": "ALL",
        "RiskBefore": risk_before,
        "RiskAfter": risk_after,
        "ReductionPercentage": reduction_percentage
    })
    
    cursor.execute("""
        INSERT INTO dy_aiRisk (Region, Cause, RiskBefore, RiskAfter, ReductionPercentage)
        VALUES (%s, %s, %s, %s, %s)
    """, (region, "ALL", float(risk_before), float(risk_after), float(reduction_percentage)))  # 변환 추가

conn.commit()
conn.close()

# ✅ 시각화 (위험 감소 비율)
shap_df = pd.DataFrame(shap_results)
plt.figure(figsize=(12, 6))
sns.histplot(shap_df["ReductionPercentage"], bins=30, kde=True, color='purple')
plt.title('Reduction Percentage Distribution')
plt.xlabel('Reduction Percentage (%)')
plt.show()

print("✅ 모델 성능 평가 및 데이터 저장 완료 (MySQL)")
