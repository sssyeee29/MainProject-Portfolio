package com.ecovery.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/*
 * 환경톡톡 게시글 테이블(env)
 * @author : yukyeong
 * @fileName : EnvVO
 * @since : 250708
 * @history
     - 250708 | yukyeong | VO 기본 필드 작성
     - 250711 | yukyeong | 작성자 닉네임(nickName) 필드 추가
     - 250725 | yukyeong | 카테고리 필드 추가
 */

@Getter @Setter
@ToString
public class EnvVO {
    private Long envId; // 게시글 ID
    private Long memberId; // 작성자 ID
    private String title; // 제목
    private String content; // 내용
    private String category; // 카테고리 추가
    private int viewCount; // 조회수
    private LocalDateTime createdAt; // 작성일자
    private LocalDateTime updatedAt; // 수정일자

    private String nickname; // 작성자 닉네임 (조인된 member 테이블)

}
