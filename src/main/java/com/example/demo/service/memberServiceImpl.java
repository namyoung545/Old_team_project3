package com.example.demo.service;

import com.example.demo.dto.PHG_MemberDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dao.memberDAO;
import com.example.demo.dto.memberDTO;

@Service
public class memberServiceImpl implements memberService {

    @Autowired
	memberDAO memberDAO;

  	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

  	@Override
	public int register(memberDTO dto) throws Exception {
		// 비밀번호 암호화 후 저장
		System.out.println("암호화 전 비밀번호: " + dto.getUserPw());
		String encodedPassword = passwordEncoder.encode(dto.getUserPw());
		dto.setUserPw(encodedPassword);
		return memberDAO.register(dto);
	}

    @Override
    public int idOverlap(memberDTO dto) throws Exception {
		return memberDAO.idOverlap(dto);
    }

	@Override
	public int login(memberDTO dto) throws Exception {
		System.out.println("\n====== 로그인 검증 상세 로그 ======");
		System.out.println("1. 입력된 로그인 정보:");
		System.out.println("   - ID: " + dto.getUserId());
		System.out.println("   - PW: " + dto.getUserPw());

		// DB에서 사용자 정보 조회
		memberDTO storedUser = memberDAO.getUserById(dto.getUserId());

		System.out.println("\n2. DB에서 조회된 정보:");
		if (storedUser != null) {
			System.out.println("   - ID: " + storedUser.getUserId());
			System.out.println("   - 암호화된 PW: " + storedUser.getUserPw());
			System.out.println("   - 이메일: " + storedUser.getEmail());
			System.out.println("   - 이름: " + storedUser.getName());
		} else {
			System.out.println("   - 사용자 정보 없음");
		}

		if (storedUser == null || storedUser.getUserPw() == null) {
			System.out.println("\n3. 검증 실패: 사용자 정보 또는 비밀번호 null");
			return 0;
		}

		boolean passwordMatch = passwordEncoder.matches(dto.getUserPw(), storedUser.getUserPw());
		System.out.println("\n3. 비밀번호 검증 결과: " + (passwordMatch ? "일치" : "불일치"));
		System.out.println("====== 로그인 검증 종료 ======\n");

		return passwordMatch ? 1 : 0;
	}

	@Override
	public memberDTO getUserById(String userId) throws Exception {
		return memberDAO.getUserById(userId);
	}

	@Override
	public int memberUpdate(memberDTO currentUser, String newPassword) throws Exception {
		// 1. 현재 저장된 사용자 정보 조회
		memberDTO storedUser = memberDAO.getUserById(currentUser.getUserId());

		// 2. 현재 비밀번호 확인
		if (!passwordEncoder.matches(currentUser.getUserPw(), storedUser.getUserPw())) {
			return 0; // 현재 비밀번호가 일치하지 않음
		}

		// 3. 새 비밀번호 암호화 및 설정
		if (newPassword != null && !newPassword.trim().isEmpty()) {
			String encodedNewPassword = passwordEncoder.encode(newPassword);
			currentUser.setUserPw(encodedNewPassword);
		}

		// 4. 회원 정보 업데이트
		return memberDAO.memberUpdate(currentUser);
	}

	@Override
	public int delete(String userId) throws Exception {
		return memberDAO.delete(userId);
	}
    
}
