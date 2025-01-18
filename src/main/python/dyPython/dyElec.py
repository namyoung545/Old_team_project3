import sys
import json
import mysql.connector
from configparser import ConfigParser
import io

# 기본 인코딩을 UTF-8로 설정
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

def load_db_config():
    config = ConfigParser()

    # application.properties에 임시 섹션 추가
    with open('src/main/resources/application.properties', 'r', encoding='utf-8') as f:
        properties_data = f.read()
        properties_data = "[DEFAULT]\n" + properties_data  # 섹션 추가

    # ConfigParser가 읽을 수 있도록 데이터 로드
    config.read_string(properties_data)

    # 데이터베이스 설정 가져오기
    url = config.get('DEFAULT', 'spring.datasource.url')
    username = config.get('DEFAULT', 'spring.datasource.username')
    password = config.get('DEFAULT', 'spring.datasource.password')

    # URL에서 host와 port 분리
    host_port = url.split('/')[2]
    host, port = host_port.split(':')

    # 데이터베이스 이름 가져오기
    database = url.split('/')[-1]

    return {
        "host": host,
        "port": int(port),
        "user": username,
        "password": password,
        "database": database,
    }
def fetch_data(year):
    db_config = load_db_config()

    conn = mysql.connector.connect(
        host=db_config["host"],
        user=db_config["user"],
        password=db_config["password"],
        database=db_config["database"],
        charset="utf8"  # MySQL 연결에서 UTF-8 설정

    )
    cursor = conn.cursor(dictionary=True)
    query = """
    SELECT region, total_incidents AS totalIncidents, 
         total_damage AS totalDamage
    FROM dy_merged_fire_data
    WHERE year = %s
    """
    cursor.execute(query, (year,))
    rows = cursor.fetchall()
    conn.close()
    return rows

if __name__ == "__main__":
    year = int(sys.argv[1]) if len(sys.argv) > 1 else None
    if not year:
        print(json.dumps({"error": "Year argument is missing"}, ensure_ascii=False))
        sys.exit(1)
    
    try:
        result = fetch_data(year)
        print(json.dumps(result, ensure_ascii=False).encode('utf-8').decode('utf-8'))
    except Exception as e:
        print(json.dumps({"error": str(e)}, ensure_ascii=False))
