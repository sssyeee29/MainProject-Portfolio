package com.ecovery.mapper;

import com.ecovery.constant.OrderStatus;
import com.ecovery.domain.OrderVO;
import com.ecovery.dto.OrderHistoryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/*
 * 구매이력 조회 Mapper
 * 구매이력 조회 및 상세 내용 확인을 위한 인터페이스
 * @author : 방희경
 * @fileName : OrderMapper
 * @since : 250722
 * @history
 *  - 250723 | sehui | 주문 저장 기능 추가
 *  - 250725 | sehui | 주문 id 조회 기능 추가
 *  - 250725 | sehui | 주문 취소/결제 실패 시 관련 주문의 주문 상태 변경 기능 추가
 *  - 250728 | sehui | 주문 단건 조회 기능 추가
 *  - 250731 | 방희경 | 주문 조회 상세 조회 로직 추가
 */

@Mapper
public interface OrderMapper {

    // 로그인한 사용자의 주문 내역 조회
    public List<OrderHistoryDto> findOrderSummaries(Long memberId);

    //주문 저장
    public int insertOrder(OrderVO orderVO);

    //주문 고유 id로 주문 id 조회
    public Long findOrderIdByOrderUuid(String orderUuid);

    //주문 상태 변경
    public int updateOrderStatus(Long orderId, OrderStatus orderStatus);

    //주문 단건 조회
    public OrderVO findOrderById(Long orderId);

    // 특정 주문의 상세 내역 조회
    public OrderHistoryDto findOrdersItems(Long orderId);
}
