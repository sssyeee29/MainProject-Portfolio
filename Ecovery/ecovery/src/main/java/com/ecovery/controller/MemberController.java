package com.ecovery.controller;

import com.ecovery.dto.MemberPageDto;
import com.ecovery.security.CustomUserDetails;
import com.ecovery.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.ecovery.domain.MemberVO;

/**
 * 회원가입, 로그인를 위한 Controller
 * 회원가입 시 정보를 DB에 저장하고 회원정보 수정, 목록 조회, 중복검증 가능
 * 작성자 : 방희경
 * @history
      - 250728 | yukyeong | 일반 회원가입 시 provider 값이 null로 들어가 DB 제약조건 오류 발생
                            → provider='local', providerId=null 명시적으로 설정하여 해결
 */
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/member")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    //회원가입 페이지 이동
    @GetMapping(value = "/signup")
    public String signupForm(Model model) {
        log.info("signupForm : 회원가입...");
        model.addAttribute("member", new MemberVO());
        return "member/signup";
    }
    //회원가입 처리
    @PostMapping(value = "/signup")
    public String signup(@ModelAttribute("member") MemberVO memberVO) {

        boolean hasError = false;

        // 1. 이메일 검사
        if (memberVO.getEmail() == null || memberVO.getEmail().trim().isEmpty()) {
            hasError = true;
        } else if (!memberVO.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            hasError = true;
        } else if (memberService.getMemberByEmail(memberVO.getEmail()) != null) {
            hasError = true;
        }

        // 2. 비밀번호 검사
        String password = memberVO.getPassword();
        if (password == null || password.isEmpty()) {
            hasError = true;
        } else if (password.length() < 8 || password.length() > 20) {
            hasError = true;
        } else if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*]{8,}$")) {
            hasError = true;
        } else {
            log.info("비밀번호 유효성 검사 통과: " + password);
        }

        // 3. 닉네임 검사
        if (memberVO.getNickname() == null || memberVO.getNickname().trim().isEmpty()) {
            hasError = true;
        } else if (memberService.getMemberByNickname(memberVO.getNickname()) != null){
            hasError = true;
        }

        // 에러가 있다면 다시 회원가입 페이지로
        if (hasError) {
            log.info("회원가입 실패: 유효성 검사 오류 발생");
            return "member/signup";
        }

        // provider 값을 명시적으로 지정
        memberVO.setProvider("local");
        memberVO.setProviderId(null);

        // 모든 유효성 검사 통과 시
        log.info("회원가입 성공: 모든 유효성 검사 통과");
        memberService.registerMember(memberVO);
        return "redirect:/member/login";
    }

    // 이메일 중복 확인 (AJAX)
    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam("email") String email) {
        return memberService.getMemberByEmail(email) != null;
    }

    // 닉네임 중복 확인 (AJAX)
    @GetMapping("/check-nickname")
    @ResponseBody
    public boolean checkNickname(@RequestParam("nickname") String nickname) {
        log.info("닉네임 중복검사 요청: [" + nickname + "]");
        MemberVO member = memberService.getMemberByNickname(nickname);
        log.info("DB 조회 결과: {}", member != null ? member.getNickname() : "없음");
        return member != null;
    }

    // 로그인 페이지 이동
    @GetMapping(value = "/login")
    public String loginForm(){
        log.info("loginForm : <UNK>..." + memberService.getMemberByEmail("<UNK>"));
        return "member/login";
    }

    // 마이페이지 닉네임, 포인트 조회
    @GetMapping(value = "/mypage")
    public String mypageForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        // MemberVO에서 memberId 추출
        Long memberId = userDetails.getMemberVO().getMemberId();
        // 서비스 호출하여 마이페이지에 필요한 정보 조회(닉네임, 포인트)
        MemberPageDto memberPage = memberService.getMemberPage(memberId);
        // 화면으로 넘길 모델 데이터 세팅
        model.addAttribute("member", memberPage);

        return "member/mypage";
    }
}

