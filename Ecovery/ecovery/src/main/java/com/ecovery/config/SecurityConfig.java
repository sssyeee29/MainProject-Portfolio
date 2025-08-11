package com.ecovery.config;

import com.ecovery.domain.MemberVO;
import com.ecovery.security.CustomUserDetails;
import com.ecovery.security.CustomUserDetailsService;
import com.ecovery.service.OAuth2MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 로그인, 회원가입, 비밀번호 암호화 등을 처리하기 위한 시큐리티 클래스 파일
 * 작성자 : 방희경
 * @history
- 2507?? | 방희경 | SecurityConfig 클래스 최초 작성
- 250716 | yukyeong | 환경톡톡 게시판 누구나 접근 가능하게 변경
- 250723 | yukyeong | 공지사항 게시판 누구나 접근 가능하게 변경
- 250725 | yukyeong | OAuth2 소셜 로그인 설정 추가 (카카오 로그인 연동)
- 250730 | sehui | 에코마켓 상품 등록 관리자 권한 추가
- 250730 | sehui | 정적 리소스에 "/images/**" 누구나 접근 가능하게 변경
- 250801 | sehui | 주문 페이지 로그인한 회원만 접근 가능으로 변경
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private OAuth2MemberService oAuth2MemberService; // 소셜 로그인 사용자 처리 서비스


    //보안 정책 정의
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("----------Security Filter Chain----------");

        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/*", "/css/**", "/js/**", "/img/**", "/images/**", "/fonts/**", "/main").permitAll() //정적 리소스는 누구나 접근 가능
                        .requestMatchers("/", "/member/signup", "/member/login", "/member/check-email", "/member/check-nickname").permitAll() //기본 공개 페이지도 누구나 접근 가능
                        .requestMatchers("/disposal/*", "/disposal/disposalMain/*", "/api/disposal/*", "feedback/**", "/error").permitAll() //기본 공개 페이지도 누구나 접근 가능
                        .requestMatchers("/env/**").permitAll() // 환경톡톡 게시판 누구나 접근 가능
                        .requestMatchers("/notice/**").permitAll() // 공지사항 게시판 누구나 접근 가능
                        .requestMatchers("/eco/**").permitAll()    //에코마켓 게시판 누구나 접근 가능 (수정 필요)
                        .requestMatchers("/api/eco/new").hasRole("ADMIN")       //에코마켓 상품 등록 게시판 관리자만 접근 가능
                        .requestMatchers("/api/**").permitAll()     //AJAX 요청 모두 허용
                        .requestMatchers("/order/**").hasAnyRole("USER", "ADMIN")   //주문 페이지는 로그인한 회원만 접근 가능
                        .requestMatchers("/ecovery/**").permitAll()
                        .requestMatchers("/feedback/*", "/disposal/history/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/free/register").hasAnyRole("USER", "ADMIN") // 무료나눔 등록 - USER 또는 ADMIN만 가능
                        .requestMatchers("/free/modify/**", "/free/delete/**").hasAnyRole("USER", "ADMIN") // 무료나눔 수정, 삭제 - USER 또는 ADMIN만 가능
                        .requestMatchers("/free/**", "/free/get/**").permitAll() // 무료나눔 목록, 상세 누구나 접근 가능
                        .requestMatchers("/admin/**").hasRole("ADMIN") //관리자 페이지는 관리자만 접근 가능
                        .requestMatchers("/mypage/**").authenticated() //로그인한 사용자만 마이페이지 접근 가능
                        .anyRequest().authenticated() //그 외 모든 요청은 로그인 필수
                )
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form
                        .loginPage("/member/login")
                        .loginProcessingUrl("/member/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            // 1. 로그인한 사용자 정보 가져오기
                            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                            MemberVO member = userDetails.getMemberVO();  //memverId를 MemberVO로 바꿈

                            // 2. 세션에 memberId 저장
                            request.getSession().setAttribute("memberId", member.getMemberId());
                            request.getSession().setAttribute("memberNickname", member.getNickname()); // 추가

                            // 3. 성공 응답 반환 (기존 코드 유지)
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"message\":\"success\", \"redirect\":\"/main\"}");
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"error\":\"이메일 또는 비밀번호를 확인해주세요.\"}");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/member/logout") //로그아웃 요청 url
                        .logoutSuccessUrl("/") //로그아웃 성공 시
                        .invalidateHttpSession(true) //세션 초기화
                        .deleteCookies("JSESSIONID") //쿠키 삭제
                )

                // 소셜 로그인 기능을 활성화
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/member/login") // 소셜 로그인 시 보여줄 로그인 페이지 지정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2MemberService) // 사용자 정보 처리용 커스텀 서비스 연결
                        )
                        .defaultSuccessUrl("/main", true) // 로그인 성공 후 이동할 경로 지정
                );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService); // 우리가 만든 사용자 정보를 사용!
        authProvider.setPasswordEncoder(passwordEncoder()); // 우리가 만든 비밀번호 암호화 방식을 사용!
        return authProvider;
    }

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}