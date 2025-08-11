package com.ecovery.dto;

import lombok.*;

/*
 * 구매이력 조회 DTO
 * 주문 1건에 포함된 상품 정보
 * @author : 방희경
 * @fileName : OrderItemDto
 * @since : 250723
 * @history
 *  - 250724 | sehui | 상품 이미지 URL 추가
 *  - 250730 | 방희경 | 상품 상세설명 필드 추가
 */

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {

    // 주문 상품 정보(order_item + item)
    private Long orderItemId;
    private int price; // 상품단가
    private int count; // 구매수량
    private int orderPrice;     //해당 상품의 총 결제 금액(단가*수량)

    // 상품 정보(item)
    private Long itemId;
    private String itemName;
    private String itemDetail;

    // 상품 이미지 정보(item_img)
    private Long itemImgId;
    private String imgName;
    private String imgUrl;
}