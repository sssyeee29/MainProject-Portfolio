package com.ecovery.controller;

import com.ecovery.dto.EnvDto;
import com.ecovery.dto.NoticeDto;
import com.ecovery.service.MemberService;
import com.ecovery.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 * 공지사항 게시글 뷰 컨트롤러
 * 게시글 등록 폼, 목록 페이지, 상세 페이지 등 HTML 뷰 페이지를 반환하는 역할 수행
 * 실제 등록/조회/수정/삭제 등의 데이터 처리는 NoticeApiController에서 수행하며,
 * 이 컨트롤러는 사용자 요청에 따라 뷰 파일만 전달하고 데이터는 JS에서 비동기 처리
 * @author   : yukyeong
 * @fileName : NoticeController
 * @since    : 250723
 * @history
     - 250723 | yukyeong | 공지사항 목록 뷰 이동 메서드 작성 (GET /list)
     - 250723 | yukyeong | 게시글 등록 폼 이동 메서드 작성 (GET /register)
     - 250723 | yukyeong | 게시글 단건 조회 뷰 이동 메서드 작성 (GET /get)
     - 250802 | yukyeong | 게시글 수정 폼 이동 메서드 작성 (GET /modify/{noticeId})
 */

@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
@Slf4j
public class NoticeController {

    private final NoticeService noticeService;
    private final MemberService memberService;

    // 목록 조회
    @GetMapping("/list")
    public String noticeListPage(){

        return "notice/list"; // templates/notice/list.html 로 매핑됨
    }

    // 게시글 등록 폼 이동
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/register")
    public String registerForm() {
        return "notice/register"; // templates/notice/register.html 반환
    }

    // 게시글 단건 조회, 수정 버튼 노출 조건 처리
    @GetMapping("/get")
    public String envGetPage() {
        return "notice/get"; // templates/notice/get.html 뷰 렌더링만
    }


    // 수정 폼 이동
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/modify/{noticeId}")
    public String modifyForm(@PathVariable Long noticeId, Model model) {
        NoticeDto noticeDto = noticeService.get(noticeId);  // 단건 조회 서비스 호출
        model.addAttribute("notice", noticeDto); // 모델에 전달
        return "notice/modify"; // templates/notice/modify.html 로 이동
    }

}