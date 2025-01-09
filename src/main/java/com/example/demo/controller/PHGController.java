package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public String PostJoin(PHG_MemberDTO memberDTO, Model model) throws Exception {
        System.out.println("MemberController : " + memberDTO);
        int result = idOverlap(memberDTO);

        // 아이디 중복 시 회원가입 페이지로 리다이렉트
        if (result != 0) {
            return "redirect:/PHG_join";
        }

        // 성공 메시지와 로그인 페이지 URL 설정
        model.addAttribute("msg", "회원 가입이 완료되었읍니다.");
        model.addAttribute("url", "/PHG_login");

        // 회원 정보 등록
        memberService.register(memberDTO);

        // alertPrint.jsp페이지 이동 후 /login주소로 이동
        return "/PHG/PHG_alertPrint";
    }

    @PostMapping("/PHG_login/processing")
    public String login(PHG_MemberDTO memberDTO, String toURL, boolean rememberId, HttpServletResponse response,
            HttpServletRequest request, Model model) throws Exception {

        // 로그인 정보 검증
        if (loginCheck(memberDTO) != 1) {
            model.addAttribute("msg", "아이디 혹은 비밀번호가 일치하지 않읍니다.");
            model.addAttribute("url", "/PHG_login");
            return "/PHG/PHG_alertPrint";
        }

        // 세션 생성 및 사용자 ID 저장
        HttpSession session = request.getSession();
        session.setAttribute("user_id", memberDTO.getUser_id());

        // 아이디 저장(Remember me) 기능 처리
        if (rememberId) {
            // 7일간 유지되는 쿠키 생성
            Cookie cookie = new Cookie("user_id", memberDTO.getUser_id());
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 7);
            response.addCookie(cookie);
        } else {
            // 기존 쿠키 삭제
            Cookie idCookie = new Cookie("user_id", "");
            idCookie.setPath("/");
            idCookie.setMaxAge(0); // 쿠키 즉시 삭제
            response.addCookie(idCookie);
        }

        // 리다이렉트 URL 결정 (기본값: 메인 페이지)
        toURL = toURL == null || toURL.equals("") ? "/PHG_index" : toURL;

        return "redirect:" + toURL;
    }

    private int loginCheck(PHG_MemberDTO memberDTO) throws Exception {
        // 로그인 상태를 확인 후 결과 반환 (성공:1, 실패:0)
        int result = memberService.login(memberDTO);
        return result;
    }

    @GetMapping("/PHG_logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        // 세션 무효화
        session.invalidate();

        // 쿠키 삭제
        Cookie idCookie = new Cookie("user_id", "");
        idCookie.setPath("/");
        idCookie.setMaxAge(0);
        response.addCookie(idCookie);

        // 메인 페이지로 리다이렉트
        return "redirect:/PHG_index";
    }

    @PostMapping("/remove")
    public String remove(PHG_MemberDTO memberDTO, Model model, HttpSession session) {

        try {
            // 사용자 정보를 기반으로 계정 삭제 처리
            int rowCnt = memberService.delete(memberDTO);

            // 로그인 세션 초기화
            session.invalidate();

            // 삭제 완료 메시지 및 경로를 Model에 저장
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

    @GetMapping("/registAS")
    public String scheduleRegistAS() {
        return "/PHG/RequestCode/registAS";
    }

    @GetMapping("/registResult")
    public String scheduleRegistResult() {
        // public String scheduleRegistResult(@ModelAttribute(value = "message") String
        // message,
        // @ModelAttribute(value = "reservation") ReservationDTO reservation, Model
        // model) {
        // System.out.println(message);
        // System.out.println(reservation);
        // // ReservationDTO dto = scheduleMapper.reservationRead(Long.parseLong(rnum));

        // model.addAttribute("data", reservation);
        // model.addAttribute("message", message);
        return "/PHG/RequestCode/registResult";
    }

}
