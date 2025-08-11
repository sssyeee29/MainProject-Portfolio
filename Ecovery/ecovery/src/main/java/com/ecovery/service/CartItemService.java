package com.ecovery.service;

/*
 * 장바구니 Service Interface
 * 장바구니 상품 조회, 수정, 삭제 기능
 * 실제 구현은 CartItemItemServiceImpl에서 처리
 * @author : 방희경
 * @fileName : CartItemService
 * @since : 250721
 */

import com.ecovery.dto.CartDetailDto;

import java.util.List;

public interface CartItemService {

    // 장바구니 담기
    public String addCart(String nickname, Long itemId, int count);

    // 장바구니 목록 조회
    public List<CartDetailDto> getCartItmes(String nickname);

    // 장바구니 수량 수정
    public String updateCartCount(Long cartItemId, int count, Long itemId);

    // 장바구니 상품 삭제
    public String deleteCartItem(Long cartItemId);

}
