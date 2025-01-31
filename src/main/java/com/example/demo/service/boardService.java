package com.example.demo.service;

import java.util.List;
import com.example.demo.entity.boardData;
import com.example.demo.entity.qnaboardData;

public interface boardService {
    List<boardData> getBoardList(int pageSize, int offset);
    List<qnaboardData> getqnaBoardList(int pageSize, int offset);
    int getTotalCount();
    int getqnaTotalCount();
    void updateVisitCount(Long bnum);
    void updateqnaVisitCount(Long bnum);
    boardData getBoardById(Long bnum);
    qnaboardData getqnaBoardById(Long bnum);
    boolean register(boardData board);
    boolean registerqna(qnaboardData board);
    boolean update(boardData board);
    boolean updateqna(qnaboardData board);
    boolean delete(Long bnum);
    boolean deleteqna(Long bnum);
}