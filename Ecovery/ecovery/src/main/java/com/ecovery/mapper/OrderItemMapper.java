package com.ecovery.mapper;

import com.ecovery.domain.OrderItemVO;
import com.ecovery.dto.OrderItemDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/*
 * 에코마켓 주문 상품 Mapper
 * @author : sehui
 * @fileName : OrderItemMapper
 * @since : 250722
 * @history
 *  - 250722 | sehui | 주문한 개별 상품 정보 저장 기능 추가
 *  - 250722 | sehui | 주문 상품 단건 조회 기능 추가
 *  - 250724 | sehui | 주문 상품 단건 조회 반환타입 변경
 *  - 250728 | sehui | 주문 상품 단건 조회 메소드를 주문 상품 전체 조회로 변경
 */

@Mapper
public interface OrderItemMapper {

    //주문한 개별 상품 정보 저장
    public void insertOrderItem(OrderItemVO orderItemVO);

    //주문 상품 전체 조회
    public List<OrderItemDto> findOrderItemByOrderId(Long orderId);

}
