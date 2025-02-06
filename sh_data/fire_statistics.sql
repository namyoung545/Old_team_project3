-- 아래 테이블 생성 코드는 참고용임 JPA에서 테이블을 자동 생성함
-- Table: fires (화재 기본 정보)
CREATE TABLE fires (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date_time DATETIME NOT NULL,
    region_province VARCHAR(50) NOT NULL,
    region_city VARCHAR(50) NOT NULL,
    fire_type VARCHAR(100) NOT NULL,
    damage_property INT NOT NULL,
    casualties_deaths INT NOT NULL,
    casualties_injuries INT NOT NULL
);

-- Table: fire_causes (화재 원인 정보)
CREATE TABLE fire_causes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cause_category VARCHAR(100) NOT NULL,
    cause_subcategory VARCHAR(100) NOT NULL,
    ignition_source_category VARCHAR(100) NOT NULL,
    ignition_source_subcategory VARCHAR(100) NOT NULL,
    fire_id INT NOT NULL,
	FOREIGN KEY (fire_id) REFERENCES fires(id) ON DELETE CASCADE
);

-- Table: fire_locations (화재 장소 정보)
CREATE TABLE fire_locations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    location_main_category VARCHAR(100) NOT NULL,
    location_sub_category VARCHAR(100) NOT NULL,
    location_detail VARCHAR(100) NOT NULL,
	fire_id INT NOT NULL,
	FOREIGN KEY (fire_id) REFERENCES fires(id) ON DELETE CASCADE
);

-- Table: fire_items (최초 착화물 정보)
CREATE TABLE fire_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_category VARCHAR(100) NOT NULL,
    item_detail VARCHAR(100) NOT NULL,
	fire_id INT NOT NULL,
	FOREIGN KEY (fire_id) REFERENCES fires(id) ON DELETE CASCADE
);

CREATE TABLE statistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    year INT NOT NULL,
    stat_name VARCHAR(255) NOT NULL,
    stat_value DOUBLE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


-- Table: meta data (메타 데이터) 임시
CREATE TABLE fires_meta (
id INT AUTO_INCREMENT PRIMARY KEY,
year INT NOT NULL,
meta_name VARCHAR(100) NOT NULL,
meta_value VARCHAR(100) NOT NULL,
created_time DATETIME NOT NULL,
modified_time DATETIME NOT NULL
);


-- DROP TABLE
-- DROP TABLE fire_causes;
-- DROP TABLE fire_ignitions;
-- DROP TABLE fire_items;
-- DROP TABLE fire_locations;
-- DROP TABLE fire_regions;
-- DROP TABLE fire_statistics;
-- DROP TABLE fire_types;
-- DROP TABLE fires;


DESCRIBE fires;
SELECT * FROM fires;
SELECT COUNT(*), SUM(f.damage_property) FROM fires as f WHERE year(date_time) = 2023;
SELECT COUNT(*), SUM(f.casualties_total), SUM(f.casualties_deaths), SUM(f.casualties_injuries), SUM(f.damage_property) FROM fires as f WHERE fire_region_id IN (27, 84, 107, 138, 207) AND date_time >= 2023;
SELECT COUNT(*) as cnt FROM fires;

DESCRIBE fire_causes;
SELECT * FROM fire_causes;
SELECT COUNT(*) as cnt FROM fire_causes;
SELECT * FROM fire_causes WHERE cause_category LIKE "%전기%";
SELECT * FROM fire_causes WHERE cause_subcategory LIKE "%불씨%";

DESCRIBE fire_ignitions;
SELECT * FROM fire_ignitions;
SELECT * FROM fire_ignitions AS i WHERE i.ignition_source_category LIKE "%작동%";
SELECT * FROM fire_ignitions WHERE ignition_source_subcategory LIKE "%전기%";

DESCRIBE fire_items;
SELECT * FROM fire_items;
SELECT COUNT(*) as cnt FROM fire_items;
SELECT * FROM fire_items WHERE item_category LIKE "%전기%";
SELECT * FROM fire_items WHERE item_detail LIKE "%나무%";

DESCRIBE fire_locations;
SELECT * FROM fire_locations;
SELECT COUNT(*) as cnt FROM fire_locations;
SELECT * FROM fire_locations WHERE location_main_category LIKE "%주거%";
SELECT * FROM fire_locations WHERE location_sub_category LIKE "%단독%";
SELECT * FROM fire_locations WHERE location_detail LIKE "%자동차%";

DESCRIBE fire_regions;
SELECT * FROM fire_regions;
SELECT * FROM fire_regions WHERE region_province LIKE "%울산%";
SELECT * FROM fire_regions WHERE region_city LIKE "%남구%";

DESCRIBE fire_types;
SELECT * FROM fire_types WHERE fire_type LIKE "%건축%";

-- RENAME TABLE 
-- 	fire_type TO fire_types,
--     fire_region TO fire_regions,
--     fire_ignition TO fire_ignitions;
SELECT * FROM member;
SELECT * FROM as_reception;
DESCRIBE as_reception;
DESCRIBE fire_info_sido;
SELECT * FROM fire_info_sido;
-- DROP TABLE fire_info_sido;

DESCRIBE fire_info_sido_casualty;
SELECT * FROM fire_info_sido_casualty;
-- DROP TABLE fire_info_sido_casualty;

DESCRIBE fire_info_sido_damage;
SELECT * FROM fire_info_sido_damage;
-- DROP TABLE fire_info_sido_damage;

DESCRIBE disasters;
SELECT * FROM disasters;
-- DROP TABLE disasters; 
DESCRIBE fires;
SELECT * FROM fires;
SELECT * FROM fire_causes;

DROP TABLE fire_statistics;
DESCRIBE fire_statistics;
SELECT * FROM fire_statistics ORDER BY id DESC;
SELECT * FROM fire_statistics WHERE year = "2023";
SELECT * FROM fire_statistics WHERE stat_name LIKE "%재산피해%" AND year = "2023" ORDER BY id DESC;

DESCRIBE as_reception;
SELECT * FROM member;
SELECT * FROM authority;

-- DELETE FROM fire_statistics WHERE stat_name LIKE ("%인명피해 합계(원인)%") LIMIT 100; 
-- TRUNCATE TABLE fire_statistics;

SELECT * FROM as_reception;
SELECT * FROM as_reception WHERE preferredDateTime < NOW();
SELECT * FROM as_reception WHERE receptionDate > NOW();
UPDATE as_reception 
SET receptionDate = DATE_SUB(receptionDate, INTERVAL 7 DAY) 
WHERE receptionDate > NOW();

-- 상태 업데이트
UPDATE as_reception
SET receptionStatus = '처리 완료'
WHERE preferredDateTime < NOW();

