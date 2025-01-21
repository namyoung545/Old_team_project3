package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.asReceptionDTO;

public interface asReceptionService {
    int AS_Reception(asReceptionDTO dto) throws Exception;

    List<asReceptionDTO> AS_Status(asReceptionDTO dto) throws Exception;
}
