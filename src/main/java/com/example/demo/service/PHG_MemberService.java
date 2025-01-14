package com.example.demo.service;

import com.example.demo.dto.PHG_MemberDTO;

public interface PHG_MemberService {
	int register(PHG_MemberDTO dto) throws Exception;

	int idOverlap(PHG_MemberDTO dto) throws Exception;

	int login(PHG_MemberDTO dto) throws Exception;

	PHG_MemberDTO getUserById(String userId) throws Exception;

	int delete(String userId) throws Exception;

	int memberUpdate(PHG_MemberDTO currentUser, String newPassword) throws Exception;
}