UPDATE as_reception
SET receptionDelivery = '최기사'
WHERE preferredDateTime < NOW() AND receptionDelivery = '미배정';

DELETE FROM as_reception WHERE requestId > 2 LIMIT 100;

INSERT INTO as_reception (userId, authorityId, name, phoneNumber, email, postcode, address, detailAddress, facilityType, issueTitle, issueDetails, preferredDateTime, receptionDate, receptionStatus, receptionDelivery) VALUES
('executer10', 2, '김영수', '010-2345-6789', 'youngsoo@example.com', '06012', '서울특별시 강남구 도산대로', '302호', 
 'power', '정전 발생', '사무실 일부 구역에서 전기가 들어오지 않습니다.', '2025-01-03 09:00:00', '2024-12-26 14:12:00', '처리 완료', '박기사'),

('executer10', 2, '박지훈', '010-8765-4321', 'jihun@example.com', '03187', '서울특별시 종로구 창경궁로', 'B동 1203호', 
 'lighting', '형광등 교체 요청', '화장실 형광등이 나가서 교체가 필요합니다.', '2025-01-10 10:00:00', '2025-01-02 09:45:00', '접수 확인 중', '미배정'),

('executer10', 2, '최민정', '010-1234-5678', 'minjung@example.com', '04535', '서울특별시 중구 을지로', '901호', 
 'hvac', '난방기 작동 안됨', '회의실 난방기가 켜지지 않습니다.', '2025-01-12 11:00:00', '2025-01-04 10:30:00', '접수 확인 중', '미배정'),

('executer10', 2, '이서윤', '010-3456-7890', 'seoyoon@example.com', '07296', '서울특별시 영등포구 국회대로', '703호', 
 'communication', '전화 연결 장애', '사무실 전화가 외부 통화가 안 됩니다.', '2025-01-08 13:00:00', '2024-12-30 11:10:00', '처리 완료', '최기사'),

('executer10', 2, '정도현', '010-5678-9012', 'dohyun@example.com', '04799', '서울특별시 성동구 성수일로', '1204호', 
 'fire', '화재 경보 오작동', '화재 감지기가 오작동하여 계속 울립니다.', '2025-01-15 14:00:00', '2025-01-05 09:20:00', '접수 확인 중', '미배정'),

('executer10', 2, '김다은', '010-6789-0123', 'daeun@example.com', '01165', '서울특별시 종로구 자하문로', 'A동 1501호', 
 'plumbing', '배수구 막힘', '화장실 배수구가 막혀서 물이 내려가지 않습니다.', '2025-01-04 15:00:00', '2024-12-27 16:50:00', '처리 완료', '박기사'),

('executer10', 2, '이도윤', '010-7890-1234', 'doyoon@example.com', '08379', '서울특별시 구로구 디지털로', '3층 305호', 
 'elevator', '엘리베이터 고장', '엘리베이터가 갑자기 멈췄습니다.', '2025-02-01 09:00:00', '2025-01-21 12:15:00', '접수 확인 중', '미배정'),

('executer10', 2, '홍서준', '010-8901-2345', 'seojun@example.com', '06675', '서울특별시 서초구 강남대로', '1803호', 
 'security', '출입문 잠금장치 불량', '사무실 출입문 잠금장치가 작동하지 않습니다.', '2025-01-20 11:00:00', '2025-01-10 14:00:00', '접수 확인 중', '미배정'),

('executer10', 2, '박하늘', '010-9012-3456', 'haneul@example.com', '03998', '서울특별시 마포구 마포대로', '5층 506호', 
 'interior', '천장 타일 탈락', '사무실 천장 일부가 떨어질 위험이 있습니다.', '2025-01-06 13:00:00', '2024-12-29 10:40:00', '처리 완료', '최기사'),

('executer10', 2, '조민호', '010-0123-4567', 'minho@example.com', '08821', '서울특별시 관악구 남부순환로', '1층 101호', 
 'lighting', '복도 조명 교체 요청', '사무실 복도 조명이 나갔습니다.', '2025-01-18 16:00:00', '2025-01-09 08:30:00', '접수 확인 중', '미배정'),

('executer10', 2, '강지윤', '010-3456-7891', 'jiyoon@example.com', '07788', '서울특별시 강서구 공항대로', '7층 703호', 
 'emergency', '비상벨 점검 요청', '비상벨이 울리지 않습니다.', '2025-01-02 10:00:00', '2024-12-26 17:00:00', '처리 완료', '박기사'),

('executer10', 2, '윤태민', '010-5678-9013', 'taemin@example.com', '12012', '서울특별시 강북구 도봉로', '1702호', 
 'exterior', '건물 외벽 균열', '건물 외벽에 균열이 발생하였습니다.', '2025-02-02 15:00:00', '2025-01-23 15:20:00', '접수 확인 중', '미배정'),

('executer10', 2, '배지훈', '010-6789-0124', 'jihoon@example.com', '08511', '서울특별시 금천구 가산디지털로', '11층 1105호', 
 'power', '콘센트 전원 불량', '책상 옆 콘센트에서 전원이 안 들어옵니다.', '2025-01-25 09:00:00', '2025-01-14 11:10:00', '접수 확인 중', '미배정'),

('executer10', 2, '이채영', '010-7890-1235', 'chaeyoung@example.com', '07332', '서울특별시 영등포구 여의대로', '22층 2201호', 
 'security', 'CCTV 작동 불량', 'CCTV 화면이 나오지 않습니다.', '2025-01-07 17:00:00', '2024-12-30 13:50:00', '처리 완료', '최기사');

INSERT INTO as_reception (userId, authorityId, name, phoneNumber, email, postcode, address, detailAddress, facilityType, issueTitle, issueDetails, preferredDateTime, receptionDate, receptionStatus, receptionDelivery) VALUES
('executer10', 2, '김태우', '010-9876-5432', 'taewoo@example.com', '04345', '서울특별시 중구 서소문로', '101호', 
 'lighting', '사무실 전등이 깜빡거림', '사무실 전등이 자주 깜빡거립니다.', '2025-01-10 09:00:00', '2025-01-02 08:20:00', '처리 완료', '최기사'),

('executer10', 2, '박지훈', '010-8765-4321', 'jihun@example.com', '03187', '서울특별시 종로구 창경궁로', 'B동 1203호', 
 'plumbing', '수도관 누수', '주방 싱크대 아래쪽에서 물이 새고 있습니다.', '2025-01-12 10:00:00', '2025-01-03 15:10:00', '처리 완료', '박기사'),

('executer10', 2, '최민정', '010-1234-5678', 'minjung@example.com', '04535', '서울특별시 중구 을지로', '901호', 
 'hvac', '냉난방기 조절 불가', '난방기 온도가 변경되지 않습니다.', '2025-01-15 11:00:00', '2025-01-06 14:30:00', '처리 완료', '최기사'),

