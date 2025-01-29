import os
import sys
import pandas as pd
import json
from datetime import datetime


def insert_fires_data(dataname):
    try:
        file_dir = os.path.dirname(__file__)
        file_path = os.path.join(
            file_dir, "processed", "fire_statistics", f"{dataname}.csv"
        )

        if not os.path.exists(file_path):
            raise FileNotFoundError(f"파일을 찾을 수 없습니다. {file_path}")

        df = pd.read_csv(file_path, encoding="EUC-KR")
        df.fillna(
            {"재산피해소계": 0, "인명피해소계": 0, "사망": 0, "부상": 0}, inplace=True
        )
        df.fillna("미확인", inplace=True)

        fires_data = []

        for _, row in df.iterrows():
            fire_entry = {
                "dateTime": row.get("일시"),
                # "dateTime": row.get("일시", datetime.now().strftime('%Y-%m-%d %H:%M:%S')),
                "damageProperty": int(row.get("재산피해소계", 0)),
                "casualtiesTotal": int(row.get("인명피해소계", 0)),
                "deaths": int(row.get("사망", 0)),
                "injuries": int(row.get("부상", 0)),
                "fireCauses": {
                    "causeCategory": row.get("발화요인대분류", "미확인"),
                    "causeSubcategory": row.get("발화요인소분류", "미확인"),
                },
                "fireIgnitions": {
                    "ignitionSourceCategory": row.get("발화열원대분류", "미확인"),
                    "ignitionSourceSubcategory": row.get("발화열원소분류", "미확인"),
                },
                "fireItems": {
                    "itemCategory": row.get("최초착화물대분류", "미확인"),
                    "itemDetail": row.get("최초착화물소분류", "미확인"),
                },
                "fireLocations": {
                    "locationMainCategory": row.get("장소대분류", "미확인"),
                    "locationSubCategory": row.get("장소중분류", "미확인"),
                    "locationDetail": row.get("장소소분류", "미확인"),
                },
                "fireRegions": {
                    "regionProvince": row.get("시도", "미확인"),
                    "regionCity": row.get("시군구", "미확인"),
                },
                "fireTypes": {
                    "fireType": row.get("화재유형", "미확인"),
                },
            }
            fires_data.append(fire_entry)

        return json.dumps(fires_data, ensure_ascii=False)
    except Exception as e:
        print(f"[ERROR] Processing {file_path} : {e}")
        return json.dumps({"error": str(e)}, ensure_ascii=False)


def execute_function(function_name, args):
    functions = {"insert_fires_data": insert_fires_data}

    if function_name in functions:
        try:
            result = functions[function_name](*args)
            print(result)
        except Exception as e:
            print(f"[ERROR] Function '{function_name}' not found.")
    else:
        print(f"[ERROR] Function '{function_name}' not found.")


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("[USAGE] python filename.py <function_name> [args...] ")
        sys.exit(1)

    function_name = sys.argv[1]
    args = sys.argv[2:]
    execute_function(function_name, args)
