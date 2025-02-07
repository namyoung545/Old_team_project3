package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.asReceptionDTO;

public interface asReceptionService {
    int AS_Reception(asReceptionDTO dto) throws Exception;

    List<asReceptionDTO> AS_Status(asReceptionDTO dto) throws Exception;

    List<asReceptionDTO> getASStatusWithPaging(String userId, int authorityId, int page, int pageSize);

    int getTotalPages(String userId, int authorityId,int pageSize);

    void deliveryArrangement(int requestId, String receptionDelivery, String receptionStatus);

    int DeliveryAssignment(asReceptionDTO dto);

}
