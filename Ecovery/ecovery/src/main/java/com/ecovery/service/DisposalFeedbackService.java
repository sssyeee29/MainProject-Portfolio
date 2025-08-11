package com.ecovery.service;

import com.ecovery.domain.DisposalFeedbackVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.DisposalFeedbackDto;

import java.util.List;

public interface DisposalFeedbackService {

    //오류 신고 내역 저장
    public void saveFeedback(DisposalFeedbackVO disposalFeedbackVO);

    //특정 feedbackId로 신고 상세 조회
    public DisposalFeedbackDto getFeedbackDetail(Long feedbackId);

    //특정 disposal_history_id로 신고 여부 확인(중복 신고 방지)
    public boolean isAlreadyReported(Long disposalHistoryId);

    //관리자용 전체 신고 내역 조회
    public List<DisposalFeedbackDto> getAllFeedback(Criteria cri);

    public int getTotalCount(Criteria cri);

    //회원별 신고 내역 조회
    public List<DisposalFeedbackDto> getFeedbackByMemberId(Long memberId);
}
