package com.example.demo.dao;

import com.example.demo.dto.PHG_MemberDTO;
import com.example.demo.dto.memberDTO;

public interface memberDAO {

    int register(memberDTO dto) throws Exception;

    int idOverlap(memberDTO dto) throws Exception;

    memberDTO getUserById(String user_id) throws Exception;

    int memberUpdate(memberDTO dto) throws Exception;
    
    int delete(String userId) throws Exception;
}
