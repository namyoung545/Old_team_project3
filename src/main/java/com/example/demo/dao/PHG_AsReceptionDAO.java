package com.example.demo.dao;

import java.util.List;

import com.example.demo.dto.PHG_AsReceptionDTO;

public interface PHG_AsReceptionDAO {
    int AS_Reception(PHG_AsReceptionDTO dto) throws Exception;

    List<PHG_AsReceptionDTO> AS_Status(PHG_AsReceptionDTO dto) throws Exception;

    void deliveryArrangement(int requestId, String receptionDelivery, String receptionStatus);

    int DeliveryAssignment(PHG_AsReceptionDTO dto);
}
