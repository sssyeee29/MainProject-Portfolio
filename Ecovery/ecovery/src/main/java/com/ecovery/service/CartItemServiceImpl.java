package com.ecovery.service;

import com.ecovery.domain.CartItemVO;
import com.ecovery.domain.CartVO;
import com.ecovery.domain.ItemVO;
import com.ecovery.domain.MemberVO;
import com.ecovery.dto.CartDetailDto;
import com.ecovery.mapper.CartItemMapper;
import com.ecovery.mapper.CartMapper;
import com.ecovery.mapper.ItemMapper;
import com.ecovery.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/*
 * 장바구니 CartItemItemServiceImpl
 * 장바구니 상품 조회, 수정, 삭제 기능
 * @author : 방희경
 * @fileName : CartItemItemServiceImpl
 * @since : 250722
 */

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartMapper cartMapper;
    private final ItemMapper itemMapper;
    private final MemberMapper memberMapper;
    private final CartItemMapper cartItemMapper;

    // 장바구니 담기
    @Override
    public String addCart(String nickname, Long itemId, int count){
        MemberVO memberVO = memberMapper.findByNickname(nickname);
        if(memberVO == null)
            return "회원 정보를 찾을 수 없습니다.";

        Long cartId = cartMapper.findCartIdByMemberId(memberVO.getMemberId());

        if(cartId == null){
            log.info("회원 ID {}의 장바구니가 없어 새로 생성합니다.", memberVO.getMemberId());
        CartVO newCart = new CartVO();

        newCart.setMemberid(memberVO.getMemberId());
        newCart.setCreatedAt((new Date()));

        cartMapper.insertCart(newCart); // 장바구니 생성
        cartId = newCart.getCartId(); // 새로 생성된 cartId를 가져와 사용
        if (cartId == null){
            return "장바구니 생성에 실패 했습니다..";
            }
        }

        ItemVO item = itemMapper.findByItemId(itemId);
        if(item == null)
            return "상품이 존재하지 않습니다.";
        if(count > item.getStockNumber()){
            return "재고 수량을 초과하여 담을 수 없습니다.";
        }

        // 상품 중복 방지 및 수량 누적
        CartItemVO existing = cartItemMapper.findCartItem(cartId, itemId);
        if(existing != null){
            int total = existing.getCount() + count;
            if(total > item.getStockNumber()){
                return "기존 수량과 합쳐 재고 초과입니다.";
            }
            cartItemMapper.updateCartItem(existing.getCartItemId(), count);
        } else {
            CartItemVO newItem = new CartItemVO();
            newItem.setCartId(cartId);
            newItem.setItemId(itemId);
            newItem.setCount(count);
            newItem.setCreatedAt(new Date());
            cartItemMapper.insertCartItem(newItem);
        }
        return "장바구니에 담았습니다.";
    }

    // 장바구니 목록 조회
    public List<CartDetailDto> getCartItmes(String nickname){
        MemberVO memberVO = memberMapper.findByNickname(nickname);
        Long cartId = cartMapper.findCartIdByMemberId(memberVO.getMemberId());

        return cartItemMapper.getCartItems(cartId);
    }

    // 장바구니 수량 수정
    public String updateCartCount(Long cartItemId, int count, Long itemId){
        ItemVO item = itemMapper.findByItemId(itemId);
        if (item == null){
            return "상품 정보를 찾을 수 없습니다.";
        }
        if(count > item.getStockNumber()){
            return "재고 초과.";
        }

        cartItemMapper.updateCartItemCount(cartItemId, count);

        return "수량 변경 완료";
    }

    // 장바구니 상품 삭제
    public String deleteCartItem(Long cartItemId){
        cartItemMapper.deleteCartItem(cartItemId);
        return "삭제완료";
    }

}
