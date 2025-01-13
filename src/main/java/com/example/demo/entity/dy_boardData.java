package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "board")
public class dy_boardData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bnum; // 글 번호

    @Column(nullable = false)
    private String title; // 글 제목

    @Lob
    private String content; // 글 내용

    @Column(nullable = false)
    private LocalDateTime postdate = LocalDateTime.now(); // 기본값 설정
    
    @Column(nullable = false)
    private Integer visitcount = 0; // 조회수 기본값 설정

    @Column
    private String id; // 작성자 ID(필수 입력 아님)

    @Column(nullable = false)
    private String ename = "OldTeam"; // 값이 비어 있을 때 기본값 처리

    // Getters and Setters
    public Long getBnum() {
        return bnum;
    }

    public void setBnum(Long bnum) {
        this.bnum = bnum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getPostdate() {
        return postdate;
    }

    public void setPostdate(LocalDateTime postdate) {
        this.postdate = postdate;
    }

    public int getVisitcount() {
        return visitcount;
    }

    public void setVisitcount(int visitcount) {
        this.visitcount = visitcount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }
}
