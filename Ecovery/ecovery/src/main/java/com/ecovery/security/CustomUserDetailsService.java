package com.ecovery.security;

import com.ecovery.domain.MemberVO;
import com.ecovery.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * DB에서 사용자를 조회해서 Spring Security에 인증 정보로 넘겨주는 클래스
 * 로그인 시 사용자 정보(email)를 기준으로 인증 객체(UserDetails) 생성
 * 작성자 : 방희경
 */

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;

    // 로그인 요청 시 호출되고 사용자의 email로 DB에서 조회
    // 정보가 없으면 예외, 있으면 CustomUserDetails로 반환
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberVO memberVO = memberMapper.findByEmail(email);

        if (memberVO == null) {
            throw new UsernameNotFoundException("해당 이메일은 존재하지 않습니다 : " + email);
        }

        return new CustomUserDetails(memberVO); // Security가 인식할 수 있는 객체로 반환
    }
}
