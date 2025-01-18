package com.example.demo.service;

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

}
