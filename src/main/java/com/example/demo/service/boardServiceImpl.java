package com.example.demo.service;

import com.example.demo.entity.boardData;
import com.example.demo.entity.qnaboardData;
import com.example.demo.repository.boardRepository;
import com.example.demo.repository.qnaboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class boardServiceImpl implements boardService {
    
    @Autowired
    private boardRepository boardRepository;

    @Autowired
    private qnaboardRepository qnaboardRepository;

    @Override
    public List<boardData> getBoardList(int pageSize, int offset) {
        return boardRepository.findAllByOrderByBnumDesc().stream()
                .skip(offset)
                .limit(pageSize)
                .toList();
    }

    @Override
    public List<qnaboardData> getqnaBoardList(int pageSize, int offset) {
        return qnaboardRepository.findAllByOrderByBnumDesc().stream()
                .skip(offset)
                .limit(pageSize)
                .toList();
    }

    @Override
    public int getTotalCount() {
        return (int) boardRepository.count();
    }

    @Override
    public int getqnaTotalCount() {
        return (int) qnaboardRepository.count();
    }

    @Override
    public void updateVisitCount(Long bnum) {
        boardData board = boardRepository.findById(bnum)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        board.setVisitcount(board.getVisitcount() + 1);
        boardRepository.save(board);
    }

    @Override
    public void updateqnaVisitCount(Long bnum) {
        qnaboardData qnaboard = qnaboardRepository.findById(bnum)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        qnaboard.setVisitcount(qnaboard.getVisitcount() + 1);
        qnaboardRepository.save(qnaboard);
    }

    @Override
    public boardData getBoardById(Long bnum) {
        return boardRepository.findById(bnum)
                .orElseThrow(() -> new RuntimeException("Board not found"));
    }

    @Override
    public qnaboardData getqnaBoardById(Long bnum) {
        return qnaboardRepository.findById(bnum)
                .orElseThrow(() -> new RuntimeException("Board not found"));
    }

    @Override
    public boolean register(boardData board) {
        board.setPostdate(LocalDateTime.now()); // 현재 시간 설정
        board.setVisitcount(0); // 조회수 초기화
        boardRepository.save(board);
        return true;
    }

    @Override
    public boolean registerqna(qnaboardData qnaboard) {
        qnaboard.setPostdate(LocalDateTime.now()); // 현재 시간 설정
        qnaboard.setVisitcount(0); // 조회수 초기화
        qnaboardRepository.save(qnaboard);
        return true;
    }

    @Override
    public boolean update(boardData boardData) {
        if (boardRepository.existsById(boardData.getBnum())) {
            boardData existingData = boardRepository.findById(boardData.getBnum())
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
    public boolean updateqna(qnaboardData qnaboardData) {
        if (boardRepository.existsById(qnaboardData.getBnum())) {
            qnaboardData existingData = qnaboardRepository.findById(qnaboardData.getBnum())
                    .orElseThrow(() -> new RuntimeException("Board not found"));
    
            // 필드 업데이트 (ename이 null인 경우 기존 값 유지)
            existingData.setTitle(qnaboardData.getTitle());
            existingData.setContent(qnaboardData.getContent());
            existingData.setEname(qnaboardData.getEname() != null ? qnaboardData.getEname() : existingData.getEname());
    
            // 수정 시간 갱신
            existingData.setPostdate(LocalDateTime.now());
    
            qnaboardRepository.save(existingData);
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

    @Override
    public boolean deleteqna(Long bnum) {
        if (qnaboardRepository.existsById(bnum)) {
            qnaboardRepository.deleteById(bnum);
            return true;
        }
        return false;
    }
}
