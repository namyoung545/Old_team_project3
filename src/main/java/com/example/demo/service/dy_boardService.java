package com.example.demo.service;
import com.example.demo.entity.dy_boardData;
import java.util.List;

public interface dy_boardService {
    List<dy_boardData> getBoardList(int pageSize, int offset);
    int getTotalCount();
    void updateVisitCount(Long bnum);
    dy_boardData getBoardById(Long bnum);
    boolean register(dy_boardData board);
    boolean update(dy_boardData board);
    boolean delete(Long bnum);
}
