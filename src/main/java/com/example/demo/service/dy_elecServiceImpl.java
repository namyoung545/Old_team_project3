package com.example.demo.service;

import com.example.demo.entity.dy_elecData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class dy_elecServiceImpl implements dy_elecService {

    @Override
    public List<dy_elecData> getAllElectricFires() {
        List<dy_elecData> data = new ArrayList<>();
        return data;
    }
}
