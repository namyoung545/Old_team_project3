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
DELETE FROM as_reception WHERE detailAddress = "" LIMIT 100;
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

