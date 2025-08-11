package com.ecovery.domain;

import com.ecovery.constant.ItemSellStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/*
 * 에코마켓 상품 VO
 * @author : sehui
 * @fileName : ItemVO
 * @since : 250709
 * @history
 *  - 250709 | sehui | 에코마켓 상품 VO 생성
 *  - 250718 | sehui | category -> categoryId로 변경
 */

@Getter
@Setter
@Builder
@ToString
public class ItemVO {

    private Long itemId;
    private String itemName;
    private int price;
    private int stockNumber;
    private Long categoryId;
    private String itemDetail;
    private ItemSellStatus itemSellStatus;
    private LocalDateTime createdAt;
}
