package com.ecovery.service;

import com.ecovery.domain.EnvImgVO;
import com.ecovery.dto.EnvImgDto;
import com.ecovery.mapper.EnvImgMapper;
import com.ecovery.mapper.EnvMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * EnvImgService의 구현체
 * - 환경톡톡 게시글 이미지 등록, 조회, 삭제 기능 처리
 * - DTO ↔ VO 변환 포함
 *
 * @author : yukyeong
 * @fileName : EnvImgServiceImpl
 * @since : 250726
 * @history
      - 250726 | yukyeong | 이미지 등록/조회/삭제 기능 구현, 파일 저장 및 삭제 로직 추가
      - 250731 | yukyeong | 브라우저 접근용 URL 경로 수정: /images → /ecovery 로 변경
      - 250801 | yukyeong | 이미지 파일 없이 URL만 등록하는 register(EnvImgDto) 구현
                            이미지 URL 기반 삭제 기능 deleteByImgUrl(String imgUrl) 구현
      - 250805 | yukyeong | 본문 이미지 중복 등록 방지를 위한 existsByImgUrlAndEnvId 구현
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class EnvImgServiceImpl implements EnvImgService{

    // EnvImgMapper는 DB 연동을 위한 MyBatis 인터페이스
    private final EnvImgMapper envImgMapper;

    private final FileService fileService;

    @Value("${uploadPath}") // ex. file:///C:/ecovery/
    private String uploadPath;

    @Value("${envImgLocation}")
    private String envImgLocation;

    private final String envImgFolder = "env"; // /ecovery/env/

    // DTO를 VO로 변환하는 메서드 (DB 작업용으로 변환)
    private EnvImgVO dtoToVo(EnvImgDto dto) {
        EnvImgVO vo = new EnvImgVO(); // 빈 VO 객체 생성
        vo.setEnvImgId(dto.getEnvImgId());
        vo.setEnvId(dto.getEnvId());
        vo.setImgName(dto.getImgName());
        vo.setOriImgName(dto.getOriImgName());
        vo.setImgUrl(dto.getImgUrl());
        vo.setCreatedAt(dto.getCreatedAt());
        return vo; // 변환된 VO 반환
    }

    // VO를 DTO로 변환하는 메서드 (컨트롤러 또는 뷰로 전달용)
    private EnvImgDto voToDto(EnvImgVO vo) {
        EnvImgDto dto = new EnvImgDto(); // 빈 DTO 객체 생성
        dto.setEnvImgId(vo.getEnvImgId());
        dto.setEnvId(vo.getEnvId());
        dto.setImgName(vo.getImgName());
        dto.setOriImgName(vo.getOriImgName());
        dto.setImgUrl(vo.getImgUrl());
        dto.setCreatedAt(vo.getCreatedAt());
        return dto; // 변환된 DTO 반환
    }

    // 이미지 등록 (DTO를 VO로 변환한 후 DB에 저장)
    @Override
    public void register(EnvImgDto dto, MultipartFile imgFile) throws Exception {
        String oriImgName = imgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        if (oriImgName != null && !oriImgName.isBlank()) {
            // 실제 저장은 로컬 경로로
            imgName = fileService.uploadFile(envImgLocation, oriImgName, imgFile.getBytes());
            // 브라우저 접근용 URL
            imgUrl = "/ecovery/" + envImgFolder + "/" + imgName;
        }

        dto.setOriImgName(oriImgName);
        dto.setImgName(imgName);
        dto.setImgUrl(imgUrl);

        EnvImgVO vo = dtoToVo(dto); // DTO → VO 변환
        envImgMapper.insert(vo); // DB에 insert 수행
        log.info("이미지 등록 완료: {}", vo);
    }

    // 파일 없이 이미지 URL만 등록
    @Override
    public void register(EnvImgDto dto) {
        if (dto.getImgUrl() == null || dto.getImgUrl().isBlank()) {
            log.warn("등록하려는 이미지 URL이 비어있음: {}", dto);
            return;
        }

        EnvImgVO vo = dtoToVo(dto);
        envImgMapper.insert(vo);
        log.info("본문 이미지 등록 완료: {}", vo);
    }

    // 게시글 ID로 이미지 전체 조회 (VO 목록을 DTO로 변환하여 반환)
    @Override
    public List<EnvImgDto> getListByEnvId(Long envId) {
        List<EnvImgVO> voList = envImgMapper.getEnvImgList(envId); // VO 리스트 조회
        return voList.stream() // 스트림 변환 시작
                .map(this::voToDto) // VO → DTO 매핑
                .collect(Collectors.toList()); // 리스트로 수집하여 반환
    }

    // 단일 이미지 삭제 (이미지 ID 기준으로 삭제)
    @Override
    @Transactional
    public boolean deleteById(Long envImgId) {
        EnvImgVO img = envImgMapper.getById(envImgId);
        if (img == null) {
            log.warn("삭제 대상 이미지가 존재하지 않음: {}", envImgId);
            return false;
        }

        // 실제 이미지 파일 삭제
        try {
            if (img.getImgName() != null) {
                // 경로 끝 "/" 처리 보장
                String basePath = uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";
                String fullPath = basePath + envImgFolder + "/" + img.getImgName();

                fileService.deleteFile(fullPath);
            }
        } catch (Exception e) {
            log.error("파일 삭제 중 오류 발생", e);
        }

        int result = envImgMapper.deleteById(envImgId); // DB 삭제 수행
        log.info("단일 이미지 삭제 결과 (1이면 성공): {}", result);
        return result == 1; // 성공 여부 true/false 반환
    }


    // 게시글 ID로 이미지 전체 삭제
    @Override
    @Transactional
    public int deleteByEnvId(Long envId) {
        List<EnvImgVO> imgList = envImgMapper.getEnvImgList(envId);

        String basePath = uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";

        for (EnvImgVO img : imgList) {
            try {
                if (img.getImgName() != null) {
                    String fullPath = basePath + envImgFolder + "/" + img.getImgName();
                    fileService.deleteFile(fullPath);
                }
            } catch (Exception e) {
                log.warn("파일 삭제 실패: {}", img.getImgName(), e);
            }
        }

        int deleted = envImgMapper.deleteByEnvId(envId); // 일괄 삭제 수행
        log.info("게시글 전체 이미지 삭제 완료. 삭제 수: {}", deleted);
        return deleted; // 삭제된 행 수 반환
    }

    // 이미지 URL 삭제
    @Override
    @Transactional
    public int deleteByImgUrl(String imgUrl) {
        if (imgUrl == null || imgUrl.isBlank()) {
            log.warn("imgUrl이 비어 있음: '{}'", imgUrl);
            return 0;
        }

        // 추가
        imgUrl = imgUrl.trim(); // 앞뒤 공백 제거

        log.info("deleteByImgUrl() 호출됨 - imgUrl: '{}'", imgUrl);

        // 파일 삭제
        try {
            String fileName = extractFileName(imgUrl);
            String fullPath = (uploadPath.endsWith("/") ? uploadPath : uploadPath + "/") + envImgFolder + "/" + fileName;
            fileService.deleteFile(fullPath);
            log.info("파일 삭제 완료: {}", fullPath);
        } catch (Exception e) {
            log.error("파일 삭제 중 오류 발생", e);
        }

        // DB 삭제
        int deleted = envImgMapper.deleteByImgUrl(imgUrl);
        if (deleted == 0) {
            log.warn("DB 삭제 실패 - 해당 imgUrl 없음: {}", imgUrl);
        } else {
            log.info("DB 삭제 성공: {}", imgUrl);
        }

        return deleted;
    }


    private String extractFileName(String imgUrl) {
        if (imgUrl == null || !imgUrl.contains("/")) return null;
        return imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
    }

    // 본문 이미지 중복 등록 방지를 위한 existsByImgUrlAndEnvId 구현
    @Override
    public boolean existsByImgUrlAndEnvId(String imgUrl, Long envId) {
        return envImgMapper.existsByImgUrlAndEnvId(imgUrl, envId);
    }

}