package com.ecovery.service;

import com.ecovery.domain.EnvVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.EnvDto;
import com.ecovery.dto.EnvFormDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/*
 * 환경톡톡 게시글 서비스 인터페이스
 * 게시글 등록, 조회, 수정, 삭제, 목록 조회(페이징) 기능의 서비스 계층 역할을 정의
 * 컨트롤러와 Mapper를 연결하는 중간 레이어
 * @author : yukyeong
 * @fileName : EnvService
 * @since : 250715
 * @history
     - 250715 | yukyeong | EnvService 인터페이스 최초 작성 (CRUD, 페이징)
     - 250716 | yukyeong | 조회수 증가 추가
     - 250718 | yukyeong | VO를 DTO로 변경
     - 250728 | yukyeong | 이미지 포함 게시글 등록 및 수정 메서드로 변경 (EnvFormDto + MultipartFile)
     - 250731 | yukyeong | 이미지 리스트를 EnvFormDto 내부에서만 전달하는 구조로 수정
 */

public interface EnvService {

    // 게시글 등록 (본문 + 이미지 파일)
    public void register(EnvFormDto envFormDto) throws Exception;

    public EnvDto get(Long envId); // 게시글 단건 조회 (DTO 반환)

    // 게시글 수정 (본문 + 이미지 ID 목록 + 새 이미지 파일)
    public boolean modify(EnvFormDto envFormDto) throws Exception;

    public boolean remove(Long envId); // 게시글 삭제 (삭제는 ID만으로)

    public List<EnvDto> getList(Criteria cri); // 게시글 목록 조회 (페이징 포함) (목록도 DTO로)

    public int getTotal(Criteria cri); // 게시글 총 개수 조회 (페이징 처리에 사용)

    public void increaseViewCount(Long envId); // 게시글 조회수 증가

    // 이미지 URL 삭제
    public int deleteContentImg(String imgUrl);


}
