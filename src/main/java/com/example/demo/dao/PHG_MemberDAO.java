package com.example.demo.dao;

import com.example.demo.dto.PHG_MemberDTO;

public interface PHG_MemberDAO {

	int register(PHG_MemberDTO dto) throws Exception;

	int idOverlap(PHG_MemberDTO dto) throws Exception;

	int login(PHG_MemberDTO dto) throws Exception;

	int delete(PHG_MemberDTO dto) throws Exception;
}
