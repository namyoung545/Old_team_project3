package com.example.demo.service;

import com.example.demo.entity.dy_elecData;
import com.example.demo.repository.dy_elecRepository;
import com.example.demo.service.dy_elecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class dy_elecServiceImpl implements dy_elecService {

    @Autowired
    private dy_elecRepository repository;

    @Override
    public List<dy_elecData> getElectricalFireData(Integer year, String region) {
        if (year != null && region != null) {
            return repository.findByYearAndRegion(year, region);
        } else if (year != null) {
            return repository.findByYear(year);
        } else if (region != null) {
            return repository.findByRegion(region);
        } else {
            return repository.findAll();
        }
    }
}
