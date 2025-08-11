package com.ecovery.mapper;

import com.ecovery.domain.CartItemVO;
import com.ecovery.dto.CartDetailDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 장바구니 상품조회 Mapper Test
 * @author : 방희경
 * @fileName : CartItemMapperTest
 * @since : 250728
 */

@SpringBootTest
@Slf4j
@Data
class CartItemMapperTest {

    @Autowired
    CartItemMapper cartItemMapper;

    // 테스트 전용 값들(DB에 저장되어 있어야 가능)
    Long testCartId = 2L;
    Long testItemId = 2L;
    int testAddCount = 100;
    Long testCartItemId = 3L;

    // 장바구니 목록 조회
    @Test
    void getCartItems() {
        List<CartDetailDto> items = cartItemMapper.getCartItems(testCartId);

        for(CartDetailDto item : items) {
            log.info(item.toString());
        }
    }

    // 장바구니 상품 추가
    @Test
    void insertCartItem(){
        CartItemVO vo = new CartItemVO();
        vo.setCartId(testCartId);
        vo.setItemId(testItemId);
        vo.setCount(1);

        cartItemMapper.insertCartItem(vo);
    }

    @Test
    void addSecondItemToCart() {
        Long secondItemId = 1L;  // 에코 텀블러
        int addCount = 2;

        // 기존에 장바구니에 있는지 확인
        CartItemVO found = cartItemMapper.findCartItem(testCartId, secondItemId);

        if (found != null) {
            // 있으면 수량 증가
            Long cartItemId = found.getCartItemId();
            int oldCount = found.getCount();

            cartItemMapper.updateCartItem(cartItemId, addCount);
            CartItemVO updated = cartItemMapper.findCartItem(testCartId, secondItemId);

            log.info("두 번째 상품 수량 증가: 기존={} → 변경={}", oldCount, updated.getCount());
            assertEquals(addCount, updated.getCount());

        } else {
            // 없으면 새로 추가
            CartItemVO newItem = new CartItemVO();
            newItem.setCartId(testCartId);
            newItem.setItemId(secondItemId);
            newItem.setCount(addCount);
            newItem.setCreatedAt(new java.util.Date());

            cartItemMapper.insertCartItem(newItem);
            CartItemVO inserted = cartItemMapper.findCartItem(testCartId, secondItemId);

            log.info("두 번째 상품 새로 추가: 수량={}", inserted.getCount());
            assertNotNull(inserted);
            assertEquals(addCount, inserted.getCount());
        }
    }

    // 장바구니 중복 조회
    @Test
    void updateCartItem(){
        CartItemVO item = cartItemMapper.findCartItem(testCartId, testItemId);
        if(item != null) {
            item.setCount(3);
            log.info("중복 항목 수량: " + item.getCount());
        } else
            log.info("장바구니에 해당 상품 없음");
    }
    
    // 수량 변경
    @Test
    void updateCartItemCount(){
        cartItemMapper.updateCartItemCount(2L, 10);
        log.info("수정완료");
    }
    
    // 장바구니 삭제
    @Test
    void deleteCartItem(){
        cartItemMapper.deleteCartItem(6L);
        log.info("삭제완료");
    }

    // 장바구니에 있어면 갯수 수정, 없으면 상품 추가
    @Test
    void findCartItem(){
        // 1. 장바구니에서 해당 상품 있는지 확인
        CartItemVO found = cartItemMapper.findCartItem(testCartId, testItemId);

        if (found != null) {
            // 2. 있으면 수량 증가
            Long cartItemId = found.getCartItemId();
            int oldCount = found.getCount();

            cartItemMapper.updateCartItem(cartItemId, testAddCount);
            CartItemVO updated = cartItemMapper.findCartItem(testCartId, testItemId);

            log.info("수량증가", oldCount, updated.getCount());
            assertEquals(oldCount + testAddCount, updated.getCount());

        } else {
            // 3. 없으면 새로 추가
            CartItemVO newItem = new CartItemVO();
            newItem.setCartId(testCartId);
            newItem.setItemId(testItemId);
            newItem.setCount(testAddCount);
            newItem.setCreatedAt(new java.util.Date());

            cartItemMapper.insertCartItem(newItem);
            CartItemVO inserted = cartItemMapper.findCartItem(testCartId, testItemId);

            log.info("상품 추가 = {}", inserted.getCount());
            assertNotNull(inserted);
            assertEquals(testAddCount, inserted.getCount());
        }
    }

}