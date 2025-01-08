import json

def get_result():
    # JSON 데이터를 반환합니다.
    return {"message": "안녕하세요! Python에서 반환된 메시지입니다."}

if __name__ == "__main__":
    result = get_result()
    print(json.dumps(result))  # JSON 형식으로 출력
