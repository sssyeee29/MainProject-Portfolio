package com.ecovery.controller;

/*
 * 구매 상품 조회 Controller
 * @author : 방희경
 * @fileName : OrderHistoryController
 * @since : 250722
 */

import com.ecovery.dto.OrderHistoryDto;
import com.ecovery.service.OrderHistoryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/orders")
@Slf4j
public class OrderHistoryController {

    private final OrderHistoryService orderHistoryService;

    // 구매이력 간락 목록 조회
    @GetMapping(value = "/list")
    public String orderHistory(Model model, HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/member/login"; //비로그인 상태면 로그인 페이지 이동
        }

        // 간략한 주문 목록을 모델에 담기
        List<OrderHistoryDto> orderSummaries = orderHistoryService.getOrderSummaries(memberId);
        model.addAttribute("orderSummaries", orderSummaries);

        return "order/orderhistory";
    }

    // 주문 상세 보기
    @GetMapping(value = "/{orderId}/detail")
    public String orderDetail(@PathVariable("orderId") Long orderId, Model model, HttpSession session) {
        log.info("📌 주문 상세 조회 요청 들어옴: orderId = {}", orderId);
        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/member/login"; //비로그인 상태면 로그인 페이지 이동
        }
        // 특정 주문의 상세정보 가져오기
        OrderHistoryDto orderDetail = orderHistoryService.getOrderDetail(orderId);

        // 해당 주문이 현재 로그인한 회원의 주문이 맞는지 확인하는 로직
        if (orderDetail == null) {
            log.warn("❗ orderId={}에 대한 주문 정보를 찾을 수 없습니다.", orderId);
            return "redirect:/error";
        }

        // 로그인한 회원의 주문이 맞는지 확인
        if (!Objects.equals(orderDetail.getMemberId(), memberId)) {
            log.warn("❗ 현재 로그인한 회원의 주문이 아닙니다. 요청 회원: {}, 주문 소유자: {}", memberId, orderDetail.getMemberId());
            return "redirect:/error";
        }

        log.info("✅ 주문 상세 정보 조회 성공: {}", orderDetail);
        model.addAttribute("orderDetail", orderDetail);
        model.addAttribute("order", orderDetail);
        return "order/order-detail";
    }
}
