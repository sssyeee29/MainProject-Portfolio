package com.ecovery.service;

import com.ecovery.dto.PaymentResultDto;

/*
 * 에코마켓 결제 Service
 * @author : sehui
 * @fileName : PaymentService
 * @since : 250725
 * @history
 *  - 250725 | sehui | 결제내역 저장 기능 추가
 */

public interface PaymentService {

    //결제내역 저장
    public Long confirmPayment(PaymentResultDto paymentResult, Long memberId);
}
