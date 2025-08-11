package com.ecovery.mapper;

import com.ecovery.domain.EnvImgVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 환경톡톡 이미지 Mapper 인터페이스
 * env_img 테이블과 매핑되는 MyBatis Mapper
 * 이미지 등록, 조회, 삭제 기능 제공
 *
 * @author : yukyeong
 * @fileName : EnvImgMapper
 * @since : 250726
 * @history
 *     - 250726 | yukyeong | 이미지 등록, 조회, 삭제 메서드 정의
       - 250801 | yukyeong | 이미지 URL 기반 삭제 메서드 deleteByImgUrl(String imgUrl) 추가
       - 250805 | yukyeong | 게시글 ID + 이미지 URL 중복 여부 검사용 existsByImgUrlAndEnvId 메서드 추가 (본문 이미지 중복 insert 방지)
 */

@Mapper
public interface EnvImgMapper {

    // 게시글에 연결된 모든 이미지 조회
    public List<EnvImgVO> getEnvImgList(Long envId);

    // 단일 이미지 조회
    public EnvImgVO getById(Long envImgId);

    // 이미지 등록
    public void insert(EnvImgVO envImg);

    // 이미지 삭제 (게시글은 그대로 유지하고, 특정 이미지 1개만 삭제하고 싶을 때)
    public int deleteById(Long envImgId);

    // 게시글의 모든 이미지 삭제 (수정 시 사용)
    public int deleteByEnvId(Long envId);

    // 이미지 URL 삭제
    public int deleteByImgUrl(String imgUrl);

    // 게시글 ID + 이미지 URL 중복 여부 검사용 (본문 이미지 중복 insert 방지)
    public boolean existsByImgUrlAndEnvId(@Param("imgUrl") String imgUrl,
                                          @Param("envId") Long envId);

}
