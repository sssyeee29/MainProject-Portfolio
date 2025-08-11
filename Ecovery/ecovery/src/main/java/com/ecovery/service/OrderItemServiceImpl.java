package com.ecovery.service;

import com.ecovery.domain.ItemImgVO;
import com.ecovery.domain.ItemVO;
import com.ecovery.domain.OrderItemVO;
import com.ecovery.dto.OrderItemDto;
import com.ecovery.dto.OrderItemRequestDto;
import com.ecovery.mapper.OrderItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * 에코마켓 주문 상품 ServiceImpl
 * @author : sehui
 * @fileName : OrderItemServiceImpl
 * @since : 250722
 * @history
 *  - 250723 | sehui | 주문 페이지 출력용 객체 생성 기능 추가
 *  - 250723 | sehui | 주문 상품 저장용 객체 생성 기능 추가
 *  - 250728 | sehui | 주문 상품 전체 조회 기능 추가
 */

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderItemServiceImpl implements OrderItemService {

    private final ItemService itemService;
    private final ItemImgService itemImgService;
    private final OrderItemMapper orderItemMapper;

    //주문 페이지 출력용 객체 생성
    @Override
    public OrderItemDto buildOrderItem(OrderItemRequestDto requestDto) {

        //상품 DB 조회
        ItemVO item = itemService.findByItemId(requestDto.getItemId());

        //상품 이미지 DB 조회
        ItemImgVO itemImg = itemImgService.getRepImgByItemId(item.getItemId());

        //이미지 존재하지 않은 예외 처리
        if(itemImg == null) {
            throw new IllegalArgumentException("대표 이미지가 존재하지 않습니다.");
        }

        //주문 상품 객체 생성
        OrderItemDto dto = OrderItemDto.builder()
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .price(item.getPrice())
                .count(requestDto.getCount())
                .orderPrice(item.getPrice() * requestDto.getCount())
                .itemImgId(itemImg.getItemImgId())
                .imgName(itemImg.getImgName())
                .itemDetail(item.getItemDetail())
                .imgUrl(itemImg.getImgUrl())
                .build();

        return dto;
    }

    //주문 상품 저장용 객체 생성
    @Override
    public OrderItemVO saveOrderItem(OrderItemDto orderItemDto, Long orderId) {

        //재고 차감
        itemService.removeStock(orderItemDto.getItemId(), orderItemDto.getCount());

        //저장할 주문 상품 VO 객체 생성
        OrderItemVO orderItem =  OrderItemVO.builder()
                .orderId(orderId)
                .itemId(orderItemDto.getItemId())
                .count(orderItemDto.getCount())
                .orderPrice(orderItemDto.getOrderPrice())
                .build();

        //DB 저장
        orderItemMapper.insertOrderItem(orderItem);

        return orderItem;
    }

    //주문 상품 전체 조회
    @Override
    public List<OrderItemDto> getOrderItemsByOrderId(Long orderId) {
        return orderItemMapper.findOrderItemByOrderId(orderId);
    }
}
