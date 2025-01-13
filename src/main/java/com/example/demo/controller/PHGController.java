package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.PHG_MemberDTO;
import com.example.demo.service.PHG_MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class PHGController {

    @Autowired
    PHG_MemberService memberService;

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

        // 세션 생성 및 사용자 ID 저장
        HttpSession session = request.getSession();
        session.setAttribute("user_id", memberDTO.getUserId());
        System.out.println("세션 생성 완료 - 사용자 ID: " + memberDTO.getUserId());

        // 아이디 저장 쿠키 처리
        if (rememberId) {
            Cookie cookie = new Cookie("user_id", memberDTO.getUserId());
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 7); // 7일
            response.addCookie(cookie);
            System.out.println("자동로그인 쿠키 생성 완료");
        } else {
            Cookie idCookie = new Cookie("user_id", "");
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
        Cookie idCookie = new Cookie("user_id", "");
        idCookie.setPath("/");
        idCookie.setMaxAge(0);
        response.addCookie(idCookie);
        return "redirect:/PHG_index";
    }

    // 회원 탈퇴 처리 (삭제 성공 후 alertPrint.jsp 페이지로 이동)
    @PostMapping("/remove")
    public String remove(PHG_MemberDTO memberDTO, Model model, HttpSession session) {
        try {
            int rowCnt = memberService.delete(memberDTO);
            session.invalidate();
            model.addAttribute("msg", "탈퇴 처리되었읍니다.");
            model.addAttribute("url", "/main");
            if (rowCnt != 1) {
                throw new Exception("remove error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/PHG/PHG_alertPrint";
    }

    @GetMapping("/schedule/registAS")
    public String scheduleRegistAS() {
        return "/PHG/RequestCode/registAS";
    }

    @GetMapping("/schedule/registResult")
    public String scheduleRegistResult() {
        return "/PHG/RequestCode/registResult";
    }

    @GetMapping("/PHG_managementPage")
    public String getManagement() {
        return "/PHG/PHG_managementPage";
    }

    @GetMapping("/PHG_ElectricalDisaster")
    public String getMethodName() {
        return "/PHG/PHG_ElectricalDisasterMonitoring";
    }

}
