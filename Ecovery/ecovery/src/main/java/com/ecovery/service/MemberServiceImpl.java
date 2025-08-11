package com.ecovery.service;

import com.ecovery.constant.Role;
import com.ecovery.domain.MemberVO;
import com.ecovery.domain.PointVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.MemberPageDto;
import com.ecovery.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 회원가입, 로그인를 위한 MemberServiceImpl
 * 회원가입 시 정보를 DB에 저장하고 회원정보 수정, 목록 조회, 중복검증 가능
 * 작성자 : 방희경
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    //회원 가입
    @Override
    public void registerMember(MemberVO memberVO){
        // 1. 평문 비밀번호 꺼냄
        String rawPassword = memberVO.getPassword();

        // 2. 암호화
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 3. 재 세팅
        memberVO.setPassword(encodedPassword);

        // 4. 권한 및 시간 세팅
        memberVO.setRole(Role.USER);
        memberVO.setCreatedAt(new Date());

        // 5. DB 저장
        memberMapper.insertMember(memberVO);
    }
    //회원 정보 수정
    @Override
    public boolean updateMember(MemberVO memberVO){

        log.info("updateMember.......");

        // 비밀번호 암호화
        String rawPassword = memberVO.getPassword();
        log.info("rawPassword: {}", rawPassword);
        if (rawPassword != null && !rawPassword.isEmpty()){
            String encodedPassword = passwordEncoder.encode(rawPassword);
            memberVO.setPassword(encodedPassword);
        }

        int result = memberMapper.updateMember(memberVO);
        log.info("업데이트된 행 수: {}", result);  // <= ★ 이거 꼭 찍어보기

        return result == 1;
    }

    @Override
    public boolean updateMemberByAdmin(MemberVO memberVO) {
        log.info("updateMemberByAdmin.......");

        boolean result = memberMapper.updateMemberByAdmin(memberVO) == 1;
        log.info("updateMember result: {}", result);

        return result;
    }

    //회원 번호(PK)로 회원 조회
    @Override
    public MemberVO getMemberById(Long memberId){

        return memberMapper.findByMemberId(memberId);
    }
    //이메일로 회원 조회(로그인, 이메일 중복 체크 등에 사용)
    @Override
    public MemberVO getMemberByEmail(String email){

        return memberMapper.findByEmail(email);
    }

    //닉네임으로 회원 조회(로그인, 이메일 중복 체크 등에 사용)
    @Override
    public MemberVO getMemberByNickname(String nickname){

        return memberMapper.findByNickname(nickname);
    }

    //전체 회원 조회
    @Override
    public List<MemberVO> getAllMembers(Criteria cri){

        return memberMapper.findAllMembers(cri);
    }
    @Override
    public int getTotal(Criteria cri) {
        return memberMapper.getTotalCount(cri);
    }

    // 마이페이지 닉네임, 포인트 조회
    @Override
    public MemberPageDto getMemberPage(Long memberId){

        return memberMapper.getMemberPage(memberId);
    }

    // 마이페이지 포인트 전체 조회
    @Override
    public List<PointVO> getPointHistoryMemberById(Long memberId){
        return memberMapper.getPointHistoryMemberById(memberId);
    }

}
