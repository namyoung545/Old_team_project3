package com.example.demo.dao;

import com.example.demo.dto.PHG_MemberDTO;

public interface PHG_MemberDAO {

	int register(PHG_MemberDTO dto) throws Exception;

	int idOverlap(PHG_MemberDTO dto) throws Exception;

	int delete(String userId) throws Exception;

	PHG_MemberDTO getUserById(String user_id) throws Exception;

	int memberUpdate(PHG_MemberDTO dto) throws Exception;
}
