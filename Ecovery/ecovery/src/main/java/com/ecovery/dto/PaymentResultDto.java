package com.ecovery.dto;

import com.ecovery.constant.PayMethod;
import lombok.Getter;
import lombok.Setter;

/*
 * 결제 API의 결제 결과 전달용 Dto
 * @author : sehui
 * @fileName : PaymentResultDto
 * @since : 250724
 */

@Getter
@Setter
public class PaymentResultDto {

    private String paymentKey;       //외부 결제 API에서 반환하는 결제 고유 id
    private String orderUuid;       //결제 API에서 사용하는 주문 고유번호
    private int amount;
    private PayMethod payMethod;    //결제 방식
}
