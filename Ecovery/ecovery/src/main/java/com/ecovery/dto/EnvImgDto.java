package com.ecovery.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 환경톡톡 이미지 DTO
 * - env_img 테이블의 레코드를 표현하는 객체
 * - 클라이언트 <-> 서비스 계층 간 이미지 정보 전송에 사용
 * - 뷰 렌더링, 이미지 목록 조회, 등록 응답 등에 활용됨
 *
 * @author : yukyeong
 * @fileName : EnvImgDto
 * @since : 250726
 * @history
 *     - 250726 | yukyeong | 환경톡톡 이미지 DTO 클래스 최초 생성
 */

@Getter @Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnvImgDto {

    private Long envImgId; // 이미지 ID (PK)
    private Long envId; // 게시글 ID (FK)
    private String imgName; // 서버 저장 이미지 파일명
    private String oriImgName; // 원본 파일명
    private String imgUrl; // 이미지 접근 URL
    private LocalDateTime createdAt; // 등록일
}
