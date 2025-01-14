package com.example.demo.controller;

import com.example.demo.entity.dy_boardData;
import com.example.demo.service.dy_boardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class dy_boardController {

    @Autowired
    private dy_boardService service;

    @GetMapping("/dy_list")
    public String boardList(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 10; // 페이지당 게시물 수
        int offset = (page - 1) * pageSize;

        List<dy_boardData> boardList = service.getBoardList(pageSize, offset);
        model.addAttribute("boardlist", boardList);

        int totalCount = service.getTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "dy_html/dy_list"; // Thymeleaf 템플릿 반환
    }

    /**
     * 게시글 상세보기 및 수정 페이지
     */
    @GetMapping({"/dy_post"})
    public String boardPost(@RequestParam(value = "bnum", defaultValue = "1") Long bnum, Model model) {
        service.updateVisitCount(bnum); // 조회수 증가
        model.addAttribute("board", service.getBoardById(bnum));
        return "dy_html/dy_post"; // post.html 템플릿 반환
    }

    /**
     * 게시글 등록 페이지
     */
    @GetMapping("/dy_register")
    public String boardRegister() {
        return "dy_html/dy_register"; // dy_register.html 템플릿 반환
    }

    /**
     * 게시글 등록 처리
     */
    @PostMapping("/dy_register")
    public String register(dy_boardData boardData, RedirectAttributes rttr) {
        service.register(boardData); // 엔티티 저장
        rttr.addFlashAttribute("result", boardData.getBnum()); // 저장된 게시글 번호 전달
        return "redirect:/dy_list"; // 등록 후 목록 페이지로 리다이렉트
    }
    
    // GET 요청: 수정 화면 제공
    @GetMapping("/dy_update")
    public String updateForm(@RequestParam("bnum") Long bnum, Model model) {
        dy_boardData boardData = service.getBoardById(bnum);
        model.addAttribute("board", boardData);
        return "dy_html/dy_update"; // 수정 화면 템플릿 반환
    }
    // POST 요청: 수정 작업 처리
    @PostMapping("/dy_update")
    public String update(dy_boardData boardData, RedirectAttributes rttr) {
        if (service.update(boardData)) {
            rttr.addFlashAttribute("result", "success");
        } else {
            rttr.addFlashAttribute("result", "fail");
        }
        return "redirect:/dy_post?bnum=" + boardData.getBnum(); // 수정 후 상세보기로 리다이렉트
    }

    /**
     * 게시글 삭제 처리
     */
    @PostMapping("/dy_delete")
    public String delete(@RequestParam("bnum") Long bnum, RedirectAttributes rttr) {
        if (service.delete(bnum)) {
            rttr.addFlashAttribute("result", "success");
        } else {
            rttr.addFlashAttribute("result", "fail");
        }
        return "redirect:/dy_list";
    }
}
