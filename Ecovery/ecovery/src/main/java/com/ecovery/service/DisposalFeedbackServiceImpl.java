package com.ecovery.service;

import com.ecovery.domain.DisposalFeedbackVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.DisposalFeedbackDto;
import com.ecovery.mapper.DisposalFeedbackMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisposalFeedbackServiceImpl implements DisposalFeedbackService {

    private final DisposalFeedbackMapper disposalFeedbackMapper;

    @Override
    public void saveFeedback(DisposalFeedbackVO disposalFeedbackVO) {
        disposalFeedbackMapper.insertFeedback(disposalFeedbackVO);
    }

    @Override
    public boolean isAlreadyReported(Long disposalHistoryId) {
        return disposalFeedbackMapper.countByDisposalHistoryId(disposalHistoryId) > 0;
    }

    @Override
    public List<DisposalFeedbackDto> getAllFeedback(Criteria cri) {
        return disposalFeedbackMapper.findAllFeedbackWithImg(cri);
    }

    @Override
    public int getTotalCount(Criteria cri) {
        return disposalFeedbackMapper.getTotalCount(cri);
    }

    @Override
    public DisposalFeedbackDto getFeedbackDetail(Long feedbackId) {
        return disposalFeedbackMapper.selectDetailByDisposalHistoryId(feedbackId);
    }

    @Override
    public List<DisposalFeedbackDto> getFeedbackByMemberId(Long memberId) {
        return disposalFeedbackMapper.findMyFeedbackWithImg(memberId);
    }
}