('executer10', 2, '이서윤', '010-3456-7890', 'seoyoon@example.com', '07296', '서울특별시 영등포구 국회대로', '703호', 
 'security', '출입문 자동 잠금장치 고장', '출입문이 자동으로 잠기지 않습니다.', '2025-01-22 13:00:00', '2025-01-10 10:50:00', '처리 완료', '박기사'),

('executer10', 2, '정도현', '010-5678-9012', 'dohyun@example.com', '04799', '서울특별시 성동구 성수일로', '1204호', 
 'fire', '화재경보 오작동', '화재경보기가 가끔씩 울립니다.', '2025-01-25 14:00:00', '2025-01-16 09:40:00', '처리 완료', '최기사'),

('executer10', 2, '김다은', '010-6789-0123', 'daeun@example.com', '01165', '서울특별시 종로구 자하문로', 'A동 1501호', 
 'power', '사무실 일부 정전', '사무실 일부 구역에서 전기가 들어오지 않습니다.', '2025-02-02 15:00:00', '2025-01-23 12:10:00', '접수 확인 중', '미배정'),

('executer10', 2, '이도윤', '010-7890-1234', 'doyoon@example.com', '08379', '서울특별시 구로구 디지털로', '3층 305호', 
 'elevator', '엘리베이터 작동 안됨', '엘리베이터가 5층에서 멈춘 상태입니다.', '2025-02-05 09:00:00', '2025-01-27 11:15:00', '접수 확인 중', '미배정'),

('executer10', 2, '홍서준', '010-8901-2345', 'seojun@example.com', '06675', '서울특별시 서초구 강남대로', '1803호', 
 'security', '사무실 CCTV 작동 불량', 'CCTV 화면이 나오지 않습니다.', '2025-02-08 11:00:00', '2025-01-30 09:30:00', '접수 확인 중', '미배정'),

('executer10', 2, '박하늘', '010-9012-3456', 'haneul@example.com', '03998', '서울특별시 마포구 마포대로', '5층 506호', 
 'communication', '인터넷 연결 불안정', '사무실 인터넷이 자주 끊깁니다.', '2025-02-10 13:00:00', '2025-02-01 14:20:00', '접수 확인 중', '미배정'),

('executer10', 2, '조민호', '010-0123-4567', 'minho@example.com', '08821', '서울특별시 관악구 남부순환로', '1층 101호', 
 'lighting', '복도 조명 교체 요청', '사무실 복도 조명이 나갔습니다.', '2025-02-12 16:00:00', '2025-02-03 08:30:00', '접수 확인 중', '미배정');

INSERT INTO as_reception (userId, authorityId, name, phoneNumber, email, postcode, address, detailAddress, facilityType, issueTitle, issueDetails, preferredDateTime, receptionDate, receptionStatus, receptionDelivery) VALUES
('executer10', 2, '김현우', '010-1111-2222', 'hyunwoo@example.com', '03127', '서울특별시 종로구 종로', '301호', 
 'lighting', '사무실 조명 깜빡임', '사무실 천장 조명이 깜빡거립니다.', '2025-02-01 09:00:00', '2025-01-23 08:50:00', '처리 완료', '최기사'),

('executer10', 2, '이수민', '010-2222-3333', 'sumin@example.com', '04123', '서울특별시 중구 다동길', 'A동 704호', 
 'power', '콘센트 전력 불량', '사무실 일부 콘센트에 전력이 들어오지 않습니다.', '2025-02-02 10:00:00', '2025-01-25 14:20:00', '처리 완료', '박기사'),

('executer10', 2, '박정훈', '010-3333-4444', 'junghoon@example.com', '05052', '서울특별시 강남구 테헤란로', '10층 1003호', 
 'elevator', '엘리베이터 고장', '엘리베이터가 5층에서 멈춰 움직이지 않습니다.', '2025-02-03 11:00:00', '2025-01-26 10:10:00', '처리 완료', '최기사'),

('executer10', 2, '최지영', '010-4444-5555', 'jiyoung@example.com', '07236', '서울특별시 영등포구 여의대로', '1801호', 
 'plumbing', '화장실 변기 고장', '화장실 변기가 내려가지 않습니다.', '2025-02-04 13:00:00', '2025-01-28 09:30:00', '처리 완료', '박기사'),

('executer10', 2, '윤도영', '010-5555-6666', 'doyoung@example.com', '08874', '서울특별시 관악구 신림로', '2층 202호', 
 'fire', '화재경보기 오작동', '화재경보기가 주기적으로 울립니다.', '2025-02-05 14:00:00', '2025-01-29 08:45:00', '처리 완료', '최기사'),

('executer10', 2, '김서연', '010-6666-7777', 'seoyeon@example.com', '01192', '서울특별시 종로구 혜화로', '5층 503호', 
 'security', '출입문 잠금장치 오류', '출입문이 자동으로 잠기지 않습니다.', '2025-02-06 15:00:00', '2025-01-30 11:15:00', '접수 확인 중', '미배정'),

('executer10', 2, '이태준', '010-7777-8888', 'taejun@example.com', '04567', '서울특별시 중구 충무로', '702호', 
 'emergency', '비상 조명 점검 필요', '비상 조명이 작동하지 않습니다.', '2025-02-07 16:00:00', '2025-02-01 09:40:00', '접수 확인 중', '미배정'),

('executer10', 2, '한지원', '010-8888-9999', 'jiwon@example.com', '06234', '서울특별시 강남구 강남대로', '20층 2001호', 
 'hvac', '난방기 작동 불량', '난방기가 켜지지 않습니다.', '2025-02-08 10:00:00', '2025-02-02 10:50:00', '접수 확인 중', '미배정'),

('executer10', 2, '조영훈', '010-9999-0000', 'younghoon@example.com', '04789', '서울특별시 성동구 왕십리로', '1304호', 
 'lighting', '회의실 조명 점검 필요', '회의실 조명이 너무 어둡습니다.', '2025-02-09 11:00:00', '2025-02-03 12:30:00', '접수 확인 중', '미배정'),

('executer10', 2, '백민석', '010-0000-1111', 'minseok@example.com', '07365', '서울특별시 영등포구 여의공원로', '3층 305호', 
 'interior', '사무실 벽면 페인트 벗겨짐', '사무실 벽이 부분적으로 벗겨졌습니다.', '2025-02-10 13:00:00', '2025-02-04 08:10:00', '접수 확인 중', '미배정'),

