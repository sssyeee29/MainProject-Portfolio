package com.ecovery.controller;

import com.ecovery.dto.OrderHistoryDto;
import com.ecovery.dto.OrderItemRequestDto;
import com.ecovery.service.OrderHistoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * 에코마켓 주문 View Controller
 * @author : sehui
 * @fileName : OrderViewController
 * @since : 250728
 * @history
 *  - 250728 | sehui | View 반환을 위해 컨트롤러 생성
 *  - 250801 | sehui | 주문 페이지 첫 화면 요청 GET -> POST로 변경
 *  - 250801 | sehui | 반환하는 View 변경
 *  - 250801 | sehui | 주문 페이지 첫 화면에서 model로 전달하는 값 추가
 *  - 250802 | sehui | 주문 페이지 첫 화면에 orderItemRequestDto를 JSON 문자열로 반환 로직 추가
 *  - 250804 | 방희경 | 여러개 상품 주문을 위한 로직 변경
 *  - 250804 | yukyeong | 주문 완료 페이지 이동 메서드 추가
 *  - 250804 | sehui | 주문 페이지 첫 화면 @RequestBody 추가 시 오류 발생하여 삭제함
 */


@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderViewController {

    private final OrderHistoryService orderHistoryService;

    //주문 페이지 첫 화면
    @PostMapping("/prepare")
    public String preparePage(OrderItemRequestDto orderItemRequest, Model model) {

        try{
            //OrderItemRequestDto를 JSON 문자열로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(List.of(orderItemRequest));

            model.addAttribute("orderItemRequests", json);
        }catch (JsonProcessingException e){
            throw new RuntimeException("JSON 변환 실패", e);
        }

        return "order/order-payment";
    }

    //결제 실패 시 다시 주문 페이지
    @GetMapping("/retry/{orderId}")
    public String RetryPage(@PathVariable String orderId, Model model){

        model.addAttribute("orderId", orderId);

        return "order/order-payment";
    }

    //주문 완료 페이지
    @GetMapping("/complete/{orderId}")
    public String completePage(@PathVariable Long orderId, Model model) {
        // 1. 주문 상세 정보 조회 (OrderHistoryDto 리턴)
        OrderHistoryDto order = orderHistoryService.getOrderDetail(orderId);
        // 2. 모델에 "order"라는 이름으로 객체 전달 → 뷰에서 ${order}로 사용 가능
        model.addAttribute("order", order); // 전체 주문 정보를 넘김
        // 3. 해당 뷰로 이동 (order/order-complete)
        return "order/order-complete";
    }
}
