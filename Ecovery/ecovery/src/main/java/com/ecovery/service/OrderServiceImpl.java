package com.ecovery.service;

import com.ecovery.constant.OrderStatus;
import com.ecovery.domain.OrderVO;
import com.ecovery.dto.OrderDto;
import com.ecovery.dto.OrderItemDto;
import com.ecovery.dto.OrderItemRequestDto;
import com.ecovery.dto.PaymentResultDto;
import com.ecovery.mapper.OrderItemMapper;
import com.ecovery.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/*
 * 에코마켓 주문 ServiceImpl
 * @author : sehui
 * @fileName : OrderServiceImpl
 * @since : 250722
 * @history
 *  - 250723 | sehui | 주문 페이지에 보여줄 주문 정보 세팅 기능 추가
 *  - 250723 | sehui | 주문 저장 기능 추가
 *  - 250724 | sehui | 주문 고유번호 UUID 생성하는 로직 추가
 *  - 250725 | sehui | 주문 id 조회 기능 추가
 *  - 250725 | sehui | 주문 취소/결제 실패 시 관련 주문의 주문 상태 변경 기능 추가
 *  - 250728 | sehui | 주문 페이지 재출력용 주문 단건 조회 기능 추가
 *  - 250728 | sehui | 주문 저장 기능에 totalPrice 추가
 *  - 250802 | sehui | orderUuid 가독성을 위해 날짜 + 랜덤 조합으로 변경
 *  - 250804 | sehui | 실제 주문 저장 반환 타입 변경, 반환값에 orderUuid 추가
 */

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderItemService orderItemService;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    //주문 페이지 출력용 임시 OrderDto 생성
    @Override
    public OrderDto prepareOrderDto(List<OrderItemRequestDto> requestDtoList, Long memberId) {

        List<OrderItemDto> orderItemList = new ArrayList<>();

        int totalPrice = 0;

        //주문 상품 orderItem
        for(OrderItemRequestDto requestDto : requestDtoList) {
            //주문 상품 객체 생성 + 재고 차감
            OrderItemDto orderItem = orderItemService.buildOrderItem(requestDto);

            orderItemList.add(orderItem);

            totalPrice += orderItem.getOrderPrice();
        }

        //주문 UUID 생성 (날짜 + 랜덤 조합)
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);     //"20250802"
        String randomPart = UUID.randomUUID().toString().substring(0,6).toUpperCase();  //대문자, 6자리
        String orderUuid = datePart + "-" + randomPart;

        //주문 OrderDto 객체 생성, 주문 상품 List, 총 금액 값 설정
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderUuid(orderUuid);
        orderDto.setMemberId(memberId);
        orderDto.setOrderItems(orderItemList);
        orderDto.setTotalPrice(totalPrice);

        return orderDto;
    }

    //주문 저장
    @Override
    public Map<String, Object> saveOrder(OrderDto orderDto, Long memberId) {

        //DB에 저장하기 전에 totalPrice 다시 계산
        int calculatedTotalPrice = orderDto.getOrderItems().stream()
                .mapToInt(item -> item.getPrice() * item.getCount())
                .sum();

        //주문 저장하기 위해 OrderVO 생성 및 값 설정
        OrderVO order = OrderVO.builder()
                .orderUuid(orderDto.getOrderUuid())
                .memberId(memberId)
                .orderStatus(OrderStatus.ORDER)
                .name(orderDto.getName())
                .zipcode(orderDto.getZipcode())
                .roadAddress(orderDto.getRoadAddress())
                .detailAddress(orderDto.getDetailAddress())
                .phoneNumber(orderDto.getPhoneNumber())
                .totalPrice(calculatedTotalPrice)
                .build();

        //주문 DB 저장
        int result = orderMapper.insertOrder(order);

        if(result != 1) {
            throw new RuntimeException("주문 저장에 실패하였습니다.");
        }

        //주문 id, 주문 고유 번호 확인
        Long orderId = order.getOrderId();
        String orderUuid = order.getOrderUuid();

        //주문 상품 저장
        for(OrderItemDto orderItemDto : orderDto.getOrderItems()) {
            //DB에 저장하기 전에 OrderDto의 orderPrice 다시 계산
            int calculatedOrderPrice = orderItemDto.getPrice() * orderItemDto.getCount();
            orderItemDto.setOrderPrice(calculatedOrderPrice);

            orderItemService.saveOrderItem(orderItemDto, orderId);
        }

        //Map으로 반환
        Map<String, Object> saveResult = new HashMap<>();
        saveResult.put("orderId", orderId);
        saveResult.put("orderUuid", orderUuid);

        return saveResult;
    }

    //주문 고유 id로 주문 id 조회
    @Override
    public Long getOrderId(String orderUuid) {

        return orderMapper.findOrderIdByOrderUuid(orderUuid);
    }

    //주문 상태 변경
    @Override
    public boolean updateOrderStatus(PaymentResultDto paymentResult, OrderStatus orderStatus) {

        //주문 id 조회
        Long orderId = getOrderId(paymentResult.getOrderUuid());

        if(orderId == null) {
            throw new IllegalArgumentException("존재하지 않는 주문입니다.");
        }

        //DB의 주문 상태 변경
        int result = orderMapper.updateOrderStatus(orderId, orderStatus);

        if(result != 1) {
            throw new IllegalArgumentException("주문 상태 변경에 실패했습니다.");
        }

        return true;
    }

    //주문 단건 조회
    @Override
    public OrderDto getOrderDto(Long orderId, Long memberId) {
        
        //주문 정보 단건 조회
        OrderVO order = orderMapper.findOrderById(orderId);
    
        if(order == null || !memberId.equals(order.getMemberId())) {
            throw new IllegalArgumentException("주문이 존재하지 않거나 권한이 없습니다.");
        }

        //주문 상품 전체 조회
        List<OrderItemDto> orderItemDtoList = orderItemService.getOrderItemsByOrderId(orderId);

        if(orderItemDtoList == null || orderItemDtoList.isEmpty()) {
            throw new IllegalArgumentException("주문 상품이 존재하지 않습니다.");
        }

        return new OrderDto(order, orderItemDtoList);
    }
}
