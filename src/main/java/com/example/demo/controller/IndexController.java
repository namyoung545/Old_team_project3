package com.example.demo.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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

import com.example.demo.dto.PHG_MemberDTO;
import com.example.demo.dto.memberDTO;
import com.example.demo.service.memberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class IndexController {

    @Autowired
    memberService memberService;

    // localhost:8080 요청을 처리하는 메서드
    @GetMapping("/")
    public String getindex() {
        return "index";
    }

    @GetMapping("/test")
    public String gettest() {
        return "test";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("/signup")
    public String getSingUp() {
        return "signup";
    }

    @ResponseBody
    @PostMapping("/signup/idOverlap")
    public Integer idOverlap(memberDTO memberDTO) throws Exception {
        try {
            int result = memberService.idOverlap(memberDTO);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // 에러 발생 시 -1 반환
        }
    }

    @PostMapping("/signup/register")
    public String postJoin(memberDTO memberDTO, Model model) throws Exception {
        System.out.println("indexController : " + memberDTO);
        int result = idOverlap(memberDTO);

        // 아이디 중복 시 회원가입 페이지로 리다이렉트
        if (result != 0) {
            return "redirect:/signup";
        }

        // 회원 정보 등록(회원가입 완료 시 DB에 암호화된 비밀번호 저장)
        memberService.register(memberDTO);

        // 성공 메시지와 로그인 페이지 URL 설정
        model.addAttribute("msg", "회원 가입이 완료되었읍니다.");
        model.addAttribute("url", "/login");

        // alertPrint.jsp 페이지 이동 후 /login 주소로 이동
        return "/alertPrint";
    }

    @PostMapping("/login/processing")
    public String login(memberDTO memberDTO, String toURL, boolean rememberId,
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
            model.addAttribute("url", "/login");
            return "/alertPrint";
        }

        // 세션 생성 및 사용자 ID 저장
        HttpSession session = request.getSession();
        session.setAttribute("userId", memberDTO.getUserId());
        System.out.println("세션 생성 완료 - 사용자 ID: " + memberDTO.getUserId());

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

        toURL = (toURL == null || toURL.equals("")) ? "/managementPage" : toURL;
        System.out.println("리다이렉트 될 URL: " + toURL);
        System.out.println("====== 로그인 프로세스 종료 ======");

        return "redirect:" + toURL;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response, Model model) {
        // 세션 무효화 및 쿠키 삭제
        session.invalidate();
        Cookie idCookie = new Cookie("userId", "");
        idCookie.setPath("/");
        idCookie.setMaxAge(0);
        response.addCookie(idCookie);

        // 로그아웃 완료 메시지 설정
        model.addAttribute("msg", "로그아웃되었습니다.");
        model.addAttribute("url", "/managementPage"); // 로그아웃 후 이동할 페이지

        // alertPrint로 이동
        return "/alertPrint";
    }

    @GetMapping("/memberModify")
    public String getMemberModify(HttpSession session, Model model) {
        // 세션에서 userId 가져옴
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("msg", "로그인이 필요합니다.");
            model.addAttribute("url", "/login");
            return "/alertPrint";
        }

        try {
            // Service를 통해 DB 조회
            memberDTO storedUser = memberService.getUserById(userId);
            // 모델에 실어서 Thymeleaf로 넘김
            model.addAttribute("user", storedUser);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", "회원 정보를 가져오는 중 오류가 발생했습니다.");
            model.addAttribute("url", "/managementPage");
            return "/alertPrint";
        }

        return "/memberModify";
    }

    @PostMapping("/memberModify")
    public String getMemberUpdate(memberDTO memberDTO,
            @RequestParam("userNewPw") String newPassword,
            HttpSession session,
            Model model) throws Exception {
        // 세션 체크
        String userId = (String) session.getAttribute("userId");
        if (userId == null || !userId.equals(memberDTO.getUserId())) {
            model.addAttribute("msg", "잘못된 접근입니다.");
            model.addAttribute("url", "/login");
            return "/alertPrint";
        }

        int result = memberService.memberUpdate(memberDTO, newPassword);

        if (result != 1) {
            model.addAttribute("msg", "현재 비밀번호가 일치하지 않습니다.");
            model.addAttribute("url", "/memberModify");
            return "/alertPrint";
        }

        model.addAttribute("msg", "회원정보가 수정되었습니다.");
        model.addAttribute("url", "/managementPage");
        return "/alertPrint";
    }

    // 회원 탈퇴 처리 (삭제 성공 후 alertPrint.jsp 페이지로 이동)
    @PostMapping("/remove")
    public String remove(memberDTO memberDTO, Model model, HttpSession session) {
        try {
            String userId = (String) session.getAttribute("userId");
            int rowCnt = memberService.delete(userId);
            session.invalidate();
            model.addAttribute("msg", "탈퇴 처리되었읍니다.");
            model.addAttribute("url", "/login");
            if (rowCnt != 1) {
                throw new Exception("remove error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/alertPrint";
    }

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        try {
            // JSON 파일 경로
            String filePath = "src/main/resources/static/json/dy_risk.json";

            // JSON 파일 읽기 (UTF-8 인코딩)
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");

            // JSON 데이터를 모델에 추가
            model.addAttribute("jsonData", jsonContent);

            // 기본 연도 설정
            model.addAttribute("defaultYear", 2023);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Failed to load risk data: " + e.getMessage());
            return "error"; // 오류 템플릿 반환
        }
        
        return "dashboard";
    }

}