CREATE DATABASE old_team_project3;

USE old_team_project3;

-- CREATE TABLE ed_statistics(
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     year SMALLINT NOT NULL UNIQUE,
--     total_damage BIGINT NOT NULL,
--     cause_counts TEXT,
--     first_ignition_counts TEXT,
--     location_counts TEXT,
--     hourly_analysis TEXT,
--     region_counts TEXT
-- );

CREATE TABLE ed_statistics (
	id SMALLINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    year SMALLINT UNSIGNED NOT NULL UNIQUE,
    
    province TEXT,
    district TEXT,
    fire_type TEXT,
    heat_source_main TEXT,
    heat_source_sub TEXT,
    cause_main TEXT,
    cause_sub TEXT,
    ignition_material_main TEXT,
    ignition_material_sub TEXT,
    casualties_total TEXT,
    deaths TEXT,
    injuries TEXT,
    total_property_damage TEXT,
    location_main TEXT,
    location_mid TEXT,
    location_sub TEXT
);

DESCRIBE ed_statistics;
SELECT * FROM ed_statistics;
DROP TABLE ed_statistics;



