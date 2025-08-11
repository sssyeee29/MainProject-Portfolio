package com.ecovery.service;

import com.ecovery.dto.EnvImgDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 환경톡톡 이미지 서비스 인터페이스
 * 컨트롤러와 Mapper 사이의 비즈니스 로직 계층으로,
 * 이미지 등록, 조회, 삭제 등의 기능 정의
 *
 * @author : yukyeong
 * @fileName : EnvImgService
 * @since : 250726
 * @history
      - 250726 | yukyeong | 이미지 등록, 조회, 삭제 서비스 메서드 정의 (EnvImgDto 기반)
      - 250801 | yukyeong | 이미지 URL만 등록하는 register(EnvImgDto) 메서드 추가
                            이미지 URL 기반 삭제 메서드 deleteByImgUrl(String imgUrl) 추가
      - 250805 | yukyeong | 본문 이미지 중복 등록 방지를 위한 existsByImgUrlAndEnvId 메서드 추가
 */

public interface EnvImgService {

    // 이미지 등록
    public void register(EnvImgDto dto, MultipartFile imgFile) throws Exception;

    // 이미지 URL 등록
    public void register(EnvImgDto dto);

    // 게시글 ID로 이미지 전체 조회
    public List<EnvImgDto> getListByEnvId(Long envId);

    // 단일 이미지 삭제
    public boolean deleteById(Long envImgId);

    // 게시글 ID로 이미지 전체 삭제
    public int deleteByEnvId(Long envId);

    // 이미지 URL 삭제
    public int deleteByImgUrl(String imgUrl);

    // 본문 이미지 중복 등록 방지
    public boolean existsByImgUrlAndEnvId(String imgUrl, Long envId);
}
