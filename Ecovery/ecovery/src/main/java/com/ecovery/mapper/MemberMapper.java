package com.ecovery.mapper;

import com.ecovery.domain.MemberVO;
import com.ecovery.domain.PointVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.MemberPageDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 회원가입, 로그인, 마이페이지를 위한 MemberMapper
 * 회원가입 시 정보를 DB에 저장하고 회원정보 수정, 목록 조회, 중복검증 가능
 * 마이페이지에서 회원 닉네임, 포인트 조회 진행
 * MyBatis 매퍼 인터페이스 / MemberMapper.xml과 연동되어 SQL 실행
 * 작성자 : 방희경
 * @history
      - 250724 | yukyeong | 소셜 로그인 사용자 조회 메서드 추가 (findBySocialId)
      - 250808 | yukyeong | 소셜 로그인 닉네임 중복 여부 확인 메서드 추가
 */
@Mapper
public interface MemberMapper {

    /**
     * 회원 등록
     * - 입력한 MemberVO 정보를 DB에 저장
     */
    public void insertMember(MemberVO member);

    /**
     * 회원 정보 수정
     * - 닉네임, 비밀번호 변경
     */
    public int updateMember(MemberVO member);

    /**
     * 회원 정보 수정
     * - 관리자의 회원 권한 변경
     */
    public int updateMemberByAdmin(MemberVO member);

    /**
     * 회원 번호(PK)로 회원 조회
     */
    public MemberVO findByMemberId(Long memberId);

    /**
     * 이메일로 회원 조회
     * - 로그인, 이메일 중복 체크 등에 사용
     */
    public MemberVO findByEmail(String email);

    /**
     * 닉네임으로 회원 조회
     * - 로그인, 이메일 중복 체크 등에 사용
     */
    public MemberVO findByNickname(String nickname);

    /**
     * 전체 회원 목록 조회
     */
    public List<MemberVO> findAllMembers(Criteria cri);

    //페이지당 존재하는 게시물 개수
    public int getTotalCount(Criteria cri);

    // 마이페이지 닉네임, 포인트 조회
    public MemberPageDto getMemberPage(Long memberId);

    // 마이페이지 포인트 전체 조회
    public List<PointVO> getPointHistoryMemberById(Long memberId);

    // 소셜 로그인(provider + providerId)으로 회원 조회 (카카오, 구글 등 소셜 로그인 사용자 확인용)
    public MemberVO findBySocialId(String provider, String providerId);
    
    // 소셜 로그인 이용자 닉네임 중복 여부 확인, true: 중복 있음, false: 중복 없음
    public boolean existsByNickname(String nickname);
}
