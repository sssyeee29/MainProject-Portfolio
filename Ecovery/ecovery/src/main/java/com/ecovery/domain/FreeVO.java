package com.ecovery.domain;

import com.ecovery.constant.DealStatus;
import com.ecovery.constant.ItemCondition;
import lombok.*;

import java.time.LocalDateTime;

/*
 * 무료나눔 게시글 테이블(free)
 * 무료나눔 게시글 정보를 담는 vo 클래스
 * @author : yeonsu
 * @fileName : FreeVO
 * @since : 250708
 * @edit : itemCondition, dealStatus 타입변경 (enum으로)
 */


@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeVO {

    private Long freeId;        // 게시글 고유번호 (PK)
    private Long memberId;      // 작성자 회원ID (FK)
    private String title;       // 게시글 제목
    private String content;     // 게시글 상세 내용
    private String category;    // 품목 카테고리
    private String regionGu;    // 나눔지역 - 구
    private String regionDong;  // 나눔지역 - 동
    private int viewCount; // 게시글 조회수
    private LocalDateTime createdAt; // 게시글 등록/수정일(시간포함)

    // enum 타입
    private ItemCondition itemCondition; // 삼품 상태(HIGH/MEDIUM/LOW)
    private DealStatus dealStatus; // 거래 상태(ONGOING, DONE)

    private String nickname;

    private String imgUrl;
}
