package com.example.demo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.asReceptionDTO;

@Service
public class SHFullCalendarService {

    @Autowired
    asReceptionService asReceptionService;

    public List<Map<String, Object>> getFullCalendarEvents(LocalDate start, LocalDate end) throws Exception {
        List<Map<String, Object>> events = new ArrayList<>();
        asReceptionDTO dto = new asReceptionDTO();
        dto.setAuthorityId(1);
        List<asReceptionDTO> receptionList = asReceptionService.AS_Status(dto);

        receptionList.stream()
            .forEach(reception -> {
                // System.out.println("reception : " + reception);
                Map<String, Object> event = new HashMap<>();

                event.put("name", getOrDefault(reception.getName(), "이름 없음"));
                event.put("phone", getOrDefault(reception.getPhoneNumber(), "연락처 없음"));
                event.put("email", getOrDefault(reception.getEmail(), "이메일 없음"));
                event.put("postCode", getOrDefault(reception.getPostcode(), "우편번호 없음"));
                event.put("address", getOrDefault(reception.getAddress(), "주소 없음"));
                event.put("detailAddress", getOrDefault(reception.getDetailAddress(), "상세 주소 없음"));
                event.put("facilityType", getOrDefault(reception.getFacilityType(), "시설 유형 없음"));
                event.put("issueTitle", getOrDefault(reception.getIssueTitle(), "상태 없음"));
                event.put("issueDetails", getOrDefault(reception.getIssueDetails(), "상세 내용 없음"));
                event.put("prefferedDateTime", getOrDefault(reception.getPreferredDateTime(), "날짜 없음"));
                event.put("receptionStatus", getOrDefault(reception.getReceptionStatus(), "접수 확인 중"));
                event.put("receptionDelivery", getOrDefault(reception.getReceptionDelivery(), "미배정"));
    
                events.add(event);
            });

        return events;
    }

    private String getOrDefault(String value, String defaultValue) {
        return value != null && !value.trim().isEmpty() ? value : defaultValue;
    }
}