('executer10', 2, '강서윤', '010-1111-2223', 'seoyoon@example.com', '04112', '서울특별시 중구 무교로', '901호', 
 'communication', '전화 연결 불량', '사무실 전화가 자주 끊깁니다.', '2025-02-11 14:00:00', '2025-02-05 09:20:00', '접수 확인 중', '미배정'),

('executer10', 2, '오지훈', '010-2222-3334', 'jihun@example.com', '08837', '서울특별시 관악구 봉천로', '1502호', 
 'fire', '소방 스프링클러 점검 요청', '스프링클러 점검이 필요합니다.', '2025-02-12 16:00:00', '2025-02-06 10:00:00', '접수 확인 중', '미배정'),

('executer10', 2, '임하진', '010-3333-4445', 'hajin@example.com', '07263', '서울특별시 영등포구 국제금융로', '1003호', 
 'security', '사무실 보안 시스템 오류', '보안 시스템이 정상적으로 작동하지 않습니다.', '2025-02-13 09:00:00', '2025-02-07 11:30:00', '접수 확인 중', '미배정'),

('executer10', 2, '양서현', '010-4444-5556', 'seohyun@example.com', '06255', '서울특별시 강남구 삼성로', '6층 604호', 
 'hvac', '에어컨 냉각 기능 작동 안됨', '에어컨이 찬바람을 내보내지 않습니다.', '2025-02-14 10:00:00', '2025-02-08 09:15:00', '접수 확인 중', '미배정'),

('executer10', 2, '신도윤', '010-5555-6667', 'doyoon@example.com', '05032', '서울특별시 강남구 논현로', '1201호', 
 'elevator', '엘리베이터 이상 소음', '엘리베이터에서 이상한 소리가 납니다.', '2025-02-15 11:00:00', '2025-02-09 10:40:00', '접수 확인 중', '미배정');




-- 더미 데이터 접수시간 오류
INSERT INTO as_reception (userId, authorityId, name, phoneNumber, email, postcode, address, detailAddress, facilityType, issueTitle, issueDetails, preferredDateTime, receptionDate, receptionStatus, receptionDelivery) VALUES 
('executer10', 2, '이철수', '010-5678-1234', 'chulsoo@example.com', '04146', '서울특별시 종로구 세종대로', '101동 1204호', 
 'lighting', '전등 불량 신고', '거실 전등이 깜빡이고 일정 시간 후 꺼집니다.', '2025-01-05 09:30:00', NOW(), '접수 확인 중', '최기사'),

('executer10', 2, '박영희', '010-4321-8765', 'younghee@example.com', '08826', '서울특별시 관악구 남부순환로', '302호', 
 'plumbing', '배관 누수 신고', '화장실 세면대 아래 배관에서 물이 샙니다.', '2025-01-10 14:00:00', NOW(), '접수 확인 중', '미배정'),

('executer10', 2, '김민수', '010-9999-5555', 'minsoo@example.com', '22340', '인천광역시 남동구 인주대로', 'B동 401호', 
 'heating', '난방기 고장 신고', '온풍기가 정상적으로 작동하지 않고 찬 바람이 나옵니다.', '2025-01-15 16:00:00', NOW(), '접수 확인 중', '박기사'),

('executer10', 2, '손정우', '010-3333-2222', 'jungwoo@example.com', '35252', '대전광역시 유성구 대학로', '3층 307호', 
 'elevator', '승강기 오작동 신고', '엘리베이터가 특정 층에서 멈추고 움직이지 않습니다.', '2025-01-20 11:30:00', NOW(), '접수 확인 중', '최기사'),

('executer10', 2, '이서연', '010-8888-7777', 'seoyeon@example.com', '48234', '부산광역시 해운대구 센텀중앙로', '아파트 502동 702호', 
 'other', '가스 누출 의심 신고', '주방에서 가스 냄새가 지속적으로 납니다.', '2025-01-25 13:00:00', NOW(), '접수 확인 중', '미배정'),

('executer10', 2, '정한울', '010-7777-6666', 'hanul@example.com', '63248', '제주특별자치도 제주시 노형로', '단독주택 1층', 
 'other', '온수 공급 문제', '온수가 나오지 않고 찬물만 나옵니다.', '2025-01-30 10:00:00', NOW(), '접수 확인 중', '박기사'),

('executer10', 2, '김나연', '010-6666-5555', 'nayoun@example.com', '12939', '경기도 성남시 분당구 정자일로', '1505호', 
 'lighting', '복도 조명 고장', '아파트 복도 조명이 나가서 어두워졌습니다.', '2025-02-02 15:00:00', NOW(), '접수 확인 중', '미배정'),

('executer10', 2, '박준혁', '010-5555-4444', 'junhyuk@example.com', '27895', '충청북도 청주시 상당구 상당로', '빌라 2층', 
 'other', '옥상 방수층 파손', '옥상 방수층이 벗겨져 비가 새고 있습니다.', '2025-02-08 09:00:00', NOW(), '접수 확인 중', '최기사'),

('executer10', 2, '서우진', '010-4444-3333', 'woojin@example.com', '31259', '경상북도 포항시 남구 중앙로', '주택 101호', 
 'hvac', '난방 불량', '보일러를 켜도 난방이 전혀 되지 않습니다.', '2025-02-15 11:00:00', NOW(), '접수 확인 중', '박기사'),

('executer10', 2, '한지민', '010-2222-1111', 'jimin@example.com', '47891', '경상남도 창원시 성산구 중앙대로', '아파트 301동 1403호', 
 'lighting', '베란다 조명 고장', '베란다 조명이 깜빡이다가 꺼지고 다시 켜지지 않습니다.', '2025-02-22 17:00:00', NOW(), '접수 확인 중', '미배정');


INSERT INTO as_reception (userId, authorityId, name, phoneNumber, email, postcode, address, detailAddress, 
                         facilityType, issueTitle, issueDetails, preferredDateTime, receptionDate, 
                         receptionStatus, receptionDelivery)
VALUES 
('executer10', 2, '최민호', '010-1111-2222', 'minho@example.com', '04123', '서울특별시 서초구 서초대로', '101동 1201호', 
 'lighting', '사무실 조명 고장', '사무실 천장 조명이 전혀 켜지지 않습니다.', '2025-01-03 09:30:00', NOW(), '접수 확인 중', '박기사'),

('executer10', 2, '김지영', '010-2222-3333', 'jiyeong@example.com', '08877', '서울특별시 송파구 올림픽로', '202동 1402호', 
 'power', '콘센트 전력 차단', '책상 옆 콘센트에서 전원이 들어오지 않습니다.', '2025-01-06 14:00:00', NOW(), '접수 확인 중', '최기사'),

