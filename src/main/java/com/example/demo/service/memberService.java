package com.example.demo.service;

import com.example.demo.dto.PHG_MemberDTO;
import com.example.demo.dto.memberDTO;

public interface memberService {
	int register(memberDTO dto) throws Exception;

	int idOverlap(memberDTO dto) throws Exception;

	int login(memberDTO dto) throws Exception;

	memberDTO getUserById(String userId) throws Exception;

	int memberUpdate(memberDTO currentUser, String newPassword) throws Exception;

	int delete(String userId) throws Exception;
}
