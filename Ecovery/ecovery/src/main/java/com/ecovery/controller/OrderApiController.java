package com.ecovery.controller;

import com.ecovery.domain.MemberVO;
import com.ecovery.dto.OrderDto;
import com.ecovery.dto.OrderItemRequestDto;
import com.ecovery.security.CustomUserDetails;
import com.ecovery.service.MemberService;
import com.ecovery.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * 에코마켓 주문 Api Controller
 * @author : sehui
 * @fileName : OrderApiController
 * @since : 250723
 * @history
 *  - 250723 | sehui | 주문 페이지 요청 기능 추가
 *  - 250723 | sehui | 결제 버튼 클릭 시 주문 저장 기능 추가
 *  - 250728 | sehui | 결제 실패 시 주문 페이지 재구성 기능 추가
 *  - 250802 | sehui | 주문 페이지 요청 Principal를 Authentication으로 변경
 *  - 250804 | sehui | 주문 저장 요청 Principal를 Authentication으로 변경
 *  - 250804 | sehui | 주문 페이지 재구성 요청 Principal를 Authentication으로 변경
 *  - 250804 | sehui | 주문 저장 반환값에 orderUuid가 추가된 result로 변경
 *  - 250805 | sehui | 주문 페이지 재구성 기능 삭제
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
@Slf4j
public class OrderApiController {

    private final OrderService orderService;
    private final MemberService memberService;

    //주문 페이지 요청
    @PostMapping("/prepare")
    public ResponseEntity<?> prepareOrder(@Valid @RequestBody List<OrderItemRequestDto> orderItemRequests,
                                                 BindingResult bindingResult,
                                                 Authentication auth) {
        try{
            //유효성 검사
            if(bindingResult.hasErrors()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            //로그인한 사용자가 아닐 경우
            if(auth == null || !auth.isAuthenticated()) {
                throw new IllegalArgumentException("로그인한 사용자만 주문할 수 있습니다.");
            }

            //로그인한 사용자 정보 조회
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            String email = userDetails.getEmail();
            
            MemberVO member = memberService.getMemberByEmail(email);
            
            //사용자 정보 DB에 없는 경우
            if(member == null) {
                throw new IllegalArgumentException("해당 이메일로 등록된 사용자가 없습니다." + email);
            }

            Long memberId  = memberService.getMemberByEmail(email).getMemberId();

            //주문 페이지에 출력할 OrderDto 생성
            OrderDto orderDto = orderService.prepareOrderDto(orderItemRequests, memberId);

            return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);

        }catch (IllegalArgumentException e) {
            log.error("주문 정보 조회 중 오류 : {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        }catch (Exception e) {
            log.error("알수 없는 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    //주문 저장
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveOrder(@Valid @RequestBody OrderDto orderDto,
                                                         BindingResult bindingResult,
                                                         Authentication auth) {
        try{
            //유효성 검사
            if(bindingResult.hasErrors()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            //로그인한 사용자가 아닐 경우
            if(auth == null || !auth.isAuthenticated()) {
                throw new IllegalArgumentException("로그인한 사용자만 주문할 수 있습니다.");
            }

            //로그인한 사용자 정보 조회
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            String email = userDetails.getEmail();

            MemberVO member = memberService.getMemberByEmail(email);

            //사용자 정보 DB에 없는 경우
            if(member == null) {
                throw new IllegalArgumentException("해당 이메일로 등록된 사용자가 없습니다." + email);
            }

            Long memberId  = memberService.getMemberByEmail(email).getMemberId();

            //저장 요청(result: orderId, orderUuid)
            Map<String, Object> result = orderService.saveOrder(orderDto, memberId);

            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "주문 등록 중 에러가 발생하였습니다.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
