package com.ecovery.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/*
 * 회원의 장바구니 상품 VO
 * @author : sehui
 * @fileName : CartItemVO
 * @since : 250709
 */

@Getter
@Setter
public class CartItemVO {

    private Long cartItemId;
    private Long cartId;
    private Long itemId;
    private int count;
    private Date createdAt;
}
