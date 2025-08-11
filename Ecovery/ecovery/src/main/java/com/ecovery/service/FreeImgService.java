package com.ecovery.service;


import com.ecovery.domain.FreeImgVO;
import com.ecovery.domain.ItemImgVO;
import com.ecovery.dto.FreeImgDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/*
 * 무료나눔 이미지 서비스 인터페이스
 * 실제 구현은 FreeImgServiceImpl에서 처리
 *
 * @author : yeonsu
 * @fileName : FreeImgService
 * @since : 250721
 */


public interface FreeImgService {

    //상품 이미지 등록
   public void saveFreeImg( FreeImgVO freeImgVO, MultipartFile freeImgFile) throws Exception;

    //상품 이미지 수정
    public void updateFreeImg(Long freeId, List<FreeImgDto> freeImgDtoList, List<MultipartFile> newFreeImgFiles) throws Exception;

    //이미지 삭제
    public void deleteFreeImg(Long freeId) throws Exception;

    //다중 이미지 업로드
    public void saveAllFreeImages(Long freeId, List<MultipartFile> imgFiles) throws Exception;

    //대표 이미지(썸네일) 조회
    public FreeImgDto getRepImg(Long freeId);

    // 이미지 전체 조회
    List<FreeImgVO> getAll(Long freeId);

    // 특정 게시글(freeId)에 속한 모든 이미지 삭제
    public void deleteAllByFreeId(Long freeId) throws Exception;
 }


