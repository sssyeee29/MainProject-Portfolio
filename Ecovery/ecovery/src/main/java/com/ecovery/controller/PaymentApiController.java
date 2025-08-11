package com.ecovery.controller;

import com.ecovery.constant.OrderStatus;
import com.ecovery.dto.OrderDto;
import com.ecovery.dto.PaymentResultDto;
import com.ecovery.security.CustomUserDetails;
import com.ecovery.service.MemberService;
import com.ecovery.service.OrderService;
import com.ecovery.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/*
 * 에코마켓 결제 Api Controller
 * @author : sehui
 * @fileName : PaymentApiController
 * @since : 250725
 * @history
 *  - 250725 | sehui | 결제 성공 시 결제 정보 저장 기능 추가
 *  - 250725 | sehui | 결제 실패 시 주문 상태 변경 기능 추가
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Slf4j
public class PaymentApiController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final MemberService memberService;

    //결제 성공 시 결제 정보 저장
    @PostMapping("/success")
    public ResponseEntity<Long> savePayment(@RequestBody PaymentResultDto paymentResult, Authentication auth) {

        //로그인한 사용자의 email 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String email = userDetails.getEmail();

        Long memberId = memberService.getMemberByEmail(email).getMemberId();

        //결제 정보 저장
        Long savedOrderId = paymentService.confirmPayment(paymentResult, memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrderId);
    }

    //주문 취소 시 주문 상태 변경
    @PostMapping("/fail")
    public ResponseEntity<Map<String, Object>> failPayment(@RequestBody PaymentResultDto paymentResult, Authentication auth) {

        Map<String, Object> response = new HashMap<>();

        try{
            //로그인한 사용자의 email 가져오기
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            String email = userDetails.getEmail();

            Long memberId = memberService.getMemberByEmail(email).getMemberId();

            //주문 상태 'READY'로 변경
            boolean result = orderService.updateOrderStatus(paymentResult, OrderStatus.READY);

            if(result){
                Long orderId = orderService.getOrderId(paymentResult.getOrderUuid());

                response.put("orderId", orderId);
                response.put("message", "결제 실패");

                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("message", "주문 상태 변경에 실패했습니다.");

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }catch (Exception e){
            response.put("message", "서버 오류로 인해 결제 실패 처리가 불가능합니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }
}
