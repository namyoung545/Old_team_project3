package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
    public String insertAsReception(PHG_AsReceptionDTO asReceptionDTO, Model model) {
        try {
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
        }

        return "/PHG/PHG_alertPrint";
    }

    // --------------------------------------------------------------------------------------------------
    @GetMapping("/schedule/calendar")
    public String getFullCalendar() {
        return "/PHG/RequestCode/PHG_FullCalendar";
    }

    // --------------------------------------------------------------------------------------------------
    @GetMapping("/schedule/processStatus")
    public String scheduleRegistResult(HttpSession session, Model model, PHG_AsReceptionDTO dto) throws Exception {
        String userId = (String) session.getAttribute("userId");
        int authorityId = (int) session.getAttribute("authorityId");

        if (userId == null) {
            model.addAttribute("msg", "로그인이 필요합니다.");
            model.addAttribute("url", "/PHG_login");
            return "/PHG/PHG_alertPrint";
        }

        dto.setUserId(userId);
        dto.setAuthorityId(authorityId);

        List<PHG_AsReceptionDTO> asReceptionList = asReceptionService.AS_Status(dto);
        model.addAttribute("asReceptionList", asReceptionList);

        return "/PHG/RequestCode/registProcessStatus";
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
