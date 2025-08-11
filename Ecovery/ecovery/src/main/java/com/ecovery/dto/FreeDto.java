package com.ecovery.dto;

import com.ecovery.constant.DealStatus;
import com.ecovery.constant.ItemCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/*
 * 무료나눔 게시글 DTO
 * 게시글 정보를 화면에 전달하기 위한 데이터 전달 객체
 * @author : yeonsu
 * @fileName : FreeDto
 * @since : 250710
 */

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeDto {

    private Long freeId; // 게시글 번호

    @NotBlank(message = "제목은 필수입니다.")
    private String title; // 게시글 제목

    private String nickname; // 작성자
    private Long memberId; // 작성자 회원 ID (삭제, 수정 시 확인용)

    private LocalDateTime createdAt; // 등록일, 수정일

    @NotNull(message = "상품 상태를 선택해주세요.")
    private ItemCondition itemCondition; // 상품상태

    @NotBlank(message = "구 선택은 필수입니다.")
    private String regionGu; // 지역구

    @NotBlank(message = "동 선택은 필수입니다.")
    private String regionDong; // 지역동

    @NotBlank(message = "내용 필수입니다.")
    private String content; // 게시글 상세 내용

    private DealStatus dealStatus; // 거래 상태
    private int viewCount; // 조회수

    @NotBlank(message = "대표 이미지를 등록해주세요.")
    private String imgUrl; // 이미지 경로

    private String category;

    private List<FreeImgDto> imgList;


}
