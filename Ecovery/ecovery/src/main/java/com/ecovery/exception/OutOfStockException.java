package com.ecovery.exception;

/*
 * 상품 주문 수량 > 재고 수량 예외 발생 클래스
 * @author : sehui
 * @fileName : OutOfStockException
 * @since : 250722
 */

public class OutOfStockException extends RuntimeException {

    //예외 메시지 RuntimeException에게 전달
    public OutOfStockException(String message) {
        super(message);
    }
}
