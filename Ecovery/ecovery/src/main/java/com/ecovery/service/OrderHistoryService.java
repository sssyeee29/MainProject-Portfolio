package com.ecovery.service;

/*
 * 구매이력 조회 서비스 인터페이스
 * 실제 구현은 OrderHistoryServiceImpl에서 처리
 * @author : 방희경
 * @fileName : OrderHistoryService
 * @since : 250723
 * @history
 *  - 20250731 | 방희경 | 구매이력 간략 + 상세 분리 로직 구현
 */

import com.ecovery.dto.OrderHistoryDto;

import java.util.List;

public interface OrderHistoryService {

    // 로그인한 사용자의 간략한 주문 내역 조회
    public List<OrderHistoryDto> getOrderSummaries(Long memberId);

    // 특정 주문의 상세 내역 조회
    public OrderHistoryDto getOrderDetail(Long orderId);
}
