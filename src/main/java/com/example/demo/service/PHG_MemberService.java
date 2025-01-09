package com.example.demo.service;

import com.example.demo.dto.PHG_MemberDTO;

public interface PHG_MemberService {
	int register(PHG_MemberDTO dto) throws Exception;
	
	int idOverlap(PHG_MemberDTO dto) throws Exception;
	
	int login(PHG_MemberDTO dto) throws Exception;

	int delete(PHG_MemberDTO dto) throws Exception;
}
