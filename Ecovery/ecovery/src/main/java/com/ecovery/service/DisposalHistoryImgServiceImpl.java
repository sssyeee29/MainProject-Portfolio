package com.ecovery.service;

import com.ecovery.domain.DisposalHistoryImgVO;
import com.ecovery.domain.DisposalHistoryVO;
import com.ecovery.mapper.DisposalHistoryImgMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DisposalHistoryImgServiceImpl implements DisposalHistoryImgService{

    private final DisposalHistoryImgMapper disposalHistoryImgMapper;

    private final FileService fileService;

    @Value("${disposalImgLocation}")  //application.yml에서 disposal 이미지 저장 경로 주입
    private String disposalImgLocation;


    @Override
    public DisposalHistoryImgVO saveDisposalImageFile(Long disposalHistoryId, MultipartFile disposalImgFile) throws Exception {
        // 필수 파라미터 유효성 검증
        if (disposalHistoryId == null) {
            throw new IllegalArgumentException("disposalHistoryId는 필수 값입니다.");
        }
        if (disposalImgFile == null || disposalImgFile.isEmpty()) {
            throw new IllegalArgumentException("업로드할 이미지 파일이 존재하지 않습니다.");
        }

        String oriDisposalImgName = disposalImgFile.getOriginalFilename();
        String disposalImgName = null; // 서버에 저장될 고유 파일명
        String disposalImgUrl = null;  // 웹 접근 URL

        try {
            // 파일 업로드 (원본 파일명이 유효한 경우에만 진행)
            // StringUtils.hasText()는 null, "", " " 모두 false로 처리하여 더 견고합니다.
            if (StringUtils.hasText(oriDisposalImgName)) {
                disposalImgName = fileService.uploadFile(
                        disposalImgLocation,
                        oriDisposalImgName,
                        disposalImgFile.getBytes()
                );
                // 웹 접근 URL 조합: 예시) /images/disposal/고유파일명.jpg
                disposalImgUrl ="/ecovery/disposalImg/" + disposalImgName;
                log.info("Disposal image uploaded: savedFileName={}, imageUrl={} for historyId={}", disposalImgName, disposalImgUrl, disposalHistoryId);
            } else {
                log.warn("Original image file name is empty or null for historyId: {}", disposalHistoryId);
                throw new IllegalArgumentException("원본 이미지 파일명이 유효하지 않아 파일 저장을 진행할 수 없습니다.");
            }

            // 분리배출 이미지 정보 VO 객체 생성 및 값 설정
            DisposalHistoryImgVO disposalHistoryImgVO = DisposalHistoryImgVO.builder()
                    .disposalHistoryId(disposalHistoryId)   // 외래 키 설정
                    .disposalImgName(disposalImgName)       // 서버에 저장된 고유 파일명 설정
                    .oriDisposalImgName(oriDisposalImgName) // 원본 파일명 설정
                    .disposalImgUrl(disposalImgUrl)         // 웹 접근 URL 설정
                    .build();

            // 분리배출 이미지 정보 DB 저장
            // 이 시점에서 disposalImgId (PK)는 DB에서 자동 생성되어 disposalHistoryImgVO 객체에 다시 설정됩니다.
            disposalHistoryImgMapper.insertDisposalImg(disposalHistoryImgVO);
            log.info("DisposalHistoryImg record saved to DB: imgId={}, historyId={}", disposalHistoryImgVO.getDisposalImgId(), disposalHistoryId);

            return disposalHistoryImgVO; // 저장 완료된 VO 반환

        } catch (Exception e) { // FileService에서 던질 수 있는 Exception을 IOException으로 통일
            log.error("Failed to process and save disposal image for historyId {}: {}", disposalHistoryId, e.getMessage(), e);
            // @Transactional 덕분에 이 예외 발생 시 DB 작업은 롤백됩니다.
            throw new IOException("이미지 처리 및 DB 저장에 실패했습니다.", e);
        }
    }

    @Override
    public DisposalHistoryImgVO getDisposalImg(Long disposalHistoryId) {
        return disposalHistoryImgMapper.findDisposalImgByDisposalHistoryId(disposalHistoryId);
    }
}
