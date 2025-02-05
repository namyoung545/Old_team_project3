package com.example.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<asReceptionDTO> getASStatusWithPaging(String userId, int authorityId, int page, int pageSize) {
          int offset = (page - 1) * pageSize;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("authorityId", authorityId);
        params.put("limit", pageSize);
        params.put("offset", offset);
        System.out.println(params);
        
        return asReceptionDAO.getASStatusWithPaging(params);
    }

    @Override
    public int getTotalPages(String userId, int authorityId, int pageSize) {
        int totalCount = asReceptionDAO.getTotalCount(userId, authorityId);
        return (int) Math.ceil((double) totalCount / pageSize);
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
