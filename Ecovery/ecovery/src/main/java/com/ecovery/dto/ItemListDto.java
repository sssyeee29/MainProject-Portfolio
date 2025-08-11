package com.ecovery.dto;

import com.ecovery.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
 * 에코마켓 상품 목록 DTO
 * @author : sehui
 * @fileName : ItemListDto
 * @since : 250715
 */

@Getter
@Setter
@ToString
public class ItemListDto {

    private Long itemId;
    private String itemNm;
    private String category;
    private ItemSellStatus itemSellStatus;
    private String imgUrl;
}
