import json
import os
import sys
import pandas as pd

def analyze_statistics(dataname):
    try:
        # 데이터 불러오기
        file_dir = os.path.dirname(__file__)
        file_path = os.path.join(file_dir, 'processed', 'fire_statistics', f'{dataname}.csv')
        df = pd.read_csv(file_path, encoding='EUC-KR')

        # "전기적 요인" 데이터 필터링
        electric_cause_df = df[df['발화요인대분류'] == '전기적 요인']

        # 통계 계산
        hourly_analysis = electric_cause_df['일시'].apply(lambda x: pd.to_datetime(x).hour).value_counts().sort_index()
        province = electric_cause_df['시도'].value_counts()
        district = electric_cause_df['시군구'].value_counts()
        fire_type = electric_cause_df['화재유형'].value_counts()
        heat_source_main = electric_cause_df['발화열원대분류'].value_counts()
        heat_source_sub = electric_cause_df['발화열원소분류'].value_counts()
        cause_main = electric_cause_df['발화요인대분류'].value_counts()
        cause_sub = electric_cause_df['발화요인소분류'].value_counts()
        ignition_material_main = electric_cause_df['최초착화물대분류'].value_counts()
        ignition_material_sub = electric_cause_df['최초착화물소분류'].value_counts()
        casualties_total = electric_cause_df['인명피해소계'].value_counts()
        deaths = electric_cause_df['사망'].value_counts()
        injuries = electric_cause_df['부상'].value_counts()
        total_property_damage = electric_cause_df['재산피해소계'].sum()
        location_counts = electric_cause_df['장소대분류'].value_counts()
        location_mid = electric_cause_df['장소중분류'].value_counts()
        location_sub = electric_cause_df['장소소분류'].value_counts()

        # 통계 결과 정리
        result = {
            "year": int(dataname),
            "hourly_analysis": hourly_analysis.to_dict(),
            "province": province.to_dict(),
            "district": district.to_dict(),
            "fire_type": fire_type.to_dict(),
            "heat_source_main": heat_source_main.to_dict(),
            "heat_source_sub": heat_source_sub.to_dict(),
            "cause_main": cause_main.to_dict(),
            "cause_sub": cause_sub.to_dict(),
            "ignition_material_main": ignition_material_main.to_dict(),
            "ignition_material_sub": ignition_material_sub.to_dict(),
            "casualties_total": casualties_total.to_dict(),
            "deaths": deaths.to_dict(),
            "injuries": injuries.to_dict(),
            "total_property_damage": int(total_property_damage),
            "location_counts": location_counts.to_dict(),
            "location_mid": location_mid.to_dict(),
            "location_sub": location_sub.to_dict(),
        }

        # JSON 형식으로 반환
        return json.dumps(result,ensure_ascii=False)

    except Exception as e:
        print(f"Error processing {file_path}: {e}")
        return json.dumps({"error": str(e)}, ensure_ascii=False)

# CLI를 통한 함수 실행 지원
def execute_function(function_name, args):
    # 함수 맵핑
    functions = {
        "analyze_statistics": analyze_statistics
    }

    if function_name in functions:
        try:
            # 함수 호출 및 결과 반환
            result = functions[function_name](*args)
            print(result)
        except Exception as e:
            print(f"Error executing function '{function_name}': {str(e)}")
    else:
        print(f"Error: Function '{function_name}' not found.")

# 파이썬 함수 실행
if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python fileName.py <function_name> [args...]")
        sys.exit(1)

    function_name = sys.argv[1]
    args = sys.argv[2:]
    execute_function(function_name, args)


# import pandas as pd
# import os

# # 데이터 파일 로드
# file_dir = os.path.dirname(__file__)
# file_path = os.path.join(file_dir, "raw", "2023.xlsx")
# df = pd.read_excel(file_path)

# # 1. "전기적 요인" 데이터 필터링
# electric_cause_df = df[df["발화요인대분류"] == "전기적 요인"]

# # 2. 빈도 분석
# cause_counts = electric_cause_df["발화열원소분류"].value_counts()
# print("발화열원소분류 별 빈도:\n", cause_counts)

# # 3. 최초 착화물 분석
# first_ignition_counts = electric_cause_df["최초착화물대분류"].value_counts()
# print("최초 착화물 대분류 빈도:\n", first_ignition_counts)

# # 4. 장소 분석
# location_counts = electric_cause_df["장소대분류"].value_counts()
# print("장소 대분류 빈도:\n", location_counts)

# # 5. 재산 피해 분석
# total_damage = electric_cause_df["재산피해소계"].sum()
# print("전기적 요인으로 인한 총 재산 피해 (원):", total_damage)

# # 6. 시간대 분석
# electric_cause_df["발생시간"] = pd.to_datetime(electric_cause_df["일시"])
# hourly_analysis = electric_cause_df["발생시간"].dt.hour.value_counts().sort_index()
# print("시간대별 전기적 요인 화재 빈도:\n", hourly_analysis)

# # 7. 지역 분석
# region_counts = electric_cause_df["시도"].value_counts()
# print("지역별 전기적 요인 화재 빈도:\n", region_counts)

# # # 결과 시각화 (선택사항)
# # import matplotlib.pyplot as plt

# # # 시간대 분석 시각화
# # plt.figure(figsize=(10, 6))
# # hourly_analysis.plot(kind='bar', color='skyblue')
# # plt.title('시간대별 전기적 요인 화재 발생 빈도')
# # plt.xlabel('시간대 (시)')
# # plt.ylabel('화재 발생 건수')
# # plt.grid(True)
# # plt.show()

# # # 장소 분석 시각화
# # plt.figure(figsize=(10, 6))
# # location_counts.plot(kind='bar', color='orange')
# # plt.title('장소별 전기적 요인 화재 발생 빈도')
# # plt.xlabel('장소 대분류')
# # plt.ylabel('화재 발생 건수')
# # plt.grid(True)
# # plt.show()
