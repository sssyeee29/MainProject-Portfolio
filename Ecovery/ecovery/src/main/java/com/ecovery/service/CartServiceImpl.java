package com.ecovery.service;

import com.ecovery.domain.MemberVO;
import com.ecovery.mapper.CartMapper;
import com.ecovery.mapper.ItemMapper;
import com.ecovery.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * 장바구니 Service Interface
 * 장바구니 상품아이디 조회용
 * 상품 수정, 삭제는 CartItemItemServiceImpl에서 처리
 * @author : 방희경
 * @fileName : CartServiceImpl
 * @since : 250722
 */

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final MemberMapper memberMapper;

    // 닉네임으로 장바구니 ID 조회
    public Long getCartByNickname(String nickname){
        MemberVO memberVO = memberMapper.findByNickname(nickname);
        if(memberVO == null){
            return null;
        }
        return cartMapper.findCartIdByMemberId(memberVO.getMemberId());
    }
}
