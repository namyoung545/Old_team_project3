package com.example.demo.dao;

import java.util.List;

import com.example.demo.dto.asReceptionDTO;

public interface asReceptionDAO {
    int AS_Reception(asReceptionDTO dto) throws Exception;

    List<asReceptionDTO> AS_Status(asReceptionDTO dto) throws Exception;

    void deliveryArrangement(int requestId, String receptionDelivery, String receptionStatus);

    int DeliveryAssignment(asReceptionDTO dto);
}