('executer10', 2, '박상혁', '010-3333-4444', 'sanghyuk@example.com', '22388', '경기도 수원시 팔달구 중부대로', 'B동 501호', 
 'communication', '인터넷 연결 불량', '사무실 인터넷이 자주 끊깁니다.', '2025-01-08 16:30:00', NOW(), '접수 확인 중', '미배정'),

('executer10', 2, '윤서진', '010-4444-5555', 'seojin@example.com', '35266', '부산광역시 해운대구 해운대로', '3층 307호', 
 'plumbing', '화장실 배관 누수', '화장실 변기 옆 배관에서 물이 샙니다.', '2025-01-12 11:00:00', NOW(), '접수 확인 중', '박기사'),

('executer10', 2, '정도윤', '010-5555-6666', 'doyun@example.com', '48299', '대전광역시 서구 둔산로', '502동 1203호', 
 'hvac', '냉난방기 고장', '난방기를 켜도 찬바람만 나옵니다.', '2025-01-15 13:30:00', NOW(), '접수 확인 중', '미배정'),

('executer10', 2, '한승호', '010-6666-7777', 'seungho@example.com', '63245', '광주광역시 서구 상무대로', '오피스텔 7층', 
 'elevator', '엘리베이터 문 닫힘 불량', '엘리베이터 문이 닫히지 않습니다.', '2025-01-17 10:30:00', NOW(), '접수 확인 중', '최기사'),

('executer10', 2, '오지훈', '010-7777-8888', 'jihoon@example.com', '12988', '제주특별자치도 서귀포시 중앙로', '빌라 1층', 
 'fire', '화재경보 오작동', '화재경보가 아무 이유 없이 자주 울립니다.', '2025-01-21 15:00:00', NOW(), '접수 확인 중', '박기사'),

('executer10', 2, '배수연', '010-8888-9999', 'sooyeon@example.com', '27899', '울산광역시 남구 번영로', '주택 2층', 
 'security', 'CCTV 작동 불량', 'CCTV 화면이 나오지 않고 녹화가 되지 않습니다.', '2025-01-24 09:00:00', NOW(), '접수 확인 중', '미배정'),

('executer10', 2, '서진우', '010-9999-0000', 'jinwoo@example.com', '31288', '강원도 춘천시 중앙로', '아파트 1102호', 
 'emergency', '비상벨 고장', '비상벨을 눌러도 소리가 나지 않습니다.', '2025-01-28 10:00:00', NOW(), '접수 확인 중', '최기사'),

('executer10', 2, '김한결', '010-1234-5678', 'hankyeol@example.com', '47890', '전라남도 목포시 영산로', '아파트 402호', 
 'interior', '벽지 손상 신고', '사무실 벽지가 벗겨지고 있습니다.', '2025-02-01 14:00:00', NOW(), '접수 확인 중', '박기사'),

('executer10', 2, '유시온', '010-2345-6789', 'sion@example.com', '04567', '충청남도 천안시 서북구 충무로', '101동 503호', 
 'exterior', '건물 외벽 균열', '건물 외벽에 균열이 발생하였습니다.', '2025-02-04 11:00:00', NOW(), '접수 확인 중', '미배정'),

('executer10', 2, '임지훈', '010-3456-7890', 'jihoon@example.com', '05879', '경상남도 창원시 의창구 창원대로', '아파트 8층', 
 'other', '기타 시설 이상', '건물 내 안내 표지판이 떨어졌습니다.', '2025-02-08 16:30:00', NOW(), '접수 확인 중', '최기사'),

('executer10', 2, '이하은', '010-4567-8901', 'haeun@example.com', '12789', '서울특별시 용산구 한강대로', '601동 802호', 
 'lighting', '복도 조명 깜빡임', '아파트 복도 조명이 지속적으로 깜빡입니다.', '2025-02-12 09:30:00', NOW(), '접수 확인 중', '박기사'),

('executer10', 2, '신지윤', '010-5678-9012', 'jiyoon@example.com', '14899', '경기도 고양시 일산서구 중앙로', '3층 307호', 
 'power', '사무실 정전', '사무실 전체가 정전이 발생하였습니다.', '2025-02-15 14:30:00', NOW(), '접수 확인 중', '미배정'),

('executer10', 2, '최도현', '010-6789-0123', 'dohyun@example.com', '17899', '부산광역시 남구 수영로', '아파트 502호', 
 'communication', '전화 연결 불량', '사무실 전화가 걸려오지 않습니다.', '2025-02-18 15:00:00', NOW(), '접수 확인 중', '최기사');


INSERT INTO as_reception (userId, authorityId, name, phoneNumber, email, postcode, address, detailAddress, 
                         facilityType, issueTitle, issueDetails, preferredDateTime, receptionDate, 
                         receptionStatus, receptionDelivery)
VALUES 
('executer10', 2, '김하늘', '010-1010-2020', 'haneul@example.com', '04567', '서울특별시 종로구 종로1길', '10층', 
 'lighting', '복도 조명 깜빡임', '복도의 LED 조명이 깜빡거려서 교체가 필요합니다.', '2025-01-12 11:00:00', NOW(), '처리 완료', '박기사'),

('executer10', 2, '이서준', '010-2020-3030', 'seojoon@example.com', '07892', '경기도 성남시 분당구 판교로', '702호', 
 'power', '사무실 내 전원 차단', '한 층 전체 전원이 나갔습니다.', '2025-01-20 15:00:00', NOW(), '처리 완료', '최기사'),

('executer10', 2, '박지수', '010-3030-4040', 'jisu@example.com', '12987', '부산광역시 해운대구 달맞이길', '4층', 
 'communication', '인터넷 연결 불량', '사무실 와이파이 신호가 약하고 끊깁니다.', '2025-01-25 09:30:00', NOW(), '처리 완료', '미배정'),

('executer10', 2, '정민호', '010-4040-5050', 'minho@example.com', '23876', '대전광역시 유성구 도룡동', '903호', 
 'plumbing', '화장실 세면대 막힘', '물이 잘 내려가지 않습니다.', '2025-02-01 16:30:00', NOW(), '처리 완료', '박기사'),

('executer10', 2, '배수연', '010-5050-6060', 'suyeon@example.com', '32789', '광주광역시 서구 상무대로', 'B동 2층', 
 'hvac', '난방 작동 불량', '난방이 전혀 가동되지 않습니다.', '2025-02-02 13:00:00', NOW(), '처리 완료', '최기사'),

('executer10', 2, '윤태호', '010-6060-7070', 'taeho@example.com', '45678', '울산광역시 남구 삼산로', 'A동 401호', 
 'elevator', '엘리베이터 버튼 고장', '버튼을 눌러도 반응이 없습니다.', '2025-02-03 10:00:00', NOW(), '처리 완료', '미배정'),

