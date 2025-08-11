package com.ecovery.controller;


import com.ecovery.domain.MemberVO;
import com.ecovery.dto.MemberPageDto;
import com.ecovery.security.CustomUserDetails;
import com.ecovery.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 마이페이지 비밀번호 수정 전용 컨트롤러
 * - 이메일, 닉네임은 수정 불가
 * - 비밀번호만 수정
 * 작성자 : 방희경
 */
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/mypage")
@Slf4j
public class MyPageController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    // 마이페이지 이동
    @GetMapping(value = {"", "/"})
    public String myPageHome(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        Long memberId = user.getMemberVO().getMemberId();
        MemberVO member = memberService.getMemberById(memberId);

        // 1. MemberPageDto 객체생성
        int temporaryPoints = 400; // 화면 띄우기 임시 포인트 값
        MemberPageDto memberPageDto = new MemberPageDto(member.getNickname(), temporaryPoints);
        
        // 2. Model에 필요한 객체를 담아 전달
        model.addAttribute("member", member);  // 닉네임, 포인트 등 전달
        model.addAttribute("memberPageDto", memberPageDto);

        // 탭 제목 수정
        model.addAttribute("pageTitle", "마이페이지");

        return "mypage/home";
    }

    // 마이페이지 정보 수정
    @GetMapping(value = "/edit")
    public String showEditPage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        Long memberId = user.getMemberVO().getMemberId();
        MemberVO member = memberService.getMemberById(memberId);

        model.addAttribute("member", member); //닉네임, 이메일은 읽기 전용으로 보여주기

        return "mypage/edit";
    }

    // 마이페이지 정보 수정 처리
    @PostMapping(value = "/update", produces = "application/json")
    @ResponseBody
    public Map<String, Object> updatePassword(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("password") String newPassword) {

        Map<String, Object> result = new HashMap<>();

        MemberVO member = memberService.getMemberById(user.getMemberVO().getMemberId());

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            result.put("success", false);
            result.put("errorCode", "WRONG_PASSWORD");
            result.put("message", "현재 비밀번호가 일치하지 않습니다.");
            return result;
        }

        // 비밀번호 업데이트
        member.setPassword(newPassword);
        boolean updated = memberService.updateMember(member);

        result.put("success", updated);
        result.put("message", updated ? "비밀번호가 성공적으로 변경되었습니다." : "비밀번호 변경 실패");

        return result;
    }

}
