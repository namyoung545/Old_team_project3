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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.PHG_AsReceptionDTO;
import com.example.demo.dto.PHG_MemberDTO;
import com.example.demo.service.PHG_AsReceptionService;
import com.example.demo.service.PHG_MemberService;
import com.example.demo.utils.PHG_PythonExecutor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class PHGController {

    @Autowired
    PHG_MemberService memberService;

    @Autowired
    PHG_AsReceptionService asReceptionService;

    @GetMapping("/PHG_index")
    public String getIndex() {
        return "/PHG/PHG_index";
    }

    @GetMapping("/PHG_login")
    public String getLogin() {
        return "/PHG/PHG_login";
    }

    @GetMapping("/PHG_join")
    public String getSingUp() {
        return "/PHG/PHG_join";
    }

    @ResponseBody
    @PostMapping("/PHG_join/idOverlap")
    public Integer idOverlap(PHG_MemberDTO memberDTO) throws Exception {
        try {
            int result = memberService.idOverlap(memberDTO);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // 에러 발생 시 -1 반환
        }
    }

    @PostMapping("/PHG_join/register")
    public String postJoin(PHG_MemberDTO memberDTO, Model model) throws Exception {
        System.out.println("PHGController : " + memberDTO);
        int result = idOverlap(memberDTO);

        // 아이디 중복 시 회원가입 페이지로 리다이렉트
        if (result != 0) {
            return "redirect:/PHG_join";
        }

        // 회원 정보 등록(회원가입 완료 시 DB에 암호화된 비밀번호 저장)
        memberService.register(memberDTO);

        // 성공 메시지와 로그인 페이지 URL 설정
        model.addAttribute("msg", "회원 가입이 완료되었읍니다.");
        model.addAttribute("url", "/PHG_login");

        // alertPrint.jsp 페이지 이동 후 /PHG_login 주소로 이동
        return "/PHG/PHG_alertPrint";
    }

    @PostMapping("/PHG_login/processing")
    public String login(PHG_MemberDTO memberDTO, String toURL, boolean rememberId,
            HttpServletResponse response, HttpServletRequest request, Model model) throws Exception {

        System.out.println("====== 로그인 프로세스 시작 ======");
        System.out.println("입력된 사용자 정보: " + memberDTO);
        System.out.println("자동로그인 체크여부: " + rememberId);

        // 로그인 정보 검증
        int loginResult = memberService.login(memberDTO);
        System.out.println("로그인 검증 결과: " + (loginResult == 1 ? "성공" : "실패"));

        if (loginResult != 1) {
            System.out.println("로그인 실패 - 리다이렉트");
            model.addAttribute("msg", "아이디 혹은 비밀번호가 일치하지 않습니다.");
            model.addAttribute("url", "/PHG_login");
            return "/PHG/PHG_alertPrint";
        }

        // 로그인 성공 시 사용자 정보를 새로 조회
        PHG_MemberDTO userInfo = memberService.getUserById(memberDTO.getUserId());

        // 세션 생성 및 사용자 ID 저장
        HttpSession session = request.getSession();
        session.setAttribute("userId", userInfo.getUserId());
        session.setAttribute("authorityId", userInfo.getAuthorityId());
        System.out.println("세션 생성 완료 - 사용자 ID: " + userInfo.getUserId());
        System.out.println("세션 생성 완료 - 사용자 권한: " + userInfo.getAuthorityId());
        // 아이디 저장 쿠키 처리
        if (rememberId) {
            Cookie cookie = new Cookie("userId", memberDTO.getUserId());
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 7); // 7일
            response.addCookie(cookie);
            System.out.println("자동로그인 쿠키 생성 완료");
        } else {
            Cookie idCookie = new Cookie("userId", "");
            idCookie.setPath("/");
            idCookie.setMaxAge(0);
            response.addCookie(idCookie);
            System.out.println("기존 자동로그인 쿠키 삭제");
        }

        toURL = (toURL == null || toURL.equals("")) ? "/PHG_index" : toURL;
        System.out.println("리다이렉트 될 URL: " + toURL);
        System.out.println("====== 로그인 프로세스 종료 ======");

        return "redirect:" + toURL;
    }

    @GetMapping("/PHG_logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        // 세션 무효화 및 쿠키 삭제
        session.invalidate();
        Cookie idCookie = new Cookie("userId", "");
        idCookie.setPath("/");
        idCookie.setMaxAge(0);
        response.addCookie(idCookie);
        return "redirect:/PHG_index";
    }

    // 회원 탈퇴 처리 (삭제 성공 후 alertPrint.jsp 페이지로 이동)
    @PostMapping("/PHG_remove")
    public String remove(PHG_MemberDTO memberDTO, Model model, HttpSession session) {
        try {
            String userId = (String) session.getAttribute("userId");
            int rowCnt = memberService.delete(userId);
            session.invalidate();
            model.addAttribute("msg", "탈퇴 처리되었읍니다.");
            model.addAttribute("url", "/PHG_login");
            if (rowCnt != 1) {
                throw new Exception("remove error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/PHG/PHG_alertPrint";
    }

    @GetMapping("/PHG_memberModify")
    public String getMemberModify(HttpSession session, Model model) {
        // 세션에서 userId 가져옴
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("msg", "로그인이 필요합니다.");
            model.addAttribute("url", "/PHG_login");
            return "/PHG/PHG_alertPrint";
        }

        try {
            // Service를 통해 DB 조회
            PHG_MemberDTO storedUser = memberService.getUserById(userId);
            // 모델에 실어서 Thymeleaf로 넘김
            model.addAttribute("user", storedUser);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", "회원 정보를 가져오는 중 오류가 발생했습니다.");
            model.addAttribute("url", "/PHG_index");
            return "/PHG/PHG_alertPrint";
        }

        return "/PHG/PHG_memberModify";
    }

    @PostMapping("/PHG_memberModify")
    public String getMemberUpdate(PHG_MemberDTO memberDTO,
            @RequestParam("userNewPw") String newPassword,
            HttpSession session,
            Model model) throws Exception {
        // 세션 체크
        String userId = (String) session.getAttribute("userId");
        if (userId == null || !userId.equals(memberDTO.getUserId())) {
            model.addAttribute("msg", "잘못된 접근입니다.");
            model.addAttribute("url", "/PHG_login");
            return "/PHG/PHG_alertPrint";
        }

        int result = memberService.memberUpdate(memberDTO, newPassword);

        if (result != 1) {
            model.addAttribute("msg", "현재 비밀번호가 일치하지 않습니다.");
            model.addAttribute("url", "/PHG_memberModify");
            return "/PHG/PHG_alertPrint";
        }

        model.addAttribute("msg", "회원정보가 수정되었습니다.");
        model.addAttribute("url", "/PHG_managementPage");
        return "/PHG/PHG_alertPrint";
    }

    // --------------------------------------------------------------------------------------------------
    @GetMapping("/schedule/registAS")
    public String scheduleRegistAS(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            model.addAttribute("msg", "로그인이 필요합니다.");
            model.addAttribute("url", "/PHG_login");
            return "/PHG/PHG_alertPrint";
        }

        return "/PHG/RequestCode/registAS";
    }

    @PostMapping("/schedule/registAS/insert")
    public String insertAsReception(PHG_AsReceptionDTO asReceptionDTO, Model model, PHG_AsReceptionDTO dto) {
        try {
            int DeliveryAssignment = asReceptionService.DeliveryAssignment(dto);
            System.out.println("DeliveryAssignment : " + DeliveryAssignment);
            // model.addAttribute("DeliveryAss1ignment", DeliveryAssignment);
            if (DeliveryAssignment < 1) {
                model.addAttribute("msg", "해당 시각은 예약이 가득 찼습니다.");
                model.addAttribute("url", "/schedule/registAS"); // 접수 페이지 URL
                return "/PHG/PHG_alertPrint";
            }

            int result = asReceptionService.AS_Reception(asReceptionDTO);
            if (result > 0) {
                model.addAttribute("msg", "A/S 접수가 성공적으로 완료되었습니다.");
                model.addAttribute("url", "/PHG_managementPage"); // 목록 페이지 URL
            } else {
                model.addAttribute("msg", "A/S 접수 처리 중 문제가 발생했습니다.");
                model.addAttribute("url", "/schedule/registAS"); // 접수 페이지 URL
            }

        } catch (Exception e) {
            model.addAttribute("msg", "시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            model.addAttribute("url", "/schedule/registAS");
            System.out.println("PreferredDate 체크: " + asReceptionDTO.getPreferredDateTime());
            e.printStackTrace();
        }

        return "/PHG/PHG_alertPrint";
    }

    @GetMapping("/schedule/registAS/checkAvailableTimeSlots")
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

                PHG_AsReceptionDTO dto = new PHG_AsReceptionDTO();

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

    // --------------------------------------------------------------------------------------------------
    @GetMapping("/schedule/calendar")
    public String getFullCalendar() {
        return "/PHG/RequestCode/PHG_FullCalendar";
    }

    // --------------------------------------------------------------------------------------------------
    @GetMapping("/schedule/processStatus")
    public String scheduleRegistResult(HttpSession session, Model model, PHG_AsReceptionDTO asReceptionDTO,
            PHG_MemberDTO memberDTO)
            throws Exception {
        String userId = (String) session.getAttribute("userId");
        int authorityId = (int) session.getAttribute("authorityId");

        if (userId == null) {
            model.addAttribute("msg", "로그인이 필요합니다.");
            model.addAttribute("url", "/PHG_login");
            return "/PHG/PHG_alertPrint";
        }

        asReceptionDTO.setUserId(userId);
        asReceptionDTO.setAuthorityId(authorityId);

        List<PHG_MemberDTO> deliverySelect = memberService.deliverySelect(memberDTO);
        List<PHG_AsReceptionDTO> asReceptionList = asReceptionService.AS_Status(asReceptionDTO);
        model.addAttribute("asReceptionList", asReceptionList);
        model.addAttribute("deliverySelect", deliverySelect);

        return "/PHG/RequestCode/registProcessStatus";
    }

    @PostMapping("/schedule/processStatus/deliveryArrangement")
    public String deliveryArrangement(@RequestParam("selectedRequestId") String requestId,
            @RequestParam("receptionDelivery") String receptionDelivery,
            @RequestParam("receptionStatus") String receptionStatus,
            Model model) {

        asReceptionService.deliveryArrangement(Integer.parseInt(requestId), receptionDelivery, receptionStatus);
        try {
            model.addAttribute("msg", "배정 완료되었습니다.");
            model.addAttribute("url", "/schedule/processStatus"); // 목록 페이지 URL

        } catch (Exception e) {
            model.addAttribute("msg", "문제 발생");
            model.addAttribute("url", "/schedule/processStatus"); // 접수 페이지 URL
            e.printStackTrace();
        }
        return "/PHG/PHG_alertPrint";
    }

    // --------------------------------------------------------------------------------------------------
    @GetMapping("/PHG_managementPage")
    public String getManagement(HttpSession session, Model model) {
        // 세션에서 userId 확인
        if (session.getAttribute("userId") == null) {
            model.addAttribute("msg", "로그인이 필요합니다.");
            model.addAttribute("url", "/PHG_login");
            return "/PHG/PHG_alertPrint";
        }
        return "/PHG/PHG_managementPage";
    }

    // --------------------------------------------------------------------------------------------------
    @GetMapping("/PHG_ElectricalDisaster")
    public String getElectricalDisaster() {
        return "/PHG/PHG_ElectricalDisasterMonitoring";
    }

    @PostMapping("/PHG/PHG_ElectricalDisaster")
    @ResponseBody
    public Map<String, Object> getFireStatistics() {

        Map<String, Object> response = new HashMap<>();
        try {
            PHG_PythonExecutor executor = new PHG_PythonExecutor(
                    "./src/main/python/PHG",
                    "ElectricalFireStatisticsDashboard");
            Object ElectricalFireStatistics = executor.executeFunction("ElectricalFireStatistics");

            Object RegionalIgnitionCauses = executor.executeFunction("RegionalIgnitionCauses");

            response.put("status", "success");
            response.put("ElectricalFireStatistics", ElectricalFireStatistics);
            response.put("RegionalIgnitionCauses", RegionalIgnitionCauses);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }

        return response;
    }

    // --------------------------------------------------------------------------------------------------
}
