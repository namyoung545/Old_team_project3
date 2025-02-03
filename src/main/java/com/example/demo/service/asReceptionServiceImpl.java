package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.asReceptionDAO;
import com.example.demo.dto.asReceptionDTO;


@Service
public class asReceptionServiceImpl implements asReceptionService {

    @Autowired
    asReceptionDAO asReceptionDAO;

    @Override
    public int AS_Reception(asReceptionDTO dto) throws Exception {
        return asReceptionDAO.AS_Reception(dto);
    }

    @Override
    public List<asReceptionDTO> AS_Status(asReceptionDTO dto) throws Exception {
        return asReceptionDAO.AS_Status(dto);
    }

    @Override
    public void deliveryArrangement(int requestId, String receptionDelivery, String receptionStatus) {
        asReceptionDAO.deliveryArrangement(requestId, receptionDelivery, receptionStatus);
    }

    @Override
    public int DeliveryAssignment(asReceptionDTO dto) {

        return asReceptionDAO.DeliveryAssignment(dto);
    }

}
