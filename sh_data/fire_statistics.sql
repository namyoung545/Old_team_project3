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

-- Table: fire_statistics (통계 데이터 저장)
CREATE TABLE fire_statistics (
    year INT NOT NULL,
    category VARCHAR(100) NOT NULL,
    value FLOAT NOT NULL,
    PRIMARY KEY (year, category)
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

DESCRIBE fires;
SELECT COUNT(*) as cnt FROM fires;
-- DROP TABLE fires;

SELECT * FROM fires;
SELECT * FROM fire_causes;
SELECT COUNT(*) as cnt FROM fire_causes;
SELECT * FROM fire_locations;
SELECT * FROM fire_items;
