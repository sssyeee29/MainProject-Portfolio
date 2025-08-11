package com.ecovery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/*
 * 공지사항 게시글 DTO
 * 공지사항 등록, 수정, 상세조회, 목록조회 시 데이터를 전달하는 객체
 * 사용 목적:
 * - Controller ↔ Service 계층 간 데이터 전달
 * - 화면 표시용 닉네임 포함 (nickname)
 * - 유효성 검증용 어노테이션(@NotBlank) 포함
 *
 * @author  : yukyeong
 * @fileName: NoticeDto
 * @since   : 250723
 * @history
     - 250723 | yukyeong | 공지사항 게시글 DTO 클래스 최초 작성
     - 250729 | yukyeong | 카테고리 필드 추가
     - 250802 | yukyeong | 작성자 권한(role) 필드 추가 (화면 표시용)
 */

@Getter
@Setter
@ToString
public class NoticeDto {

    private Long noticeId; // 공지사항 게시글 ID

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

    private String role; // 작성자 권한 (화면 표시용)
}
