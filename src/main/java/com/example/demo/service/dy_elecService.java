package com.example.demo.service;

import com.example.demo.entity.dy_elecData;

import java.util.List;

public interface dy_elecService {
    List<dy_elecData> getElectricalFireData(Integer year, String region);
}
