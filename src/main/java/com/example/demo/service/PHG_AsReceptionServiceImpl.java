package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.PHG_AsReceptionDAO;
import com.example.demo.dto.PHG_AsReceptionDTO;

@Service
public class PHG_AsReceptionServiceImpl implements PHG_AsReceptionService {

    @Autowired
    PHG_AsReceptionDAO asReceptionDAO;

    @Override
    public int AS_Reception(PHG_AsReceptionDTO dto) throws Exception {
        return asReceptionDAO.AS_Reception(dto);
    }

    @Override
    public List<PHG_AsReceptionDTO> AS_Status(PHG_AsReceptionDTO dto) throws Exception {
        return asReceptionDAO.AS_Status(dto);
    }

    @Override
    public void deliveryArrangement(int requestId, String receptionDelivery, String receptionStatus) {
        asReceptionDAO.deliveryArrangement(requestId, receptionDelivery, receptionStatus);
    }

}
