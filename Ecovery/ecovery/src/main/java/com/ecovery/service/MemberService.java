package com.ecovery.service;

import com.ecovery.domain.MemberVO;
import com.ecovery.domain.PointVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.MemberPageDto;

import java.util.List;

/**
 * 회원가입, 로그인를 위한 Service
 * 회원가입 시 정보를 DB에 저장하고 회원정보 수정, 목록 조회, 중복검증 가능
 * 작성자 : 방희경
 */

public interface MemberService {

    //회원 가입
    public void registerMember(MemberVO memberVO);

    //회원 정보 수정
    public boolean updateMember(MemberVO memberVO);

    //관리자 회원 권한 수정
    public boolean updateMemberByAdmin(MemberVO memberVO);

    //회원 번호(PK)로 회원 조회
    public MemberVO getMemberById(Long memberId);

    //이메일로 회원 조회(로그인, 이메일 중복 체크 등에 사용)
    public MemberVO getMemberByEmail(String email);

    //닉네임으로 회원 조회(로그인, 이메일 중복 체크 등에 사용)
    public MemberVO getMemberByNickname(String nickname);

    //전체 회원 조회
    public List<MemberVO> getAllMembers(Criteria cri);

    //전체 레코드 조회 or 조건에 맞는 데이터 조회
    public int getTotal(Criteria cri);

    // 마이페이지 닉네임, 포인트 조회
    public MemberPageDto getMemberPage(Long memberId);

    // 마이페이지 포인트 전체 조회
    public List<PointVO> getPointHistoryMemberById(Long memberId);



}
