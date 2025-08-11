package com.ecovery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 장바구니 Dto
 * 장바구니 상품 조회, 추가, 수정, 삭제 관련 Dto
 * @author : 방희경
 * @fileName : CartDetailDto
 * @since : 0250722
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDetailDto {

    private Long cartItemId; //장바구니 상품 아이디
    private String itemNm; //상품명
    private int price; //상품 금액
    private int count; //수량
    private String imgUrl; //상품 이미지 경로
    private int stockNumber;  // 남은 재고
    private Long itemId; // 상품 아이디

}
