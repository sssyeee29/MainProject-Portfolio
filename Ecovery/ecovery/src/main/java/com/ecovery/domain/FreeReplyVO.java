package com.ecovery.domain;

import lombok.*;

import java.time.LocalDateTime;

/*
 * 무료나눔 댓글 테이블(free_reply)
 * 무료나눔 게시글에 작성된 댓글 정보를 담는 vo 클래스
 * @author : yeonsu
 * @fileName : FreeReplyVO
 * @since : 250708
 */

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FreeReplyVO {

    private Long replyId; // 댓글 고유번호(PK)
    private Long freeId; // 게시글 ID (FK)
    private Long memberId; // 댓글 작성자 ID (FK)
    private String content; // 댓글 본문 내용
    private Long parentId; // 부모 댓글 ID (NULL이면 일반댓글)
    private LocalDateTime createdAt; // 댓글 작성일시
}
