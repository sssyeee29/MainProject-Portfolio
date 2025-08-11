package com.ecovery.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/*
 * 회원의 장바구니 VO
 * @author : sehui
 * @fileName : CartVO
 * @since : 250709
 */

@Getter
@Setter
public class CartVO {

    private Long cartId;
    private Long memberid;
    private Date createdAt;
}
