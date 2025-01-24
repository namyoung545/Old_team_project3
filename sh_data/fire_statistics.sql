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

DESCRIBE disasters;
SELECT * FROM disasters;
-- DROP TABLE disasters; 
DESCRIBE fires;
SELECT * FROM fires;
SELECT * FROM fire_causes;

-- DROP TABLE fire_statistics;
DESCRIBE fire_statistics;
SELECT * FROM fire_statistics ORDER BY id DESC;
SELECT * FROM fire_statistics WHERE year = "2023";
SELECT * FROM fire_statistics WHERE stat_name LIKE "%재산피해%" AND year = "2023" ORDER BY id DESC;

-- DELETE FROM fire_statistics WHERE stat_name LIKE ("%인명피해 합계(원인)%") LIMIT 100; 
-- TRUNCATE TABLE fire_statistics;