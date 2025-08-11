package com.ecovery.mapper;

import com.ecovery.constant.AgreeType;
import com.ecovery.constant.Role;
import com.ecovery.domain.MemberVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 회원 Mapper 테스트 클래스
 * - MemberMapper의 주요 메서드에 대한 단위 테스트 수행
 * - 회원 등록, 소셜 로그인 회원 조회 등의 기능 검증
 *
 * @author : yukyeong
 * @fileName : MemberMapperTest
 * @since : 250724
 * @history
 *     - 250724 | yukyeong | 소셜 로그인 회원 조회 테스트(findBySocialId) 작성
 */

@SpringBootTest
@Transactional
class MemberMapperTest {

    @Autowired
    private MemberMapper memberMapper;

    @Test
    @DisplayName("소셜 로그인 사용자 조회 테스트 - findBySocialId")
    void testFindBySocialId() {
        // Given - 테스트용 소셜 사용자 정보를 가진 MemberVO 객체 생성
        MemberVO member = MemberVO.builder()
                .email("kakao@test.com") // 소셜 로그인 응답에서 제공된 이메일 주소
                .password(null) // 소셜 로그인은 비밀번호 없음
                .nickname("카카오사용자") // 소셜 응답에서 제공되는 닉네임 사용
                .role(Role.USER) // 기본적으로 USER 부여 (관리자가 아닌 일반 사용자)
                .createdAt(new Date()) // new Date() 또는 DB 기본값으로 처리
                .agreeOptional(AgreeType.Y) // 소셜 회원도 약관 동의가 필요하다면 기본값 설정 (보통 Y)
                .provider("kakao") // 로그인 방식 (kakao, google 등)
                .providerId("kakao_12345") // 카카오에서 제공하는 고유 사용자 ID (예: 123456789)
                .build();

        // 테스트용 회원을 DB에 저장 (insertMember)
        memberMapper.insertMember(member);

        // When - 저장한 사용자 정보를 소셜 로그인 정보로 조회
        MemberVO result = memberMapper.findBySocialId("kakao", "kakao_12345");

        // Then - 조회된 회원 정보가 null이 아니고, 예상값과 일치하는지 검증
        assertNotNull(result); // 결과가 null이 아님
        assertEquals("kakao@test.com", result.getEmail()); // 이메일 일치
        assertEquals("카카오사용자", result.getNickname()); // 닉네임 일치
        assertEquals("kakao", result.getProvider()); // provider 일치
        assertEquals("kakao_12345", result.getProviderId()); // providerId 일치
    }

}