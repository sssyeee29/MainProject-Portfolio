package com.ecovery.service;

import com.ecovery.domain.ItemVO;
import com.ecovery.domain.OrderItemVO;
import com.ecovery.dto.OrderItemDto;
import com.ecovery.dto.OrderItemRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 에코마켓 주문 상품 Service Test
 * @author : sehui
 * @fileName : OrderItemServiceTest
 * @since : 250722
 * @history
 *  - 250722 | sehui | 주문 페이지 출력용 객체 생성 Test 실행
 *  - 250723 | sehui | 주문 상품 저장용 객체 생성 Test 실행
 *  - 250728 | sehui | 주문 상품 전체 조회 기능 Test 실행
 */

@SpringBootTest
@Transactional
@Slf4j
class OrderItemServiceTest {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ItemService itemService;

    @Test
    @DisplayName("주문 페이지 출력용")
    public void testBuild() {

        //given : 주문 요청 정보 OrderItemRequestDto 생성
        OrderItemRequestDto requestDto = new OrderItemRequestDto();
        requestDto.setItemId(9L);
        requestDto.setCount(3);

        //when : 주문 상품 객체 생성
        OrderItemDto dto = orderItemService.buildOrderItem(requestDto);

        //then : 결과 검증
        assertNotNull(dto);
        assertEquals(9L, dto.getItemId());
        assertEquals(3, dto.getCount());

        log.info("dto >> {} ", dto.toString());
    }

    @Test
    @DisplayName("주문 상품 저장용")
    public void testCreate(){

        //given : 주문 상품 정보 OrderItemDto, 주문 id 설정
        OrderItemDto orderItemDto = OrderItemDto.builder()
                .itemId(10L)
                .count(2)
                .orderPrice(2000)
                .build();

        Long orderId = 1L;

        //when : 주문 상품 객체 생성
        OrderItemVO vo = orderItemService.saveOrderItem(orderItemDto, orderId);

        //then : 결과 검증
        assertNotNull(vo);
        assertEquals(10L, vo.getItemId());
        assertEquals(2, vo.getCount());

        log.info("vo >> {} ", vo.toString());
    }

    @Test
    @DisplayName("주문 상품 전체 조회")
    public void testGetOrderItems(){

        //given : 주문 id 설정
        Long orderId = 4L;

        //when : 주문 상품 전체 조희
        List<OrderItemDto> orderItemList = orderItemService.getOrderItemsByOrderId(orderId);

        //then : 결과 검증
        assertNotNull(orderItemList);

        orderItemList.forEach(orderItem -> log.info(orderItem.toString()));

    }

}