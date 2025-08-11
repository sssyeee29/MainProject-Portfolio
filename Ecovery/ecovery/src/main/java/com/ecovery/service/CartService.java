package com.ecovery.service;

/*
 * 장바구니 Service Interface
 * 장바구니 상품아이디 조회용
 * 실제 구현은 CartServiceImpl에서 처리
 * 상품 수정, 삭제는 CartItemService에서 처리
 * @author : 방희경
 * @fileName : CartService
 * @since : 250721
 */

import com.ecovery.dto.CartDetailDto;

import java.util.List;

public interface CartService {

    // 닉네임으로 장바구니 ID 조회
    public Long getCartByNickname(String nickname);

}
