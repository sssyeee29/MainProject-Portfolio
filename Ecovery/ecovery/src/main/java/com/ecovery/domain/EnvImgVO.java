package com.ecovery.domain;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 환경톡톡 이미지 VO
 * env_img 테이블의 각 컬럼과 매핑되는 도메인 객체
 * 게시글에 첨부된 이미지 정보를 표현하며, 이미지 업로드/조회/삭제 등에 사용
 *
 * @author : yukyeong
 * @fileName : EnvImgVO
 * @since : 250726
 * @history
      - 250726 | yukyeong | 환경톡톡 이미지 VO 클래스 최초 생성
 */

@Getter @Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvImgVO {

    private Long envImgId; // 이미지 ID (PK)
    private Long envId; // 게시글 ID (FK)
    private String imgName; // 서버 저장 이미지 파일명
    private String oriImgName; // 원본 파일명
    private String imgUrl; // 이미지 접근 URL
    private LocalDateTime createdAt; // 등록일

}
