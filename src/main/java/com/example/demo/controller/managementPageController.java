package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.memberDTO;
import com.example.demo.service.memberService;
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

    @Autowired
    memberService memberService;

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
    @GetMapping({ "/boardPost" })
    public String boardPost(@RequestParam(value = "bnum", defaultValue = "1") Long bnum,
            @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
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
    @GetMapping({ "/qnaboardPost" })
    public String qnaboardPost(@RequestParam(value = "bnum", defaultValue = "1") Long bnum,
            @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
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
    public String insertAsReception(asReceptionDTO asReceptionDTO, Model model, asReceptionDTO dto) {
        try {
            int DeliveryAssignment = asReceptionService.DeliveryAssignment(dto);
            System.out.println("DeliveryAssignment : " + DeliveryAssignment);
            // model.addAttribute("DeliveryAss1ignment", DeliveryAssignment);
            if (DeliveryAssignment < 1) {
                model.addAttribute("msg", "해당 시각은 예약이 가득 찼습니다.");
                model.addAttribute("url", "/managementPage/registAS"); // 접수 페이지 URL
                return "/alertPrint";
            }

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
            System.out.println("PreferredDate 체크: " + asReceptionDTO.getPreferredDateTime());
            e.printStackTrace();
        }

        return "/alertPrint";
    }

    @GetMapping("/registAS/checkAvailableTimeSlots")
    @ResponseBody
    public ResponseEntity<?> checkAvailableTimeSlots(@RequestParam("selectedDate") String selectedDate) {
        try {
            // 메서드 진입 로그
            System.out.println(">>> checkAvailableTimeSlots() 호출됨. 선택된 날짜: " + selectedDate);

            List<String> availableTimeSlots = new ArrayList<>();

            String[] timeSlots = {
                    "09:00:00", "10:00:00", "11:00:00", "12:00:00",
                    "14:00:00", "15:00:00", "16:00:00", "17:00:00"
            };

            for (String timeSlot : timeSlots) {
                String fullDateTime = selectedDate + " " + timeSlot;

                // 반복문 내 변수 확인용 로그
                System.out.println(">>> 현재 확인 중인 시간 슬롯: " + fullDateTime);

                asReceptionDTO dto = new asReceptionDTO();

                dto.setPreferredDateTime(fullDateTime);

                // 0과 1 이상으로 비교
                int DeliveryAssignment = asReceptionService.DeliveryAssignment(dto);

                // 서비스 호출 후 결과 확인 로그
                System.out.println(">>> DeliveryAssignment 결과: " + DeliveryAssignment);

                if (DeliveryAssignment > 0) {
                    System.out.println(">>> " + timeSlot + " 시간대는 사용 가능합니다.");
                    availableTimeSlots.add(timeSlot);
                } else {
                    System.out.println(">>> " + timeSlot + " 시간대는 사용 불가능합니다.");
                }
            }

            // 최종 결과 로그
            System.out.println(">>> 사용 가능한 모든 시간대: " + availableTimeSlots);

            return ResponseEntity.ok(availableTimeSlots);
        } catch (Exception e) {
            // 에러 시 로그
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("시간 슬롯 조회 중 오류 발생: " + e.getMessage());
        }
    }

    // as 처리현황 페이지
    @GetMapping("/ASprocessStatus")
    public String getASprocessStatus(
            HttpSession session,
            Model model,
            asReceptionDTO asReceptionDTO,
            memberDTO memberDTO,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
            )
            throws Exception {

        if (session.getAttribute("userId") == null) {
            model.addAttribute("msg", "로그인이 필요합니다.");
            model.addAttribute("url", "/login");
            return "/alertPrint";
        }

        // 세션에서 사용자 정보 가져오기
        String userId = (String) session.getAttribute("userId");
        int authorityId = (int) session.getAttribute("authorityId");

        // DTO에 사용자 정보 설정
        asReceptionDTO.setUserId(userId);
        asReceptionDTO.setAuthorityId(authorityId);

        // 정보 가져오기
        List<memberDTO> deliverySelect = memberService.deliverySelect(memberDTO);
        List<asReceptionDTO> asReceptionList = asReceptionService.AS_Status(asReceptionDTO);
        model.addAttribute("asReceptionList", asReceptionList);
        model.addAttribute("deliverySelect", deliverySelect);

        // 페이징 처리
        List<asReceptionDTO> receptions = asReceptionService.getASStatusWithPaging(userId, authorityId, page, pageSize);
        int totalPages = asReceptionService.getTotalPages(userId, authorityId, pageSize);

        model.addAttribute("receptions", receptions);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);

        return "ASprocessStatusBoard"; // jsp 파일 경로
    }

    @PostMapping("/ASprocessStatus/deliveryArrangement")
    public String deliveryArrangement(@RequestParam("selectedRequestId") String requestId,
            @RequestParam("receptionDelivery") String receptionDelivery,
            @RequestParam("receptionStatus") String receptionStatus,
            Model model) {

        asReceptionService.deliveryArrangement(Integer.parseInt(requestId), receptionDelivery, receptionStatus);
        try {
            model.addAttribute("msg", "배정 완료되었습니다.");
            model.addAttribute("url", "/managementPage/ASprocessStatus"); // 목록 페이지 URL

        } catch (Exception e) {
            model.addAttribute("msg", "문제 발생");
            model.addAttribute("url", "/managementPage/ASprocessStatus"); // 접수 페이지 URL
            e.printStackTrace();
        }
        return "/alertPrint";
    }

    @GetMapping("/deepChatllm")
    public String getChatllm() {
        return "chatllm";
    }

    @GetMapping("/threejs")
    public String getthreejs() {
        return "threeJS";
    }

}
