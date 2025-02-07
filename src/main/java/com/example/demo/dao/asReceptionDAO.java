package com.example.demo.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.asReceptionDTO;

public interface asReceptionDAO {
    int AS_Reception(asReceptionDTO dto) throws Exception;

    List<asReceptionDTO> AS_Status(asReceptionDTO dto) throws Exception;

    List<asReceptionDTO> getASStatusWithPaging(@Param("params") Map<String, Object> params);

    int getTotalCount(@Param("userId") String userId, @Param("authorityId") int authorityId);

    void deliveryArrangement(int requestId, String receptionDelivery, String receptionStatus);

    int DeliveryAssignment(asReceptionDTO dto);
}