('executer10', 2, '문가은', '010-7070-8080', 'gaeun@example.com', '56987', '경기도 고양시 일산서구 중앙로', '10층', 
 'fire', '화재경보 오작동', '사무실 내 화재경보기가 이유 없이 울립니다.', '2025-02-04 09:00:00', NOW(), '처리 완료', '박기사'),

('executer10', 2, '김지훈', '010-8080-9090', 'jihoon@example.com', '67834', '강원도 춘천시 중앙로', '301호', 
 'security', '출입문 잠금 고장', '도어락이 작동하지 않습니다.', '2025-02-05 14:30:00', NOW(), '접수 확인 중', '최기사'),

('executer10', 2, '이예진', '010-9090-0101', 'yejin@example.com', '78923', '충청남도 천안시 서북구 불당로', '오피스 7층', 
 'emergency', '비상구 문 잠김', '비상구 문이 내부에서도 열리지 않습니다.', '2025-02-06 15:00:00', NOW(), '접수 확인 중', '미배정'),

('executer10', 2, '박태영', '010-0101-1111', 'taeyoung@example.com', '89012', '전라남도 목포시 해안로', '2층', 
 'interior', '사무실 벽 페인트 벗겨짐', '벽 일부에서 페인트가 떨어지고 있습니다.', '2025-02-07 12:00:00', NOW(), '접수 확인 중', '박기사'),

('executer10', 2, '강서윤', '010-1111-2222', 'seoyun@example.com', '90123', '경상북도 경주시 보문로', '5층', 
 'exterior', '건물 외벽 균열', '외벽에 금이 가 있는 부분이 있습니다.', '2025-02-08 11:30:00', NOW(), '접수 확인 중', '최기사'),

('executer10', 2, '손유빈', '010-2222-3333', 'yubin@example.com', '01234', '제주특별자치도 제주시 애월읍', '1층', 
 'other', '기타 시설 보수 요청', '주차장 바닥 페인트가 많이 벗겨졌습니다.', '2025-02-09 09:30:00', NOW(), '접수 확인 중', '미배정'),

('executer10', 2, '김태윤', '010-3333-4444', 'taeyun@example.com', '12345', '서울특별시 강서구 공항대로', '15층', 
 'lighting', '사무실 조명 교체 요청', '조명이 어두워서 새로운 LED로 교체해야 합니다.', '2025-02-10 16:00:00', NOW(), '접수 확인 중', '박기사'),

('executer10', 2, '조서진', '010-4444-5555', 'seojin@example.com', '23456', '경기도 수원시 팔달구 인계로', 'B동 302호', 
 'power', '전력 사용량 급증 경고', '특정 시간대에 전력 사용량이 비정상적으로 높습니다.', '2025-02-11 14:00:00', NOW(), '접수 확인 중', '최기사'),

('executer10', 2, '윤도영', '010-5555-6666', 'doyoung@example.com', '34567', '부산광역시 남구 대연로', '3층', 
 'communication', '사내 전화 회선 문제', '일부 전화선이 작동하지 않습니다.', '2025-02-12 11:30:00', NOW(), '접수 확인 중', '미배정'),

('executer10', 2, '정하율', '010-6666-7777', 'hayul@example.com', '45678', '대전광역시 서구 둔산로', '6층', 
 'plumbing', '화장실 배수관 누수', '화장실 배수관에서 물이 샙니다.', '2025-02-13 10:00:00', NOW(), '접수 확인 중', '박기사');

INSERT INTO as_reception (userId, authorityId, name, phoneNumber, email, postcode, address, detailAddress, facilityType, issueTitle, issueDetails, preferredDateTime, receptionDate, receptionStatus, receptionDelivery) VALUES
('executer10', 2, '정민우', '010-1234-5678', 'minwoo@example.com', '04562', '서울특별시 중구 을지로', '101호', 
 'lighting', '사무실 조명 불량', '사무실 조명이 일부 켜지지 않습니다.', '2025-02-15 09:00:00', '2025-02-07 08:50:00', '접수 확인 중', '미배정'),

('executer10', 2, '박지윤', '010-2345-6789', 'jiyoon@example.com', '03112', '서울특별시 종로구 대학로', '502호', 
 'power', '전력 차단 문제', '사무실 전기가 가끔씩 꺼집니다.', '2025-02-16 10:00:00', '2025-02-08 09:20:00', '접수 확인 중', '미배정'),

('executer10', 2, '김도현', '010-3456-7890', 'dohyun@example.com', '06256', '서울특별시 강남구 역삼로', '20층 2003호', 
 'hvac', '난방기 과열 문제', '난방기가 너무 뜨겁게 작동합니다.', '2025-02-17 11:00:00', '2025-02-09 10:10:00', '접수 확인 중', '미배정'),

('executer10', 2, '이수호', '010-4567-8901', 'suho@example.com', '07346', '서울특별시 영등포구 국회대로', '1202호', 
 'fire', '화재경보 시스템 점검', '화재경보 시스템이 작동하지 않습니다.', '2025-02-18 13:00:00', '2025-02-10 09:30:00', '접수 확인 중', '미배정'),

('executer10', 2, '배서현', '010-5678-9012', 'seohyun@example.com', '08811', '서울특별시 관악구 남부순환로', '7층 701호', 
 'elevator', '엘리베이터 오작동', '엘리베이터 문이 제대로 닫히지 않습니다.', '2025-02-19 14:00:00', '2025-02-11 08:45:00', '접수 확인 중', '미배정'),

('executer10', 2, '손예준', '010-6789-0123', 'yejun@example.com', '01188', '서울특별시 종로구 인사동길', '5층 503호', 
 'security', '출입문 보안 문제', '출입문이 잠기지 않습니다.', '2025-02-20 15:00:00', '2025-02-12 11:15:00', '접수 확인 중', '미배정'),

('executer10', 2, '권민서', '010-7890-1234', 'minseo@example.com', '04589', '서울특별시 중구 명동길', '702호', 
 'plumbing', '화장실 배관 누수', '화장실 배관에서 물이 샙니다.', '2025-02-21 16:00:00', '2025-02-13 09:40:00', '접수 확인 중', '미배정'),

('executer10', 2, '장하린', '010-8901-2345', 'harin@example.com', '06241', '서울특별시 강남구 논현로', '4층 403호', 
 'hvac', '에어컨 가동 불가', '에어컨이 전혀 작동하지 않습니다.', '2025-02-22 10:00:00', '2025-02-14 10:50:00', '접수 확인 중', '미배정'),

('executer10', 2, '유태호', '010-9012-3456', 'taeho@example.com', '04782', '서울특별시 성동구 성수이로', '1304호', 
 'lighting', '복도 조명 교체 필요', '복도 조명이 너무 어둡습니다.', '2025-02-23 11:00:00', '2025-02-15 12:30:00', '접수 확인 중', '미배정'),

