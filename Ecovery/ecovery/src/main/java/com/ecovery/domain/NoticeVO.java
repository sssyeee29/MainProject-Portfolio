package com.ecovery.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/*
 * 공지사항 게시글 VO
 * DB notice 테이블과 매핑되는 도메인 객체이며, member 테이블 조인을 통해 작성자 닉네임도 함께 포함
 *
 * 사용 목적:
 * - MyBatis Mapper와 DB 연동 시 사용
 * - DB 저장/조회 최적화
 * - 작성자 닉네임(nickname)은 화면 표시용으로 member 테이블과의 조인 결과 포함
 *
 * @author  : yukyeong
 * @fileName: NoticeVO
 * @since   : 250708
 * @history
     - 250708 | yukyeong | VO 기본 필드 작성
     - 250723 | yukyeong | 작성자 닉네임(nickname) 필드 추가 (member 테이블 조인용)
     - 250729 | yukyeong | 카테고리 필드 추가
     - 250802 | yukyeong | 작성자 권한(role) 필드 추가 (member 테이블 조인용)
 */

@Getter @Setter
@ToString
public class NoticeVO {
    private Long noticeId; // 공지사항 ID
    private Long memberId; // 작성자 ID
    private String title; // 제목
    private String content; // 내용
    private String category; // 카테고리 추가
    private int viewCount; // 조회수
    private LocalDateTime createdAt; // 작성일자
    private LocalDateTime updatedAt; // 수정일자

    private String nickname; // 작성자 닉네임 (조인된 member 테이블)
    private String role; // 작성자 권한 (조인된 member 테이블)
}
