package com.ecovery.mapper;

import com.ecovery.constant.OrderStatus;
import com.ecovery.domain.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 에코마켓 주문 Mapper Test
 * @author : sehui
 * @fileName : OrderMapperTest
 * @since : 250723
 * @history
 *  - 250723 | sehui | 주문 저장 Test 실행
 *  - 250724 | sehui | orderUuid 추가하여 주문 저장 Test 재실행
 *  - 250725 | sehui | 주문 id 조회 Test 실행
 *  - 250725 | sehui | 주문 취소/결제 실패 시 관련 주문의 주문 상태 변경 기능 Test 실행
 *  - 250728 | sehui | 주문 단건 조회 기능 Test 실행
 *  - 250728 | sehui | 주문 저장 기능에 totalPrice 추가하여 Test 재실행
 *  - 250728 | sehui | 주문 단건 조회 기능 Test 재실행
 */

@SpringBootTest
@Transactional
@Rollback(false)
@Slf4j
class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    @DisplayName("주문 저장")
    public void testInsert() {

        //given : OrderVO 객체 설정
        OrderVO order = OrderVO.builder()
                .orderUuid("test_uuid")
                .memberId(2L)
                .orderStatus(OrderStatus.ORDER)
                .name("tester2")
                .zipcode("12345")
                .roadAddress("서울시 00구 00동")
                .detailAddress("1동 1호")
                .phoneNumber("010-1234-5678")
                .totalPrice(10000)
                .build();

        //when : 주문 저장
        int result = orderMapper.insertOrder(order);

        //then : 결과 검증
        assertEquals(1, result);

        log.info("저장된 주문 >> {}", order);

    }

    @Test
    @DisplayName("주문 id 조회")
    public void testFindOrderId(){

        //given : orderUuid 설정 (DB 존재하는 값 사용)
        String orderUuid = "test_uuid";

        //when : 주문 id 조회
        Long orderId = orderMapper.findOrderIdByOrderUuid(orderUuid);

        //then : 결과 검증
        assertNotNull(orderId);

        log.info("orderId >> {}", orderId);
    }

    @Test
    @DisplayName("주문 상태 변경 - 결제 실패")
    public void testUpdateOrderStatus(){

        //given : 주문 id, 주문 상태 설정
        Long orderId = 1L;
        OrderStatus orderStatus = OrderStatus.READY;

        //when : 주문 상태 변경
        int result = orderMapper.updateOrderStatus(orderId, orderStatus);

        //then : 결과 검증
        assertEquals(1, result);
    }

    @Test
    @DisplayName("주문 단건 조회")
    public void testFindOrder() {

        //given : 주문 id 설정
        Long orderId = 9L;

        //when : 주문 단건 조회
        OrderVO order = orderMapper.findOrderById(orderId);

        //then : 결과 검증
        assertNotNull(order);

        log.info("order >> {}", order);
    }

}