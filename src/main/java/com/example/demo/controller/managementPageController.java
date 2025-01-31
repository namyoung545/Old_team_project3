package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.asReceptionDTO;
import com.example.demo.entity.boardData;
import com.example.demo.entity.qnaboardData;
import com.example.demo.service.asReceptionService;
import com.example.demo.service.boardService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/managementPage")
public class managementPageController {
    @Autowired
    private boardService service;

    @Autowired
    asReceptionService asReceptionService;
    
    @GetMapping("")
    public String getindex() {
        return "managementPage";
    }

    @GetMapping("/boardIndex")
    public String getBoardIndex() {
		return "BoardIndex";
	}

    // 공지사항 게시판 페이지
	@GetMapping("/noticeBoard")
    public String getNoticeBoard(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 10; // 페이지당 게시물 수
        int offset = (page - 1) * pageSize;

        List<boardData> boardList = service.getBoardList(pageSize, offset);
        model.addAttribute("boardlist", boardList);

        int totalCount = service.getTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
		return "noticeBoard"; // jsp 파일 경로
	}

    // 게시글 상세보기 및 수정 페이지
    @GetMapping({"/boardPost"})
    public String boardPost(@RequestParam(value = "bnum", defaultValue = "1") Long bnum, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        service.updateVisitCount(bnum); // 조회수 증가
        model.addAttribute("board", service.getBoardById(bnum));
        model.addAttribute("page", page); // 페이지 정보 추가
        return "boardPost"; // post.html 템플릿 반환
    }

    // 게시글 등록 페이지
    @GetMapping("/boardRegister")
    public String boardRegister() {
        return "boardRegister"; // dy_register.html 템플릿 반환
    }

    // 게시글 등록 처리
    @PostMapping("/boardRegister")
    public String register(boardData boardData, RedirectAttributes rttr) {
        service.register(boardData); // 엔티티 저장
        rttr.addFlashAttribute("result", boardData.getBnum()); // 저장된 게시글 번호 전달
        return "redirect:/managementPage/noticeBoard"; // 등록 후 목록 페이지로 리다이렉트
    }
    
    // GET 요청: 수정 화면 제공
    @GetMapping("/boardUpdate")
    public String updateForm(@RequestParam("bnum") Long bnum, Model model) {
        boardData boardData = service.getBoardById(bnum);
        model.addAttribute("board", boardData);
        return "boardUpdate"; // 수정 화면 템플릿 반환
    }
    // POST 요청: 수정 작업 처리
    @PostMapping("/boardUpdate")
    public String update(boardData boardData, RedirectAttributes rttr) {
        if (service.update(boardData)) {
            rttr.addFlashAttribute("result", "success");
        } else {
            rttr.addFlashAttribute("result", "fail");
        }
        return "redirect:/managementPage/boardPost?bnum=" + boardData.getBnum(); // 수정 후 상세보기로 리다이렉트
    }

    // 게시글 삭제 처리
    @PostMapping("/boardDelete")
    public String delete(@RequestParam("bnum") Long bnum, RedirectAttributes rttr) {
        if (service.delete(bnum)) {
            rttr.addFlashAttribute("result", "success");
        } else {
            rttr.addFlashAttribute("result", "fail");
        }
        return "redirect:/managementPage/noticeBoard";
    }

    // QnA 게시판 페이지
	@GetMapping("/qnaBoard")
    public String getQnaBoard(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 10; // 페이지당 게시물 수
        int offset = (page - 1) * pageSize;

        List<qnaboardData> qnaboardList = service.getqnaBoardList(pageSize, offset);
        model.addAttribute("qnaboardlist", qnaboardList);

        int totalCount = service.getqnaTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
		return "qnaBoard"; // jsp 파일 경로
	}

    // qna 게시글 상세보기 및 수정 페이지
    @GetMapping({"/qnaboardPost"})
    public String qnaboardPost(@RequestParam(value = "bnum", defaultValue = "1") Long bnum, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        service.updateqnaVisitCount(bnum); // 조회수 증가
        model.addAttribute("board", service.getqnaBoardById(bnum));
        model.addAttribute("page", page); // 페이지 정보 추가
        return "qnaboardPost"; // post.html 템플릿 반환
    }

    // qna 게시글 등록 페이지
    @GetMapping("/qnaboardRegister")
    public String qnaboardRegister() {
        return "qnaboardRegister"; // dy_register.html 템플릿 반환
    }

    // qna 게시글 등록 처리
    @PostMapping("/qnaboardRegister")
    public String qnaregister(qnaboardData qnaboardData, RedirectAttributes rttr) {
        service.registerqna(qnaboardData); // 엔티티 저장
        rttr.addFlashAttribute("result", qnaboardData.getBnum()); // 저장된 게시글 번호 전달
        return "redirect:/managementPage/qnaBoard"; // 등록 후 목록 페이지로 리다이렉트
    }
    
    // GET 요청: qna 수정 화면 제공
    @GetMapping("/qnaboardUpdate")
    public String updateqnaForm(@RequestParam("bnum") Long bnum, Model model) {
        qnaboardData qnaboardData = service.getqnaBoardById(bnum);
        model.addAttribute("board", qnaboardData);
        return "qnaboardUpdate"; // 수정 화면 템플릿 반환
    }
    // POST 요청: qna 수정 작업 처리
    @PostMapping("/qnaboardUpdate")
    public String updateqna(qnaboardData qnaboardData, RedirectAttributes rttr) {
        if (service.updateqna(qnaboardData)) {
            rttr.addFlashAttribute("result", "success");
        } else {
            rttr.addFlashAttribute("result", "fail");
        }
        return "redirect:/managementPage/qnaboardPost?bnum=" + qnaboardData.getBnum(); // 수정 후 상세보기로 리다이렉트
    }

    // qna 게시글 삭제 처리
    @PostMapping("/qnaboardDelete")
    public String deleteqna(@RequestParam("bnum") Long bnum, RedirectAttributes rttr) {
        if (service.deleteqna(bnum)) {
            rttr.addFlashAttribute("result", "success");
        } else {
            rttr.addFlashAttribute("result", "fail");
        }
        return "redirect:/managementPage/qnaBoard";
    }

    @GetMapping("/fullCalendar")
    public String loadFullCalendarPage(Model model) {
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
    public String scheduleRegistAS(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            model.addAttribute("msg", "로그인이 필요합니다.");
            model.addAttribute("url", "/login");
            return "/alertPrint";
        }
        return "registAS";
    }

    @PostMapping("/registAS/insert")
    public String insertAsReception(asReceptionDTO asReceptionDTO, Model model) {
        try {
            int result = asReceptionService.AS_Reception(asReceptionDTO);

            if (result > 0) {
                model.addAttribute("msg", "A/S 접수가 성공적으로 완료되었습니다.");
                model.addAttribute("url", "/managementPage"); // 목록 페이지 URL
            } else {
                model.addAttribute("msg", "A/S 접수 처리 중 문제가 발생했습니다.");
                model.addAttribute("url", "/managementPage/registAS"); // 접수 페이지 URL
            }

        } catch (Exception e) {
            model.addAttribute("msg", "시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            model.addAttribute("url", "/managementPage/registAS");
        }

        return "/alertPrint";
    }

    // as 처리현황 페이지
	@GetMapping("/ASprocessStatus")
    public String getASprocessStatus() {
	// public String getProcessStatus(Model model) {
		// List<ReservationDTO> statusList = scheduleMapper.getStatusList();
		// model.addAttribute("statusList", statusList);
		return "ASprocessStatusBoard"; // jsp 파일 경로
	}

}
