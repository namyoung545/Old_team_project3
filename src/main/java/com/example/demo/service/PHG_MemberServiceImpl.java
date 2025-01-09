package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.PHG_MemberDAO;
import com.example.demo.dto.PHG_MemberDTO;

@Service
public class PHG_MemberServiceImpl implements PHG_MemberService {

	@Autowired
	PHG_MemberDAO memberDAO;
	
	@Override
	public int register(PHG_MemberDTO dto) throws Exception {
		return memberDAO.register(dto);
	}

	@Override
	public int idOverlap(PHG_MemberDTO dto) throws Exception {
		return memberDAO.idOverlap(dto);
	}

	@Override
	public int login(PHG_MemberDTO dto) throws Exception {
		return memberDAO.login(dto);
	}

	@Override
	public int delete(PHG_MemberDTO dto) throws Exception {
		return memberDAO.delete(dto);
	}

}
