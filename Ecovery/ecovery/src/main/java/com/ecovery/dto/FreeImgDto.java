package com.ecovery.dto;

import lombok.*;

/*
 * 무료나눔 이미지 DTO
 * 게시글 이미지 정보를 화면에 전달하기 위한 데이터 전달 객체
 * @author : yeonsu
 * @fileName : FreeImgDto
 * @since : 250710
 */

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FreeImgDto {

    private Long freeImgId;
    private Long freeId;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repImgYn;

    private boolean toBeDeleted; // 사용자에 의해 삭제 요청된 이미지 여부
}
