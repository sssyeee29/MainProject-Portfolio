package com.ecovery.dto;

import com.ecovery.constant.OrderStatus;
import com.ecovery.constant.PayStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/*
 * 구매이력 조회 DTO
 * 주문 1건에 대한 정보 + 주문 상세 목록 포함
 * @author : 방희경
 * @fileName : OrderHistoryDto
 * @since : 250723
 * @history
 *  - 20250725 | 방희경 | 주문 고유번호 추가
 *  - 20250730 | 방희경 | DTO 역할 명확화 및 필드 정리
 */

@Setter @Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryDto {

    // 주문자 정보(orders)
    private Long orderId;
    private OrderStatus orderStatus;
    private Date createdAt;
    private Date updatedAt;
    private String name;
    private String zipcode;
    private String roadAddress;
    private String detailAddress;
    private String phoneNumber;
    private String orderUuid;
    private Long memberId;


    // 결제 정보(payment)
    private Long paymentId;
    private String payMethod;
    private int payAmount;
    private PayStatus payStatus;
    private LocalDateTime paidAt;

    private List<OrderItemDto> orderItems; // 주문 상세 상품 목록
}