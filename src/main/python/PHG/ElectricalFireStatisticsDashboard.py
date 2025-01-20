import json
import pandas as pd
from sqlalchemy import create_engine

# 데이터베이스 연결 정보를 한 곳에서 관리할 수 있도록 별도 변수로 선언합니다.
DATABASE_URI = 'mysql+pymysql://root:1234@192.168.30.89/old_team_project3'

def get_engine():
    return create_engine(DATABASE_URI)

class DefaultDataFrame:
    def fetch_fires():
        """
        fires 테이블의 전체 데이터를 조회하여 DataFrame으로 반환
        """
        engine = get_engine()
        try:
            query = "SELECT * FROM fires"
            df = pd.read_sql(query, engine)
            return df
        finally:
            engine.dispose()

    def fetch_fire_causes():
        """
        fire_causes 테이블의 전체 데이터를 조회하여 DataFrame으로 반환
        """
        engine = get_engine()
        try:
            query = "SELECT * FROM fire_causes"
            df = pd.read_sql(query, engine)
            return df
        finally:
            engine.dispose()

    def fetch_fire_ignitions():
        """
        fire_ignitions 테이블의 전체 데이터를 조회하여 DataFrame으로 반환
        """
        engine = get_engine()
        try:
            query = "SELECT * FROM fire_ignitions"
            df = pd.read_sql(query, engine)
            return df
        finally:
            engine.dispose()

    def fetch_fire_items():
        """
        fire_items 테이블의 전체 데이터를 조회하여 DataFrame으로 반환
        """
        engine = get_engine()
        try:
            query = "SELECT * FROM fire_items"
            df = pd.read_sql(query, engine)
            return df
        finally:
            engine.dispose()

    def fetch_fire_locations():
        """
        fire_locations 테이블의 전체 데이터를 조회하여 DataFrame으로 반환
        """
        engine = get_engine()
        try:
            query = "SELECT * FROM fire_locations"
            df = pd.read_sql(query, engine)
            return df
        finally:
            engine.dispose()

    def fetch_fire_regions():
        """
        fire_regions 테이블의 전체 데이터를 조회하여 DataFrame으로 반환
        """
        engine = get_engine()
        try:
            query = "SELECT * FROM fire_regions"
            df = pd.read_sql(query, engine)
            return df
        finally:
            engine.dispose()

    def fetch_fire_types():
        """
        fire_types 테이블의 전체 데이터를 조회하여 DataFrame으로 반환
        """
        engine = get_engine()
        try:
            query = "SELECT * FROM fire_types"
            df = pd.read_sql(query, engine)
            return df
        finally:
            engine.dispose()

def ElectricalFireStatistics():
    engine = get_engine()
    try:
        query = (" SELECT fr.region_province, COUNT(*) AS accident_count"
                 " FROM fires f"
                 " JOIN fire_regions fr"
                 " ON f.fire_region_id = fr.region_id"
                 " WHERE f.casualties_deaths = 1"
                 " AND f.date_time >= '2023-01-01'"
                 " GROUP BY fr.region_province;")


        df = pd.read_sql(query, engine)
        return json.dumps(df.to_dict(orient='records'), ensure_ascii=False, indent=2, default=str)
    finally:
            engine.dispose()

def RegionalIgnitionCauses():
    engine = get_engine()
    try:
        query = (" SELECT "
                " fr.region_province AS region,"
                " fi.ignition_source_category AS ignition_type,"
                " COUNT(*) AS ignition_count"
                " FROM fires f"
                " JOIN fire_regions fr"
                " ON f.fire_region_id = fr.region_id"
                " JOIN fire_ignitions fi"
                " ON f.fire_ignition_id = fi.ignition_id"
                " WHERE f.casualties_deaths = 1"
                " AND f.date_time >= '2023-01-01'"
                " GROUP BY fr.region_province, fi.ignition_source_category;")


        df = pd.read_sql(query, engine)
        return json.dumps(df.to_dict(orient='records'), ensure_ascii=False, indent=2, default=str)
    finally:
            engine.dispose()