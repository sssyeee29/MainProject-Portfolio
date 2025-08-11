package com.ecovery.service;

import com.ecovery.constant.OrderStatus;
import com.ecovery.dto.OrderDto;
import com.ecovery.dto.OrderItemDto;
import com.ecovery.dto.OrderItemRequestDto;
import com.ecovery.dto.PaymentResultDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 에코마켓 주문 Service Test
 * @author : sehui
 * @fileName : OrderServiceTest
 * @since : 250723
 * @history
 *  - 250723 | sehui | 주문 페이지에 보여줄 주문 정보 세팅 Test 실행
 *  - 250723 | sehui | 실제 주문 저장 Test 실행
 *  - 250725 | sehui | 주문 id 조회 Test 실행
 *  - 250725 | sehui | 주문 취소/결제 실패 시 관련 주문의 주문 상태 변경 기능 Test 실행
 *  - 250728 | sehui | 주문 페이지 재출력용 주문 단건 조회 기능 Test 실행
 *  - 250728 | sehui | 주문 저장에 totalPrice 추가하여 Test 재실행
 */

@SpringBootTest
@Transactional
@Rollback(false)    //DB 반영
@Slf4j
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("주문 페이지의 주문 정보")
    public void testPageOrderDto() {

        //given : 주문 요청 정보 설정
        List<OrderItemRequestDto> requestDtoList = new ArrayList<>();

        OrderItemRequestDto requestDto1 = new OrderItemRequestDto();
        requestDto1.setItemId(5L);
        requestDto1.setCount(2);

        OrderItemRequestDto requestDto2 = new OrderItemRequestDto();
        requestDto2.setItemId(9L);
        requestDto2.setCount(3);

        requestDtoList.add(requestDto1);
        requestDtoList.add(requestDto2);

        Long memberId = 2L;

        //when : 주문 정보 OrderDto 세팅
        OrderDto orderDto = orderService.prepareOrderDto(requestDtoList, memberId);

        //then : 결과 검증
        assertNotNull(orderDto);

        log.info("orderDto >> {}", orderDto.toString());

    }

    @Test
    @DisplayName("주문 저장")
    public void testSaveOrder(){

        //given : 주문 정보 설정
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderUuid("test_orderUuid1");
        orderDto.setMemberId(2L);
        orderDto.setName("tester22");
        orderDto.setZipcode("12345");
        orderDto.setRoadAddress("서울시 00구 00동");
        orderDto.setDetailAddress("000동 000호");
        orderDto.setPhoneNumber("010-1111-2222");

        List<OrderItemDto> requestDtoList = new ArrayList<>();

        OrderItemDto requestDto1 = OrderItemDto.builder()
                .itemId(11L)
                .itemName("수정 제품")
                .price(10000)
                .count(2)
                .orderPrice(10000 * 2)
                .build();

        OrderItemDto requestDto2 = OrderItemDto.builder()
                .itemId(12L)
                .itemName("test 수정Item")
                .price(5000)
                .count(3)
                .orderPrice(5000 * 3)
                .build();

        requestDtoList.add(requestDto1);
        requestDtoList.add(requestDto2);

        orderDto.setOrderItems(requestDtoList);

        Long memberId = 2L;

        //when : 주문 저장
//        Long savedOrderId = orderService.saveOrder(orderDto, memberId);

        //then : 결과 검증
//        assertNotNull(savedOrderId);
//
//        log.info("savedOrderId >> {}", savedOrderId);

    }

    @Test
    @DisplayName("주문 id 조회")
    public void testGetOrderId(){

        //given : orderUuid 설정 (DB 존재하는 값 사용)
        String orderUuid = "test_uuid";

        //when : 주문 id 조회
        Long orderId = orderService.getOrderId(orderUuid);

        //then : 결과 검증
        assertNotNull(orderId);

        log.info("orderId >> {}", orderId);
    }

    @Test
    @DisplayName("주문 상태 변경 - 결제 실패")
    public void testUpdateOrderStatus(){

        //given : 결제 정보, 주문 상태 설정
        PaymentResultDto paymentResult = new PaymentResultDto();
        paymentResult.setPaymentKey("test_imp_key");
        paymentResult.setOrderUuid("test_uuid");
        paymentResult.setAmount(2);

        //결제 실패 상태
        OrderStatus orderStatus = OrderStatus.READY;

        //when : 주문 상태 변경
        boolean result = orderService.updateOrderStatus(paymentResult, orderStatus);

        //then : 결과 검증
        assertTrue(result, "주문 상태 변경이 실패했습니다.");
    }

    @Test
    @DisplayName("주문 단건 조회 - 재조회")
    public void testGetOrderDto(){

        //given : 주문 id, 회원 id 설정
        Long orderId = 4L;
        Long memberId = 2L;

        //when : 주문 단건 조회
        OrderDto orderDto = orderService.getOrderDto(orderId, memberId);

        //then : 결과 검증
        assertNotNull(orderDto);
        log.info("orderDto >> {}", orderDto.toString());
    }
}