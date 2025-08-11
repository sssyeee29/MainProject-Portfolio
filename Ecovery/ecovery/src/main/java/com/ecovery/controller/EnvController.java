package com.ecovery.controller;

import com.ecovery.constant.Role;
import com.ecovery.domain.EnvVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.EnvDto;
import com.ecovery.dto.PageDto;
import com.ecovery.security.CustomUserDetails;
import com.ecovery.service.EnvService;
import com.ecovery.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

/*
 * 환경톡톡 게시글 컨트롤러
 * 게시글 등록, 조회, 수정, 삭제, 목록 조회(페이징) 기능의 컨트롤러 역할을 수행
 * 클라이언트의 요청을 받아 서비스 계층(EnvService)과 상호작용하여 처리하고 View에 데이터를 전달
 * @author : yukyeong
 * @fileName : EnvController
 * @since : 250716
 * @history
     - 250716 | yukyeong | EnvController 클래스 최초 작성 (목록 조회)
     - 250717 | yukyeong | 게시글 등록 폼 이동, 등록 처리, 단건 조회 및 수정 화면, 수정 처리, 삭제 추가
     - 250718 | yukyeong | 게시글 목록 조회 (페이징 + 검색)
     - 250721 | yukyeong | 게시글 등록 폼 이동, 게시글 등록 처리, 게시글 단건 조회, 수정 버튼 노출 조건 처리, 게시글 수정 처리, 삭제 처리 추가
     - 250722 | yukyeong | REST API 연동을 위한 비동기 전용 View 메서드 분리 (envListPage, envGetPage)
     - 250731 | yukyeong | 게시글 수정 폼 이동 추가
 */

@Controller
@RequestMapping("/env")
@RequiredArgsConstructor
@Slf4j
public class EnvController {

    private final EnvService envService;
    private final MemberService memberService;

    // 목록 조회
    @GetMapping("/list")
    public String envListPage(){

        return "env/list"; // templates/env/list.html 로 매핑됨
    }

    // 게시글 등록 폼 이동
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/register")
    public String registerForm() {
        return "env/register"; // templates/env/register.html 반환
    }


    // 게시글 단건 조회, 수정 버튼 노출 조건 처리
    @GetMapping("/get")
    public String envGetPage() {
        return "env/get"; // templates/env/get.html 뷰 렌더링만
    }

    // 수정 폼 이동
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/modify/{envId}")
    public String modifyForm(@PathVariable Long envId, Model model) {
        EnvDto envDto = envService.get(envId);  // 단건 조회 서비스 호출
        model.addAttribute("env", envDto); // 모델에 전달
        return "env/modify"; // templates/env/modify.html 로 이동
    }


}