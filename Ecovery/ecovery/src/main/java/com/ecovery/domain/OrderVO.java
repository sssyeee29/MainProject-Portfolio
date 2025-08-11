package com.ecovery.domain;

import com.ecovery.constant.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/*
 * 회원의 주문 정보 VO
 * @author : sehui
 * @fileName : OrderVO
 * @since : 250709
 * @history
 *  - 250709 | sehui | OrderVO 생성
 *  - 250724 | sehui | 주문 고유번호 orderUuid 변수 추가
 *  - 250728 | sehui | 총 주문 금액 totalPrice 변수 추가
 */

@Getter
@Setter
@Builder
@ToString
public class OrderVO {

    private Long orderId;           //주문 Id
    private String orderUuid;       //주문 고유번호
    private Long memberId;          //회원 Id
    private OrderStatus orderStatus;    //주문 상태
    private Date createdAt;         //주문한 날짜
    private Date updatedAt;         //주문 변경한 날짜
    private String name;            //주문자 이름
    private String zipcode;         //배송지 우편번호
    private String roadAddress;     //배송지 도로명 주소
    private String detailAddress;   //배송지 상세 주소
    private String phoneNumber;     //주문자 핸드폰 번호
    private int totalPrice;         //총 주문 금액
}
