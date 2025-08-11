package com.ecovery.dto;

import com.ecovery.domain.OrderVO;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

/*
 * 에코마켓 사용자의 전체 주문 정보 + 사용자 입력 정보 Dto
 * @author : sehui
 * @fileName : OrderDto
 * @since : 250723
 * @history
 *  - 250723 | sehui | OrderDto 생성
 *  - 250724 | sehui | 주문 고유번호 UUID 변수 추가
 *  - 250728 | sehui | OrderDto 변환용 생성자 추가
 *  - 250730 | 방희경 | 주문조회에서 상세설명을 보기 위해 변수 추가 + 필드 정리
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long orderId;           //주문 id
    private String orderUuid;       //주문 고유번호 UUID
    private Long memberId;          //주문한 사용자 id

    private List<OrderItemDto> orderItems; // 해당 주문으로 생성될 상품 목록

    //사용자 입력 정보
    @NotBlank
    private String name;

    @NotBlank
    private String zipcode;

    @NotBlank
    private String roadAddress;

    @NotBlank
    private String detailAddress;

    @NotBlank
    private String phoneNumber;

    private int totalPrice;             //총 주문 금액

    //OrderDto 변환용 생성자
    public OrderDto(OrderVO order, List<OrderItemDto> orderItems) {
        this.orderId = order.getOrderId();
        this.orderUuid = order.getOrderUuid();
        this.memberId = order.getMemberId();
        this.orderItems = orderItems;
        this.name = order.getName();
        this.zipcode = order.getZipcode();
        this.roadAddress = order.getRoadAddress();
        this.detailAddress = order.getDetailAddress();
        this.phoneNumber = order.getPhoneNumber();
        this.totalPrice = order.getTotalPrice();
    }
}
