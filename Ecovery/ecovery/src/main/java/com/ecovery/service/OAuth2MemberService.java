package com.ecovery.service;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

/*
 * 소셜 로그인 사용자 서비스 인터페이스
 * OAuth2UserRequest를 기반으로 사용자 정보를 조회하고
 * DB에 회원이 존재하지 않으면 자동 회원가입 처리 후 반환
 * Spring Security에 전달할 OAuth2User 객체 생성 역할을 수행
 * @author : yukyeong
 * @fileName : OAuth2MemberService
 * @since : 250724
 * @history
       - 250724 | yukyeong | OAuth2 소셜 로그인 사용자 서비스 인터페이스 최초 작성
       - 250725 | yukyeong | Spring Security 연동 위해 OAuth2UserService 상속 추가
 */

public interface OAuth2MemberService extends OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    public OAuth2User loadUser(OAuth2UserRequest userRequest);

}