('executer10', 2, '백다현', '010-0123-4567', 'dahyun@example.com', '07364', '서울특별시 영등포구 국제금융로', '3층 305호', 
 'interior', '사무실 벽면 손상', '사무실 벽면이 파손되었습니다.', '2025-02-24 13:00:00', '2025-02-16 08:10:00', '접수 확인 중', '미배정'),

('executer10', 2, '강시우', '010-1234-5670', 'siwoo@example.com', '04122', '서울특별시 중구 서소문로', '901호', 
 'communication', '인터넷 연결 불안정', '사무실 인터넷이 자주 끊깁니다.', '2025-02-25 14:00:00', '2025-02-17 09:20:00', '접수 확인 중', '미배정'),

('executer10', 2, '윤도하', '010-2345-6781', 'doha@example.com', '08835', '서울특별시 관악구 신림로', '1502호', 
 'fire', '소방 시스템 점검', '소방 시스템이 작동하지 않습니다.', '2025-02-26 16:00:00', '2025-02-18 10:00:00', '접수 확인 중', '미배정'),

('executer10', 2, '임주혁', '010-3456-7892', 'juhyuk@example.com', '07271', '서울특별시 영등포구 여의대로', '10층 1003호', 
 'security', 'CCTV 작동 불량', 'CCTV 화면이 나오지 않습니다.', '2025-02-27 09:00:00', '2025-02-19 11:30:00', '접수 확인 중', '미배정'),

('executer10', 2, '양소윤', '010-4567-8903', 'soyun@example.com', '06253', '서울특별시 강남구 삼성로', '6층 604호', 
 'hvac', '환풍기 소음 문제', '환풍기에서 이상한 소리가 납니다.', '2025-02-28 10:00:00', '2025-02-20 09:15:00', '접수 확인 중', '미배정');
 
 INSERT INTO as_reception (userId, authorityId, name, phoneNumber, email, postcode, address, detailAddress, facilityType, issueTitle, issueDetails, preferredDateTime, receptionDate, receptionStatus, receptionDelivery) VALUES
('executer10', 2, '최민재', '010-1111-2222', 'minjae@example.com', '04511', '서울특별시 종로구 세종대로', '15층 1502호', 
 'lighting', '사무실 조명 수리', '조명이 깜빡거리고 있습니다.', '2025-03-01 09:00:00', '2025-02-21 08:50:00', '접수 확인 중', '미배정'),

('executer10', 2, '박서윤', '010-2222-3333', 'seoyoon@example.com', '03177', '서울특별시 중구 명동길', '3층 302호', 
 'power', '전력 공급 문제', '전기가 간헐적으로 차단됩니다.', '2025-03-05 10:00:00', '2025-02-25 09:20:00', '접수 확인 중', '미배정'),

('executer10', 2, '이준혁', '010-3333-4444', 'junhyuk@example.com', '06258', '서울특별시 강남구 테헤란로', '8층 801호', 
 'hvac', '냉방기 가동 불가', '에어컨이 작동하지 않습니다.', '2025-03-10 11:00:00', '2025-02-28 10:10:00', '접수 확인 중', '미배정'),

('executer10', 2, '김나연', '010-4444-5555', 'nayeon@example.com', '07331', '서울특별시 영등포구 국회대로', '1204호', 
 'fire', '화재 감지기 오작동', '화재 감지기가 자주 울립니다.', '2025-03-15 13:00:00', '2025-03-05 09:30:00', '접수 확인 중', '미배정'),

('executer10', 2, '손예빈', '010-5555-6666', 'yebin@example.com', '08801', '서울특별시 관악구 남부순환로', '6층 602호', 
 'elevator', '엘리베이터 문 닫힘 오류', '문이 닫힐 때 멈추는 현상이 발생합니다.', '2025-03-20 14:00:00', '2025-03-10 08:45:00', '접수 확인 중', '미배정'),

('executer10', 2, '정도현', '010-6666-7777', 'dohyun@example.com', '01121', '서울특별시 종로구 인사동길', '8층 803호', 
 'security', '출입 카드 인식 불량', '출입문이 카드 인식을 하지 않습니다.', '2025-03-25 15:00:00', '2025-03-15 11:15:00', '접수 확인 중', '미배정'),

('executer10', 2, '조하영', '010-7777-8888', 'hayoung@example.com', '04591', '서울특별시 중구 을지로', '2층 205호', 
 'plumbing', '화장실 배관 누수', '화장실 바닥에 물이 계속 고입니다.', '2025-03-30 16:00:00', '2025-03-20 09:40:00', '접수 확인 중', '미배정'),

('executer10', 2, '배준호', '010-8888-9999', 'junho@example.com', '06263', '서울특별시 강남구 논현로', '3층 304호', 
 'hvac', '환기구 냄새 문제', '환기구에서 심한 냄새가 납니다.', '2025-04-02 10:00:00', '2025-03-23 10:50:00', '접수 확인 중', '미배정'),

('executer10', 2, '윤다솔', '010-9999-0000', 'dasol@example.com', '04761', '서울특별시 성동구 성수이로', '6층 605호', 
 'lighting', '회의실 조명 교체 요청', '전구가 나가서 회의실이 어둡습니다.', '2025-04-07 11:00:00', '2025-03-28 12:30:00', '접수 확인 중', '미배정'),

('executer10', 2, '백소미', '010-0000-1111', 'somi@example.com', '07351', '서울특별시 영등포구 국제금융로', '10층 1006호', 
 'interior', '사무실 벽면 페인트 벗겨짐', '벽이 벗겨져 보기 안 좋습니다.', '2025-04-12 13:00:00', '2025-04-02 08:10:00', '접수 확인 중', '미배정'),

('executer10', 2, '강태민', '010-1111-2222', 'taemin@example.com', '04111', '서울특별시 중구 서소문로', '7층 705호', 
 'communication', '전화선 연결 문제', '사무실 전화가 끊어집니다.', '2025-04-17 14:00:00', '2025-04-07 09:20:00', '접수 확인 중', '미배정'),

('executer10', 2, '윤세진', '010-2222-3333', 'sejin@example.com', '08802', '서울특별시 관악구 신림로', '9층 902호', 
 'fire', '비상구 불빛 고장', '비상구 안내등이 작동하지 않습니다.', '2025-04-20 16:00:00', '2025-04-10 10:00:00', '접수 확인 중', '미배정'),

('executer10', 2, '임태연', '010-3333-4444', 'taeyeon@example.com', '07201', '서울특별시 영등포구 여의대로', '5층 502호', 
 'security', 'CCTV 녹화 불량', 'CCTV 영상이 저장되지 않습니다.', '2025-04-25 09:00:00', '2025-04-15 11:30:00', '접수 확인 중', '미배정'),

