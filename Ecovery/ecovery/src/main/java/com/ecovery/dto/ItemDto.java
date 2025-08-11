package com.ecovery.dto;

import com.ecovery.constant.ItemSellStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * 에코마켓 상품 정보 DTO
 * @author : sehui
 * @fileName : ItemDto
 * @since : 250709
 * @history
 *  - 250709 | sehui | 상품 정보 DTO 생성
 *  - 250718 | sehui | 카테고리 수정
 */

@Getter
@Setter
public class ItemDto {

    private Long itemId;
    private String itemNm;
    private int price;
    private int stockNumber;
    private CategoryDto category;
    private String itemDetail;
    private ItemSellStatus itemSellStatus;
}
