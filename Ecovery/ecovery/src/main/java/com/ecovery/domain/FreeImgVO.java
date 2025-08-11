package com.ecovery.domain;

import lombok.*;

import java.time.LocalDateTime;

/*
 * 무료나눔 이미지 테이블(free_img)
 * 무료나눔 게시글의 이미지 정보를 담는 vo 클래스
 * @author : yeonsu
 * @fileName : FreeImgVO
 * @since : 250708
 */

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FreeImgVO {

    private Long freeImgId; // 게시글 이미지 고유번호
    private Long freeId; // 게시글 ID (FK)
    private String imgName; // 서버에 저장된 이미지 파일명
    private String oriImgName; // 사용자가 업로드한 원본 파일명
    private String imgUrl; // 이미지 접근 경로
    private String repImgYn; // 대표 이미지 여부
    private LocalDateTime createdAt; // 이미지 등록일(수정시 갱신)
}
