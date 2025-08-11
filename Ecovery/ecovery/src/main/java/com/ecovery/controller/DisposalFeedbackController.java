package com.ecovery.controller;

import com.ecovery.domain.DisposalFeedbackVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.DisposalFeedbackDto;
import com.ecovery.dto.DisposalHistoryDto;
import com.ecovery.dto.PageDto;
import com.ecovery.service.DisposalFeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/feedback")
public class DisposalFeedbackController {

    private final DisposalFeedbackService disposalFeedbackService;

    //오류신고 버튼 클릭시 disposalfeedback db에 저장 후 이미지 업로드 페이지로 전환
    @PostMapping("/report")
    @ResponseBody
    public String feedbackReport(DisposalFeedbackVO feedbackVO) {
        if (!disposalFeedbackService.isAlreadyReported(feedbackVO.getDisposalHistoryId())) {
            disposalFeedbackService.saveFeedback(feedbackVO);
            log.info("DisposalFeedbackVO : {}", feedbackVO);
            log.info("feedbackVo : {}", feedbackVO.getMemberId());
            return "ok"; // 정상 처리
        } else {
            log.info("중복 신고 시도됨: DisposalHistoryId={}", feedbackVO.getDisposalHistoryId());
            return "duplicate"; // 중복 신고임을 알림
        }
    }



    @GetMapping("/history")
    public String feedbackHistory(Criteria cri, Model model) {
        List<DisposalFeedbackDto> adminFeedbackHistory = disposalFeedbackService.getAllFeedback(cri);
        model.addAttribute("adminFeedbackHistory", adminFeedbackHistory);
        double avgAiConfidence = adminFeedbackHistory.stream()
                .map(DisposalFeedbackDto::getAiConfidence)       // Double 객체 스트림
                .filter(java.util.Objects::nonNull)             // null 제거
                .mapToDouble(Double::doubleValue)               // double로 변환
                .average()
                .orElse(0);
        avgAiConfidence = Math.round(avgAiConfidence * 100.0) / 100.0;
        model.addAttribute("avgAiConfidence", avgAiConfidence);
        int total = disposalFeedbackService.getTotalCount(cri);
        model.addAttribute("feedbackHistoryPage", new PageDto(cri, total));

        return "feedback/feedbackHistory";
    }

    @GetMapping("/history/{memberId}")
    public String feedbackHistoryOfMember(@PathVariable("memberId") Long memberId, Model model) {
        List<DisposalFeedbackDto> myFeedbackHistory = disposalFeedbackService.getFeedbackByMemberId(memberId);
        model.addAttribute("myFeedbackHistory", myFeedbackHistory);
        return "feedback/memberFeedbackHistory";
    }

    //리스트에서 목록 클릭시 오류 신고 상세 조회 페이지로 이동하는 코드
    @GetMapping("/detail/{disposalHistoryId}")
    public String feedbackDetail(@PathVariable("disposalHistoryId") Long disposalHistoryId, Model model) {
        DisposalFeedbackDto feedback = disposalFeedbackService.getFeedbackDetail(disposalHistoryId);
        model.addAttribute("feedback", feedback);
        return "feedback/detail"; // 예: detail.jsp
    }

    @GetMapping("/api/detail/{feedbackId}")
    @ResponseBody
    public ResponseEntity<DisposalFeedbackDto> getFeedbackApiDetail(@PathVariable("feedbackId") Long feedbackId) {
        DisposalFeedbackDto feedbackDetail = disposalFeedbackService.getFeedbackDetail(feedbackId);

        log.info("API 요청: ID {}에 대한 분리배출 상세 내역 조회", feedbackId);

        if (feedbackDetail != null) {
            log.info("ID {}의 데이터 조회 성공: {}", feedbackId, feedbackDetail);
            return ResponseEntity.ok(feedbackDetail);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
