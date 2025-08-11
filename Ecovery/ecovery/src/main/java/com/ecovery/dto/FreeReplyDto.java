package com.ecovery.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/*
 * 무료나눔 댓글 DTO
 * 댓글 내용을 화면에 전달하기 위한 데이터 전달 객체
 * 부모-자식 구조로 대댓글 계층 표현 가능
 * @author : yeonsu
 * @fileName : FreeReplyDto
 * @since : 250711
 */

@Getter
@Setter
@ToString
public class FreeReplyDto {

    private Long replyId;    // 댓글 번호
    private Long freeId;     // 댓글이 달린 게시글 번호
    private Long parentId;   // 대댓글인 경우 부모 댓글 ID(null이면 일반 댓글)

    private Long memberId;   // 작성자 ID
    private String nickname; // 작성자 닉네임

    private String content;   // 댓글 내용
    private LocalDateTime createdAt; // 댓글 작성 일시

    private List<FreeReplyDto> childReplies; // 대댓글 목록
}
