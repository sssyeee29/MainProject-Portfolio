package com.ecovery.service;

import com.ecovery.domain.FreeVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.FreeDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/*
* 무료나눔 게시글 서비스 인터페이스
* 게시글에 대한 등록, 조회, 수정, 삭제, 페이징 처리, 조회수 증가 등의 기능 정의
* 실제 구현은 FreeServiceImpl에서 처리
*
* @author : yeonsu
* @fileName : FreeService
* @since : 250717
*/

public interface FreeService {

    public Long register(FreeDto dto, List<MultipartFile> freeImgFileList) throws Exception;   // 게시글 등록

    public List<FreeDto> getAll(Criteria cri); // 게시글 전체 목록 조회

    public void modify(FreeDto dto, List<MultipartFile> newImgFiles) throws Exception;   // 게시글 수정

    public boolean remove(FreeDto dto);  // 게시글 삭제

    public int getTotalCount(Criteria cri); // 전체 게시글 수 (페이징용)

    public void updateViewCount(Long freeId); // 조회수 증가

    public FreeDto get(Long freeId); // 작성자 닉네임 포함 상세 조회


}
