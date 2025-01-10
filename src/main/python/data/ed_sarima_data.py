import os
import sys
import json
import pandas as pd
from statsmodels.tsa.arima.model import ARIMA
import matplotlib.pyplot as plt
import seaborn as sns
from statsmodels.tsa.statespace.sarimax import SARIMAX

# from fbprophet import Prophet


def correlation_analysis(dataname):
    try:
        file_dir = os.path.dirname(__file__)
        file_path = os.path.join(
            file_dir, "processed", "fire_statistics", f"{dataname}.csv"
        )

        # 파일 존재 여부 확인
        if not os.path.exists(file_path):
            raise FileNotFoundError(f"파일을 찾을 수 없습니다: {file_path}")

        try:
            df = pd.read_csv(file_path, encoding='EUC-KR')
        except UnicodeDecodeError:
            df = pd.read_csv(file_path, encoding='utf-8')
        df.fillna("미확인", inplace=True)

        df["시간대"] = pd.to_datetime(df["일시"]).dt.hour
        df["날짜"] = pd.to_datetime(df["일시"]).dt.date
        df["요일"] = pd.to_datetime(df["일시"]).dt.dayofweek  # 월=0, 화=1, ...
        df["계절"] = df["시간대"].apply(
            lambda x: (
                "봄"
                if 3 <= x <= 5
                else "여름" if 6 <= x <= 8 else "가을" if 9 <= x <= 11 else "겨울"
            )
        )
        df["주말여부"] = df["요일"].apply(
            lambda x: 1 if x >= 5 else 0
        )  # 주말: 1, 평일: 0


        
        # Seaborn
        # hourly_data = df.groupby(["날짜", "시간대"]).size().unstack(fill_value=0)
        hourly_data = df.groupby(["날짜", "시간대"]).size().unstack(fill_value=0)
        sns.set_theme(font="NanumGothic")
        sns.heatmap(hourly_data, cmap="coolwarm")

        # # df = df[['시간대', '날짜', '요일', '계절', '주말여부', '발화요인대분류']].corr()
        # # sns.heatmap(annot = True, fmt='.2f', cbar = True, data=df)
        # exit()


        electric_cause_df = df[df["발화요인대분류"] == "전기적 요인"]
        # print(electric_cause_df)

        # 장소대분류별 평균 재산피해소계 계산
        # grouped = electric_cause_df.groupby('장소대분류')['재산피해소계'].mean()
        # print(grouped)

        # 시간대별 발생 건수 집계
        time_counts = electric_cause_df.groupby("시간대").size()

        # 시간대별 비율 확인
        time_probabilities = time_counts / time_counts.sum()
        # print(time_probabilities)

        # 시간대별 화재 발생 건수를 시계열 데이터로 준비
        time_series = time_counts.reindex(range(0, 24), fill_value=0)

        # # ARIMA 모델 학습
        # model = ARIMA(time_series, order=(1, 1, 1))  # ARIMA(p, d, q)
        # model_fit = model.fit()
        # forecast = model_fit.forecast(steps=24)

        # SARIMA
        sArimaModel = SARIMAX(
            time_series, order=(0, 1, 0), seasonal_order=(1, 1, 1, 24)
        )
        sArimaModel_fit = sArimaModel.fit()
        forecast = sArimaModel_fit.forecast(steps=24)

        # FBProphet
        # hourly_data = hourly_data.reset_index().rename(columns={'날짜': 'ds', '발생건수': 'y'})
        # fbProphetModel = Prophet()
        # model.fit(hourly_data)
        # forecast = model.make_future_dataframe(periods=24)
        # prediction = model.predict(forecast)


        # 예측 결과 시각화
        plt.rc("font", family="NanumGothic")
        plt.figure(figsize=(12, 6))
        plt.plot(range(24), time_series, label="Historical Data", marker="o")
        plt.plot(range(24), forecast, label="Forecast", linestyle="--", marker="x")
        plt.xlabel("시간대")
        plt.ylabel("전기적 화재 발생 건수")
        plt.title("시간대별 전기적 화재 발생 예측")
        plt.legend()
        plt.show()

        # 결과 반환
        return {
            "forecast": forecast.tolist(),
            "time_probabilities": time_probabilities.to_dict(),
        }
    except Exception as e:
        print(f"Error processing {file_path} : {e}")
        return json.dumps({"error": str(e)}, ensure_ascii=False)


def execute_function(function_name, args):
    functions = {"correlation_analysis": correlation_analysis}

    if function_name in functions:
        try:
            result = functions[function_name](*args)
            print(result)
        except Exception as e:
            print(f"Error executing function  '{function_name}' : {str(e)}")
    else:
        print(f"Error: Function '{function_name}' not found.")


# if __name__ == "__main__":
#     if len(sys.argv) < 2:
#         print("Useage: python fileName.py <function_name> [args...]")
#         sys.exit(1)
#     function_name = sys.argv[1]
#     args = sys.argv[2:]
#     execute_function(function_name, args)


correlation_analysis(2016)
