package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.dy_boardData;
import com.example.demo.repository.dy_boardRepository;

@Service
public class dy_TestService {
    @Autowired
    private dy_boardRepository boardRepository;

    public void test() {
        System.out.println("TestService test() 호출");
        dy_boardData data = new dy_boardData();
        data.setTitle("test");
        boardRepository.save(data);
        boardRepository.findAll().forEach(board -> {
            System.out.println(board);
        });
    }
}
