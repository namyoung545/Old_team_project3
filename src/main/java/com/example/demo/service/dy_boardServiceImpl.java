package com.example.demo.service;

import com.example.demo.entity.dy_boardData;
import com.example.demo.repository.dy_boardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class dy_boardServiceImpl implements dy_boardService {

    @Autowired
    private dy_boardRepository boardRepository;

    @Override
    public List<dy_boardData> getBoardList(int pageSize, int offset) {
        return boardRepository.findAll().stream()
                .skip(offset)
                .limit(pageSize)
                .toList();
    }

    @Override
    public int getTotalCount() {
        return (int) boardRepository.count();
    }

    @Override
    public void updateVisitCount(Long bnum) {
        dy_boardData board = boardRepository.findById(bnum)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        board.setVisitcount(board.getVisitcount() + 1);
        boardRepository.save(board);
    }

    @Override
    public dy_boardData getBoardById(Long bnum) {
        return boardRepository.findById(bnum)
                .orElseThrow(() -> new RuntimeException("Board not found"));
    }

    @Override
    public boolean register(dy_boardData board) {
        board.setPostdate(LocalDateTime.now()); // 현재 시간 설정
        board.setVisitcount(0); // 조회수 초기화
        boardRepository.save(board);
        return true;
    }

    @Override
    public boolean update(dy_boardData boardData) {
        if (boardRepository.existsById(boardData.getBnum())) {
            dy_boardData existingData = boardRepository.findById(boardData.getBnum())
                    .orElseThrow(() -> new RuntimeException("Board not found"));
    
            // 필드 업데이트 (ename이 null인 경우 기존 값 유지)
            existingData.setTitle(boardData.getTitle());
            existingData.setContent(boardData.getContent());
            existingData.setEname(boardData.getEname() != null ? boardData.getEname() : existingData.getEname());
    
            // 수정 시간 갱신
            existingData.setPostdate(LocalDateTime.now());
    
            boardRepository.save(existingData);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Long bnum) {
        if (boardRepository.existsById(bnum)) {
            boardRepository.deleteById(bnum);
            return true;
        }
        return false;
    }
}
