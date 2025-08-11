package com.ecovery.mapper;

import com.ecovery.domain.EnvVO;
import com.ecovery.dto.Criteria;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/*
 * 환경톡톡 게시글 Mapper
 * DB 처리 (목록, 상세, 등록, 수정, 삭제, 페이징, 검색)를 담당하는 인터페이스
 * @author : yukyeong
 * @fileName : EnvMapper
 * @since : 250711
 * @history
     - 250711 | yukyeong | Mapper 인터페이스 최초 작성 (목록, 상세조회, 등록, 수정, 삭제, 페이징, 검색 메서드 정의)
     - 250716 | yukyeong | 조회수 증가 추가
 */

@Mapper
public interface EnvMapper {

    // 게시글 전체 목록 조회
    public List<EnvVO> getList();

    // 게시글 단건 조회
    public EnvVO read(Long envId);

    // 게시글 등록
    public void insert(EnvVO env);

    // 게시글 등록 후 PK 자동 생성 값 가져오기
    public void insertSelectKey(EnvVO env);

    // 게시글 삭제
    public int delete(Long envId);

    // 게시글 수정
    public int update(EnvVO env);

    // 게시글 목록 조회 (페이징 + 검색)
    public List<EnvVO> getListWithPaging(Criteria cri);

    // 게시글 총 개수 조회 (페이징 + 검색)
    public int getTotalCount(Criteria cri);

    // 게시글 조회수 증가
    public void updateViewCount(Long envId);

}
