package com.ecovery.domain;

import com.ecovery.constant.PayMethod;
import com.ecovery.constant.PayStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/*
 * 회원의 결제 정보 VO
 * @author : sehui
 * @fileName : PaymentVO
 * @since : 250709
 * @history
 *  - 250709 | sehui | 결제 정보 VO 생성
 *  - 250724 | sehui | 필수 변수 추가
 */

@Getter
@Setter
@Builder
@ToString
public class PaymentVO {

    private Long paymentId;

    private Long orderId;       //Order 테이블과 연동
    private String orderUuid;   //결제 API에 전달할 주문 고유번호

    private Long memberId;      //회원 정보

    private String paymentKey;  //API에서 전달받은 결제 키
    private PayMethod payMethod;   //결제 수단(카카오페이, 토스페이)
    private int payAmount;      //총 결제 금액
    private PayStatus payStatus;    //결제 처리 상태
    private LocalDateTime paidAt;   //결제 완료 시간

}
