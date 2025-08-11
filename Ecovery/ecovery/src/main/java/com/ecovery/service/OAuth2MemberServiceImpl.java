package com.ecovery.service;

import com.ecovery.constant.AgreeType;
import com.ecovery.constant.Role;
import com.ecovery.domain.MemberVO;
import com.ecovery.mapper.MemberMapper;
import com.ecovery.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/*
 * 소셜 로그인(OAuth2) 사용자 처리 서비스 구현체
 * - OAuth2 로그인 요청 시 사용자 정보 추출 및 신규/기존 회원 처리
 * - providerId + provider 값을 기준으로 회원 존재 여부 확인 및 자동 회원가입
 * - CustomUserDetails 객체로 변환 후 Spring Security 인증 객체로 반환
 *
 * @author : yukyeong
 * @fileName : OAuth2MemberServiceImpl
 * @since : 250724
 * @history
       - 250724 | yukyeong | 소셜 로그인 사용자 정보 처리 로직 최초 작성
       - 250724 | yukyeong | CustomUserDetails에 attributes 주입 방식으로 수정
       - 250725 | yukyeong | 카카오 소셜 로그인 시 nickname 값이 누락될 가능성에 대비해 로직 추가
       - 250725 | yukyeong | 구글 소셜 로그인 로직 추가 및 nickname 누락 시 기본값 처리
       - 250808 | yukyeong | 닉네임 중복 방지 로직 추가 (nickname_provider 형태로 중복 시 숫자 suffix 증가)
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2MemberServiceImpl extends DefaultOAuth2UserService
        implements OAuth2MemberService{

    private final MemberMapper memberMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        // access token을 이용해 소셜 사용자 정보(OAuth2User)를 받아옴
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 어떤 소셜 로그인인지 (kakao, google 등)
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 받은 사용자 정보 전체 (JSON 구조)
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 소셜 제공자별 공통 사용자 정보 (providerId, email, nickname) 추출
        String providerId;
        String email = null;
        String nickname = null;

        if("kakao".equals(provider)){
            // 카카오 응답에서 세부 정보 추출
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            providerId = String.valueOf(attributes.get("id")); // 카카오 고유 사용자 ID
            email = (String) kakaoAccount.get("email"); // 사용자 이메일

            // nickname 설정 (null 방지)
            if (profile != null && profile.get("nickname") != null) {
                nickname = (String) profile.get("nickname");
            } else {
                nickname = "카카오사용자_" + providerId;
            }
        } else if ("google".equals(provider)) {
            // 구글은 attributes에서 직접 값 추출
            providerId = (String) attributes.get("sub"); // 고유 ID
            email = (String) attributes.get("email"); // 이메일
            nickname = (String) attributes.get("name"); // 이름 → 닉네임 대체

            if (nickname == null) {
                nickname = "구글사용자_" + providerId;
            }

        } else {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인: " + provider);
        }

        // 닉네임 중복 방지 로직 시작
        String baseNickname = nickname + "_" + provider; // "eco_user_kakao"
        String finalNickname = baseNickname; // 일단 기본 닉네임으로 시작
        int suffix = 1;

        while (memberMapper.existsByNickname(finalNickname)) {
            finalNickname = baseNickname + suffix; // 뒤에 숫자 붙이기
            suffix++;
        }

        nickname = finalNickname; // 최종 닉네임 확정

        // DB에서 해당 provider + providerId로 회원 조회
        MemberVO member = memberMapper.findBySocialId(provider, providerId);

        // DB에서 회원이 없으면 신규 회원으로 등록
        if (member == null){
            member = MemberVO.builder()
                    .email(email)
                    .password(null) // 소셜 로그인은 비밀번호 저장 안함
                    .nickname(nickname)
                    .role(Role.USER) // 기본 User 권한
                    .createdAt(new Date())
                    .agreeOptional(AgreeType.Y) // 기본 약관 동의 처리
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            memberMapper.insertMember(member); // DB에 신규 회원 저장
            log.info("신규 소셜 회원 등록: {}", email);
        } else {
            log.info("기존 소셜 회원 로그인>: {}", email);
        }

        // CustomUserDetails 생성
        CustomUserDetails customUser = new CustomUserDetails(member);

        // 소셜 로그인 사용자 정보(OAuth2 attributes) 주입
        customUser.setAttributes(attributes);

        // 최종 반환
        return customUser;
    }
}