('executer10', 2, '양지후', '010-4444-5555', 'jihu@example.com', '06212', '서울특별시 강남구 삼성로', '2층 203호', 
 'hvac', '공조기 소음 발생', '공조기가 작동 시 심한 소음이 납니다.', '2025-04-30 10:00:00', '2025-04-20 09:15:00', '접수 확인 중', '미배정');



-- 더미 데이터 3월 ~ 4월
INSERT INTO as_reception (userId, authorityId, name, phoneNumber, email, postcode, address, detailAddress, 
                         facilityType, issueTitle, issueDetails, preferredDateTime, receptionDate, 
                         receptionStatus, receptionDelivery)
VALUES 
('executer10', 2, '김도윤', '010-1112-3456', 'doyun@example.com', '03145', '서울특별시 강남구 테헤란로', '1203호', 
 'lighting', '회의실 조명 깜빡임', '회의실 천장 조명이 지속적으로 깜빡입니다.', '2025-03-05 10:30:00', NOW(), '접수 확인 중', '박기사'),

('executer10', 2, '박서연', '010-2223-4567', 'seoyeon@example.com', '07821', '경기도 성남시 분당구 황새울로', 'A동 902호', 
 'power', '서버실 정전', '서버실 전원이 차단되어 복구가 필요합니다.', '2025-03-07 13:00:00', NOW(), '접수 확인 중', '최기사'),

('executer10', 2, '이민준', '010-3334-5678', 'minjun@example.com', '14234', '부산광역시 해운대구 센텀로', '오피스 10층', 
 'communication', '전화기 신호 없음', '사무실 내 유선 전화기가 작동하지 않습니다.', '2025-03-10 09:00:00', NOW(), '접수 확인 중', '미배정'),

('executer10', 2, '정하린', '010-4445-6789', 'harin@example.com', '28763', '대전광역시 유성구 대학로', '301호', 
 'plumbing', '세면대 배수 불량', '화장실 세면대 배수가 원활하지 않습니다.', '2025-03-12 14:30:00', NOW(), '접수 확인 중', '박기사'),

('executer10', 2, '윤건호', '010-5556-7890', 'gunho@example.com', '32789', '광주광역시 동구 충장로', '4층', 
 'hvac', '에어컨 냉방 불량', '사무실 에어컨에서 냉방이 잘 되지 않습니다.', '2025-03-15 15:00:00', NOW(), '접수 확인 중', '최기사'),

('executer10', 2, '배시온', '010-6667-8901', 'sionbae@example.com', '43678', '울산광역시 중구 태화로', 'B동 3층', 
 'elevator', '엘리베이터 소음', '엘리베이터에서 큰 소음이 발생합니다.', '2025-03-17 11:30:00', NOW(), '접수 확인 중', '미배정'),

('executer10', 2, '김준서', '010-7778-9012', 'junseo@example.com', '53421', '강원도 원주시 단계로', '아파트 1602호', 
 'fire', '화재감지기 경보 오작동', '사무실 화재감지기가 계속 울립니다.', '2025-03-20 09:30:00', NOW(), '접수 확인 중', '박기사'),

('executer10', 2, '이하율', '010-8889-0123', 'hayul@example.com', '62987', '충청북도 청주시 상당구 상당로', '302호', 
 'security', '출입문 잠금 불량', '출입문이 정상적으로 잠기지 않습니다.', '2025-03-22 14:00:00', NOW(), '접수 확인 중', '최기사'),

('executer10', 2, '정유진', '010-9990-1234', 'yujin@example.com', '78451', '경상남도 창원시 성산구 중앙대로', '오피스 2층', 
 'emergency', '비상조명 미점등', '비상시에 작동해야 할 조명이 켜지지 않습니다.', '2025-03-25 10:00:00', NOW(), '접수 확인 중', '미배정'),

('executer10', 2, '손지후', '010-1111-2345', 'jihuson@example.com', '81245', '전라북도 전주시 완산구 서곡로', '5층', 
 'interior', '복도 바닥 타일 파손', '복도 바닥 타일이 깨져서 교체가 필요합니다.', '2025-03-27 16:00:00', NOW(), '접수 확인 중', '박기사'),

('executer10', 2, '박도연', '010-2222-3456', 'doyeon@example.com', '92034', '서울특별시 마포구 월드컵로', '301호', 
 'exterior', '건물 외벽 도장 벗겨짐', '외벽 도장이 벗겨져서 보수가 필요합니다.', '2025-03-30 12:30:00', NOW(), '접수 확인 중', '최기사'),

('executer10', 2, '송서현', '010-3333-4567', 'seohyun@example.com', '10234', '경기도 용인시 기흥구 기흥로', '아파트 1103호', 
 'other', '기타 시설 보수 요청', '건물 내 스프링클러 고정 장치가 헐거워졌습니다.', '2025-04-02 09:00:00', NOW(), '접수 확인 중', '미배정'),

('executer10', 2, '문지안', '010-4444-5678', 'jian@example.com', '23894', '부산광역시 수영구 광안로', '3층', 
 'lighting', '복도 조명 교체 필요', '복도 전등이 꺼져 교체가 필요합니다.', '2025-04-05 14:00:00', NOW(), '접수 확인 중', '박기사'),

('executer10', 2, '이강민', '010-5555-6789', 'gangmin@example.com', '34988', '대전광역시 동구 중앙로', '1002호', 
 'power', '사무실 내 정전 발생', '한 구역에서만 전기가 들어오지 않습니다.', '2025-04-08 15:30:00', NOW(), '접수 확인 중', '최기사'),

('executer10', 2, '권수빈', '010-6666-7890', 'subin@example.com', '45987', '울산광역시 북구 명촌로', 'B동 402호', 
 'communication', '인터넷 연결 불량', '사무실 인터넷이 간헐적으로 끊깁니다.', '2025-04-10 11:00:00', NOW(), '접수 확인 중', '미배정'),

('executer10', 2, '배태양', '010-7777-8901', 'taeyang@example.com', '56823', '경상북도 포항시 북구 대이로', '아파트 1층', 
 'plumbing', '주방 싱크대 누수', '싱크대 하부에서 물이 새고 있습니다.', '2025-04-12 13:30:00', NOW(), '접수 확인 중', '박기사'),

('executer10', 2, '김도훈', '010-8888-9012', 'dohun@example.com', '62987', '강원도 강릉시 교동로', '상가 302호', 
 'hvac', '난방기 작동 불량', '난방기가 켜지지 않습니다.', '2025-04-15 09:30:00', NOW(), '접수 확인 중', '최기사');
