import pymysql # 파이썬과 MySQL 라이브러리
print("pymysql 모듈이 정상적으로 설치되었습니다.")
import csv #CSV파일 읽고 데이터 파싱

#MySQL 연결 설정
connection = pymysql.connect(
    host="192.168.30.89",       # MySQL 서버 IP주소 또는 도메인
    user="root",   # MySQL 사용자 이름
    password="1234",  # MySQL 비밀번호
    database="old_team_project3",  # 데이터베이스 이름
    charset="utf8mb4"       # 문자셋
)

#CSV파일 열기 및 데이터 읽기
try:
    #커서 생성
    cursor = connection.cursor()

    # CSV 파일 열기
    csv_file_path = "D:/01-STUDY/3차 팀PJ/electric_ai_data.csv" # CSV 파일 경로
    with open(csv_file_path, mode="r", encoding="utf-8") as file: # 파일 읽기 모드("r")로 열고 인코딩
            csv_reader = csv.reader(file) # CSV파일을 읽어 Python 리스트 형태로 변환
            next(csv_reader)  # CSV 첫 번째 줄(헤더) 건너뜀

            # 데이터 삽입
            for row in csv_reader: # CSV파일의 각 줄을 반복적으로 읽음
                sql = """  
                INSERT INTO electric_ai_data (region_province, cause_category, cause_subcategory, total_incidents, total_damage)
                VALUES (%s, %s, %s, %s, %s)
                """
                cursor.execute(sql, row)  # 실행할 SQL쿼리, CSV파일에서 읽은 현재 행의 데이터 대체.

    # 변경사항 커밋
    connection.commit() # 모든 SQL 명령이 성공적으로 실행 시 변경 사항을 DB에 저장
    print("CSV 데이터를 성공적으로 삽입했습니다!")

except Exception as e:
    print(f"오류 발생: {e}")
    connection.rollback()  # 오류 발생 시 롤백

finally:
    # 연결 종료
    cursor.close() # 커서 닫고 DB연결 해제
    connection.close() # MySQL서버 연결 종료