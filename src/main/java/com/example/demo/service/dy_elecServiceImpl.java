// package com.example.demo.service;

// import com.example.demo.repository.dy_elecRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// @Service
// public class dy_elecServiceImpl implements dy_elecService {

//     @Autowired
//     private dy_elecRepository elecRepository;

//     @Override
//     public List<Map<String, Object>> getFireDataAsJson() {
//         List<Object[]> results = elecRepository.getFireDataByYearAndRegion();
//         List<Map<String, Object>> jsonResponse = new ArrayList<>();

//         for (Object[] row : results) {
//             Map<String, Object> dataMap = new HashMap<>();
//             dataMap.put("year", row[0]);
//             dataMap.put("region", row[1]);
//             dataMap.put("total_incidents", row[2]);
//             dataMap.put("total_damage", row[3]);
//             jsonResponse.add(dataMap);
//         }

//         return jsonResponse;
//     }
// }
