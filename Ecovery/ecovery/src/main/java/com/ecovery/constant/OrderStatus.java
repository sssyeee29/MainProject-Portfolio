package com.ecovery.constant;

/*
 * 회원의 주문 상태
 * @author : 오세희
 * @fileName : OrderStatus(enum)
 * @since : 250709
 * @history
 *  - 250709 | sehui | 주문 상태 초기 설정
 *  - 250725 | sehui | 결제 전 대기 상태 변수 추가
 */

public enum OrderStatus {
    READY,      //주문 생성 완료, 결제 전 대기 상태 (결제 취소/실패 포함)
    ORDER,      //결제 완료, 주문 확정
    CANCEL      //주문 취소
}
