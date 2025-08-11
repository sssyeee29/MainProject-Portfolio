package com.ecovery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

/*
 * 환경톡톡 게시글 DTO
 * 목록, 상세, 등록, 수정에 공통으로 사용되는 데이터 전달 객체
     - @NotBlank는 자동으로 유효성 검증을 위해 추가 (컨트롤러에서 @Valid와 BindingResult 사용)
 * @author : yukyeong
 * @fileName : EnvDTO
 * @since : 250711
 * @history
     - 250708 | yukyeong | DTO 클래스 최초 작성
     - 250718 | yukyeong | 테스트를 위해 @ToString 추가
     - 250725 | yukyeong | 카테고리 필드 추가
 */

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvDto {

    private Long envId; // 환경 톡톡 게시글 ID

    @NotBlank(message = "제목은 필수입니다.")
    private String title; // 게시글 제목

    @NotBlank(message = "내용은 필수입니다.")
    private String content; // 게시글 내용

    private String category; // 카테고리 추가
    private Long memberId; // 작성자 ID (DB 저장용)
    private String nickname; // 작성자 닉네임 (화면 표시용)
    private int viewCount; // 조회수
    private LocalDateTime createdAt; // 등록일
    private LocalDateTime updatedAt; // 수정일
}
