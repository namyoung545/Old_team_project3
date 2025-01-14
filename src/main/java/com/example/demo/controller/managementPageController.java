package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entity.dy_boardData;
import com.example.demo.service.dy_boardService;

@Controller
@RequestMapping("/managementPage")
public class managementPageController {
    @Autowired
    private dy_boardService service;
    
    @GetMapping("")
    public String getindex(Model model) {
        model.addAttribute("headerState", "default");
        return "managementPage";
    }

    @GetMapping("/boardIndex")
    public String getBoardIndex(Model model) {
	// public String boardList(Model model, @RequestParam(defaultValue = "1") int page) {
	// 	int pageSize = 10; // 한 페이지에 표시할 게시물 수
	// 	int offset = (page - 1) * pageSize;

	// 	// 현재 페이지에 해당하는 게시물 목록 조회
	// 	List<BoardDTO> boardList = boardMapper.getBoardList(pageSize, offset);
	// 	model.addAttribute("boardlist", boardList);

	// 	// 전체 게시물 수 조회 (페이징 계산을 위한)
	// 	int totalCount = boardMapper.getTotalCount();
	// 	int totalPages = (int) (Math.ceil((double) totalCount / pageSize));

	// 	// 페이징 정보 모델에 추가
	// 	model.addAttribute("currentPage", page);
	// 	model.addAttribute("totalPages", totalPages);
        model.addAttribute("isDashboard", true); // 특정 페이지 여부 전달
		return "BoardIndex";
	}

    // 공지사항 게시판 페이지
	@GetMapping("/noticeBoard")
    public String getNoticeBoard(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 10; // 페이지당 게시물 수
        int offset = (page - 1) * pageSize;

        List<dy_boardData> boardList = service.getBoardList(pageSize, offset);
        model.addAttribute("boardlist", boardList);

        int totalCount = service.getTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("isDashboard", true); // 특정 페이지 여부 전달
		return "noticeBoard"; // jsp 파일 경로
	}

    // QnA 게시판 페이지
	@GetMapping("/qnaBoard")
    public String getQnaBoard(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 10; // 페이지당 게시물 수
        int offset = (page - 1) * pageSize;

        List<dy_boardData> boardList = service.getBoardList(pageSize, offset);
        model.addAttribute("boardlist", boardList);

        int totalCount = service.getTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("isDashboard", true); // 특정 페이지 여부 전달
		return "qnaBoard"; // jsp 파일 경로
	}

    @GetMapping("/fullCalendar")
    public String loadFullCalendarPage(Model model) {
        model.addAttribute("isDashboard", true); // 특정 페이지 여부 전달
        return "fullCalendar"; // templates/fullCalendar.html
    }

    // FullCalendar 이벤트 데이터 제공 (JSON)
    @ResponseBody
    @GetMapping("/full-calendar-events")
    public List<Map<String, Object>> getFullCalendarEvents() {
        List<Map<String, Object>> events = new ArrayList<>();

        // 예제 데이터
        Map<String, Object> event1 = new HashMap<>();
        event1.put("title", "회의");
        event1.put("start", "2025-01-10");

        Map<String, Object> event2 = new HashMap<>();
        event2.put("title", "프로젝트 마감");
        event2.put("start", "2025-01-15");

        events.add(event1);
        events.add(event2);

        return events; // JSON으로 반환
    }

    // as 등록 페이지
    @GetMapping("/registAS")
    public String scheduleRegistAS(Model model) {
        model.addAttribute("isDashboard", true); // 특정 페이지 여부 전달
        return "registAS";
    }

    // as 처리현황 페이지
	@GetMapping("/ASprocessStatus")
    public String getASprocessStatus(Model model) {
	// public String getProcessStatus(Model model) {
		// List<ReservationDTO> statusList = scheduleMapper.getStatusList();
		// model.addAttribute("statusList", statusList);
        model.addAttribute("isDashboard", true); // 특정 페이지 여부 전달
		return "ASprocessStatusBoard"; // jsp 파일 경로
	}

}
