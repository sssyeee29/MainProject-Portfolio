package com.ecovery.controller;

import com.ecovery.constant.Role;
import com.ecovery.domain.FreeVO;
import com.ecovery.domain.MemberVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.FreeDto;
import com.ecovery.dto.FreeImgDto;
import com.ecovery.dto.PageDto;
import com.ecovery.service.CategoryService;
import com.ecovery.service.FreeImgService;
import com.ecovery.service.FreeService;
import com.ecovery.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

/*
 * 무료나눔 게시글 View 컨트롤러
 * - 화면 이동(View 렌더링) 전용
 * - 목록, 상세, 등록, 수정 페이지 경로 처리
 *
 * [접근 권한]
 * - 목록/상세: 누구나 접근 가능
 * - 등록/수정: 로그인 사용자만 접근 (보안은 JS나 Security로 처리)
 *
 * [주의]
 * - 데이터 처리(X), 단순 페이지 이동만 담당
 * - CRUD 기능은 FreeRestController에서 처리
 *
 * @author : yeonsu
 * @fileName : FreeController
 * @since : 250722
 */


@Controller
@RequiredArgsConstructor
@RequestMapping("/free")
@Slf4j
public class FreeController {

    private final FreeService freeService;
    private final MemberService memberService;
    private final CategoryService categoryService;

    //무료나눔 목록 페이지
    @GetMapping("/list")
    public String list() {
        return "free/list"; // 템플릿으로 이동
    }

    //무료나눔 상세 페이지
    @GetMapping("/get/{freeId}")
    public String get(@PathVariable Long freeId, @RequestParam(defaultValue = "1") int page, Principal principal, Model model) {
        // 게시글 정보 조회 (board.nickname 등 포함)
        FreeDto board = freeService.get(freeId);
        model.addAttribute("board", board); // Thymeleaf에서 board.nickname 등 사용

        // 로그인 사용자 정보
        String loginMemberNickname = null;
        Role loginMemberRole = null;

        if (principal != null) {
            String nickname = principal.getName(); // 이게 진짜 nickname이 나오는 구조인지 확인해야 함
            MemberVO loginMember = memberService.getMemberByNickname(nickname);

            if (loginMember != null) {
                loginMemberNickname = loginMember.getNickname();
                loginMemberRole = loginMember.getRole();

                // ✅ JS에서 사용할 loginMember 객체 추가 (memberId, role 등 접근 가능)
                model.addAttribute("loginMember", loginMember);
            }
        }

        model.addAttribute("loginMemberNickname", loginMemberNickname);
        model.addAttribute("loginMemberRole", loginMemberRole);

        // 페이지 정보를 모델에 추가
        model.addAttribute("page", page);

        return "free/get";
    }

    //무료나눔 등록 페이지 - 로그인 사용자만 가능
    @GetMapping("/register")
    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 접근 가능
    public String register(Model model){
        model.addAttribute("sharingForm", new FreeDto()); // 빈 DTO
        return "free/register";
    }

    //무료나눔 수정 페이지 - 로그인 사용자만 가능
    @GetMapping("/modify/{freeId}")
    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 접근 가능
    public String modify(@PathVariable Long freeId, Model model, Principal principal){

        // 게시글 정보 가져오기
        FreeDto free = freeService.get(freeId);

        // 로그인 사용자가 작성자 또는 관리자 여부 확인해도 좋음
        model.addAttribute("free", free); // HTML에서 사용할 객체

        return "free/modify";
    }
}
