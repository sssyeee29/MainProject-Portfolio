package com.ecovery.service;

import com.ecovery.constant.OrderStatus;
import com.ecovery.dto.OrderDto;
import com.ecovery.dto.OrderItemRequestDto;
import com.ecovery.dto.PaymentResultDto;

import java.util.List;
import java.util.Map;

/*
 * 에코마켓 주문 Service
 * @author : sehui
 * @fileName : OrderService
 * @since : 250722
 * @history
 *  - 250723 | sehui | 주문 페이지에 보여줄 주문 정보 세팅 기능 추가
 *  - 250723 | sehui | 실제 주문 저장 기능 추가
 *  - 250725 | sehui | 주문 id 조회 기능 추가
 *  - 250725 | sehui | 주문 취소/결제 실패 시 관련 주문의 주문 상태 변경 기능 추가
 *  - 250728 | sehui | 주문 단건 조회 기능 추가
 *  - 250804 | sehui | 실제 주문 저장 반환 타입 변경
 */


public interface OrderService {

    //주문 페이지용 주문 정보 세팅
    public OrderDto prepareOrderDto(List<OrderItemRequestDto> requestDtoList, Long memberId);

    //실제 주문 저장
    public Map<String, Object> saveOrder(OrderDto orderDto, Long memberId);

    //주문 고유 id로 주문 id 조회
    public Long getOrderId(String orderUuid);

    //주문 상태 변경
    public boolean updateOrderStatus(PaymentResultDto paymentResult, OrderStatus orderStatus);

    //주문 단건 조회
    public OrderDto getOrderDto(Long orderId, Long memberId);

}
