package com.ecovery.mapper;

import com.ecovery.domain.FreeImgVO;
import com.ecovery.dto.FreeImgDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/*
 * 무료나눔 이미지 Mapper
 * 무료나눔 게시글과 연결된 이미지 DB 연동을 위한 인터페이스
 * @author : yeonsu
 * @fileName : FreeImgMapper
 * @since : 250711
 * @edit : 250724 - 대표이미지 다루는 코드 추가 (3개)
 */

@Mapper
public interface FreeImgMapper {

    public void insert(FreeImgVO freeImgVO);             // 이미지 등록

    public int update(FreeImgVO freeImgVO);              // 이미지 수정시 이미지 교체

    public List<FreeImgVO> getFreeImgList(Long freeId); // 전체 이미지 조회

    public FreeImgDto getById(Long freeImgId);           // 이미지 번호로 이미지 1건 조회

    public int delete(Long freeId);                      // 게시글 id로 게시글 이미지 수정을 위한 삭제

    public FreeImgDto getRepImg(Long freeId);            // 대표 이미지 조회 (썸네일용)

    public void setRepImg(Long freeImgId, String repImgYn);              // 대표 이미지로 설정 (free_img_id기준)

    //public int deleteAllByFreeId(Long freeId);         // 특정 게시글에 속한 모든 이미지 삭제
}
