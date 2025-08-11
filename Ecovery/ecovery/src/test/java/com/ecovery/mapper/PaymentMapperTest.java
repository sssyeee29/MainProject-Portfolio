package com.ecovery.mapper;

import com.ecovery.constant.PayMethod;
import com.ecovery.constant.PayStatus;
import com.ecovery.domain.PaymentVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 에코마켓 결제 Mapper Test
 * @author : sehui
 * @fileName : PaymentMapperTest
 * @since : 250725
 * @history
 *  - 250725 | sehui | 결제내역 저장 Test 실행
 */

@SpringBootTest
@Transactional
@Slf4j
class PaymentMapperTest {

    @Autowired
    private PaymentMapper paymentMapper;

    @Test
    @DisplayName("결제내역 저장")
    public void testInsert() {

        //given : 결제내역 설정
        PaymentVO payment = PaymentVO.builder()
                .orderId(1L)
                .orderUuid(UUID.randomUUID().toString())
                .memberId(2L)
                .paymentKey("key123")
                .payMethod(PayMethod.KAKAOPAY)
                .payStatus(PayStatus.PAID)
                .payAmount(1000)
                .build();

        //when : 결제내역 저장
        paymentMapper.insertPayment(payment);

        //then : 결과 검증
        assertNotNull(payment.getPaymentId());

        log.info("Inserted payment >> {}", payment);
    }

}