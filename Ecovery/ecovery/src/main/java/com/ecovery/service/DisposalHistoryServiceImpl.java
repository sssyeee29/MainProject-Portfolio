package com.ecovery.service;

import com.ecovery.domain.DisposalHistoryVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.DisposalHistoryDto;
import com.ecovery.mapper.DisposalHistoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DisposalHistoryServiceImpl implements DisposalHistoryService {

    private final DisposalHistoryMapper disposalHistoryMapper;

    @Override
    public Long saveHistory(DisposalHistoryVO disposalHistoryVO) {
        disposalHistoryMapper.insertDisposalHistory(disposalHistoryVO);
        return disposalHistoryVO.getDisposalHistoryId();
    }

    @Override
    public DisposalHistoryDto getHistory(Long disposalHistoryId) {
        log.info("Service getHistory : {}", disposalHistoryId);
        return disposalHistoryMapper.findByDisposalHistoryId(disposalHistoryId);
    }

    @Override
    public List<DisposalHistoryDto> getHistoryByMemberId(Long memberId) {
        return disposalHistoryMapper.findDisposalHistoryWithImgByMemberId(memberId);
    }

    @Override
    public List<DisposalHistoryDto> getAllHistory(Criteria cri) {
        return disposalHistoryMapper.findAllDisposalHistory(cri);
    }

    @Override
    public int getTotal(Criteria cri) {
        return disposalHistoryMapper.getTotalCount(cri);
    }

    @Override
    public int updateHistory(DisposalHistoryVO disposalHistoryVO) {
        int updatedRows = disposalHistoryMapper.updateDisposalHistory(disposalHistoryVO);
        if (updatedRows == 0) {
            log.warn("No DisposalHistory updated for ID: {}", disposalHistoryVO.getDisposalHistoryId());
            // 업데이트할 대상이 없거나, 이미 같은 값으로 설정되어 변경이 없는 경우
            // 필요에 따라 RuntimeException을 던져서 호출자에게 알릴 수 있습니다.
            // throw new RuntimeException("업데이트할 이력을 찾을 수 없거나 변경 사항이 없습니다.");
        } else {
            log.info("DisposalHistory with ID {} updated. Affected rows: {}", disposalHistoryVO.getDisposalHistoryId(), updatedRows);
        }
        return updatedRows;
    }
}
