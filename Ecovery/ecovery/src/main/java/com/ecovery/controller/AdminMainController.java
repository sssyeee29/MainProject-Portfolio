package com.ecovery.controller;

import com.ecovery.domain.MemberVO;
import com.ecovery.dto.*;
import com.ecovery.security.CustomUserDetails;
import com.ecovery.service.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 관리자페이지 Controller
 * 관리자페이지를 만들어 분리배출 관리, 나눔 관리, 회원 관리 연결하여 사용 용도
 * 작성자 : 방희경
 */

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/admin")
@Slf4j
public class AdminMainController {
    private final DisposalHistoryService disposalHistoryService;
    private final DisposalFeedbackService disposalFeedbackService;
    private final MemberService memberService;
    private final EnvService envService;
    private final NoticeService noticeService;


    @GetMapping("/main")
    public String adminMainPage(Model model, Criteria cri) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Long memberId = userDetails.getMemberVO().getMemberId();

            log.info("memberid : {}", memberId);

            model.addAttribute("memberId", memberId); // Thymeleaf로 전달할 때
        } else {
            log.info("비회원 접근입니다.");
        }

        List<DisposalHistoryDto> adminDisposalHistory = disposalHistoryService.getAllHistory(cri);
        if (adminDisposalHistory.size() > 3) {
            adminDisposalHistory = adminDisposalHistory.subList(0, 3);
        }

        List<DisposalFeedbackDto> adminDisposalFeedback = disposalFeedbackService.getAllFeedback(cri);
        if (adminDisposalFeedback.size() > 3) {
            adminDisposalFeedback = adminDisposalFeedback.subList(0, 3);
        }

        List<MemberVO> adminMember = memberService.getAllMembers(cri);
        if (adminMember.size() > 3) {
            adminMember = adminMember.subList(0, 3);
        }

        List<EnvDto> adminEnv = envService.getList(cri);
        if (adminEnv.size() > 3) {
            adminEnv = adminEnv.subList(0, 3);
        }

        List<NoticeDto> adminNotice = noticeService.getList(cri);
        if (adminNotice.size() > 3) {
            adminNotice = adminNotice.subList(0, 3);
        }


        model.addAttribute("recentWasteRecords", adminDisposalHistory);
        model.addAttribute("recentSharingList", adminDisposalFeedback);
        model.addAttribute("recentUsers", adminMember);
        model.addAttribute("recentEnvTalks", adminEnv);
        model.addAttribute("recentNotices", adminNotice);

        double avgAiConfidence = adminDisposalHistory.stream()
                .map(DisposalHistoryDto::getAiConfidence)       // Double 객체 스트림
                .filter(java.util.Objects::nonNull)             // null 제거
                .mapToDouble(Double::doubleValue)               // double로 변환
                .average()
                .orElse(0);
        avgAiConfidence = Math.round(avgAiConfidence * 100.0) / 100.0;
        model.addAttribute("avgAiConfidence", avgAiConfidence);

        double feedbackAvgAiConfidence = adminDisposalFeedback.stream()
                .map(DisposalFeedbackDto::getAiConfidence)       // Double 객체 스트림
                .filter(java.util.Objects::nonNull)             // null 제거
                .mapToDouble(Double::doubleValue)               // double로 변환
                .average()
                .orElse(0);
        feedbackAvgAiConfidence = Math.round(feedbackAvgAiConfidence * 100.0) / 100.0;
        model.addAttribute("feedbackAvgAiConfidence", feedbackAvgAiConfidence);



        int disposalHistoryTotal = disposalHistoryService.getTotal(cri);
        model.addAttribute("disposalHistoryTotal", disposalHistoryTotal);
        int memberTotal = memberService.getTotal(cri);
        model.addAttribute("memberTotal", memberTotal);
        int feedbackTotal = disposalFeedbackService.getTotalCount(cri);
        model.addAttribute("feedbackTotal", feedbackTotal);
        int envTotal = envService.getTotal(cri);
        model.addAttribute("envTotal", envTotal);
        int noticeTotal = noticeService.getTotal(cri);
        model.addAttribute("noticeTotal", noticeTotal);

        return "admin/adminMain";
    }
}
