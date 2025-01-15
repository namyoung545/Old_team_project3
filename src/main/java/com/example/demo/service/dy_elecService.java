package com.example.demo.service;

import java.util.List;
import java.util.Map;

public interface dy_elecService {
    /**
     * 데이터를 JSON 형식으로 반환합니다.
     * @return 연도별, 지역별 화재 데이터 리스트
     */
    List<Map<String, Object>> getFireDataAsJson();
}
