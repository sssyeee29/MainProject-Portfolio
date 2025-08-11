package com.ecovery.service;

import com.ecovery.dto.DisposalHistoryDto;
import com.ecovery.dto.AIFinalDisposalRequestDto;
import com.ecovery.dto.AIInitialDisposalResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface AIDisposalRequestProcessingService {

    public AIInitialDisposalResponseDto processInitialDisposalRequest(
            MultipartFile multipartFile, String regionGu, Long memberId
    ) throws Exception;

    public DisposalHistoryDto finalizeDisposalRequest(
            AIFinalDisposalRequestDto requestDto) throws Exception;
}