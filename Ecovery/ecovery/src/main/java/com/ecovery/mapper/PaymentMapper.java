package com.ecovery.mapper;

import com.ecovery.domain.PaymentVO;
import org.apache.ibatis.annotations.Mapper;

/*
 * 에코마켓 결제 Mapper
 * 에코마켓 결제내역 DB 저장 및 조회를 위한 인터페이스
 * @author : 오세희
 * @fileName : PaymentMapper
 * @since : 250725
 * @history
 *  - 250725 | sehui | 결제내역 저장 기능 추가
 */

@Mapper
public interface PaymentMapper {

    //결제내역 저장
    public void insertPayment(PaymentVO payment);
}
