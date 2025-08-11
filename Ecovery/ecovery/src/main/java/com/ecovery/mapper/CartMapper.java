package com.ecovery.mapper;

import com.ecovery.domain.CartVO;
import org.apache.ibatis.annotations.Mapper;


/**
 * 장바구니 조회 Mapper
 * 장바구니 ID 찾기, 장바구니 없을 경우 생성
 * @author : 방희경
 * @fileName : CartMapper
 * @since : 20250721
 */

@Mapper
public interface CartMapper {

    // 회원 ID로 장바구니 ID 조회
    public Long findCartIdByMemberId(Long memberId);

    // 장바구니가 없을 경우 생성
    public void insertCart(CartVO cartVO);

}
