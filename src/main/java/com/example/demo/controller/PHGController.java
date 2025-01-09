package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.PHG_MemberDTO;
import com.example.demo.service.PHG_MemberService;

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
        return "/PHG/alertPrint";
    }

}
